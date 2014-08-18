
package pl.kotcrab.arget.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;

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
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isValidFileName (String file) {
		File f = new File(file);
		try {
			return f.getCanonicalFile().getName().equals(file);
		} catch (IOException e) {
			return false;
		}
	}
}
