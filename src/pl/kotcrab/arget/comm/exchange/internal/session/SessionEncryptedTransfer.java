
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.session.data.InternalSessionExchange;
import pl.kotcrab.crypto.EncryptedData;

/** Seriazlied and encrypted {@link InternalSessionExchange}, see it for more info.
 * @author Pawel Pastuszak */
public class SessionEncryptedTransfer extends SessionExchange {

	public EncryptedData data;

	@Deprecated
	public SessionEncryptedTransfer () {
		super(null);
	}

	public SessionEncryptedTransfer (UUID id, EncryptedData data) {
		super(id);
		this.data = data;
	}
}
