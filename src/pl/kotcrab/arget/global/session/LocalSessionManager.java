
package pl.kotcrab.arget.global.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAcceptedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitError;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherKeysTrsanfer;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCloseNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCreateRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionEncryptedTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRejectedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteAcceptRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteReadyNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionUnrecoverableBroken;
import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.global.GlobalClient;
import pl.kotcrab.arget.global.gui.MainWindowCallback;
import pl.kotcrab.arget.util.KryoUtils;
import pl.kotcrab.arget.util.ProcessingQueue;

import com.esotericsoftware.kryo.Kryo;

//TODO implement queue for send!!!, current implementation may be thread unsafe wich will be bad
public class LocalSessionManager {
	private static final String TAG = "SessionManager";

	private GlobalClient server;
	private MainWindowCallback guiCallback;
	private LocalSessionListener listener;

	private List<LocalSession> sessions;

	private ProcessingQueue<SessionExchange> receivingQueue;
	private ProcessingQueue<SessionExchange> sendingQueue;

	private Kryo receiverKryo;
	private Kryo senderKryo;

	public LocalSessionManager (GlobalClient server, MainWindowCallback guiCallback, LocalSessionListener listener) {
		this.server = server;
		this.guiCallback = guiCallback;
		this.listener = listener;

		sessions = Collections.synchronizedList(new ArrayList<LocalSession>());
		receiverKryo = new Kryo();
		senderKryo = new Kryo();
		KryoUtils.registerInternalSessionClasses(receiverKryo);
		KryoUtils.registerInternalSessionClasses(senderKryo);

		receivingQueue = new ProcessingQueue<SessionExchange>("SessionManager Receiving Queue") {
			@Override
			protected void processQueueElement (SessionExchange ex) {
				processReceivedExchange(ex);
			}
		};

		sendingQueue = new ProcessingQueue<SessionExchange>("SessionManager Sending Queue") {

			@Override
			protected void processQueueElement (SessionExchange ex) {
				processExchangeToSend(ex);
			}

		};
	}

	private void processReceivedExchange (SessionExchange ex) {
		if (ex instanceof SessionRemoteAcceptRequest) {
			SessionRemoteAcceptRequest request = (SessionRemoteAcceptRequest)ex;

			if (guiCallback.isKeyInContacts(request.requesterKey)) {
				sendLater(new SessionAcceptedNotification(request.id));
				sessions.add(new LocalSession(request.id, request.requesterKey));
				listener.sessionCreated(request.id, request.requesterKey);
				return;
			} else {
				sendLater(new SessionRejectedNotification(request.id));
				return;
			}

		}

		LocalSession session = getSessionByUUID(ex.id);

		if (session == null) {
			Log.w(TAG, "Could not found session by UUID: " + ex.id);
			return;
		}

		if (ex instanceof SessionUnrecoverableBroken) {
			session.sessionReady = false;
			listener.sessionBroken(ex);
			sessions.remove(session);
			return;
		}

		if (ex instanceof SessionAcceptedNotification) {
			sendLater(new SessionCipherKeysTrsanfer(ex.id, session.cipherInitLine));
			return;
		}

		if (ex instanceof SessionCipherKeysTrsanfer) {
			SessionCipherKeysTrsanfer init = (SessionCipherKeysTrsanfer)ex;
			boolean success = session.initCipherWithKeys(server.getProfile().rsa.getPrivateKey(), init.initData);

			if (success) {
				session.sessionReady = true;
				listener.sessionReady(init.id);
				sendLater(new SessionRemoteReadyNotification(init.id));
			} else
				sendLater(new SessionCipherInitError(init.id));
		}

		if (ex instanceof SessionRemoteReadyNotification) {
			session.sessionReady = true;
			listener.sessionReady(ex.id);
			return;
		}

		if (ex instanceof SessionEncryptedTransfer) {
			SessionEncryptedTransfer enc = (SessionEncryptedTransfer)ex;
			byte[] data = session.decrypt(enc.data);
			if (data != null)
				listener.sessionDataRecieved((InternalSessionExchange)KryoUtils.readClassAndObjectFromByteArray(receiverKryo, data));
		}

	}

	private void processExchangeToSend (SessionExchange ex) {
		if (ex instanceof InternalSessionExchange) {
			LocalSession session = getSessionByUUID(ex.id);

			if (session != null && session.sessionReady) {
				byte[] data = KryoUtils.writeClassAndObjectToByteArray(senderKryo, ex);
				server.send(new SessionEncryptedTransfer(ex.id, session.encrypt(data)));
			}
			
			return;
		}

		server.send(ex);
	}

	public void processReceivedElementLater (SessionExchange ex) {
		receivingQueue.processLater(ex);
	}

	public void sendLater (SessionExchange exchange) {
		sendingQueue.processLater(exchange);
	}

	public LocalSession getSessionByUUID (UUID id) {
		for (LocalSession s : sessions) {
			if (s.id.compareTo(id) == 0) return s;
		}

		return null;
	}

	public void createSession (ContactInfo contact) {
		UUID id = UUID.randomUUID();

		LocalSession session = new LocalSession(id, contact.publicProfileKey);
		session.initCipher(server.getProfile().rsa.getPrivateKey());
		sessions.add(session);

		listener.sessionCreated(id, contact.publicProfileKey);
		sendLater(new SessionCreateRequest(id, contact.publicProfileKey));
	}

	public void closeAll () {
		for (LocalSession session : sessions) {
			session.sessionReady = false;
			listener.sessionClosed(session.id);
			sendLater(new SessionCloseNotification(session.id));
		}
	}

	public void stop () {
		receivingQueue.stop();
		sendingQueue.stop();
	}

}
