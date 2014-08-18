
package pl.kotcrab.arget.comm.exchange.internal;

import java.util.ArrayList;

public class KeychainExchange implements InternalExchange {
	public ArrayList<String> publicKeys;

	@Deprecated
	public KeychainExchange () {
	}

	public KeychainExchange (ArrayList<String> keys) {
		this.publicKeys = keys;
	}
}
