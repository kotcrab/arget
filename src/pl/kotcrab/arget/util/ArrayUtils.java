
package pl.kotcrab.arget.util;

import java.util.Arrays;

public class ArrayUtils {
	public static byte[] trim (byte[] bytes, int endMargin) {
		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}

		return Arrays.copyOf(bytes, i + 1 + endMargin);
	}
}
