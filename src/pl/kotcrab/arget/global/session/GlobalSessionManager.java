
package pl.kotcrab.arget.global.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAlreadyExistNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCreateRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidIDNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidReciever;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRemoteAcceptRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionTargetKeyNotFound;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionUnrecoverableBroken;
import pl.kotcrab.arget.global.ResponseServer;
import pl.kotcrab.arget.util.ProcessingQueue;

//TODO clean outdated session when creating new ones
public class GlobalSessionManager extends ProcessingQueue<GlobalSessionUpdate> {
	private static final String TAG = "SessionManager";

	private Collection<ResponseServer> servers;
	private List<GlobalSession> sessions;

	public GlobalSessionManager (Collection<ResponseServer> servers) {
		super("GlobalSessionManager");
		this.servers = servers;

		sessions = new ArrayList<GlobalSession>();
	}

	@Override
	protected void processQueueElement (GlobalSessionUpdate update) {

		SessionExchange ex = update.exchange;

		if (ex instanceof SessionCreateRequest) {
			SessionCreateRequest request = (SessionCreateRequest)ex;

			if (isIDInUse(request.id)) {
				update.reciever.send(new SessionInvalidIDNotification(request.id));
				return;
			}

			ResponseServer target = getServerForKey(request.targetKey);
			if (target == null) {
				update.reciever.send(new SessionTargetKeyNotFound(request.id));
				return;
			}

			for (GlobalSession ses : sessions) {
				if ((ses.requester == target && ses.target == update.reciever)
					|| (ses.requester == update.reciever && ses.target == target)) {
					update.reciever.send(new SessionAlreadyExistNotification(request.id));
					return;
				}

			}

			target.send(new SessionRemoteAcceptRequest(request.id, update.reciever.getProfilePublicKey()));
			sessions.add(new GlobalSession(request.id, update.reciever, target));
			Log.l(TAG, "Created: " + request.id);
			return;
		}

		GlobalSession session = getSessionByID(ex);

		if (isValidUpdateForSession(session, update)) {
			if (ex instanceof SessionUnrecoverableBroken) {
				Log.l(TAG, "Closed: " + session.id);
				sendToOposite(session, update);
				sessions.remove(session);
				return;
			}

			sendToOposite(session, update);
		}

	}

	private void sendToOposite (GlobalSession session, GlobalSessionUpdate update) {
		if (session.requester == update.reciever)
			session.target.send(update.exchange);
		else
			session.requester.send(update.exchange);
	}

	private GlobalSession getSessionByID (SessionExchange ex) {
		for (GlobalSession ses : sessions) {
			if (ses.id.compareTo(ex.id) == 0) return ses;
		}

		return null;
	}

	private boolean isValidUpdateForSession (GlobalSession session, GlobalSessionUpdate update) {
		UUID id = update.exchange.id;

		if (session == null) {
			Log.w(TAG, "Could not found session by UUID: " + id + " Receiver ID: " + update.reciever.getId());
			return false;
		}

		if (session.requester == update.reciever || session.target == update.reciever)
			return true;
		else {
			update.reciever.send(new SessionInvalidReciever(id));
			Log.w(TAG, "Not valid update for session UUID: " + id + " Receiver ID: " + update.reciever.getId());
			return false;
		}
	}

	private boolean isIDInUse (UUID id) {
		for (GlobalSession s : sessions) {
			if (s.id.compareTo(id) == 0) return true;
		}

		return false;
	}

	private ResponseServer getServerForKey (String key) {
		for (ResponseServer s : servers) {
			if (s.getProfilePublicKey().equals(key)) return s;
		}

		return null;
	}
}
