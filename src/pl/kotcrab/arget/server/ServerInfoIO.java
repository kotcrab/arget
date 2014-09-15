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

package pl.kotcrab.arget.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

//TODO don't have to be static class, change to instance maybe?
//TODO rename to ServerConfig
public class ServerInfoIO {
	private static final int SERVER_CONFIG_VERSION = App.SERVER_CONFIG_VERSION;
	private static final String TAG = "ServerConfigIO";

	private static Kryo kryo;

	private static final String SERVERS_DIRECTORY_PATH = App.APP_FOLDER + "servers" + SERVER_CONFIG_VERSION + File.separator;
	private static final File SERVERS_DIRECTORY = new File(SERVERS_DIRECTORY_PATH);

	public static void init () {
		SERVERS_DIRECTORY.mkdirs();
		kryo = new Kryo();
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.register(ServerInfo.class);
	}

	public static void saveInfo (File file, ServerInfo info) {
		try {
			if (file.exists() == false) file.createNewFile();
			Output output = new Output(new FileOutputStream(file));
			kryo.writeObject(output, info);
			output.close();
			// Log.l(TAG, "Configuration saved");
		} catch (IOException e) {
			Log.err(TAG, "IO Error while saving configuration, your changes will be lost if you shutdown server.");
			Log.exception(e);
		}
	}

	public static ServerInfo loadInfo (File file) {
		if (file.exists() == false) return new ServerInfo();

		try {

			Input input = new Input(new FileInputStream(file));
			ServerInfo info = kryo.readObject(input, ServerInfo.class);
			input.close();
			return info;

		} catch (IOException e) {
			Log.err(TAG, "IO Error while loading configuration from file: " + file + ". Cannot continue.");
			Log.exception(e);
			System.exit(-1);
		}

		System.exit(-1);
		return null;
	}

	public static ServerInfo loadInfoByName (String infoFileName) {
		return loadInfo(getFileFromName(infoFileName));
	}
	
	public static File getFileFromName(String infoFileName){
		return new File(SERVERS_DIRECTORY_PATH + infoFileName);
	}
}
