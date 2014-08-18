
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionInvalidReciever extends SessionExchange {

	@Deprecated
	public SessionInvalidReciever () {
		super(null);
	}

	public SessionInvalidReciever (UUID id) {
		super(id);
	}

}
