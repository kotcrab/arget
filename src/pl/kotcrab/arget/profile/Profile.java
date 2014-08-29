/*******************************************************************************
    Copyright 2014 Pawel Pastuszak
 
    This file is part of Arget.

    Arget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Arget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Arget.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.arget.profile;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ServerDescriptor;
import pl.kotcrab.crypto.RSACipher;
import pl.kotcrab.crypto.RSAKeySet;

public class Profile {
	private RSAKeySet keyset;
	public transient RSACipher rsa;

	public transient String fileName;
	public transient File file;
	public transient SecretKeySpec profileKeySpec;

	public ArrayList<ContactInfo> contacts;
	public ArrayList<ServerDescriptor> servers;

	public ProfileOptions options;

	public ServerDescriptor autoconnectInfo;
	public Rectangle mainWindowBounds;

	public Profile () { // no-arg constructor for kryo
		// for backward compatibility
		if (options == null) options = new ProfileOptions();
	}

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
		servers = new ArrayList<ServerDescriptor>();
		contacts = new ArrayList<ContactInfo>();

		keyset = new RSAKeySet(rsa.getPublicKeySpec(), rsa.getPrivateKeySpec());

		options = new ProfileOptions();

		autoconnectInfo = null;
		mainWindowBounds = null;

		return this;
	}
}
