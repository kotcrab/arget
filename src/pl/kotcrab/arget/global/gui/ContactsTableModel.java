
package pl.kotcrab.arget.global.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import pl.kotcrab.arget.global.ContactInfo;

public class ContactsTableModel extends AbstractTableModel {
	List<ContactInfo> contacts;

	public ContactsTableModel (List<ContactInfo> contacts) {
		this.contacts = contacts;
	}

	@Override
	public Class<?> getColumnClass (int columnIndex) {
		return ContactInfo.class;
	}

	@Override
	public int getColumnCount () {
		return 1;
	}

	@Override
	// not used, table header is disabled, still we shouldn't return null or ""
	public String getColumnName (int columnIndex) {
		return "Contacts";
	}

	@Override
	public int getRowCount () {
		return (contacts == null) ? 0 : contacts.size();
	}

	@Override
	public Object getValueAt (int rowIndex, int columnIndex) {
		return (contacts == null) ? null : contacts.get(rowIndex);
	}

	@Override
	public boolean isCellEditable (int columnIndex, int rowIndex) {
		return true;
	}
}
