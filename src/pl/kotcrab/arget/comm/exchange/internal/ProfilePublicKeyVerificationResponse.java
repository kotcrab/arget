
package pl.kotcrab.arget.comm.exchange.internal;

/** Response for a test message that is send to verify that client have both public and private profile keys valid.
 * @author Pawel Pastuszak */
public class ProfilePublicKeyVerificationResponse implements InternalExchange {
	public String decryptedTestData;

	@Deprecated
	public ProfilePublicKeyVerificationResponse () {
	}

	public ProfilePublicKeyVerificationResponse (byte[] decryptedTestData) {
		this.decryptedTestData = new String(decryptedTestData);
	}
}
