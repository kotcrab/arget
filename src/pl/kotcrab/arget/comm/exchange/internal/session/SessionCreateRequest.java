
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionCreateRequest extends SessionExchange {
	public String targetKey;

	@Deprecated
	public SessionCreateRequest () {
		super(null);
	}

	public SessionCreateRequest (UUID id, String targetKey) {
		super(id);
		this.targetKey = targetKey;
	}
}
