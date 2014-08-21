
package pl.kotcrab.arget.comm.exchange;

import pl.kotcrab.arget.global.EncryptionMode;

/** Tells client what {@link EncryptionMode} is used on server
 * @author Pawel Pastuszak */
public class EncryptionModeTransfer implements Exchange {
	public EncryptionMode mode;

	@Deprecated
	public EncryptionModeTransfer () {
	}

	public EncryptionModeTransfer (EncryptionMode mode) {
		this.mode = mode;
	}
}
