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
