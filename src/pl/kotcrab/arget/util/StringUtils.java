
package pl.kotcrab.arget.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import pl.kotcrab.arget.Log;

public class StringUtils {
	
	public static String toString (InputStream stream) {
		try {
			return IOUtils.toString(stream);
		} catch (IOException e) {
			Log.exception(e);
		}

		return "";
	}
	
}
