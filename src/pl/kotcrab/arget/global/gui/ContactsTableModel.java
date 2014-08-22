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
