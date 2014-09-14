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

package pl.kotcrab.arget.gui.session.msg;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.imgscalr.Scalr;

import pl.kotcrab.arget.gui.session.ImageDisplayPanel;

public class ImageMessage extends MessageComponent {
	private JLabel imageLabel;

	public ImageMessage (MsgType type, final BufferedImage image, final String fileName) {
		super(type);

		setLayout(new BorderLayout());
		imageLabel = new JLabel();
		add(imageLabel, BorderLayout.CENTER);

		// TODO optimize for gif, png, jpg

		BufferedImage thumbnail = Scalr.resize(image, 150);
		imageLabel.setIcon(new ImageIcon(thumbnail));
		imageLabel.setBorder(new EmptyBorder(3, 3, 1, 1));

		imageLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked (MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) new ImageDisplayPanel(image, fileName);
			}
		});
	}
}
