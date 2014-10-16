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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.gui.components.ESCClosableDialog;

public class VersionMismatchDialog extends ESCClosableDialog {

	public VersionMismatchDialog (JFrame parrent, String serverVersion, int serverVersionCompatibilityCode) {
		super(parrent, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Version Mismatch Details");
		setSize(371, 141);
		setPositionToCenter(parrent);
		getContentPane().setLayout(new BorderLayout());

		JPanel contentPanel = new JPanel();
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setBorder(new EmptyBorder(0, 3, 0, 3));
		contentPanel.setLayout(new MigLayout("", "[46px][grow]", "[25.00][14px][27.00][]"));

		String sText = "Server version: " + serverVersion + " (compatibility code: " + serverVersionCompatibilityCode + ")";
		String cText = "Your client version: " + App.APP_VERSION + " (compatibility code: " + App.VERSION_COMPATIBILITY_CODE + ")";

		JLabel serverVersionLabel = new JLabel(sText);
		JLabel clientErrorLabel = new JLabel(cText);
		JButton okButton = new JButton("OK");

		contentPanel.add(new JLabel("Error: Server is running different version of Arget"), "cell 0 0,aligny top");
		contentPanel.add(serverVersionLabel, "cell 0 1,alignx left,aligny top");
		contentPanel.add(clientErrorLabel, "cell 0 2,alignx left,aligny top");
		contentPanel.add(okButton, "cell 1 3,alignx right");

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		setVisible(true);
	}

}
