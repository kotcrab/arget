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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Settings;
import pl.kotcrab.arget.profile.Profile;
import pl.kotcrab.arget.profile.ProfileGenerator;
import pl.kotcrab.arget.profile.ProfileGeneratorDialogListener;
import pl.kotcrab.arget.profile.ProfileIO;

public class LoginFrame extends JFrame {
	private LoginFrame instnace;

	private JComboBox<String> profilesCombobox;
	private JButton loginButton;
	private JCheckBox autoLoginCheckbox;

	public LoginFrame (String name) {
		tryToLoadProfileForName(name, null);
		return;
	}

	/** Create the frame. */
	public LoginFrame () {
		this.instnace = this;

		if (Settings.autoLoginProfileName.equals("") == false) {
			tryToLoadProfileForName(Settings.autoLoginProfileName, null);
			return;
		}

		setTitle(App.APP_NAME + " " + App.APP_VERSION + " - Login");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 0, 0);
		setIconImage(App.loadImage("/data/icon/icon.png"));

		JPanel mainPanel = new JPanel(new MigLayout("", "[][87.00][grow]", "[][][][]"));
		setContentPane(mainPanel);

		final JPasswordField passwordField = new JPasswordField();
		profilesCombobox = new JComboBox<String>();
		autoLoginCheckbox = new JCheckBox("Login automatically ");
		loginButton = new JButton("Login");
		JButton createProfileButton = new JButton("Create..");
		JButton loadExternalButton = new JButton("Load External");

		mainPanel.add(new JLabel(App.loadImageIcon("/data/banner.png")), "cell 0 0 3 1");
		mainPanel.add(new JLabel("Profile:"), "cell 0 1,alignx left");
		mainPanel.add(new JLabel("Password:"), "cell 0 2,alignx left");
		mainPanel.add(profilesCombobox, "cell 1 1 2 1,growx");
		mainPanel.add(passwordField, "cell 1 2 2 1,growx");
		mainPanel.add(autoLoginCheckbox, "cell 0 3 2 1");
		mainPanel.add(createProfileButton, "flowx,cell 2 3,alignx right");
		mainPanel.add(loadExternalButton, "cell 2 3,alignx right");
		mainPanel.add(loginButton, "cell 2 3,alignx right");

		// -------

		buildProfilesList();

		createProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				ProfileGenerator.genereteViaGUI(new ProfileGeneratorDialogListener() {

					@Override
					public void ok (String name, char[] password) {
						buildProfilesList();
					}
				});
			}
		});

		loadExternalButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				// FIXME implement load external
				JOptionPane.showMessageDialog(instnace, "Feature not available yet!");
			}
		});

		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent evt) {

				Profile profile = tryToLoadProfile(getSelectedProfileName(), passwordField.getPassword());

				if (autoLoginCheckbox.isSelected()) {
					Settings.autoLoginProfileName = getSelectedProfileName();
					Settings.save();
				}

				if (profile != null) {
					createMainWindow(profile);
					dispose();
				}
			}
		});

		profilesCombobox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				checkIfAutoLoginAvaiable();
			}
		});

		pack();
		setVisible(true);

	}

	private void tryToLoadProfileForName (String profileName, char[] password) {
		Profile profile = null;

		try {
			profile = tryToLoadProfile(profileName, password);
		} catch (IllegalStateException e) {
			JOptionPane.showMessageDialog(instnace,
				"Fatal error while logging in. Please restart application. If you are using command line --login, remove it.");
			Settings.resetAutoLogin();
			dispose();
			throw e;
		}

		createMainWindow(profile);
		dispose();
	}

	private Profile tryToLoadProfile (String name, char[] password) {
		try {
			return ProfileIO.loadProfileByName(name, password);
		} catch (IOException e) {
			Settings.resetAutoLogin();
			JOptionPane.showMessageDialog(instnace, "Error while loading profile: " + e.getMessage());
		} catch (GeneralSecurityException e) {
			Settings.resetAutoLogin();
			JOptionPane.showMessageDialog(instnace, "Security error while loading profile: " + e.getMessage()
				+ ". Is password valid?");
			return null;
		}

		throw new IllegalStateException("Fatal error when loading profile.");
	}

	private void checkIfAutoLoginAvaiable () {
		String name = getSelectedProfileName();

		if (name == null) {
			autoLoginCheckbox.setEnabled(false);
			return;
		}

		if (ProfileIO.isProfileEncryptedByName(name))
			autoLoginCheckbox.setEnabled(false);
		else
			autoLoginCheckbox.setEnabled(true);
	}

	private String getSelectedProfileName () {
		Object o = profilesCombobox.getSelectedItem();
		return (o == null) ? null : o.toString();
	}

	private void createMainWindow (final Profile profile) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run () {
				new MainWindow(profile);
			}
		});
	}

	private void buildProfilesList () {
		File[] files = ProfileIO.listProfiles();
		String[] names = new String[files.length];

		for (int i = 0; i < files.length; i++) {
			names[i] = files[i].getName();
		}

		profilesCombobox.setModel(new DefaultComboBoxModel<String>(names));

		if (files.length == 0)
			loginButton.setEnabled(false);
		else
			loginButton.setEnabled(true);

		checkIfAutoLoginAvaiable();
	}

}
