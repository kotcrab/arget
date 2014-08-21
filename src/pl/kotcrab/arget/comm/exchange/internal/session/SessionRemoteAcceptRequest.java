
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Send by server to target, request to accept session from requester. Valid responses are {@link SessionRejectedNotification} and
 * {@link SessionAcceptedNotification}
 * @author Pawel Pastuszak */
public class SessionRemoteAcceptRequest extends SessionExchange {
	public String requesterKey;

	@Deprecated
	public SessionRemoteAcceptRequest () {
		super(null);
	}

	public SessionRemoteAcceptRequest (UUID id, String requesterKey) {
		super(id);
		this.requesterKey = requesterKey;
	}
}
