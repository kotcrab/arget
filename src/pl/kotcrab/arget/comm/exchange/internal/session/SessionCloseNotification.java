
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionCloseNotification extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionCloseNotification () {
		super(null);
	}

	public SessionCloseNotification (UUID id) {
		super(id);
	}
}
