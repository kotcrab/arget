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

package pl.kotcrab.arget.server.session;

import java.security.PrivateKey;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import pl.kotcrab.arget.Log;
import pl.kotcrab.crypto.CascadeCipher;
import pl.kotcrab.crypto.CryptoUtils;
import pl.kotcrab.crypto.EncryptedData;
import pl.kotcrab.crypto.RSACipher;
import pl.kotcrab.crypto.RSAEncrypter;
import pl.kotcrab.crypto.RSASignature;

//TODO stop using cipherinitline, not required when we have kryonet
public class LocalSession {
	private static final String KEY_ELEMENT_DELIMITER = "#";

	private CascadeCipher cipher;

	public String encryptedSymtericKey64;
	public String keySignature64;
	public String cipherInitLine;

	public UUID id;
	public String remoteProfileKey;

	public boolean cipherReady;
	public boolean sessionReady;

	public LocalSession (UUID id, String key) {
		this.id = id;
		this.remoteProfileKey = key;
	}

	public void initCipher (PrivateKey profilePrivateKey) {
		cipher = new CascadeCipher();
		cipher.initGenerateKeys();

		RSAEncrypter encrypter = new RSAEncrypter(remoteProfileKey);

		byte[] aes = encrypter.encrypt(cipher.getKey1());
		byte[] twofish = encrypter.encrypt(cipher.getKey2().getBytes());
		byte[] serpent = encrypter.encrypt(cipher.getKey3().getBytes());

		String aes64 = Base64.encodeBase64String(aes);
		String twofish64 = Base64.encodeBase64String(twofish);
		String serpent64 = Base64.encodeBase64String(serpent);

		encryptedSymtericKey64 = aes64 + KEY_ELEMENT_DELIMITER + twofish64 + KEY_ELEMENT_DELIMITER + serpent64;

		byte[] signature = RSASignature.sign(encryptedSymtericKey64.getBytes(), profilePrivateKey);

		keySignature64 = Base64.encodeBase64String(signature);

		cipherInitLine = keySignature64 + KEY_ELEMENT_DELIMITER + encryptedSymtericKey64;
		cipherReady = true;
	}

	public boolean initCipherWithKeys (PrivateKey profilePrivateKey, String ciperInitLine) {
		cipher = new CascadeCipher();

		String[] splited = ciperInitLine.split(KEY_ELEMENT_DELIMITER, 2);

		keySignature64 = splited[0];
		encryptedSymtericKey64 = splited[1];

		if (RSASignature.verifySignarture(encryptedSymtericKey64.getBytes(), Base64.decodeBase64(keySignature64),
			CryptoUtils.getRSAPublicKeyFromString64(remoteProfileKey))) {

			String[] keys = encryptedSymtericKey64.split(KEY_ELEMENT_DELIMITER);

			byte[] aes = RSACipher.decrypt(profilePrivateKey, Base64.decodeBase64(keys[0]));
			EncryptedData twofish = new EncryptedData(RSACipher.decrypt(profilePrivateKey, Base64.decodeBase64(keys[1])));
			EncryptedData serpent = new EncryptedData(RSACipher.decrypt(profilePrivateKey, Base64.decodeBase64(keys[2])));

			cipher.initWithKeys(aes, twofish, serpent);

			cipherReady = cipher.isReady();
			return cipherReady;
		} else
			return false;
	}

	public EncryptedData encrypt (byte[] data) {
		if (cipherReady)
			return cipher.encrypt(data);
		else
			Log.err("Session", "Tried to decrypt data while session cipher not ready!");

		return null;
	}

	public byte[] decrypt (EncryptedData data) {
		if (cipherReady)
			return cipher.decrypt(data);
		else
			Log.err("Session", "Tried to decrypt data while session cipher not ready!");

		return null;
	}

}
