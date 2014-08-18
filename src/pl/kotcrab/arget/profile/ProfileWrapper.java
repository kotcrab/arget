
package pl.kotcrab.arget.profile;

public class ProfileWrapper {
	public byte[] data; // profile serialized (and encrypted if salt != null) data
	public byte[] salt;

	public boolean isEncrypted () {
		return salt != null;
	}
}
