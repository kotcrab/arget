
package pl.kotcrab.arget.global.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.validator.routines.InetAddressValidator;

import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.arget.gui.ESCClosableDialog;
import pl.kotcrab.arget.gui.components.DocumentFieldsChangeListener;

public class CreateServerInfoDialog extends ESCClosableDialog {

	private ServerInfo existingDesc;
	private CreateServerDialogFinished listener;

	private JTextField nameField;
	private JTextField ipField;
	private JTextField portField;
	private JButton okButton;

	/** @wbp.parser.constructor */
	public CreateServerInfoDialog (Window window, CreateServerDialogFinished listener) {
		this(window, null, listener);
	}

	public CreateServerInfoDialog (Window window, ServerInfo descriptor) {
		this(window, descriptor, null);
	}

	public CreateServerInfoDialog (Window window, ServerInfo existingDesc, CreateServerDialogFinished listener) {
		super(window, ModalityType.APPLICATION_MODAL);
		init(existingDesc, listener);
	}

	private void init (ServerInfo existingDesc, CreateServerDialogFinished listener) {
		this.existingDesc = existingDesc;
		this.listener = listener;

		FieldsChangeListener fieldsChangeListener = new FieldsChangeListener();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Edit Server Details");
		setSize(233, 149);
		setPositionToCenter(getParent());
		setResizable(false);

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new GridLayout(0, 2, 0, 5));

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

		contentPanel.add(new JLabel("Server name:"));
		contentPanel.add(nameField);
		contentPanel.add(new JLabel("Ip:"));
		contentPanel.add(ipField);
		contentPanel.add(new JLabel("Port:"));
		contentPanel.add(portField);

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
					listener.finished(new ServerInfo(nameField.getText(), ipField.getText(), Integer.parseInt(portField.getText())));
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

			if (nameField.getText().isEmpty() || nameField.getText().contains(":")) lockOkButton();
			if (isInteger(portField.getText()) == false) lockOkButton();

			if (ipField.getText().equals("localhost")) return;
			// if (ipField.getText().startsWith("http")) return; //TODO proper support for url adresses
			if (InetAddressValidator.getInstance().isValidInet4Address(ipField.getText()) == false) lockOkButton();
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

interface CreateServerDialogFinished {
	public void finished (ServerInfo desc);
}
