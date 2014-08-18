
package pl.kotcrab.arget;

import java.util.prefs.Preferences;

public class Settings {
	private static Preferences prefs;

	public static final String autoLoginProfileNamePref = "autologinprofilename";
	public static String autoLoginProfileName;

	public static void init () {
		prefs = Preferences.userNodeForPackage(Settings.class);
		autoLoginProfileName = prefs.get(autoLoginProfileNamePref, "");
	}

	public static void save () {
		prefs.put(autoLoginProfileNamePref, autoLoginProfileName);
	}

	public static void resetAutoLogin () {
		autoLoginProfileName = "";
		save();
	}

}
