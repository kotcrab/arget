
package pl.kotcrab.arget.global.session.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.imgscalr.Scalr;

public class ImageMessage extends MessageComponent {

	public ImageMessage (int type, final BufferedImage image, final String fileName) {
		super(type);

		JLabel imageLabel = new JLabel();
		add(imageLabel);

		// TODO optimzie for gif, png, jpg
		BufferedImage thumbnail = Scalr.resize(image, 150);
		imageLabel.setIcon(new ImageIcon(thumbnail));
		// imageLabel.setIcon(new ImageIcon(data));

		imageLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked (MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) new ImageDisplayPanel(image, fileName);
			}
		});
	}

}
