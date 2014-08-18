
package pl.kotcrab.arget.comm.exchange;

public class RSAPublicKeyExchange implements Exchange {
	public byte[] key;

	public RSAPublicKeyExchange () {
	}

	public RSAPublicKeyExchange (byte[] key) {
		this.key = key;
	}
}
