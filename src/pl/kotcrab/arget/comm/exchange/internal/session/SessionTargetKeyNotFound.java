
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionTargetKeyNotFound extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionTargetKeyNotFound () {
		super(null);
	}

	public SessionTargetKeyNotFound (UUID id) {
		super(id);
	}
}
