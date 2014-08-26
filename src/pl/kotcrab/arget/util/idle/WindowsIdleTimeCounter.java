
package pl.kotcrab.arget.util.idle;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public class WindowsIdleTimeCounter extends IdleTimeCounter {

	// kernel mapping
	public interface Kernel32 extends StdCallLibrary {
		Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class);

		// returns milliseconds since system was started
		public long GetTickCount ();
	}

	// user mapping
	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class);

		// contains time of last input
		public static class LASTINPUTINFO extends Structure {
			public int cbSize = 8;

			// Tick count of when the last input event was received.
			public int dwTime;

			@Override
			protected List<String> getFieldOrder () {
				return Arrays.asList(new String[] {"cbSize", "dwTime"});
			}
		}

		// returns time of last input
		public boolean GetLastInputInfo (LASTINPUTINFO result);
	}

	@Override
	public long getSystemIdleTime () {
		User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
		User32.INSTANCE.GetLastInputInfo(lastInputInfo);
		return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
	}
}
