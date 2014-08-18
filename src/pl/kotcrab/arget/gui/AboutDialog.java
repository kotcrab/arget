
package pl.kotcrab.arget.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.IOUtils;

import pl.kotcrab.arget.App;

//TODO add gpl license, add more info
public class AboutDialog extends ESCClosableDialog {

	public AboutDialog (JFrame owner) {
		super(owner, true);
		setSize(450, 300);
		setTitle(App.APP_NAME + " - About");
		setMinimumSize(new Dimension(450, 300));
		setPositionToCenter(owner);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		mainPanel.setLayout(new MigLayout("", "[grow]", "[][][grow][]"));

		JTextArea licenseTextArea = new JTextArea();

		try { // Comment this if you want to use gui designer, otherwise you will get java heap space exception
			licenseTextArea.setText(IOUtils.toString(App.getResourceAsStream("/data/3rdlicense.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		licenseTextArea.setCaretPosition(0);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});
		mainPanel.add(new JLabel(App.APP_NAME + " " + App.APP_VERSION), "cell 0 0");
		mainPanel.add(new JLabel("Licenses information:"), "cell 0 1");
		mainPanel.add(new JScrollPane(licenseTextArea), "cell 0 2,grow");
		mainPanel.add(okButton, "cell 0 3,alignx right");

		setVisible(true);
	}
}
