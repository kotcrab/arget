
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

public class SessionCipherInitDataExchange extends SessionExchange {
	public String initData;

	@Deprecated
	public SessionCipherInitDataExchange () {
		super(null);
	}

	public SessionCipherInitDataExchange (UUID id, String initData) {
		super(id);
		this.initData = initData;
	}
}
