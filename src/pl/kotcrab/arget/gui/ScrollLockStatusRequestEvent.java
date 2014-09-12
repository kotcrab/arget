
package pl.kotcrab.arget.gui;

import pl.kotcrab.arget.event.Event;

public class ScrollLockStatusRequestEvent implements Event {
	public ScrollLockStatusRequestEvent () {
	}

	@Override
	public boolean isExectueOnEDT () {
		return false;
	}
}
