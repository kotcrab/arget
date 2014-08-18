
package pl.kotcrab.arget;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.Security;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import pl.kotcrab.arget.profile.ProfileIO;
import pl.kotcrab.arget.util.DesktopUtils;

import com.alee.laf.WebLookAndFeel;

public class App {
	public static final boolean DEBUG = false;

	public static final String APP_NAME = "Arget";

	public static final String APP_VERSION = "1.3-BETA7";

// public static final String APP_DIRECTORY_NAME = "1.3-BETA";
	private static final String APP_DIRECTORY_NAME = "1.3-SNAPSHOT";
	private static final String SYSTEM_APPDATA_LOCATION = System.getProperty("user.home") + File.separator;
	public static final String APP_FOLDER = SYSTEM_APPDATA_LOCATION + ".arget-" + App.APP_DIRECTORY_NAME + File.separator;

	public static String JAR_FOLDER_PATH;
	public static String DOWNLOAD_FOLDER_PATH;

	private static boolean guiAvailable;
	private static boolean appInitialized;

	public static void init () {
		init(true);
	}

	/** Initializes application: sets global UIManager properties, adds Bouncy Castle security provider, creates app data storage
	 * folder, and checks if proper charset is set */
	public static void init (boolean initGui) {
		if (appInitialized == false) {

			Log.init();

			com.esotericsoftware.minlog.Log.NONE();
			// com.esotericsoftware.minlog.Log.DEBUG();

			checkCharset();
			Security.addProvider(new BouncyCastleProvider());

			if (initGui) {
				if (WebLookAndFeel.install() == false) throw new IllegalStateException("Failed to install WebLookAndFell");
			}

			guiAvailable = initGui;

			JAR_FOLDER_PATH = DesktopUtils.getJarPath();
			DOWNLOAD_FOLDER_PATH = JAR_FOLDER_PATH + File.separator + "File Transfer" + File.separator;

			new File(APP_FOLDER).mkdirs();
			new File(DOWNLOAD_FOLDER_PATH).mkdirs();

			Settings.init();
			ProfileIO.init();

		} else
			throw new IllegalStateException("App has been already initialized!");
	}

	/** Checks if proper charset is set, if not tries to change it, if that fails method will throw IllegalStateException */
	private static void checkCharset () {
		if (Charset.defaultCharset().name().equals("UTF-8") == false) {
			System.err.println("UTF-8 is not default charset, trying to change...");

			try {
				System.setProperty("file.encoding", "UTF-8");
				Field charset = Charset.class.getDeclaredField("defaultCharset");
				charset.setAccessible(true);
				charset.set(null, null);
				System.out.println("Success, run with VM argument: -Dfile.encoding=UTF-8 to avoid this.");
			} catch (Exception e) {
				throw new IllegalStateException(
					"Failed! UTF-8 charset is not default for this system and attempt to change it failed, cannot continue! Default is: "
						+ Charset.defaultCharset().name() + ", run with VM argument: -Dfile.encoding=UTF-8 to fix this.");
			}
		}
	}

	public static boolean isGuiAvailable () {
		return guiAvailable;
	}

	public static URL getResource (String path) {
		return App.class.getResource(path);
	}

	public static InputStream getResourceAsStream (String path) {
		return App.class.getResourceAsStream(path);
	}

	public static Image loadImage (String path) {
		try {
			return ImageIO.read(getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ImageIcon loadImageIcon (String path) {
		return new ImageIcon(getResource(path));
	}

}
