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

package pl.kotcrab.arget.server.session.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.imgscalr.Scalr;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.gui.MainWindow;
import pl.kotcrab.arget.gui.components.ESCClosableDialog;
import pl.kotcrab.arget.util.ImageUitls;

//TODO centered on screen
public class ImageDisplayPanel extends ESCClosableDialog {

	private BufferedImage orginalImage;
	private BufferedImage image;
	private boolean saved;

	public ImageDisplayPanel (BufferedImage bufImage, final String fileName) {
		super(MainWindow.instance);

		this.orginalImage = bufImage;
		this.image = bufImage;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(758, 499);
		setTitle(App.APP_NAME + " Image Preview - " + fileName);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(0, 0));

		JLabel imageLabel = new JLabel("");
		mainPanel.add(imageLabel, BorderLayout.CENTER);

		final JButton saveButton = new JButton("Save");
		JButton closeButton = new JButton("Close");

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (fileName != null) buttonPane.add(saveButton);
		buttonPane.add(closeButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		if (image.getWidth() > 800 || image.getHeight() > 800) image = Scalr.resize(image, 800);
		imageLabel.setIcon(new ImageIcon(image));

		pack();

		setVisible(true);

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (saved == false) {
					File out = new File(App.DOWNLOAD_FOLDER_PATH + fileName);
					if (out.exists()) out = new File(App.DOWNLOAD_FOLDER_PATH + Math.random() + ") " + fileName);

					ImageUitls.write(orginalImage, out);

					saveButton.setText("Saved");
					saved = true;
				} else
					JOptionPane.showMessageDialog(MainWindow.instance, "Image already saved!");
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

	}

}
