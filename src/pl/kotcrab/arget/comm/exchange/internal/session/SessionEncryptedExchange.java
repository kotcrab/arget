
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.crypto.EncryptedData;

public class SessionEncryptedExchange extends SessionExchange {

	public EncryptedData data;

	@Deprecated
	public SessionEncryptedExchange () {
		super(null);
	}

	public SessionEncryptedExchange (UUID id, EncryptedData data) {
		super(id);
		this.data = data;
	}
}
