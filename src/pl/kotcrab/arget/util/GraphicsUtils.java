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

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class GraphicsUtils {
	public static boolean isRectangleDisplayableOnScreen (Rectangle rect) {
		GraphicsDevice[] gd = getScreenDevices();

		for (int i = 0; i < gd.length; i++) {
			Rectangle screen = gd[i].getDefaultConfiguration().getBounds();
			if (screen.contains(rect)) return true;
		}

		return false;
	}

	public static Rectangle getPrimaryMonitorBounds () {
		// because gd[0] is not always a primary monitor we must search for it

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // this is definitely primary monitor bounds

		GraphicsDevice[] gd = getScreenDevices();

		for (int i = 0; i < gd.length; i++) {
			Rectangle bounds = gd[i].getDefaultConfiguration().getBounds();

			if (bounds.width == screenSize.width && bounds.height == screenSize.height) return bounds;
		}

		// Ultimate fallback, if we could not match screenSize to any of the screens, return bounds of gd[0]
		// which may or may not be primary monitor bounds
		if (gd.length > 0) return gd[0].getDefaultConfiguration().getBounds();

		return null;
	}

	public static GraphicsDevice[] getScreenDevices () {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}

	public static boolean isPerpixelTransparencySupported () {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
	}
}
