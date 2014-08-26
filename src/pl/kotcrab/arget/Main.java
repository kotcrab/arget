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

package pl.kotcrab.arget;

import java.awt.EventQueue;

import pl.kotcrab.arget.gui.LoginFrame;
import pl.kotcrab.arget.profile.ProfileGenerator;
import pl.kotcrab.arget.server.ArgetServer;

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

		if (args[0].equals("--server")) {
			App.init(false);
			if (args.length == 2)
				new ArgetServer(Integer.parseInt(args[1]));
			else
				new ArgetServer(31415);
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
