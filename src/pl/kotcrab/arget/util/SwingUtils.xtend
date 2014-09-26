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
package pl.kotcrab.arget.util

import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.JPopupMenu
import javax.swing.text.JTextComponent

class SwingUtils {
	static def createDefaultPopupMenu(JTextComponent comp) {
		val JPopupMenu m = new JPopupMenu()

		m.add(createAction("Cut", [comp.cut]))
		m.add(createAction("Copy", [comp.copy]))
		m.add(createAction("Paste", [comp.paste]))
		m.addSeparator()
		m.add(createAction("Select All", [comp.selectAll]))

		comp.addMouseListener(new PopupListener(m))
	}

	def private static createAction(String name, ()=>void action) {
		return new AbstractAction(name) {
			override actionPerformed(ActionEvent e) {
				action.apply()
			}
		}
	}
}

class PopupListener extends MouseAdapter {
	val JPopupMenu popup

	new(JPopupMenu popupMenu) {
		popup = popupMenu
	}

	override public void mousePressed(MouseEvent e) {
		maybeShowPopup(e)
	}

	override public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e)
	}

	def private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger())
			popup.show(e.getComponent(), e.getX(), e.getY())
	}
}
