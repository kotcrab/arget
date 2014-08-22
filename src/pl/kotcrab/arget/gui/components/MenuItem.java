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

package pl.kotcrab.arget.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.event.MenuEvent;
import pl.kotcrab.arget.event.MenuEventType;

public class MenuItem extends JMenuItem {
	private MenuEvent event;

	public MenuItem (String name, MenuEventType eventType) {
		super(name);
		this.event = new MenuEvent(eventType);
		addActionListener(new MenuItemActionListener());
	}

	class MenuItemActionListener implements ActionListener {

		@Override
		public void actionPerformed (ActionEvent e) {
			App.eventBus.post(event);
		}
	}

}
