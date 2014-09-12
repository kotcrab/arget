
package pl.kotcrab.arget.gui;

import pl.kotcrab.arget.event.Event;

public class ScrollLockEvent implements Event {
	public boolean enabled;

	public ScrollLockEvent (boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isExectueOnEDT () {
		return false;
	}
}
