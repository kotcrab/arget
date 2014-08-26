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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.Drawable;
import com.sun.jna.platform.unix.X11.Window;

/** Instances of this class provide the computer idle time on a Linux system with X11.
 * 
 * @author Laurent Cohen */
public class X11LinuxIdleTimeCounter extends IdleTimeCounter {
	/** Structure providing info on the XScreensaver. */
	public class XScreenSaverInfo extends Structure {
		/** screen saver window */
		public Window window;
		/** ScreenSaver{Off,On,Disabled} */
		public int state;
		/** ScreenSaver{Blanked,Internal,External} */
		public int kind;
		/** milliseconds */
		public NativeLong til_or_since;
		/** milliseconds */
		public NativeLong idle;
		/** events */
		public NativeLong event_mask;

		@Override
		protected List<String> getFieldOrder () {
			return Arrays.asList(new String[] {"window", "state", "kind", "til_or_since", "idle", "event_mask"});

		}
	}

	/** Definition (incomplete) of the Xext library. */
	public interface Xss extends Library {
		/** Instance of the Xext library bindings. */
		Xss INSTANCE = (Xss)Native.loadLibrary("Xss", Xss.class);

		/** Allocate a XScreensaver information structure.
		 * 
		 * @return a {@link XScreenSaverInfo} instance. */
		XScreenSaverInfo XScreenSaverAllocInfo ();

		/** Query the XScreensaver.
		 * 
		 * @param display the display.
		 * @param drawable a {@link Drawable} structure.
		 * @param saver_info a previously allocated {@link XScreenSaverInfo} instance.
		 * @return an int return code. */
		int XScreenSaverQueryInfo (Display display, Drawable drawable, XScreenSaverInfo saver_info);
	}

	@Override
	public long getSystemIdleTime () {
		X11.Window window = null;
		XScreenSaverInfo info = null;
		Display display = null;

		long idleMillis = 0L;
		try {
			display = X11.INSTANCE.XOpenDisplay(null);
			window = X11.INSTANCE.XDefaultRootWindow(display);
			info = new XScreenSaverInfo();
			Xss.INSTANCE.XScreenSaverQueryInfo(display, window, info);
			idleMillis = info.idle.longValue();
		} finally {
			info = null;

			if (display != null) X11.INSTANCE.XCloseDisplay(display);
			display = null;
		}
		return idleMillis;
	}
}
