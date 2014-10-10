package pl.kotcrab.arget

import java.awt.EventQueue
import java.util.Arrays
import pl.kotcrab.arget.gui.LoginFrame
import pl.kotcrab.arget.profile.ProfileGenerator
import pl.kotcrab.arget.server.ArgetServer

class Main {
	static String[] args

	def static void main(String[] args) {
		Main.args = args

		if (args.length == 0) {
			App.init()
			EventQueue.invokeLater[new LoginFrame()]
			return
		}

		if (isMatch("help", "h", 0)) {
			println("Arget " + App.APP_VERSION)
			println()
			println("Usage:")
			println("--help, -h                     print this message")
			println("--login, -l <profile-name>     skips login prompt and automaticly login to profile with")
			println("                               provided name, profile must be not encrypted")
			println("--server, -s (port)            start server with provided port, if port is not")
			println("                               provided 31415 is used")
			println("--profile-gen, -pg             starts profile generator in no gui mode")
			println("--profile-gen-gui, -pgg        generates profile using gui creator")
			return
		}

		if (isMatch("login", "l", 1)) {
			App.init()
			EventQueue.invokeLater[new LoginFrame(args.get(1))]
			return
		}

		if (isMatch("server", "s", 1, true)) {
			App.init(false);
			if (argsCount == 1)
				new ArgetServer(Integer.parseInt(args.get(1)))
			else
				new ArgetServer(31415)
			return;
		}
		
		if (isMatch("profile-gen", "pg")) {
			App.init(false)
			ProfileGenerator.genereteViaConsole()
			return
		}

		if (isMatch("profile-gen-gui", "pgg")) {
			App.init()
			ProfileGenerator.genereteViaGUI(null)
			return
		}

		System.err.println("Failed to parse command line options: " + Arrays.toString(args))
	}

	def private static int getArgsCount() {
		return args.length - 1
	}

	def private static boolean isMatch(String longName, String shortName, int cmdArgCount, boolean isArgOptional) {
		if (args.get(0) == "--" + longName || args.get(0) == "-" + shortName) {
			if (isArgOptional) return true

			if (cmdArgCount == getArgsCount())
				return true
			else
				System.err.println("Missing argument for command: '" + longName + "', run with -h to see help")
		}
		
		return false
	}

	def private static boolean isMatch(String longName, String shortName, int argCount) {
		isMatch(longName, shortName, argCount, false)
	}

	def private static boolean isMatch(String longName, String shortName) {
		isMatch(longName, shortName, 0, true)
	}
}
