
package pl.kotcrab.arget.global;

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
public class GlobalServerInfoIO {
	private static final String TAG = "ServerConfigIO";
	private static Kryo kryo;

	private static final String SERVERS_DIRECTORY_PATH = App.APP_FOLDER + "server" + File.separator;
	private static final File SERVERS_DIRECTORY = new File(SERVERS_DIRECTORY_PATH);

	public static File getServersInfoDirectory () {
		return SERVERS_DIRECTORY;
	}

	public static void init () {
		SERVERS_DIRECTORY.mkdirs();
		kryo = new Kryo();
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.register(GlobalServerInfo.class);
	}

	public static void saveInfo (File file, GlobalServerInfo info) {
		try {
			if (file.exists() == false) file.createNewFile();
			Output output = new Output(new FileOutputStream(file));
			kryo.writeObject(output, info);
			output.close();
			// Log.l(TAG, "Configuration saved");
		} catch (IOException e) {
			Log.err(TAG, "IO Error while saving configuration, your changes will be lost if you shutdown server.");
			e.printStackTrace();
		}
	}

	public static GlobalServerInfo loadInfo (File file) {
		if (file.exists() == false) return new GlobalServerInfo();

		try {

			Input input = new Input(new FileInputStream(file));
			GlobalServerInfo info = kryo.readObject(input, GlobalServerInfo.class);
			input.close();
			return info;

		} catch (IOException e) {
			Log.err(TAG, "IO Error while loading configuration from file: " + file + ". Cannot continue.");
			e.printStackTrace();
			System.exit(-1);
		}

		System.exit(-1);
		return null;
	}
}
