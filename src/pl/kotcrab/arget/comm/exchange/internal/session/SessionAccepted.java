
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionAccepted extends SessionExchange {

	@Deprecated
	public SessionAccepted () {
		super(null);
	}

	public SessionAccepted (UUID id) {
		super(id);
	}

}
