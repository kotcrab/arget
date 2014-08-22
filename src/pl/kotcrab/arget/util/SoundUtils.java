
package pl.kotcrab.arget.util;

import java.io.IOException;
import java.io.InputStream;

import pl.kotcrab.arget.App;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class SoundUtils {
	public static void playSound (String classpath) {
		try {
			InputStream inputStream = App.getResourceAsStream(classpath);
			AudioStream audioStream = new AudioStream(inputStream);
			AudioPlayer.player.start(audioStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
