
package pl.kotcrab.arget.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import pl.kotcrab.arget.util.ThreadUtils;

import com.sun.awt.AWTUtilities;

import java.awt.Font;

//TODO auto hide on window iconififed
public class NotificationOverlay extends JDialog {
	private JFrame owner;

	private int width = 0;

	public NotificationOverlay (JFrame owner) {
		super(owner);
		this.owner = owner;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setUndecorated(true);
		update();
		//setBounds(0, 0, 200, 200);
		//AWTUtilities.setWindowOpaque(this, false);
		setOpacity(0.7f);
		// setAlwaysOnTop(true);
		setFocusable(false);
		setFocusableWindowState(false); // stops notification from stealing focus when appearing

		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		getContentPane().add(panel, BorderLayout.CENTER);
		JLabel label = new JLabel("Notification");
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

		setVisible(true);
		
		new Thread(new Runnable() {
			
			@Override
			public void run () {
				ThreadUtils.sleep(5000);
				slideIn();
				ThreadUtils.sleep(3000);
				slideOut();
			}

		}).start();
	}

	// public void setNotificationWidth (int width) {
	// this.width = width;
	// }

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
	

	private void slideOut () {
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

		setBounds(loc.x, loc.y, c.width, width);
		revalidate();
	}

}
