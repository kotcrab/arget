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

import com.sun.jna.Library;
import com.sun.jna.Native;

//TODO this is untested, i'm not able to test it (someone give me mac maybe?)
class MacOSXIdleTimeCounter extends IdleTimeCounter {
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
