
package pl.kotcrab.arget.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class SwingUtils {
	public static boolean isRectangleDisplayableOnScreen (Rectangle rect) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();

		for (int i = 0; i < gd.length; i++) {
			Rectangle screen = gd[i].getDefaultConfiguration().getBounds();
			if (screen.contains(rect)) return true;
		}

		return false;
	}

}
