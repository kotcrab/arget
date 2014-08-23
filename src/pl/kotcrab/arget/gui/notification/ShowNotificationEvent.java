
package pl.kotcrab.arget.gui.notification;

import java.awt.image.BufferedImage;

import pl.kotcrab.arget.event.Event;

public class ShowNotificationEvent implements Event {
	public BufferedImage image;
	public String title;
	public String text;

	public ShowNotificationEvent (String title, String text) {
		this.title = title;
		this.text = text;
	}

	public ShowNotificationEvent (BufferedImage icon, String title, String text) {
		this.image = icon;
		this.title = title;
		this.text = text;
	}

	@Override
	public boolean isExectueOnAWTEventQueue () {
		return true;
	}

}
