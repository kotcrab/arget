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

package pl.kotcrab.arget.test.manual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import pl.kotcrab.arget.util.SwingUtils;

public class NotificationDemo extends JDialog {

	public NotificationDemo () {
		setUndecorated(true);
		setSize(330, 70);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBackground(new Color(0f, 0f, 0f, 0.5f));
		setAlwaysOnTop(true);

		Rectangle size = SwingUtils.getPrimaryMonitorBounds();
		int xm = size.x + size.width - getWidth();
		// int ym = size.y + size.height - getHeight();
		setLocation(xm - 35, 35);

		getContentPane().setLayout(new BorderLayout());

		JLabel label = new JLabel("Notification", SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.PLAIN, 19));
		label.setForeground(Color.WHITE);
		getContentPane().add(label, BorderLayout.CENTER);

		setVisible(true);
		setShape(new RoundRectangle2D.Float(0, 0, 330, 70, 3, 3));
	}

	public static void main (String[] args) {
		new NotificationDemo();
	}
}
