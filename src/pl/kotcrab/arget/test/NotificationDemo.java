
package pl.kotcrab.arget.test;

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
		setShape(new RoundRectangle2D.Float(0, 0, 330, 70, 3,3));
	}

	public static void main (String[] args) {
		new NotificationDemo();
	}
}
