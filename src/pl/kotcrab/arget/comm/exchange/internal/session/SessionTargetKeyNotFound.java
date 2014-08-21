
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies that server could not find provided public profile key. (contact was not connected to server).
 * @author Pawel Pastuszak */
public class SessionTargetKeyNotFound extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionTargetKeyNotFound () {
		super(null);
	}

	public SessionTargetKeyNotFound (UUID id) {
		super(id);
	}
}
