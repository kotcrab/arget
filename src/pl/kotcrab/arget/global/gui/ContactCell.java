
package pl.kotcrab.arget.global.gui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import pl.kotcrab.arget.global.ContactInfo;

public class ContactCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

	private ContactEntryPanel panel;

	public ContactCell (JTable table, MainWindowCallback guiCallback) {
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
