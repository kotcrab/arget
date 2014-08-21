
package pl.kotcrab.arget.comm.exchange;

import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyTransfer;

/** Transfer of a public RSA key, should by used only when server is sending it's public RSA key. For profile public key transfer
 * {@link ProfilePublicKeyTransfer} should be used.
 * @author Pawel Pastuszak */
public class RSAPublicKeyTransfer implements Exchange {
	public byte[] key;

	@Deprecated
	public RSAPublicKeyTransfer () {
	}

	public RSAPublicKeyTransfer (byte[] key) {
		this.key = key;
	}
}
