
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.InternalExchange;

public abstract class SessionExchange implements InternalExchange {
	public UUID id;

	public SessionExchange (UUID id) {
		this.id = id;
	}
}
