
package pl.kotcrab.arget.comm.exchange.internal;

public class ProfilePublicKeyVerificationResponse implements InternalExchange {
	public String decryptedTestData;

	@Deprecated
	public ProfilePublicKeyVerificationResponse () {
	}

	public ProfilePublicKeyVerificationResponse (byte[] decryptedTestData) {
		this.decryptedTestData = new String(decryptedTestData);
	}
}
