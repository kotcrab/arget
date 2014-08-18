
package pl.kotcrab.arget.util;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

public class DesktopUtils {
	private static String OS = System.getProperty("os.name").toLowerCase();

	public static boolean openWebsite (URL url) {
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(url.toURI());

			return true;
		} catch (URISyntaxException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getJarPath () {
		try {
			URL url = DesktopUtils.class.getProtectionDomain().getCodeSource().getLocation();
			String path = URLDecoder.decode(url.getFile(), "UTF-8");
			return path.substring(0, path.lastIndexOf('/')); // remove jar name from path
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean isWindows () {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac () {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix () {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}
}
