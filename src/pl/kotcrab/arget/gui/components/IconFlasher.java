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

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class IconFlasher {
	private JFrame frame;
	private Image icon1;

	private boolean current;
	private boolean running;

	private Timer timer;

	public IconFlasher (final JFrame frame, final Image icon1, final Image icon2) {
		this.frame = frame;
		this.icon1 = icon1;
		frame.setIconImage(icon1);

		timer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed (ActionEvent e) {
				current = !current;

				if (current)
					frame.setIconImage(icon1);
				else
					frame.setIconImage(icon2);

			}
		});
	}

	public void start () {

		if (running == false) {
			running = true;
			timer.start();
		}
	}

	public void stop () {
		if (running) timer.stop();
		running = false;

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run () {
				frame.setIconImage(icon1);
			}
		});
	}

}
