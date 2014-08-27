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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;

import pl.kotcrab.arget.Log;

public class FileUitls {
	private static final String[] units = new String[] {"B", "KB", "MB", "GB", "TB", "EB"};

	public static String readableFileSize (long size) {
		if (size <= 0) return "0";
		int digitGroups = (int)(Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)).replace(",", ".") + " " + units[digitGroups];
	}

	public static boolean isImage (File file) {
		try {
			String mimeType = Files.probeContentType(file.toPath());

			if (mimeType == null) return false;

			if (mimeType.equals("image/jpeg") || mimeType.equals("image/png") || mimeType.equals("image/gif"))
				return true;
			else
				return false;

		} catch (IOException e) {
			Log.exception(e);
			return false;
		}
	}

	public static boolean isValidFileName (String name) {
		try {
			if (DesktopUtils.isWindows()) if (name.contains(">") || name.contains("<")) return false;
			return new File(name).getCanonicalFile().getName().equals(name);
		} catch (IOException e) {
			return false;
		}
	}
}
