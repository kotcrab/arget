
package pl.kotcrab.arget.comm.exchange;

public class SymmetricKeysExchange implements Exchange {
	public byte[] key1;
	public byte[] key2;
	public byte[] key3;

	public SymmetricKeysExchange () {
	}

	public SymmetricKeysExchange (byte[] key1) {
		this.key1 = key1;
	}

	public SymmetricKeysExchange (byte[] key1, byte[] key2, byte[] key3) {
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
	}
}
