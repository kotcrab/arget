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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.FieldVerifcationException;
import pl.kotcrab.arget.event.SaveProfileEvent;
import pl.kotcrab.arget.gui.components.DocumentFieldsChangeListener;
import pl.kotcrab.arget.gui.components.ESCClosableDialog;
import pl.kotcrab.arget.profile.Profile;
import pl.kotcrab.arget.profile.ProfileOptions;

//TODO colors changing
public class OptionsDialog extends ESCClosableDialog {

	private Profile profile;

	private JTabbedPane tabbedPane;

	// main
	private JCheckBox cmPlaySoundNewMsg;

	// notifications
	private JCheckBox cnConnectionLost;
	private JCheckBox cnUserOnline;
	private JCheckBox cnUserOffline;
	private JCheckBox cnNewMsg;
	private JCheckBox cnImageFileTrasnfer;
	private JCheckBox cnFileTrasnfer;

	// news server
	private JCheckBox csDownalodNews;
	private JTextField adressTextfield;
	private JTextField portTextfield;
	private JButton okButton;

	private DocumentFieldsChangeListener documentListener;

	public OptionsDialog (JFrame owner, Profile profile) {
		super(owner, true);

		this.profile = profile;

		setSize(387, 280);
		setPositionToCenter(owner);
		setTitle(App.APP_NAME + " - Options");

		JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setFocusable(false);
		contentPanel.add(tabbedPane);

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton cancelButton = new JButton("Cancel");
		okButton = new JButton("OK");

		buttonPane.add(cancelButton);
		buttonPane.add(okButton);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				fillProfileOptions();
				App.eventBus.post(new SaveProfileEvent());
				dispose();
			}
		});

		createOptionsTabs();

		fillOptionsFromProfile();
		setNewsPanel();

		documentListener = new DocumentFieldsChangeListener() {

			@Override
			protected void verify () {
				okButton.setEnabled(true);

				try {
					if (csDownalodNews.isSelected()) {
						if (adressTextfield.getText().length() == 0) throw new FieldVerifcationException();
						Integer.parseInt(portTextfield.getText());
					}
				} catch (NumberFormatException | FieldVerifcationException e) {
					okButton.setEnabled(false);
				}
			}
		};
		
		addDocumentListener();

		setVisible(true);
	}

	private void fillOptionsFromProfile () {
		ProfileOptions o = profile.options;

		cmPlaySoundNewMsg.setSelected(o.mainPlaySoundNewMsg);

		cnConnectionLost.setSelected(o.notifConnectionLost);
		cnUserOnline.setSelected(o.notifUserOnline);
		cnUserOffline.setSelected(o.notifUserOffline);
		cnNewMsg.setSelected(o.notifNewMsg);
		cnImageFileTrasnfer.setSelected(o.notifImageFileTrasnfer);
		cnFileTrasnfer.setSelected(o.notifFileTrasnfer);

		csDownalodNews.setSelected(o.newsEnabled);
		adressTextfield.setText(o.newsAdress);
		portTextfield.setText(String.valueOf(o.newsPort));
	}

	private void fillProfileOptions () {
		ProfileOptions o = profile.options;

		o.mainPlaySoundNewMsg = cmPlaySoundNewMsg.isSelected();

		o.notifConnectionLost = cnConnectionLost.isSelected();
		o.notifUserOnline = cnUserOnline.isSelected();
		o.notifUserOffline = cnUserOffline.isSelected();
		o.notifNewMsg = cnNewMsg.isSelected();
		o.notifImageFileTrasnfer = cnImageFileTrasnfer.isSelected();
		o.notifFileTrasnfer = cnFileTrasnfer.isSelected();

		o.newsEnabled = csDownalodNews.isSelected();
		o.newsAdress = adressTextfield.getText();
		o.newsPort = Integer.parseInt(portTextfield.getText());
	}

	private void createOptionsTabs () {
		{
			JPanel mainPanel = new JPanel();
			tabbedPane.addTab("Main", null, mainPanel, null);
			mainPanel.setLayout(new MigLayout("", "[100px,grow]", "[16px]"));

			cmPlaySoundNewMsg = new JCheckBox("Play sound on new message");
			mainPanel.add(cmPlaySoundNewMsg, "cell 0 0,alignx left,aligny top");
		}

		{
			JPanel notificationsPanel = new JPanel();
			tabbedPane.addTab("Notifications", null, notificationsPanel, null);
			notificationsPanel.setLayout(new MigLayout("", "[grow]", "[][][][][][]"));

			cnConnectionLost = new JCheckBox("Connection to server lost");
			cnUserOnline = new JCheckBox("User is online");
			cnUserOffline = new JCheckBox("User is offline");
			cnNewMsg = new JCheckBox("New message");
			cnImageFileTrasnfer = new JCheckBox("Image file transfer");
			cnFileTrasnfer = new JCheckBox("File transfer");

			notificationsPanel.add(cnConnectionLost, "cell 0 0");
			notificationsPanel.add(cnUserOnline, "cell 0 1");
			notificationsPanel.add(cnUserOffline, "cell 0 2");
			notificationsPanel.add(cnNewMsg, "cell 0 3");
			notificationsPanel.add(cnImageFileTrasnfer, "cell 0 4");
			notificationsPanel.add(cnFileTrasnfer, "cell 0 5");
		}

		{
			JPanel newsPanel = new JPanel();
			tabbedPane.addTab("News server", null, newsPanel, null);
			csDownalodNews = new JCheckBox("Download news from server");
			adressTextfield = new JTextField();
			portTextfield = new JTextField();
			adressTextfield.setColumns(10);
			portTextfield.setColumns(10);

			newsPanel.setLayout(new MigLayout("", "[][grow]", "[][][]"));
			newsPanel.add(csDownalodNews, "cell 0 0 2 1");
			newsPanel.add(adressTextfield, "cell 1 1,growx");
			newsPanel.add(new JLabel("Address:"), "cell 0 1,alignx left");
			newsPanel.add(new JLabel("Port:"), "cell 0 2,alignx left");
			newsPanel.add(portTextfield, "cell 1 2,growx");

			csDownalodNews.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					setNewsPanel();
				}
			});
		}
	}
	
	private void addDocumentListener () {
		portTextfield.getDocument().addDocumentListener(documentListener);
		adressTextfield.getDocument().addDocumentListener(documentListener);		
	}
	
	private void setNewsPanel () {
		if (csDownalodNews.isSelected()) {
			adressTextfield.setEnabled(true);
			portTextfield.setEnabled(true);
		} else {
			adressTextfield.setEnabled(false);
			portTextfield.setEnabled(false);

		}					
	}
}
