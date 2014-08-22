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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.arget.util.ArrayUtils;
import pl.kotcrab.arget.util.FastByteArrayOutputStream;
import pl.kotcrab.arget.util.FileUitls;
import pl.kotcrab.crypto.CryptoUtils;
import pl.kotcrab.crypto.EncryptedData;
import pl.kotcrab.crypto.SymmetricCipher;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

public class ProfileIO {
	private static final String PROFILE_DIRECTORY_PATH = App.APP_FOLDER + "profiles" + File.separator;
	private static final File PROFILE_DIRECTORY = new File(PROFILE_DIRECTORY_PATH);

	private static Kryo kryo;

	public static void init () {
		PROFILE_DIRECTORY.mkdirs();
		kryo = new Kryo();
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.register(ContactInfo.class);
		kryo.register(ServerInfo.class);
		kryo.register(ArrayList.class);
		kryo.register(byte[].class);
		kryo.register(BigInteger.class);
		kryo.register(ProfileWrapper.class);
		kryo.register(Profile.class);
	}

	public static Profile generateEmptyProfile () {
		return new Profile().generateProfile();
	}

	public static boolean isValidProfileName (String name) {
		if (FileUitls.isValidFileName(name) == false) return false;

		File[] files = PROFILE_DIRECTORY.listFiles();
		for (int i = 0; i < files.length; i++)
			if (files[i].getName().equals(name)) return false;

		return true;
	}

	public static String getProfilePathByName (String name) {
		File[] profiles = listProfiles();

		for (int i = 0; i < profiles.length; i++) {
			if (profiles[i].getName().equals(name)) return profiles[i].getAbsolutePath();
		}

		throw new IllegalStateException("Profile: " + name + " not found");
	}

	public static Profile loadProfileByName (String name, char[] password) throws IOException, GeneralSecurityException {
		return loadProfile(getProfilePathByName(name), password);
	}

	public static Profile loadProfile (String path, char[] password) throws IOException, GeneralSecurityException {
		ProfileWrapper wrapper = loadProfileWrapper(path);

		Input input;
		SecretKeySpec key = null;
		if (wrapper.salt != null) {
			if (password == null) throw new IOException("Profile is encrypted and password wasn't provided!");

			key = CryptoUtils.getAESKeyFromPassword(password, wrapper.salt);
			SymmetricCipher cipher = new SymmetricCipher("AES", key);

			byte[] decrypedProfile = cipher.decryptSafe(new EncryptedData(wrapper.data));
			input = new Input(decrypedProfile);
		} else
			input = new Input(wrapper.data);

		Profile profile = kryo.readObject(input, Profile.class);

		if (profile == null) throw new IOException("Deserialization error while loading profile.");

		profile.init(new File(path), key);

		return profile;
	}

	public static void saveProfile (Profile profile) {
		if (profile.file == null)
			throw new IllegalStateException("This profile can't be saved using this method.+"
				+ " Use saveProfile (Profile profile, String name, char[] password) instead");

		try {
			saveProfile(profile, profile.file.getName(), profile.profileKeySpec);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveProfile (Profile profile, String name, SecretKeySpec key) throws IOException {
		String path = PROFILE_DIRECTORY_PATH + name;

		FastByteArrayOutputStream stream = new FastByteArrayOutputStream(4096);
		Output output = new Output(stream);
		kryo.writeObject(output, profile);
		output.close();

		byte[] unencryptedProfile = ArrayUtils.trim(stream.getByteArray(), 10);

		ProfileWrapper wrapper = loadProfileWrapper(profile.file.getAbsolutePath());

		if (key != null) {
			SymmetricCipher cipher = new SymmetricCipher("AES", key);

			wrapper.data = cipher.encrypt(unencryptedProfile).getBytes();
		} else
			wrapper.data = unencryptedProfile;

		output = new Output(new FileOutputStream(new File(path)));
		kryo.writeObject(output, wrapper);
		output.close();
	}

	public static void saveProfile (Profile profile, String name, char[] password) throws IOException {
		String path = PROFILE_DIRECTORY_PATH + name;

		FastByteArrayOutputStream stream = new FastByteArrayOutputStream(4096);
		Output output = new Output(stream);
		kryo.writeObject(output, profile);
		output.close();

		byte[] unencryptedProfile = ArrayUtils.trim(stream.getByteArray(), 10);

		ProfileWrapper wrapper = new ProfileWrapper();

		if (password != null && password.equals("") == false && password.length > 0) {
			wrapper.salt = CryptoUtils.getRandomBytes16();
			SecretKeySpec key = CryptoUtils.getAESKeyFromPassword(password, wrapper.salt);
			SymmetricCipher cipher = new SymmetricCipher("AES", key);

			wrapper.data = cipher.encrypt(unencryptedProfile).getBytes();
		} else
			wrapper.data = unencryptedProfile;

		output = new Output(new FileOutputStream(new File(path)));
		kryo.writeObject(output, wrapper);
		output.close();
	}

	private static ProfileWrapper loadProfileWrapper (String path) {
		try {
			Input input = new Input(new FileInputStream(new File(path)));
			ProfileWrapper wrapper = kryo.readObject(input, ProfileWrapper.class);
			input.close();
			return wrapper;
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new IllegalStateException("Could not load ProfileWrapper for path: " + path);
	}

	public static boolean isProfileEncryptedByName (String name) {
		ProfileWrapper wrapper = loadProfileWrapper(getProfilePathByName(name));
		return (wrapper.salt == null) ? false : true;
	}

	public static File[] listProfiles () {
		return PROFILE_DIRECTORY.listFiles();
	}

}
