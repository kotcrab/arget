
package pl.kotcrab.arget.profile;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.crypto.RSACipher;
import pl.kotcrab.crypto.RSAKeySet;

public class Profile {
	private RSAKeySet keyset;
	public transient RSACipher rsa;

	public transient String fileName;
	public transient File file;
	public transient SecretKeySpec profileKeySpec;

	public ArrayList<ContactInfo> contacts;
	public ArrayList<ServerInfo> servers;

	// OPTIONS
	public boolean playSound;
	public ServerInfo autoconnectInfo;
	public Rectangle mainWindowBounds;

	public void init (File file, SecretKeySpec keySpec) {
		if (rsa == null) {
			rsa = new RSACipher(keyset);
			this.file = file;
			this.fileName = file.getName();
			this.profileKeySpec = keySpec;
		}
	}

	/** Generates profile, should only be called from {@link ProfileIO}.
	 * @return itself */
	Profile generateProfile () {
		RSACipher rsa = new RSACipher();
		servers = new ArrayList<ServerInfo>();
		contacts = new ArrayList<ContactInfo>();

		keyset = new RSAKeySet(rsa.getPublicKeySpec(), rsa.getPrivateKeySpec());

		playSound = true;
		autoconnectInfo = null;
		mainWindowBounds = null;

		return this;
	}
}
