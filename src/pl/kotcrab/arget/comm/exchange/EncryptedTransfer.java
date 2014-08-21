
package pl.kotcrab.arget.comm.exchange;

import pl.kotcrab.arget.comm.exchange.internal.InternalExchange;
import pl.kotcrab.crypto.EncryptedData;

/** Serialized and encrypted {@link InternalExchange} that will be send over TCP connection.
 * @author Pawel Pastuszak */
public class EncryptedTransfer implements Exchange {
	public EncryptedData data;

	@Deprecated
	public EncryptedTransfer () {
	}

	public EncryptedTransfer (EncryptedData data) {
		this.data = data;
	}
}
