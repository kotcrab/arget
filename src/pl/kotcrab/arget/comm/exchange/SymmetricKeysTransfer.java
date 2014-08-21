
package pl.kotcrab.arget.comm.exchange;

/** Transfer of cipher symmetric keys, must by encrypted using public RSA key got from server.
 * @author Pawel Pastuszak */
public class SymmetricKeysTransfer implements Exchange {
	public byte[] key1;
	public byte[] key2;
	public byte[] key3;

	@Deprecated
	public SymmetricKeysTransfer () {
	}

	public SymmetricKeysTransfer (byte[] key1) {
		this.key1 = key1;
	}

	public SymmetricKeysTransfer (byte[] key1, byte[] key2, byte[] key3) {
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
	}
}
