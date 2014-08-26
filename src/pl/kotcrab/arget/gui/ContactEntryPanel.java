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

package pl.kotcrab.arget.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ContactStatus;

public class ContactEntryPanel extends JPanel {
	private static ImageIcon connected;
	private static ImageIcon disconnected;
	private static ImageIcon session;
	private static ImageIcon dot;

	private JLabel nameLabel;
	private ContactInfo contact;
	private JLabel newMsgIndicatorLabel;

	static {
		connected = new ImageIcon(App.getResource("/data/contact/connected.png"));
		disconnected = new ImageIcon(App.getResource("/data/contact/disconnected.png"));
		session = new ImageIcon(App.getResource("/data/contact/session.png"));
		dot = new ImageIcon(App.getResource("/data/dot.png"));
	}

	public ContactEntryPanel (final JTable table, final MainWindowCallback guiCallback) {
		setLayout(new BorderLayout(0, 0));

		nameLabel = new JLabel();
		nameLabel.setIcon(disconnected);

		newMsgIndicatorLabel = new JLabel(" "); // space will do as right padding
		newMsgIndicatorLabel.setIcon(null);

		add(nameLabel, BorderLayout.WEST);
		add(newMsgIndicatorLabel, BorderLayout.EAST);

		addMouseListener(new MouseAdapter() {
			@Override
			// if JTable start editor all events will be send to this editor, but our JTable mouse listener handles opening popup
			// menu, so we must send this event to parent, so it will be able to open popup menu
			public void mousePressed (MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					Component source = (Component)e.getSource();
					source.getParent().dispatchEvent(e);
				}
			}

			@Override
			public void mouseClicked (MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					guiCallback.startChat(contact);
				}
			}
		});
	}

	public void setStatus (ContactStatus status) {
		if (status == ContactStatus.DISCONNECTED) nameLabel.setIcon(disconnected);
		if (status == ContactStatus.CONNECTED) nameLabel.setIcon(connected);
		if (status == ContactStatus.CONNECTED_SESSION) nameLabel.setIcon(session);
	}

	public void setUnreadMessages (boolean unread) {
		if (unread)
			newMsgIndicatorLabel.setIcon(dot);
		else
			newMsgIndicatorLabel.setIcon(null);
	}

	public void setContactName (String name) {
		nameLabel.setText(name);
	}

	public void setContact (ContactInfo contact) {
		this.contact = contact;
		setContactName(contact.name);
		setStatus(contact.status);
		setUnreadMessages(contact.unreadMessages);
	}

}
