
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.crypto.EncryptedData;

@Deprecated
public class SessionData extends SessionExchange {

	public EncryptedData data;

	@Deprecated
	public SessionData () {
		super(null);
	}

	public SessionData (UUID id, EncryptedData data) {
		super(id);
		this.data = data;
	}
}
