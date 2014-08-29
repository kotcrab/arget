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

package pl.kotcrab.arget.gui.notification;

import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.event.ConnectionStatusEvent;
import pl.kotcrab.arget.event.ContactStatusEvent;
import pl.kotcrab.arget.event.Event;
import pl.kotcrab.arget.event.EventListener;
import pl.kotcrab.arget.server.ConnectionStatus;
import pl.kotcrab.arget.server.ContactStatus;
import pl.kotcrab.arget.util.SwingUtils;

public class NotificationService implements EventListener, NotifcationListener {
	private static final int MAX_NOTIFICATIONS = 3;

	private NotificationControler controler;
	private NotificationView[] views;

	private Icon defaultIcon;
	private int defautlTime = 3;

	public NotificationService () {
		views = new NotificationView[MAX_NOTIFICATIONS];
		defaultIcon = new ImageIcon(App.getResource("/data/icon/icon_small.png"));
		App.eventBus.register(this);

		controler = new DefaultNotificationControler();
	}

	public void showNotification (Icon icon, String title, String text) {
		showNotification(icon, title, text, defautlTime);
	}

	public void showNotification (String title, String text, int timeSec) {
		showNotification(defaultIcon, title, text, timeSec);
	}

	public void showNotification (String title, String text) {
		showNotification(defaultIcon, title, text, defautlTime);
	}

	public void showNotification (Icon icon, String title, String text, int timeSec) {
		if (controler.shouldDisplayNotification()) {
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
				setNotification(view, icon, title, text, timeSec);
			} else {
				// TODO find oldest notif and replace it
			}
		}
	}

	private void setNotification (NotificationView view, Icon icon, String title, String text, int timeSec) {
		view.setData(icon, title, text, timeSec);
		view.setVisible(true);
		setPositons();
	}

	public void setControler (NotificationControler controler) {
		this.controler = controler;
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
		if (event instanceof ContactStatusEvent) {
			ContactStatusEvent e = (ContactStatusEvent)event;

			if (e.contact.status == ContactStatus.DISCONNECTED && e.previousStatus != ContactStatus.DISCONNECTED)
				showNotification(e.contact.name, e.contact.name + " is now offline");

			if (e.contact.status == ContactStatus.CONNECTED && e.previousStatus == ContactStatus.DISCONNECTED)
				showNotification(e.contact.name, e.contact.name + " is now online");
		}

		if (event instanceof ConnectionStatusEvent) {
			ConnectionStatusEvent e = (ConnectionStatusEvent)event;
			if (e.status == ConnectionStatus.TIMEDOUT) showNotification("Disconnected", "Connection timed out", 5);
			if (e.status == ConnectionStatus.SERVER_SHUTDOWN) showNotification("Disconnected", "Connection timed out", 5);
			if (e.status == ConnectionStatus.KICKED) showNotification("Disconnected", "Kicked from server", 5);
		}

//		if (event instanceof ShowNotificationEvent) {
//			ShowNotificationEvent notif = (ShowNotificationEvent)event;
//
//			Icon icon;
//			if (notif.image == null)
//				icon = defaultIcon;
//			else
//				icon = new ImageIcon(Scalr.resize(notif.image, 20, 20));
//
//			showNotification(icon, notif.title, notif.text, notif.displayTime);
//		}
	}

	@Override
	public void refrshNotifcations () {
		setPositons();
	}

}