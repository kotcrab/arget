
package pl.kotcrab.arget.gui.notification;

import pl.kotcrab.arget.event.Event;

public class ShowNotificationEvent implements Event {
	public String title;
	public String text;

	public ShowNotificationEvent (String title, String text) {
		this.title = title;
		this.text = text;
	}

	@Override
	public boolean isExectueOnAWTEventQueue () {
		return true;
	}

}
