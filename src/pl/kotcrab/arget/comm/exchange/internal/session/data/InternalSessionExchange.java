
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;

public abstract class InternalSessionExchange extends SessionExchange {

	public InternalSessionExchange (UUID id) {
		super(id);
	}

}
