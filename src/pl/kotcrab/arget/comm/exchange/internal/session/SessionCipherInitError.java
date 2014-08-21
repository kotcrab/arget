
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Notifies requester that cipher could not be initialized.
 * @author Pawel Pastuszak */
public class SessionCipherInitError extends SessionExchange implements SessionUnrecoverableBroken {

	@Deprecated
	public SessionCipherInitError () {
		super(null);
	}

	public SessionCipherInitError (UUID id) {
		super(id);
	}
}
