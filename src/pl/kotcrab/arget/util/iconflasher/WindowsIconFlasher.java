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

import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/** Icon flasher for Windows system, minimum required client is Windows XP
 * @author Pawel Pastuszak */
public class WindowsIconFlasher extends IconFlasher {
	private User32.FLASHWINFO info;

	public WindowsIconFlasher (JFrame frameWindow) {
		super(frameWindow);

		HWND hwnd = new HWND();
		hwnd.setPointer(Native.getComponentPointer(frameWindow));

		info = new User32.FLASHWINFO();
		info.dwFlags = User32.FLASHW_ALL | User32.FLASHW_TIMERNOFG; // flash continuously until the window comes to the foreground.
		info.hWnd = hwnd;
		info.cbSize = info.size();
	}

	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class);

		// http://msdn.microsoft.com/en-us/library/ms679348%28v=VS.85%29.aspx
		int FLASHW_ALL = 3;
		int FLASHW_CAPTION = 1;
		int FLASHW_STOP = 0;
		int FLASHW_TIMER = 4;
		int FLASHW_TIMERNOFG = 14;
		int FLASHW_TRAY = 2;

		boolean FlashWindowEx (FLASHWINFO info);

		// http://msdn.microsoft.com/en-us/library/ms679348%28v=VS.85%29.aspx
		public class FLASHWINFO extends Structure {
			public int cbSize;
			public HANDLE hWnd;
			public int dwFlags;
			public int uCount;
			public int dwTimeout;

			@Override
			protected List<String> getFieldOrder () {
				return Arrays.asList(new String[] {"cbSize", "hWnd", "dwFlags", "uCount", "dwTimeout"});
			}
		}
	}

	@Override
	public void flashIcon () {
		User32.INSTANCE.FlashWindowEx(info);
	}

}
