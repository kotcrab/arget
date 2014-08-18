
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionRemoteReady extends SessionExchange {

	@Deprecated
	public SessionRemoteReady () {
		super(null);
	}

	public SessionRemoteReady (UUID id) {
		super(id);
	}
}
