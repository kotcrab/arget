
package pl.kotcrab.arget.global.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.codec.binary.Base64;

import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.gui.ESCClosableDialog;
import pl.kotcrab.arget.gui.components.DocumentFieldsChangeListener;

public class CreateContactDialog extends ESCClosableDialog {
	private String profilePublicKey;

	private JTextField nickTextField;
	private JButton okButton;
	private JTextArea keyTextArea;

	private FieldsChangeListener changeListener;
	private JLabel statusLabel;

	/** @wbp.parser.constructor */
	public CreateContactDialog (JFrame frame, String profilePublicKey, CreateContactDialogFinished listener) {
		this(frame, profilePublicKey, null, listener);
	}

	public CreateContactDialog (JFrame frame, String profilePublicKey, ContactInfo contact) {
		this(frame, profilePublicKey, contact, null);
	}

	public CreateContactDialog (JFrame frame, String profilePublicKey, final ContactInfo contact,
		final CreateContactDialogFinished listener) {
		super(frame, true);

		this.profilePublicKey = profilePublicKey;

		changeListener = new FieldsChangeListener();

		setTitle("Edit Contact Details");
		setSize(477, 261);
		setMinimumSize(new Dimension(300, 250));
		setPositionToCenter(frame);

		JPanel mainPanel = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		nickTextField = new JTextField();

		keyTextArea = new JTextArea();
		keyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		keyTextArea.setLineWrap(true);

		nickTextField.getDocument().addDocumentListener(changeListener);
		keyTextArea.getDocument().addDocumentListener(changeListener);

		statusLabel = new JLabel("Nickname cannot be empty");
		statusLabel.setForeground(Color.RED);
		statusLabel.setBorder(new EmptyBorder(0, 6, 0, 0));

		JButton cancelButton = new JButton("Cancel");
		okButton = new JButton("OK");
		okButton.setEnabled(false);

		mainPanel.setLayout(new MigLayout("", "[55.00px][][390px,grow]", "[20px][15px][135px,grow][]"));

		mainPanel.add(new JLabel("Nickname:"), "cell 0 0,grow");
		mainPanel.add(new JLabel("Public key:", SwingConstants.CENTER), "cell 0 1 3 1,grow");
		mainPanel.add(nickTextField, "cell 1 0 2 1,grow");
		mainPanel.add(new JScrollPane(keyTextArea), "cell 0 2 3 1,grow");
		mainPanel.add(statusLabel, "cell 0 3 2 1");
		mainPanel.add(cancelButton, "flowx,cell 2 3,alignx right");
		mainPanel.add(okButton, "cell 2 3,alignx right");

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (listener != null) {
					ContactInfo contact = new ContactInfo(nickTextField.getText(), keyTextArea.getText());
					listener.finished(contact);
				}

				if (contact != null) {
					contact.name = nickTextField.getText();
					contact.publicProfileKey = keyTextArea.getText();
				}

				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		if (contact != null) {
			nickTextField.setText(contact.name);
			keyTextArea.setText(contact.publicProfileKey);
		}

		setVisible(true);
	}

	private class FieldsChangeListener extends DocumentFieldsChangeListener {
		@Override
		protected void verify () {

			okButton.setEnabled(true);
			statusLabel.setVisible(false);

			try {
				X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(keyTextArea.getText()));
				KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
				keyFactory.generatePublic(keySpec);
			} catch (GeneralSecurityException e) {
				setLabelError("Invalid public key");
				return;
			}

			if (nickTextField.getText().isEmpty()) {
				setLabelError("Nickname cannot be empty");
				return;
			}

			if (keyTextArea.getText().equals(profilePublicKey)) {
				setLabelError("You cannot add yourself to contact list");
				return;
			}
		}

		private void setLabelError (String text) {
			okButton.setEnabled(false);
			statusLabel.setVisible(true);
			statusLabel.setText(text);
		}
	}

}

interface CreateContactDialogFinished {
	public void finished (ContactInfo contact);
}
