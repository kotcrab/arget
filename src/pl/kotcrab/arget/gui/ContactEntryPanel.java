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
import javax.swing.border.EmptyBorder;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ContactStatus;

public class ContactEntryPanel extends JPanel {
	private static ImageIcon connected;
	private static ImageIcon disconnected;
	private static ImageIcon session;
	private static ImageIcon dot;

	private JLabel iconLabel;
	private ContactInfo contact;
	private JLabel indicatorLabel;
	private JLabel nameLabel;

	static {
		connected = new ImageIcon(App.getResource("/data/contact/connected.png"));
		disconnected = new ImageIcon(App.getResource("/data/contact/disconnected.png"));
		session = new ImageIcon(App.getResource("/data/contact/session.png"));
		dot = new ImageIcon(App.getResource("/data/dot.png"));
	}

	public ContactEntryPanel (final JTable table, final MainWindowCallback guiCallback) {
		setLayout(new BorderLayout(0, 0));

		iconLabel = new JLabel();
		iconLabel.setBorder(new EmptyBorder(0, 0, 0, 2));
		iconLabel.setIcon(disconnected);

		nameLabel = new JLabel("name");
		nameLabel.setBorder(new EmptyBorder(0, 0, 2, 0));
		
		indicatorLabel = new JLabel(" "); // space will do as right padding
		indicatorLabel.setIcon(null);

		add(nameLabel, BorderLayout.CENTER);
		add(iconLabel, BorderLayout.WEST);
		add(indicatorLabel, BorderLayout.EAST);

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
		if (status == ContactStatus.DISCONNECTED) iconLabel.setIcon(disconnected);
		if (status == ContactStatus.CONNECTED) iconLabel.setIcon(connected);
		if (status == ContactStatus.CONNECTED_SESSION) iconLabel.setIcon(session);
	}

	public void setUnreadMessages (boolean unread) {
		if (unread)
			indicatorLabel.setIcon(dot);
		else
			indicatorLabel.setIcon(null);
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
