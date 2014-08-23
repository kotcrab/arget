
package pl.kotcrab.arget.gui.notification;

import java.awt.Rectangle;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.event.Event;
import pl.kotcrab.arget.event.EventListener;
import pl.kotcrab.arget.util.SwingUtils;

public class NotificationService implements EventListener, NotifcationListener {
	private static final int MAX_NOTIFICATIONS = 3;
	private NotificationView[] views;

	public NotificationService () {
		views = new NotificationView[MAX_NOTIFICATIONS];

		App.eventBus.register(this);
	}

	public void showNotification (String title, String text) {
		NotificationView view = null;

		for (int i = 0; i < MAX_NOTIFICATIONS; i++) {
			if (views[i] == null) {
				views[i] = new NotificationView(this);
				view = views[i];
				break;
			}

			if (views[i].isVisible() == false) {
				view = views[i];
				break;
			}
		}

		if (view != null) {
			setNotification(view, title, text);
		} else {
			// TODO find oldest notif and replace it
		}
	}

	private void setNotification (NotificationView view, String title, String text) {
		view.setTexts(title, text);
		view.setVisible(true);
		setPositons();
	}

	private void setPositons () {
		int WIDTH = 330;
		int HEIGHT = 70;
		Rectangle size = SwingUtils.getPrimaryMonitorBounds();
		int x = size.x + size.width - WIDTH - 35;
		int y = 35;

		for (int i = 0; i < MAX_NOTIFICATIONS; i++) {
			if (views[i] != null && views[i].isVisible() == true) {
				views[i].setLocation(x, y);
				y += HEIGHT + 7;
			}
		}
	}

	@Override
	public void onEvent (Event event) {
		if (event instanceof ShowNotificationEvent) {
			ShowNotificationEvent notif = (ShowNotificationEvent)event;

			showNotification(notif.title, notif.text);
		}
	}

	@Override
	public void refrshNotifcations () {
		setPositons();
	}

}
