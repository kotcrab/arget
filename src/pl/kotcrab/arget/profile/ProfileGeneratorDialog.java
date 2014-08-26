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

package pl.kotcrab.arget.profile;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.gui.components.DocumentFieldsChangeListener;

public class ProfileGeneratorDialog extends JDialog {

	private JTextField nameField;
	private JPasswordField passwordField;
	private JButton okButton;
	private JLabel errorLabel;

	/** Create the dialog. */
	public ProfileGeneratorDialog (final ProfileGeneratorDialogListener listener) {
		setResizable(false);
		setBounds(100, 100, 272, 134);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Profile Generator");

		JPanel mainPanel = new JPanel(new MigLayout("", "[][grow][][]", "[][][][]"));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		nameField = new JTextField();
		nameField.getDocument().addDocumentListener(new FieldsChangeListener());
		nameField.setColumns(10);

		passwordField = new JPasswordField();

		errorLabel = new JLabel("Name cannot be empty!");
		errorLabel.setForeground(Color.RED);

		JButton cancelButton = new JButton("Cancel");
		okButton = new JButton("OK");
		okButton.setEnabled(false);

		mainPanel.add(new JLabel("Name:"), "cell 0 0,alignx left");
		mainPanel.add(new JLabel("Password:"), "cell 0 1,alignx trailing");
		mainPanel.add(nameField, "cell 1 0 3 1,growx");
		mainPanel.add(passwordField, "cell 1 1 3 1,growx");
		mainPanel.add(errorLabel, "cell 0 3 2 1");
		mainPanel.add(cancelButton, "cell 2 3");
		mainPanel.add(okButton, "cell 3 3");

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
				listener.ok(nameField.getText(), passwordField.getPassword());
			}
		});

		setVisible(true);

	}

	private class FieldsChangeListener extends DocumentFieldsChangeListener {
		@Override
		protected void verify () {

			okButton.setEnabled(true);
			errorLabel.setVisible(false);

			if (nameField.getText().equals("")) setError("Name cannot be empty!");
			if (ProfileIO.isValidProfileName(nameField.getText()) == false) setError("Invalid profile name!");
		}

		private void setError (String text) {
			okButton.setEnabled(false);
			errorLabel.setVisible(true);
			errorLabel.setText(text);
		}

	}

}
