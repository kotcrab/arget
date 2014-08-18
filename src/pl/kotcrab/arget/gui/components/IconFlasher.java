
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
