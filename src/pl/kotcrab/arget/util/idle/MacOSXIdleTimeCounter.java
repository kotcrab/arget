
package pl.kotcrab.arget.util.idle;

import com.sun.jna.Library;
import com.sun.jna.Native;

//TODO this is untested, i'm not able to test it (someone give me mac maybe?)
public class MacOSXIdleTimeCounter extends IdleTimeCounter {
	public interface ApplicationServices extends Library {

		ApplicationServices INSTANCE = (ApplicationServices)Native.loadLibrary("ApplicationServices", ApplicationServices.class);

		int kCGAnyInputEventType = ~0;
		int kCGEventSourceStatePrivate = -1;
		int kCGEventSourceStateCombinedSessionState = 0;
		int kCGEventSourceStateHIDSystemState = 1;

		public double CGEventSourceSecondsSinceLastEventType (int sourceStateId, int eventType);
	}

	@Override
	public long getSystemIdleTime () {
		double idleTimeSeconds = ApplicationServices.INSTANCE.CGEventSourceSecondsSinceLastEventType(
			ApplicationServices.kCGEventSourceStateCombinedSessionState, ApplicationServices.kCGAnyInputEventType);
		return (long)(idleTimeSeconds * 1000);
	}
}
