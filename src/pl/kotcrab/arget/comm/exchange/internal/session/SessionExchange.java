
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.InternalExchange;

/** Class that extends this abstract class will be send to session and requires providing session id
 * @author Pawel Pastuszak */
public abstract class SessionExchange implements InternalExchange {
	public UUID id;

	public SessionExchange (UUID id) {
		this.id = id;
	}
}
