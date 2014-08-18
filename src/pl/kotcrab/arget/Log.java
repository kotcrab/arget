
package pl.kotcrab.arget;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Log utility, log events are redirected to listener and printed to standard output
 * @author Pawel Pastuszak */
public class Log {
	private static final String TAG = "Log";
	private static final boolean DEBUG = App.DEBUG;

	private static boolean silentMode = false;

	private static LoggerListener listener;

	private static PrintStream standardError;
	private static PrintStream standardOutput;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm] ");

	private static final char NO_NEWLINE_CONTROL_CHARACTER = '|';

	public static void init () {
		standardError = System.err;

		System.setErr(new PrintStream(new OutputStream() {
			private StringBuilder line = new StringBuilder();

			@Override
			public void write (int b) throws IOException {
				if (b == '\n' || b == NO_NEWLINE_CONTROL_CHARACTER) {
					String s = line.toString();
					line.setLength(0);
					s = getTimestamp() + s;
					if (listener != null) listener.err(s);

					if (b == '\n') standardError.println(s);
					if (b == NO_NEWLINE_CONTROL_CHARACTER) standardError.print(s);
				} else if (b != '\r') {
					line.append((char)b);
				}
			}
		}));

		standardOutput = System.out;

		System.setOut(new PrintStream(new OutputStream() {
			private StringBuilder line = new StringBuilder();

			@Override
			public void write (int b) throws IOException {
				if (b == '\n' || b == NO_NEWLINE_CONTROL_CHARACTER) {
					String s = line.toString();
					line.setLength(0);
					s = getTimestamp() + s;
					if (listener != null) listener.log(s);

					if (b == '\n') standardOutput.println(s);
					if (b == NO_NEWLINE_CONTROL_CHARACTER) standardOutput.print(s);
				} else if (b != '\r') {
					line.append((char)b);
				}
			}
		}));
	}

	// ============STANDARD LOGGING============

	// TODO check call hierarchy and add tags

	public static void l (String msg) {
		if (silentMode == false) System.out.println(msg);
	}

	public static void w (String msg) {
		System.out.println("WARNING: " + msg);
	}

	public static void debug (String msg) {
		if (DEBUG) System.out.println("DEBUG: " + msg);
	}

	public static void err (String msg) {
		System.err.println("ERROR: " + msg);
	}

	// ============LOGGING WITHOUT NEW LINE============

	public static void lNnl (String msg) {
		if (silentMode == false) System.out.print(msg + NO_NEWLINE_CONTROL_CHARACTER);
	}

	public static void wNnl (String msg) {
		System.out.print("WARNING: " + msg + NO_NEWLINE_CONTROL_CHARACTER);
	}

	public static void debugNnl (String msg) {
		if (DEBUG) System.out.print("DEBUG: " + msg + NO_NEWLINE_CONTROL_CHARACTER);
	}

	public static void eNnl (String msg) {
		System.err.print("ERROR: " + msg + NO_NEWLINE_CONTROL_CHARACTER);
	}

	// ============LOGGING WITH TAG============

	public static void l (String tag, String msg) {
		l(tag, msg, false);
	}

	public static void l (String tag, String msg, boolean silentOverride) {
		if (silentMode == false || silentOverride) System.out.println("[" + tag + "] " + msg);
	}

	public static void w (String tag, String msg) {
		System.out.println("[" + tag + "] " + "WARNING: " + msg);
	}

	public static void debug (String tag, String msg) {
		if (DEBUG) System.out.println("[" + tag + "] " + "DEBUG: " + msg);
	}

	public static void err (String tag, String msg) {
		System.err.println("[" + tag + "] " + "ERROR: " + msg);
	}

	public static void setListener (LoggerListener listener) {
		Log.listener = listener;
	}

	public static boolean isSilentMode () {
		return silentMode;
	}

	public static void setSilentMode (boolean silentMode) {
		if (silentMode) l(TAG, "Silent mode enabled"); // this must be done before setting silent
		Log.silentMode = silentMode;
		if (!silentMode) l(TAG, "Silent mode disabled"); // this must be done after disabling silent
	}

	private static String getTimestamp () {
		return dateFormat.format(new Date());
	}

}
