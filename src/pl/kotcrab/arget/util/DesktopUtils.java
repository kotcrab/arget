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

package pl.kotcrab.arget.util;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import pl.kotcrab.arget.Log;

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
			Log.exception(e);
			return false;
		}
	}

	public static String getJarPath () {
		try {
			URL url = DesktopUtils.class.getProtectionDomain().getCodeSource().getLocation();
			String path = URLDecoder.decode(url.getFile(), "UTF-8");
			return path.substring(0, path.lastIndexOf('/')); // remove jar name from path
		} catch (UnsupportedEncodingException e) {
			Log.exception(e);
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
