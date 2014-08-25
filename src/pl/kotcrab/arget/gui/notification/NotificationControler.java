
package pl.kotcrab.arget.gui.notification;

public interface NotificationControler {
	public boolean shouldDisplayNotification ();
}

class DefaultNotificationControler implements NotificationControler {
	@Override
	public boolean shouldDisplayNotification () {
		return true;
	}
}
