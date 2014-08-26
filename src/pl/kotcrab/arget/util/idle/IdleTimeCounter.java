
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
		if (DesktopUtils.isUnix()) return new X11LinuxIdleTimeCounter();
		
		if (DesktopUtils.isMac()) {
			Log.w(TAG, "Using untested MacOSX idle time counter");
			return new MacOSXIdleTimeCounter();
		}

		Log.w(TAG, "Idle time counter for this platform not found, all idle time related features are disabled");
		return new DefaultIdleTimeCounter();
	}
}
