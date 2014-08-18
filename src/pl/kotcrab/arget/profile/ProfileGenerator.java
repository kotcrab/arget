
package pl.kotcrab.arget.profile;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Log;
import pl.kotcrab.crypto.CryptoUtils;

public class ProfileGenerator {
	private static final String FINISHED_MSG = "Profile generated in your user folder, name of the file is: '%s'. \nIf you want you can move this file to external location. "
		+ "Then when you launch " + App.APP_NAME + " you can use 'Load External' to load profile from non-standard location.";
	private static final String FINISHED_MSG_SHORT = "Profile '%s' generated.";

	private static final String CONTINUE_OR_CANCEL_MSG = "Press ENTER to continue, exit to cancel.";
	private static final String INVALID_PROFILE_NAME_MSG = "'%s' is not valid profile name! (name already in use or invalid name in system)";

	/** @param listener finished listener, WILL NOT RECEIVE PROFILE PASSWORD IF WAS ENETERED. May be null. */
	public static void genereteViaGUI (final ProfileGeneratorDialogListener listener) {
		new ProfileGeneratorDialog(new ProfileGeneratorDialogListener() {

			@Override
			public void ok (String name, char[] password) {

				try {
					Profile profile = ProfileIO.generateEmptyProfile();
					ProfileIO.saveProfile(profile, name, password);
					CryptoUtils.fillZeros(password);
					JOptionPane.showMessageDialog(null, getFinishShortMsg(name));
					if (listener != null) listener.ok(name, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
	}

	public static void genereteViaConsole () {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(App.APP_VERSION + " " + App.APP_VERSION + " Profile Generator");

			String name;
			do {
				Log.lNnl("Profile name: ");
				name = br.readLine();
			} while (isValidName(name) == false);

			if (isConsoleAvailable() == false)
				Log.w("Console interface not available using insecure password input method! If possible use GUI generator, run with --profile-gen-gui");

			Log.lNnl("Profile password (may be blank): ");

			char[] password = readPassword(br);

			if (password.length == 0)
				System.out.println("Generate unencrypted profile: '" + name + "'? " + CONTINUE_OR_CANCEL_MSG);
			else
				System.out.println("Generate encrypted profile: '" + name + "'? " + CONTINUE_OR_CANCEL_MSG);

			br.readLine();

			Profile profile = ProfileIO.generateEmptyProfile();
			ProfileIO.saveProfile(profile, name, password);

			CryptoUtils.fillZeros(password);

			System.out.println(getFinishMsg(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isValidName (String name) {
		if (ProfileIO.isValidProfileName(name))
			return true;
		else
			System.out.println(String.format(INVALID_PROFILE_NAME_MSG, name));

		return false;
	}

	private static char[] readPassword (BufferedReader br) throws IOException {
		Console c = System.console();
		if (c == null) {
			return br.readLine().toCharArray(); // console inside IDE is not available;
		} else {
			return c.readPassword(); // if console if available (outside IDE)
		}
	}

	private static boolean isConsoleAvailable () {
		return System.console() != null;
	}

	private static String getFinishMsg (String profileName) {
		return String.format(FINISHED_MSG, profileName);
	}

	private static String getFinishShortMsg (String profileName) {
		return String.format(FINISHED_MSG_SHORT, profileName);
	}
}
