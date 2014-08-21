
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies request that session with this client already exist on server. That may mean that two client wanted to create session
 * in almost same time.
 * @author Pawel Pastuszak */
public class SessionAlreadyExistNotification extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionAlreadyExistNotification () {
		super(null);
	}

	public SessionAlreadyExistNotification (UUID id) {
		super(id);
	}

}
