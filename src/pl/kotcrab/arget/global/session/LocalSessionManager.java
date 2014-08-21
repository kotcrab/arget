
package pl.kotcrab.arget.global.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.EncryptedExchange;
import pl.kotcrab.arget.comm.exchange.Exchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAccepted;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitDataExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitError;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCloseNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCreateRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionData;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionEncryptedExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRejected;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteAcceptRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteReady;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionUnrecoverableBroken;
import pl.kotcrab.arget.comm.exchange.internal.session.data.InternalSessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.data.MessageExchange;
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
				send(new SessionAccepted(request.id));
				sessions.add(new LocalSession(request.id, request.requesterKey));
				listener.sessionCreated(request.id, request.requesterKey);
				return;
			} else {
				send(new SessionRejected(request.id));
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

		if (ex instanceof SessionAccepted) {
			send(new SessionCipherInitDataExchange(ex.id, session.cipherInitLine));
			return;
		}

		if (ex instanceof SessionCipherInitDataExchange) {
			SessionCipherInitDataExchange init = (SessionCipherInitDataExchange)ex;
			boolean success = session.initCipherWithKeys(server.getProfile().rsa.getPrivateKey(), init.initData);

			if (success) {
				session.sessionReady = true;
				listener.sessionReady(init.id);
				send(new SessionRemoteReady(init.id));
			} else
				send(new SessionCipherInitError(init.id));
		}

		if (ex instanceof SessionRemoteReady) {
			session.sessionReady = true;
			listener.sessionReady(ex.id);
			return;
		}

		if (ex instanceof SessionEncryptedExchange) {
			SessionEncryptedExchange enc = (SessionEncryptedExchange)ex;
			byte[] data = session.decrypt(enc.data);
			listener.sessionDataRecieved((InternalSessionExchange)KryoUtils.readClassAndObjectFromByteArray(receiverKryo, data));
		}

		if (ex instanceof SessionData) {
			SessionData data = (SessionData)ex;
			listener.sessionDataRecieved(ex.id, getDecryptedData(session, data));
		}
	}

	private void processExchangeToSend (SessionExchange ex) {
		if (ex instanceof InternalSessionExchange) {
			LocalSession session = getSessionByUUID(ex.id);

			byte[] data = KryoUtils.writeClassAndObjectToByteArray(senderKryo, ex);
			server.send(new SessionEncryptedExchange(ex.id, session.encrypt(data)));

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

	@Deprecated
	public void sendEncryptedData (UUID id, String data) {
		LocalSession session = getSessionByUUID(id);
		if (session != null) sendEncryptedData(session, data);
	}

	@Deprecated
	public void sendEncryptedData (LocalSession session, String data) {
		if (session.sessionReady) send(new SessionData(session.id, session.encrypt(data)));
	}

	@Deprecated
	private String getDecryptedData (LocalSession session, SessionData data) {
		return session.decryptS(data.data);
	}

	@Deprecated
	private void send (SessionExchange ex) {
		server.send(ex);
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
		send(new SessionCreateRequest(id, contact.publicProfileKey));
	}

	public void closeAll () {
		for (LocalSession session : sessions) {
			session.sessionReady = false;
			listener.sessionClosed(session.id);
			send(new SessionCloseNotification(session.id));
		}
	}

	public void stop () {
		receivingQueue.stop();
		sendingQueue.stop();
	}

}
