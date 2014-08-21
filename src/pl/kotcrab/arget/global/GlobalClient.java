
package pl.kotcrab.arget.global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import pl.kotcrab.arget.comm.ExchangePinger;
import pl.kotcrab.arget.comm.ExchangeSender;
import pl.kotcrab.arget.comm.TimeoutListener;
import pl.kotcrab.arget.comm.exchange.DisconnectingNotification;
import pl.kotcrab.arget.comm.exchange.EncryptedExchange;
import pl.kotcrab.arget.comm.exchange.EncryptionModeExchange;
import pl.kotcrab.arget.comm.exchange.Exchange;
import pl.kotcrab.arget.comm.exchange.RSAPublicKeyExchange;
import pl.kotcrab.arget.comm.exchange.SymmetricKeysExchange;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventExchange;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventExchange.Type;
import pl.kotcrab.arget.comm.exchange.internal.KeyUsedByOtherNotification;
import pl.kotcrab.arget.comm.exchange.internal.KeychainExchange;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyExchange;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationRequest;
import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyVerificationResponse;
import pl.kotcrab.arget.comm.exchange.internal.ResponseOKNotification;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.global.gui.MainWindowCallback;
import pl.kotcrab.arget.global.session.LocalSessionListener;
import pl.kotcrab.arget.global.session.LocalSessionManager;
import pl.kotcrab.arget.profile.Profile;
import pl.kotcrab.arget.util.KryoUtils;
import pl.kotcrab.arget.util.ProcessingQueue;
import pl.kotcrab.arget.util.ThreadUtils;
import pl.kotcrab.crypto.CascadeCipher;
import pl.kotcrab.crypto.RSAEncrypter;
import pl.kotcrab.crypto.SimpleSymmetricCipher;
import pl.kotcrab.crypto.SymmetricCipher;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class GlobalClient extends ProcessingQueue<Exchange> {
	private enum State {
		WAIT_FOR_CONFIG, WAIT_FOR_RSA, WAIT_FOR_TEST_DATA, WAIT_FOR_OK_NOTIF, CONNECTED
	}

	private boolean successfullyInitialized = false;

	private ServerInfo info;
	private Profile profile;
	private MainWindowCallback guiCallback;

	private State state = State.WAIT_FOR_CONFIG;

	private EncryptionMode encryptionMode;
	private SimpleSymmetricCipher cipher;

	private ExchangeSender sender;
	private ExchangePinger pinger;

	private LocalSessionManager sessionManager;

	private ArrayList<String> lastKeychain;

	private Client client;

	private Kryo internalKryo;

	public GlobalClient (ServerInfo info, Profile profile, MainWindowCallback guiCallback, LocalSessionListener listener) {
		super("GlobalClient");
		this.info = info;
		this.profile = profile;
		this.guiCallback = guiCallback;

		guiCallback.setConnectionStatus(ConnectionStatus.CONNECTING);

		sessionManager = new LocalSessionManager(this, guiCallback, listener);

		internalKryo = new Kryo();
		KryoUtils.registerInternalNetClasses(internalKryo);

		try {
			initSocket(info.ip, info.port);
		} catch (IOException e) {
			guiCallback.setConnectionStatus(ConnectionStatus.ERROR, e.getMessage());

			// we don't have to print stack trace if this just was unable to connect error
			if (e.getMessage().contains("Unable to connect") == false) e.printStackTrace();
		}

	}

	private void initSocket (String globalIp, int port) throws IOException {
// if (globalIp.startsWith("http")) { //TODO proper support for url adresses
// InetAddress address = InetAddress.getByName(new URL(globalIp).getHost());
// globalIp = address.getHostAddress();
// }

		client = new Client(200000, 200000);
		KryoUtils.registerNetClasses(client.getKryo());
		client.start();

		sender = new ExchangeSender("GlobalClient Sender", client);
		pinger = new ExchangePinger(sender, "GlobalClient Pinger", new TimeoutListener() {
			@Override
			public void timedOut () {
				disconnect();
				guiCallback.setConnectionStatus(ConnectionStatus.TIMEDOUT, "Server not responded to ping messages.");
			}
		});

		client.connect(5000, globalIp, port);
		client.addListener(new Listener() {
			@Override
			public void received (Connection connection, Object object) {
				if (object instanceof Exchange) {
					Exchange exchange = (Exchange)object;
					processLater(exchange);
				}
			}
		});

		successfullyInitialized = true;
	}

	private void processKeychain (KeychainExchange keychain) {
		lastKeychain = keychain.publicKeys;
		guiCallback.updateContacts(); // this will call processLastKeychain
	}

	public void processLastKeychain () {
		if (lastKeychain != null) {
			List<ContactInfo> contacts = profile.contacts;

			for (ContactInfo c : contacts) {
				ContactStatus lastStatus = c.status;
				c.status = ContactStatus.DISCONNECTED;

				for (String key : lastKeychain) {

					if (c.publicProfileKey.equals(key)) {
						// contact may have status CONNECTED_SESSION, we don't want to reset that after getting keychain update
						if (lastStatus == ContactStatus.DISCONNECTED)
							c.status = ContactStatus.CONNECTED_GLOBAL;
						else
							c.status = lastStatus;

						break;
					}
				}
			}
		}
	}

	public void send (Exchange exchange) {
		sender.processLater(exchange);
	}

	public void createSession (ContactInfo contact) {
		sessionManager.createSession(contact);
	}

	private void disconnect () {
		disconnect(true);
	}

	private void disconnect (boolean changeGuiStatus) {
		stop();
		pinger.stop();
		sender.stop();
		client.stop();
		sessionManager.stop();

		if (changeGuiStatus) guiCallback.setConnectionStatus(ConnectionStatus.DISCONNECTED);
	}

	public Profile getProfile () {
		return profile;
	}

	public void requestDisconnect () {
		new Thread(new Runnable() {

			@Override
			public void run () {
				sessionManager.closeAll();
				if (sender != null) sender.processLater(new DisconnectingNotification());

				ThreadUtils.sleep(1000); // give some time for server to shutdown it's socket and close sessions
				disconnect();
			}
		}, "GlobalClient ExitRequest").start();

	}

	public LocalSessionManager getLocalSessionManager () {
		return sessionManager;
	}

	@Override
	protected void processQueueElement (Exchange ex) {
		if (ex instanceof EncryptedExchange) {
			EncryptedExchange enc = (EncryptedExchange)ex;
			byte[] data = cipher.decrypt(enc.data);
			ex = (Exchange)KryoUtils.readClassAndObjectFromByteArray(internalKryo, data);
		}

		if (ex instanceof EncryptionModeExchange && state == State.WAIT_FOR_CONFIG) {
			EncryptionModeExchange config = (EncryptionModeExchange)ex;
			encryptionMode = config.mode;

			switch (encryptionMode) {
			case AES:
				cipher = new SymmetricCipher("AES");
				break;
			case AES_TWOFISH_SERPENT:
				CascadeCipher cascade = new CascadeCipher();
				cascade.initGenerateKeys();
				cipher = cascade;
				break;
			default:
				// TODO add defautl
				break;
			}

			sender.enableInternalExchange(internalKryo, cipher);
			state = State.WAIT_FOR_RSA;
		}

		if (ex instanceof RSAPublicKeyExchange && state == State.WAIT_FOR_RSA) {
			RSAPublicKeyExchange keyEx = (RSAPublicKeyExchange)ex;
			RSAEncrypter rsaEncrypter = new RSAEncrypter(keyEx.key);

			switch (encryptionMode) {
			case AES:
				SymmetricCipher aesCipher = (SymmetricCipher)cipher;
				byte[] encryptedAesKey = rsaEncrypter.encrypt(aesCipher.getKeyEncoded());
				send(new SymmetricKeysExchange(encryptedAesKey));
				break;
			case AES_TWOFISH_SERPENT:
				CascadeCipher cascade = (CascadeCipher)cipher;
				byte[] encryptedKey1 = rsaEncrypter.encrypt(cascade.getKey1());
				byte[] encryptedKey2 = rsaEncrypter.encrypt(cascade.getKey2().getBytes());
				byte[] encryptedKey3 = rsaEncrypter.encrypt(cascade.getKey3().getBytes());
				send(new SymmetricKeysExchange(encryptedKey1, encryptedKey2, encryptedKey3));
				break;
			default:
				break;
			}

			send(new ProfilePublicKeyExchange(profile.rsa.getPublicKey().getEncoded()));
			state = State.WAIT_FOR_TEST_DATA;
		}

		if (ex instanceof ProfilePublicKeyVerificationRequest && state == State.WAIT_FOR_TEST_DATA) {
			ProfilePublicKeyVerificationRequest ver = (ProfilePublicKeyVerificationRequest)ex;
			send(new ProfilePublicKeyVerificationResponse(profile.rsa.decrypt(ver.encryptedTestData)));
			state = State.WAIT_FOR_OK_NOTIF;
		}

		if (ex instanceof ResponseOKNotification && state == State.WAIT_FOR_OK_NOTIF) {
			state = State.CONNECTED;
			guiCallback.setConnectionStatus(ConnectionStatus.CONNECTED);
			pinger.start();
		}

		if (ex instanceof UnsecuredEventExchange) {
			UnsecuredEventExchange resp = (UnsecuredEventExchange)ex;

			if (resp.type == Type.SERVER_FULL) {
				guiCallback.setConnectionStatus(ConnectionStatus.SERVER_FULL);
				disconnect(false);
			}

			if (resp.type == Type.SERVER_SHUTTING_DOWN) {
				guiCallback.setConnectionStatus(ConnectionStatus.SERVER_SHUTDOWN);
				disconnect(false);
			}

			if (resp.type == Type.KICKED) {
				guiCallback.setConnectionStatus(ConnectionStatus.KICKED);
				disconnect(false);
			}
		}

		if (state == State.CONNECTED) {
			pinger.update(ex);

			if (ex instanceof KeyUsedByOtherNotification)
				JOptionPane.showMessageDialog(null, "Somebody with your key connected to server, you were disconnected."
					+ " WARNING: If this wasn't you that may mean that your profile keys were stolen!");

			if (ex instanceof KeychainExchange) processKeychain((KeychainExchange)ex);
			if (ex instanceof SessionExchange) sessionManager.processReceivedElementLater((SessionExchange)ex);
			if (ex instanceof ServerInfoExchange) guiCallback.setServerInfo((ServerInfoExchange)ex);
		}
	}

	public boolean isSuccessfullyInitialized () {
		return successfullyInitialized;
	}

}
