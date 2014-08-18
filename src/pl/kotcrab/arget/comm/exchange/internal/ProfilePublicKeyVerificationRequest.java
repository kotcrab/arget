
package pl.kotcrab.arget.comm.exchange.internal;

public class ProfilePublicKeyVerificationRequest implements InternalExchange {
	public byte[] encryptedTestData;

	@Deprecated
	public ProfilePublicKeyVerificationRequest () {
	}

	public ProfilePublicKeyVerificationRequest (byte[] encryptedTestData) {
		this.encryptedTestData = encryptedTestData;
	}
}
