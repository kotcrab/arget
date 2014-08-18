
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionAlreadyExist extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionAlreadyExist () {
		super(null);
	}

	public SessionAlreadyExist (UUID id) {
		super(id);
	}

}
