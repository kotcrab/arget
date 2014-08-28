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

import org.imgscalr.Scalr;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.event.Event;
import pl.kotcrab.arget.event.EventListener;
import pl.kotcrab.arget.util.SwingUtils;

public class NotificationService implements EventListener, NotifcationListener {
	private static final int MAX_NOTIFICATIONS = 3;

	private NotificationControler controler;
	private NotificationView[] views;
	private Icon defaultIcon;

	public NotificationService () {
		views = new NotificationView[MAX_NOTIFICATIONS];
		defaultIcon = new ImageIcon(App.getResource("/data/iconsmall.png"));
		App.eventBus.register(this);

		controler = new DefaultNotificationControler();
	}

	public void showNotification (Icon icon, String title, String text) {
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
				setNotification(view, icon, title, text);
			} else {
				// TODO find oldest notif and replace it
			}
		}
	}

	private void setNotification (NotificationView view, Icon icon, String title, String text) {
		view.setData(icon, title, text);
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
		if (event instanceof ShowNotificationEvent) {
			ShowNotificationEvent notif = (ShowNotificationEvent)event;

			Icon icon;
			if (notif.image == null)
				icon = defaultIcon;
			else
				icon = new ImageIcon(Scalr.resize(notif.image, 20, 20));

			showNotification(icon, notif.title, notif.text);
		}
	}

	@Override
	public void refrshNotifcations () {
		setPositons();
	}

}
