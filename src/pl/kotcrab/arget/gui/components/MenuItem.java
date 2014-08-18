
package pl.kotcrab.arget.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import pl.kotcrab.arget.event.MenuEvent;
import pl.kotcrab.arget.event.MenuEventType;
import pl.kotcrab.arget.global.gui.MainWindow;

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
			MainWindow.eventBus.post(event);
		}
	}

}
