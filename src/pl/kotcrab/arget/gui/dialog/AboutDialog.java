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

package pl.kotcrab.arget.gui.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.gui.components.ESCClosableDialog;
import pl.kotcrab.arget.util.StringUtils;

public class AboutDialog extends ESCClosableDialog {
	private AboutDialog instance;

	public AboutDialog (JFrame owner) {
		super(owner, true);
		instance = this;

		setResizable(false);
		setSize(423, 150);
		setTitle(App.APP_NAME + " - About");
		setPositionToCenter(owner);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel(new MigLayout("", "[grow][][]", "[][][grow][]"));
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		mainPanel.add(new JLabel("Arget - open source chat program"), "cell 0 0 2 1");
		mainPanel.add(new JLabel(new ImageIcon(AboutDialog.class.getResource("/data/icon/icon_med.png"))),
			"cell 2 0 1 2,alignx right");
		mainPanel.add(new JLabel("Created by Paweł Pastszuak, licensed under GPLv3 license."), "cell 0 1 2 1,aligny top");
		mainPanel.add(new JLabel("Version: " + App.APP_NAME + " " + App.APP_VERSION), "flowx,cell 0 3,alignx left");

		JButton okButton = new JButton("OK");
		JButton viewLicenseButton = new JButton("View license");
		JButton view3rdLicenseButton = new JButton("View 3rd licenses");

		mainPanel.add(okButton, "cell 2 3,growx");
		mainPanel.add(viewLicenseButton, "cell 1 3");
		mainPanel.add(view3rdLicenseButton, "flowx,cell 1 3");

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		viewLicenseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				new ViewTextFileDialog(instance, "License", StringUtils.toString(App.getResourceAsStream("/data/LICENSE")));
			}
		});

		view3rdLicenseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				new ViewTextFileDialog(instance, "3rd licenses", StringUtils.toString(App.getResourceAsStream("/data/LICENSE3RD")));
			}
		});

		setVisible(true);
	}
}
