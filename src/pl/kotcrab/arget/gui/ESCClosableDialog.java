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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;

/** Dialog that can by closed (disposed) via ESC key
 * 
 * @author Pawel Pastuszak */
public class ESCClosableDialog extends JDialog {
	private KeyEventDispatcher keyDispatcher;

	@Override
	protected void dialogInit () {
		super.dialogInit();

		keyDispatcher = new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent (KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
					return true;
				}
				return false;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
	}

	@Override
	public void dispose () {
		super.dispose();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyDispatcher);
	}

	protected void setPositionToCenter (Component owner) {
		if (owner == null)
			setLocation(100, 100);
		else {
			int xw = (owner.getWidth() - getWidth()) / 2;
			int x = owner.getX() + xw;
			int yw = (owner.getHeight() - getHeight()) / 2;
			int y = owner.getY() + yw;

			setLocation(x, y);
		}
	}

	// ====== copied constructors, sorry :( ======
	public ESCClosableDialog () {
		super();
	}

	public ESCClosableDialog (Frame owner) {
		super(owner);
	}

	public ESCClosableDialog (Frame owner, boolean modal) {
		super(owner, modal);
	}

	public ESCClosableDialog (Frame owner, String title) {
		super(owner, title);
	}

	public ESCClosableDialog (Frame owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public ESCClosableDialog (Dialog owner) {
		super(owner);
	}

	public ESCClosableDialog (Dialog owner, boolean modal) {
		super(owner, modal);
	}

	public ESCClosableDialog (Dialog owner, String title) {
		super(owner, title);
	}

	public ESCClosableDialog (Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
	}

	public ESCClosableDialog (Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}

	public ESCClosableDialog (Window owner) {
		super(owner);
	}

	public ESCClosableDialog (Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	public ESCClosableDialog (Window owner, String title) {
		super(owner, title);
	}

	public ESCClosableDialog (Window owner, String title, Dialog.ModalityType modalityType) {
		super(owner, title, modalityType);
	}

	public ESCClosableDialog (Window owner, String title, Dialog.ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
	}
}
