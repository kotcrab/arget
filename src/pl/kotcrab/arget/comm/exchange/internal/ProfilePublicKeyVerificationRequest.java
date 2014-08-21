
package pl.kotcrab.arget.comm.exchange.internal;

/** Message that is send to verify that client have public and private profile keys valid.
 * @author Pawel Pastuszak */
public class ProfilePublicKeyVerificationRequest implements InternalExchange {
	public byte[] encryptedTestData;

	@Deprecated
	public ProfilePublicKeyVerificationRequest () {
	}

	public ProfilePublicKeyVerificationRequest (byte[] encryptedTestData) {
		this.encryptedTestData = encryptedTestData;
	}
}
