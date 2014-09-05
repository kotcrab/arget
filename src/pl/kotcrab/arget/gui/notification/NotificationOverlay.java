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

package pl.kotcrab.arget.gui.notification;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import pl.kotcrab.arget.util.ThreadUtils;

class NotificationOverlay extends JDialog {
	private JFrame owner;
	private JLabel label;

	private NotificationOverlayLayout layout;

	private int width = 0;

	public NotificationOverlay (JFrame owner, String text) {
		super(owner);
		this.owner = owner;

		layout = new NotificationOverlayLayout();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setUndecorated(true);
		update();
		setOpacity(0.7f);
		setFocusable(false);
		setFocusableWindowState(false); // stops notification from stealing focus when appearing

		getContentPane().setLayout(new BorderLayout());

		JPanel panel = new JPanel(layout);
		panel.setBackground(Color.BLACK);
		getContentPane().add(panel, BorderLayout.CENTER);

		label = new JLabel(text);
		label.setFont(new Font("Verdana", Font.PLAIN, 12));
		label.setForeground(Color.WHITE);
		panel.add(label);

		owner.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized (ComponentEvent e) {
				update();
			}

			@Override
			public void componentMoved (ComponentEvent e) {
				update();
			}

		});

		// hide when window minimized
		owner.addWindowListener(new WindowAdapter() {
			@Override
			public void windowIconified (WindowEvent e) {
				dispose();
			}
		});

		setVisible(true);

		Thread hider = new Thread(new Runnable() {

			@Override
			public void run () {
				ThreadUtils.sleep(3000);
				slideOut();
			}

		}, "OverlayHider");
		hider.setDaemon(true);
		hider.start();

	}

	public void slideIn () {
		final Timer timer = new Timer(15, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				width += 1;
				if (width == 25) timer.stop();
				update();

			}
		});

		timer.start();
	}

	public void slideOut () {
		final Timer timer = new Timer(15, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				width -= 1;
				if (width == 0) timer.stop();
				update();

			}
		});

		timer.start();
	}

	private void update () {
		Rectangle c = owner.getContentPane().getBounds();
		Point loc = new Point(c.x, 0);
		SwingUtilities.convertPointToScreen(loc, owner.getContentPane());
		
		layout.setYOffset(23 - width);
		setBounds(loc.x, loc.y, c.width, width);
		revalidate();
	}

}
