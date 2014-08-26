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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.binary.Base64;

import pl.kotcrab.arget.gui.dialog.CreateContactDialog;
import pl.kotcrab.arget.profile.Profile;
import pl.kotcrab.arget.profile.ProfileIO;
import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ContactStatus;

public class ContactsPanel extends JPanel {
	private Profile profile;
	private JTable table;

	public ContactsPanel (final Profile profile, MainWindowCallback callback) {
		this.profile = profile;

		setLayout(new BorderLayout());

		table = new JTable(new ContactsTableModel(profile.contacts));

		table.setDefaultRenderer(ContactInfo.class, new ContactsTableEditor(table, callback));
		table.setDefaultEditor(ContactInfo.class, new ContactsTableEditor(table, callback));
		table.setShowGrid(false);
		table.setTableHeader(null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight(40);

		final JPopupMenu popupMenu = new JPopupMenu();

		{
			JMenuItem menuModify = new JMenuItem("Modify");
			JMenuItem menuDelete = new JMenuItem("Delete");

			popupMenu.add(menuModify);
			popupMenu.add(menuDelete);

			menuModify.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					String key = Base64.encodeBase64String(profile.rsa.getPublicKey().getEncoded());
					new CreateContactDialog(MainWindow.instance, key, (ContactInfo)table.getValueAt(table.getSelectedRow(), 0));
					ProfileIO.saveProfile(profile);
					updateContactsTable();
				}
			});

			menuDelete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					ContactInfo contact = (ContactInfo)table.getValueAt(table.getSelectedRow(), 0);

					if (contact.status == ContactStatus.CONNECTED_SESSION) {
						JOptionPane.showMessageDialog(MainWindow.instance, "This contact cannot be deleted because session is open.", "Error",
							JOptionPane.ERROR_MESSAGE);
						return;
					}

					int result = JOptionPane.showConfirmDialog(MainWindow.instance, "Are you sure you want to delete '" + contact.name + "'?",
						"Warning", JOptionPane.YES_NO_OPTION);

					if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) return;

					profile.contacts.remove(contact);
					ProfileIO.saveProfile(profile);
					updateContactsTable();
				}
			});
		}

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed (MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), table);

					int rowNumber = table.rowAtPoint(p);
					table.editCellAt(rowNumber, 0);
					table.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);

					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		add(tableScrollPane, BorderLayout.CENTER);
	}

	public void updateContactsTable () {
		table.setModel(new ContactsTableModel(profile.contacts));
	}
}
