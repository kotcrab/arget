
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

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
