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

package pl.kotcrab.arget.util.iconflasher;

import javax.swing.JFrame;

import pl.kotcrab.arget.util.DesktopUtils;

public abstract class IconFlasher {
	private static IconFlasher SHARED_INSTANCE;

	protected JFrame frameWindow;

	public IconFlasher (JFrame frameWindow) {
		this.frameWindow = frameWindow;
	}

	public abstract void flashIcon ();

	public static IconFlasher getIconFlasher (JFrame frameWindow) {
		if (SHARED_INSTANCE == null) setSharedInstance(frameWindow);

		return SHARED_INSTANCE;
	}

	private static void setSharedInstance (JFrame frameWindow) {
		if (DesktopUtils.isWindows()) SHARED_INSTANCE = new WindowsIconFlasher(frameWindow);
		if (DesktopUtils.isLinuxX11()) SHARED_INSTANCE = new X11IconFlasher(frameWindow);

		if (SHARED_INSTANCE == null) SHARED_INSTANCE = new DefaultIconFlasher(frameWindow);
	}
}
