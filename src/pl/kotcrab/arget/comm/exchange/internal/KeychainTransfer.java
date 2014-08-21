
package pl.kotcrab.arget.comm.exchange.internal;

import java.util.ArrayList;

/** Contains all currently connected public profile keys. This is valid response for {@link KeychainRequest}
 * @author Pawel Pastuszak */
public class KeychainTransfer implements InternalExchange {
	public ArrayList<String> publicKeys;

	@Deprecated
	public KeychainTransfer () {
	}

	public KeychainTransfer (ArrayList<String> keys) {
		this.publicKeys = keys;
	}
}
