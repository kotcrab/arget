
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Send by server when it received update from illegal remote. Client that send update does not belong to session associated with
 * this update.
 * @author Pawel Pastuszak */
public class SessionInvalidReciever extends SessionExchange {

	@Deprecated
	public SessionInvalidReciever () {
		super(null);
	}

	public SessionInvalidReciever (UUID id) {
		super(id);
	}

}
