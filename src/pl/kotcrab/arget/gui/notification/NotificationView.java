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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.util.Timer;
import pl.kotcrab.arget.util.TimerListener;

//TODO add txt length check
class NotificationView extends JDialog {
	private NotifcationListener listener;
	private JLabel iconLabel;
	private JLabel titleLabel;
	private JLabel textLabel;
	private int timeSec;

	public NotificationView (NotifcationListener listener) {
		this.listener = listener;
		getContentPane().setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setUndecorated(true);
		setSize(330, 70);
		setOpacity(0.7f);
		setAlwaysOnTop(true);
		setFocusable(false);
		setFocusableWindowState(false); // stops notification from stealing focus when appearing
		setShape(new RoundRectangle2D.Float(0, 0, 330, 70, 3, 3));

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown (ComponentEvent e) {
				onShow();
			}
		});

		JPanel panel = new JPanel(new MigLayout("", "[][grow][330px,grow]", "[][grow]"));
		panel.setBorder(new EmptyBorder(-2, -7, 0, 0));
		panel.setBackground(new Color(0f, 0f, 0f, 1f));
		getContentPane().add(panel);

		titleLabel = new JLabel("Title", SwingConstants.CENTER);
		titleLabel.setBorder(new EmptyBorder(-5, 0, 0, 0));
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		titleLabel.setForeground(Color.WHITE);

		textLabel = new JLabel("Text offline");
		textLabel.setBorder(new EmptyBorder(-3, 1, 0, 0));
		textLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textLabel.setForeground(Color.WHITE);

		iconLabel = new JLabel();
		iconLabel.setBorder(new EmptyBorder(2, 0, 0, 0));

		panel.add(iconLabel, "cell 1 0,aligny top");
		panel.add(titleLabel, "cell 2 0");
		panel.add(textLabel, "cell 2 1,aligny top");

		setVisible(true);
	}

	private void onShow () {
		Timer timer = new Timer("Notification Disappear Timer");
		timer.schedule(new TimerListener() {

			@Override
			public void doTask () {
				setVisible(false);
				listener.refrshNotifcations();
				dispose();
			}
		}, timeSec * 1000);
	}

	public void setData (Icon image, String title, String text, int timeSec) {
		iconLabel.setIcon(image);
		titleLabel.setText(title);
		textLabel.setText("<html>" + text + "</html>");
		this.timeSec = timeSec;
	}

}
