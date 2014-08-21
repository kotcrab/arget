
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies remote receiver that session should be closed.
 * @author Pawel Pastuszak */
public class SessionCloseNotification extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionCloseNotification () {
		super(null);
	}

	public SessionCloseNotification (UUID id) {
		super(id);
	}
}
