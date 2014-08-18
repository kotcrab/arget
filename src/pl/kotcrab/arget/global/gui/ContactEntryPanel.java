
package pl.kotcrab.arget.global.gui;

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
import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.global.ContactStatus;

public class ContactEntryPanel extends JPanel {
	private static ImageIcon connected;
	private static ImageIcon disconnected;
	private static ImageIcon session;
	private static ImageIcon dot;

	private JLabel nameLabel;
	private ContactInfo contact;
	private JLabel newMsgIndicatorLabel;

	static {
		connected = new ImageIcon(App.getResource("/data/connected.png"));
		disconnected = new ImageIcon(App.getResource("/data/disconnected.png"));
		session = new ImageIcon(App.getResource("/data/session.png"));
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
		if (status == ContactStatus.CONNECTED_GLOBAL) nameLabel.setIcon(connected);
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
