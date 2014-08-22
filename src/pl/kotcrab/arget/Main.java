
package pl.kotcrab.arget;

import java.awt.EventQueue;

import pl.kotcrab.arget.global.GlobalServer;
import pl.kotcrab.arget.global.gui.LoginFrame;
import pl.kotcrab.arget.profile.ProfileGenerator;

public class Main {
	public static void main (final String[] args) {

		if (args.length == 0) {
			App.init();

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run () {
					new LoginFrame();
				}
			});

			return;
		}

		if (args[0].equals("--login") && args.length == 2) {
			App.init();

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run () {
					new LoginFrame(args[1]);
				}
			});

			return;
		}

		if (args[0].equals("--global")) {
			App.init(false);
			if (args.length == 2)
				new GlobalServer(Integer.parseInt(args[1]));
			else
				new GlobalServer(31415);
			return;
		}

		if (args[0].equals("--profile-gen")) {
			App.init(false);
			ProfileGenerator.genereteViaConsole();
			return;
		}
		if (args[0].equals("--profile-gen-gui")) {
			App.init();
			ProfileGenerator.genereteViaGUI(null);
			return;
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				System.err.println("Unrecognized option: " + args[i]);
				return;
			}
		}

	}
}
