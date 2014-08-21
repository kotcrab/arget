
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies requester that target accepted session request.
 * @author Pawel Pastuszak */
public class SessionAcceptedNotification extends SessionExchange {

	@Deprecated
	public SessionAcceptedNotification () {
		super(null);
	}

	public SessionAcceptedNotification (UUID id) {
		super(id);
	}

}
