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

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import pl.kotcrab.arget.server.ContactInfo;

public class ContactsTableEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

	private ContactEntryPanel panel;

	public ContactsTableEditor (JTable table, MainWindowCallback guiCallback) {
		panel = new ContactEntryPanel(table, guiCallback);
	}

	private void updateData (ContactInfo contact, boolean isSelected, JTable table) {
		panel.setContact(contact);

		if (isSelected)
			panel.setBackground(table.getSelectionBackground());
		else
			panel.setBackground(table.getSelectionForeground());
	}

	@Override
	public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int column) {
		ContactInfo feed = (ContactInfo)value;
		updateData(feed, true, table);
		return panel;
	}

	@Override
	public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
		int column) {
		ContactInfo feed = (ContactInfo)value;
		updateData(feed, false, table);
		return panel;
	}

	@Override
	public Object getCellEditorValue () {
		return null;
	}
}
