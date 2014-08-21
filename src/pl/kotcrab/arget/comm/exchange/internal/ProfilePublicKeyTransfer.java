
package pl.kotcrab.arget.comm.exchange.internal;

import org.apache.commons.codec.binary.Base64;

/** Transfer of a public profile key.
 * @author Pawel Pastuszak */
public class ProfilePublicKeyTransfer implements InternalExchange {
	public String key;

	@Deprecated
	public ProfilePublicKeyTransfer () {

	}

	public ProfilePublicKeyTransfer (byte[] key) {
		this.key = Base64.encodeBase64String(key);
	}
}
