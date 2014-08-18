
package pl.kotcrab.arget.comm.exchange.internal;

import org.apache.commons.codec.binary.Base64;

public class ProfilePublicKeyExchange implements InternalExchange {
	public String key;

	@Deprecated
	public ProfilePublicKeyExchange () {

	}

	public ProfilePublicKeyExchange (byte[] key) {
		this.key = Base64.encodeBase64String(key);
	}
}
