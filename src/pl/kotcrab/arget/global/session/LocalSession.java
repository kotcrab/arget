
package pl.kotcrab.arget.global.session;

import java.security.PrivateKey;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

import pl.kotcrab.crypto.CascadeCipher;
import pl.kotcrab.crypto.CryptoUtils;
import pl.kotcrab.crypto.EncryptedData;
import pl.kotcrab.crypto.RSACipher;
import pl.kotcrab.crypto.RSAEncrypter;
import pl.kotcrab.crypto.RSASignature;

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

	@Deprecated
	public synchronized String decryptS (EncryptedData data) {
		return new String(cipher.decrypt(data));
	}

	@Deprecated
	public synchronized String decrypt (String dataEncrypted) {
		return new String(cipher.decrypt(new EncryptedData(dataEncrypted)));
	}

	@Deprecated
	public synchronized EncryptedData encrypt (String text) {
		return cipher.encrypt(text.getBytes());
	}
	
	public EncryptedData encrypt (byte[] data) {
		return cipher.encrypt(data);
	}

	public byte[] decrypt (EncryptedData data) {
		return cipher.decrypt(data);
	}

}
