
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies requester that cipher init was successful.
 * @author Pawel Pastuszak */
public class SessionRemoteReadyNotification extends SessionExchange {

	@Deprecated
	public SessionRemoteReadyNotification () {
		super(null);
	}

	public SessionRemoteReadyNotification (UUID id) {
		super(id);
	}
}
