
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies that provided UUID is currently in use or it is invalid
 * @author Pawel Pastuszak */
public class SessionInvalidIDNotification extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionInvalidIDNotification () {
		super(null);
	}

	public SessionInvalidIDNotification (UUID id) {
		super(id);
	}

}
