
package pl.kotcrab.arget.global.session.gui;

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
import pl.kotcrab.arget.global.gui.MainWindow;
import pl.kotcrab.arget.gui.ESCClosableDialog;
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
