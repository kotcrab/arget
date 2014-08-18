
package pl.kotcrab.arget.comm.exchange;

import pl.kotcrab.crypto.EncryptedData;

/** Serialized InternalExchange that will be send over TCP connection.
 * @author Pawel Pastuszak */
public class EncryptedExchange implements Exchange {
	public EncryptedData data;

	public EncryptedExchange () {
	}

	public EncryptedExchange (EncryptedData data) {
		this.data = data;
	}
}
