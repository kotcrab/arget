
package pl.kotcrab.arget.gui.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentFieldsChangeListener implements DocumentListener {
	@Override
	public void insertUpdate (DocumentEvent e) {
		verify();
	}

	@Override
	public void removeUpdate (DocumentEvent e) {
		verify();
	}

	@Override
	public void changedUpdate (DocumentEvent e) {
		verify();
	}

	protected abstract void verify ();
}
