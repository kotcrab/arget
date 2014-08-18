
package pl.kotcrab.arget.comm.exchange;

import pl.kotcrab.arget.global.EncryptionMode;

public class EncryptionModeExchange implements Exchange {
	public EncryptionMode mode;

	public EncryptionModeExchange () {
	}

	public EncryptionModeExchange (EncryptionMode mode) {
		this.mode = mode;
	}
}
