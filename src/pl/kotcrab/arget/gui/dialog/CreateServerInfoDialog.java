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
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.gui.components.DocumentFieldsChangeListener;
import pl.kotcrab.arget.gui.components.ESCClosableDialog;
import pl.kotcrab.arget.server.ServerDescriptor;

public class CreateServerInfoDialog extends ESCClosableDialog {

	private ServerDescriptor existingDesc;
	private CreateServerDialogFinished listener;

	private JTextField nameField;
	private JTextField ipField;
	private JTextField portField;
	private JButton okButton;

	/** @wbp.parser.constructor */
	public CreateServerInfoDialog (Window window, CreateServerDialogFinished listener) {
		this(window, null, listener);
	}

	public CreateServerInfoDialog (Window window, ServerDescriptor descriptor) {
		this(window, descriptor, null);
	}

	public CreateServerInfoDialog (Window window, ServerDescriptor existingDesc, CreateServerDialogFinished listener) {
		super(window, ModalityType.APPLICATION_MODAL);
		init(existingDesc, listener);
	}

	private void init (ServerDescriptor existingDesc, CreateServerDialogFinished listener) {
		this.existingDesc = existingDesc;
		this.listener = listener;

		FieldsChangeListener fieldsChangeListener = new FieldsChangeListener();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Edit Server Details");
		setSize(270, 161);
		setPositionToCenter(getParent());
		setResizable(false);

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(null);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		nameField = new JTextField();
		nameField.getDocument().addDocumentListener(fieldsChangeListener);
		nameField.setColumns(10);

		ipField = new JTextField();
		ipField.getDocument().addDocumentListener(fieldsChangeListener);
		ipField.setColumns(10);

		portField = new JTextField();
		portField.setText("31415");
		portField.getDocument().addDocumentListener(fieldsChangeListener);
		portField.setColumns(10);
		contentPanel.setLayout(new MigLayout("", "[80.00px][127px,grow]", "[22px][22px][22px]"));

		contentPanel.add(new JLabel("Server name:"), "cell 0 0,grow");
		contentPanel.add(nameField, "cell 1 0,grow");
		contentPanel.add(new JLabel("Ip:"), "cell 0 1,grow");
		contentPanel.add(ipField, "cell 1 1,grow");
		contentPanel.add(new JLabel("Port:"), "cell 0 2,grow");
		contentPanel.add(portField, "cell 1 2,grow");

		createButtonPane();

		if (existingDesc != null) {
			nameField.setText(existingDesc.name);
			ipField.setText(existingDesc.ip);
			portField.setText(String.valueOf(existingDesc.port));
		}

		setVisible(true);
	}

	private void createButtonPane () {
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton cancelButton = new JButton("Cancel");
		okButton = new JButton("Save");
		okButton.setEnabled(false);

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (existingDesc != null) {
					existingDesc.name = nameField.getText();
					existingDesc.ip = ipField.getText();
					existingDesc.port = Integer.parseInt(portField.getText());
				}

				if (listener != null)
					listener.finished(new ServerDescriptor(nameField.getText(), ipField.getText(), Integer.parseInt(portField
						.getText())));
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		buttonPane.add(cancelButton);
		buttonPane.add(okButton);

		getRootPane().setDefaultButton(okButton);
	}

	private class FieldsChangeListener extends DocumentFieldsChangeListener {
		@Override
		protected void verify () {
			okButton.setEnabled(true);

			if (nameField.getText().isEmpty()) lockOkButton();
			if (ipField.getText().isEmpty()) lockOkButton();
			if (isInteger(portField.getText()) == false) lockOkButton();
		}

		private void lockOkButton () {
			okButton.setEnabled(false);
		}

		private boolean isInteger (String text) {
			try {
				Integer.parseInt(text);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

}
