
package pl.kotcrab.arget.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class SwingUtils {
	public static boolean isRectangleDisplayableOnScreen (Rectangle rect) {
		GraphicsDevice[] gd = getScreenDevices();

		for (int i = 0; i < gd.length; i++) {
			Rectangle screen = gd[i].getDefaultConfiguration().getBounds();
			if (screen.contains(rect)) return true;
		}

		return false;
	}

	public static Rectangle getPrimaryMonitorBounds () {
		GraphicsDevice[] gd = getScreenDevices();

		if (gd.length > 0) return gd[0].getDefaultConfiguration().getBounds();

		return null;
	}

	public static GraphicsDevice[] getScreenDevices () {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}
}
