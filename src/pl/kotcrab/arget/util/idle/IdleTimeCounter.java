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

package pl.kotcrab.arget.util.idle;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.util.DesktopUtils;

public abstract class IdleTimeCounter {
	private static final String TAG = "IdleTimeCounter";

	/** Get the System Idle Time from the OS.
	 * 
	 * @return The System Idle Time in milliseconds. */
	public abstract long getSystemIdleTime ();

	public static IdleTimeCounter getIdleTimeCounter () {
		if (DesktopUtils.isWindows()) return new WindowsIdleTimeCounter();
		
		//TODO is X11 available
		if (DesktopUtils.isUnix()) return new X11LinuxIdleTimeCounter();

		if (DesktopUtils.isMac()) {
			Log.w(TAG, "Using untested MacOSX idle time counter");
			return new MacOSXIdleTimeCounter();
		}

		Log.w(TAG, "Idle time counter for this platform not found, all idle time related features are disabled");
		return new DefaultIdleTimeCounter();
	}
}
