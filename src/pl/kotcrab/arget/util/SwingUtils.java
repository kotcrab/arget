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

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
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

	public static boolean isPerpixelTransparencySupported () {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
	}
}
