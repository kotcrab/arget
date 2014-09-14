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

package pl.kotcrab.arget;

import java.awt.image.BufferedImage;
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

import pl.kotcrab.arget.event.EventBus;
import pl.kotcrab.arget.gui.notification.NotificationService;
import pl.kotcrab.arget.profile.ProfileIO;
import pl.kotcrab.arget.util.DesktopUtils;
import pl.kotcrab.arget.util.SwingUtils;

import com.alee.laf.WebLookAndFeel;

//TODO change version scheme from BETA1 to b1, ALPHA1 to a1
public class App {
	public static final boolean DEBUG = false;
	public static final boolean SNAPSHOT = true;

	private static final String TAG = "App";
	public static final String APP_NAME = "Arget";
	public static final String APP_VERSION = "1.5-b2";

	private static final String SYSTEM_APPDATA_LOCATION = System.getProperty("user.home") + File.separator;
	public static final String APP_FOLDER = SYSTEM_APPDATA_LOCATION + ".arget" + File.separator;

	public static String JAR_FOLDER_PATH;
	public static String DOWNLOAD_FOLDER_PATH;

	private static boolean guiAvailable;
	private static boolean appInitialized;

	public static EventBus eventBus;
	public static NotificationService notificationService;

	public static final int PROFILE_VERSION;
	public static final int SERVER_CONFIG_VERSION;

	static {
		if (SNAPSHOT) {
			PROFILE_VERSION = 0;
			SERVER_CONFIG_VERSION = 0;
		} else {
			PROFILE_VERSION = 1;
			SERVER_CONFIG_VERSION = 1;
		}
	}

	public static void init () {
		init(true);
	}

	/** Initializes application: sets UIManager properties, adds Bouncy Castle security provider, creates app data storage folder,
	 * and checks if proper charset is set */
	public static void init (boolean initGui) {
		if (appInitialized == false) {

			checkCharset();
			Security.addProvider(new BouncyCastleProvider());

			eventBus = new EventBus();
			notificationService = new NotificationService();

			com.esotericsoftware.minlog.Log.NONE();
			// com.esotericsoftware.minlog.Log.DEBUG();

			if (initGui) {
				if (SwingUtils.isPerpixelTransparencySupported() == false) {
					System.err.println("Per-pixel transparency not supported");
					System.exit(-1);
				}

				if (WebLookAndFeel.install() == false) throw new IllegalStateException("Failed to install WebLookAndFell");
			}

			guiAvailable = initGui;

			JAR_FOLDER_PATH = DesktopUtils.getJarPath();
			DOWNLOAD_FOLDER_PATH = JAR_FOLDER_PATH + File.separator + "File Transfer" + File.separator;

			new File(APP_FOLDER).mkdirs();
			new File(DOWNLOAD_FOLDER_PATH).mkdirs();

			Settings.init();
			ProfileIO.init();

			appInitialized = true;
		} else
			throw new IllegalStateException("App has been already initialized!");
	}

	/** Checks if proper charset is set, if not tries to change it, if that fails method will throw IllegalStateException */
	private static void checkCharset () {
		if (Charset.defaultCharset().name().equals("UTF-8") == false) {
			Log.err(TAG, "UTF-8 is not default charset, trying to change...");

			try {
				System.setProperty("file.encoding", "UTF-8");
				Field charset = Charset.class.getDeclaredField("defaultCharset");
				charset.setAccessible(true);
				charset.set(null, null);
				Log.l(TAG, "Success, run with VM argument: -Dfile.encoding=UTF-8 to avoid this.");
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

	public static boolean isAppInitialized () {
		return appInitialized;
	}

	public static URL getResource (String path) {
		return App.class.getResource(path);
	}

	public static InputStream getResourceAsStream (String path) {
		return App.class.getResourceAsStream(path);
	}

	public static BufferedImage loadImage (String path) {
		try {
			return ImageIO.read(getResource(path));
		} catch (IOException e) {
			Log.exception(e);
		}

		return null;
	}

	public static ImageIcon loadImageIcon (String path) {
		return new ImageIcon(getResource(path));
	}

}
