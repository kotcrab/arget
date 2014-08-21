
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies requester that target rejected session request.
 * @author Pawel Pastuszak */
public class SessionRejectedNotification extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionRejectedNotification () {
		super(null);
	}

	public SessionRejectedNotification (UUID id) {
		super(id);
	}

}
