
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionInvalidIDNotification extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionInvalidIDNotification () {
		super(null);
	}

	public SessionInvalidIDNotification (UUID id) {
		super(id);
	}

}
