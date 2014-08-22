/*******************************************************************************
    Copyright 2014 Pawel Pastuszak
 
    This file is part of Arget.

    Arget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Arget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Arget.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.arget.server;

import java.util.UUID;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.ExchangePinger;
import pl.kotcrab.arget.comm.ExchangeSender;
import pl.kotcrab.arget.comm.TimeoutListener;
import pl.kotcrab.arget.comm.exchange.DisconnectingNotification;
import pl.kotcrab.arget.comm.exchange.EncryptedTransfer;
import pl.kotcrab.arget.comm.exchange.EncryptionModeTransfer;
import pl.kotcrab.arget.comm.exchange.Exchange;
import pl.kotcrab.arget.comm.exchange.RSAPublicKeyTransfer;
import pl.kotcrab.arget.comm.exchange.SymmetricKeysTransfer;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventNotification;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventNotification.Type;
import pl.kotcrab.arget.comm.exchange.internal.KeychainRequest;
import pl.kotcrab.arget.comm.exchange.internal.KeychainTransfer;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyTransfer;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationRequest;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationResponse;
import pl.kotcrab.arget.comm.exchange.internal.TestMsgResponseOKNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.server.session.GlobalSessionUpdate;
import pl.kotcrab.arget.util.KryoUtils;
import pl.kotcrab.arget.util.ProcessingQueue;
import pl.kotcrab.arget.util.ThreadUtils;
import pl.kotcrab.crypto.CascadeCipher;
import pl.kotcrab.crypto.RSACipher;
import pl.kotcrab.crypto.RSAEncrypter;
import pl.kotcrab.crypto.SimpleSymmetricCipher;
import pl.kotcrab.crypto.SymmetricCipher;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

public class ResponseServer extends ProcessingQueue<Exchange> {
// private enum State {
// AES, TWOFISH, SERPENT, WAIT_FOR_RSA_KEY, VERIFY_RSA, CONNECTED
// };

	private enum State {
		WAIT_FOR_AUTHREQUEST, WAIT_FOR_PUBLIC_PROFILE_KEY, WAIT_FOR_TEST_DATA_RESPONSE, CONNECTED
	};

	private boolean running = true;

	private String tag = "Client ";

	private Connection connection;
	private GlobalInterface global;

	private int id;
	private String profilePublicKey;

	private State state = State.WAIT_FOR_AUTHREQUEST;

	private ExchangeSender sender;
	private ExchangePinger pinger;

	private RSACipher rsaCipher;
	private SimpleSymmetricCipher cipher;

	private String randomUUID; // for public profile key verification

	private Kryo internalKryo;

	public ResponseServer (Connection connection, GlobalInterface global, int id) {
		super("ResponseServer ID: " + id);

		this.global = global;
		this.connection = connection;
		this.id = id;

		tag = "Client ID: " + id;

		Log.l(tag, "Connected, IP: " + connection.getRemoteAddressTCP().getHostString());

		rsaCipher = new RSACipher();

		internalKryo = new Kryo();
		KryoUtils.registerInternalNetClasses(internalKryo);

		initSocket();

		Log.l(tag, "Initialization finished");
	}

	private void initSocket () {
		sender = new ExchangeSender("ResponseServer Sender ID: " + id, connection);

		pinger = new ExchangePinger(sender, "ResponseServer Pinger ID: " + id, new TimeoutListener() {
			@Override
			public void timedOut () {
				Log.l(tag, "Timed out");

				stop();
			}
		});

		onConnected();
	}

	public void send (Exchange exchange) {
		if (running) sender.processLater(exchange);
	}

	private void onConnected () {
		send(new EncryptionModeTransfer(global.getEncryptionMode()));
		send(new RSAPublicKeyTransfer(rsaCipher.getPublicKey().getEncoded()));
	}

	@Override
	// TODO auto close all sessions associated with this response server
	public void stop () {
		if (running) {
			running = false;
			pinger.stop();
			sender.stop();
			super.stop();
			global.disconnect(connection);

			Log.l(tag, "Disconnected");
		}

	}

	private void stopLater () {
		new Thread(new Runnable() {

			@Override
			public void run () {
				ThreadUtils.sleep(200);
				stop();
			}
		}, tag + " Stop request").start();
	}

	public String getProfilePublicKey () {
		return profilePublicKey;
	}

	public int getId () {
		return id;
	}

	@Override
	protected void processQueueElement (Exchange ex) {
		if (ex instanceof EncryptedTransfer) {
			EncryptedTransfer enc = (EncryptedTransfer)ex;
			byte[] data = cipher.decrypt(enc.data);
			ex = (Exchange)KryoUtils.readClassAndObjectFromByteArray(internalKryo, data);
		}

		if (ex instanceof SymmetricKeysTransfer && state == State.WAIT_FOR_AUTHREQUEST) {
			SymmetricKeysTransfer auth = (SymmetricKeysTransfer)ex;

			switch (global.getEncryptionMode()) {
			case AES: {
				byte[] key1 = rsaCipher.decrypt(auth.key1);
				cipher = new SymmetricCipher("AES", key1);
				break;
			}

			case AES_TWOFISH_SERPENT: {
				byte[] key1 = rsaCipher.decrypt(auth.key1);
				byte[] key2 = rsaCipher.decrypt(auth.key2);
				byte[] key3 = rsaCipher.decrypt(auth.key3);

				CascadeCipher cascadeCipher = new CascadeCipher();
				cascadeCipher.initWithKeys(key1, key2, key3);
				cipher = cascadeCipher;
				break;
			}

			default:
				// TODO add default
				break;
			}

			sender.enableInternalExchange(internalKryo, cipher);

			state = State.WAIT_FOR_PUBLIC_PROFILE_KEY;
		}

		if (ex instanceof ProfilePublicKeyTransfer && state == State.WAIT_FOR_PUBLIC_PROFILE_KEY) {
			ProfilePublicKeyTransfer keyExchange = (ProfilePublicKeyTransfer)ex;
			profilePublicKey = keyExchange.key;

			randomUUID = UUID.randomUUID().toString();
			RSAEncrypter encrypter = new RSAEncrypter(profilePublicKey);
			byte[] toSend = encrypter.encrypt(randomUUID.getBytes());

			send(new ProfilePublicKeyVerificationRequest(toSend));

			state = State.WAIT_FOR_TEST_DATA_RESPONSE;
		}

		if (ex instanceof ProfilePublicKeyVerificationResponse && state == State.WAIT_FOR_TEST_DATA_RESPONSE) {
			ProfilePublicKeyVerificationResponse resp = (ProfilePublicKeyVerificationResponse)ex;
			if (randomUUID.equals(resp.decryptedTestData)) {

				// TODO not sure if this should return boolean if already connected and stop this method if so
				global.disconnectIfAlreadyConnected(this, profilePublicKey);

				send(new TestMsgResponseOKNotification());
				send(global.getServerInfoExchange());
				global.addPublicKey(profilePublicKey);
				pinger.start();

				Log.l(tag, "Authorization successful");
			} else {
				Log.l(tag, "Authorization failed, disconnecting...");
				send(new DisconnectingNotification());
				stop();
			}

			state = State.CONNECTED;
		}

		if (state == State.CONNECTED) {
			pinger.update(ex);
			if (ex instanceof DisconnectingNotification) stop();
			if (ex instanceof KeychainRequest) send(new KeychainTransfer(global.getKeychain()));
			if (ex instanceof SessionExchange) global.sessionUpdate(new GlobalSessionUpdate(this, (SessionExchange)ex));
		}
	}

	public Connection getConnection () {
		return connection;
	}

	public boolean isRunning () {
		return running;
	}

	public void kickUser () {
		send(new UnsecuredEventNotification(Type.KICKED));
		stopLater();

	}

}
