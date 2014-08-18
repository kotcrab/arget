
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionRejected extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionRejected () {
		super(null);
	}

	public SessionRejected (UUID id) {
		super(id);
	}

}
