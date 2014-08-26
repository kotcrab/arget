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

import java.text.SimpleDateFormat;
import java.util.Date;

/** Log utility, log events are redirected to listener and printed to standard output
 * @author Pawel Pastuszak */
// TODO remove silent
public class Log {
	private static final String TAG = "Log";
	private static final boolean DEBUG = App.DEBUG;

	private static boolean silentMode = false;

	private static LoggerListener listener;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm] ");

	// ============STANDARD LOGGING============

	// TODO check call hierarchy and add tags

	public static void l (String msg) {
		if (silentMode == false) println(msg);
	}

	public static void w (String msg) {
		println("WARNING: " + msg);
	}

	public static void debug (String msg) {
		if (DEBUG) println("DEBUG: " + msg);
	}

	public static void err (String msg) {
		printlnErr("ERROR: " + msg);
	}

	// ============LOGGING WITHOUT NEW LINE============

	public static void lNnl (String msg) {
		if (silentMode == false) print(msg);
	}

	public static void wNnl (String msg) {
		print("WARNING: " + msg);
	}

	public static void debugNnl (String msg) {
		if (DEBUG) print("DEBUG: " + msg);
	}

	public static void eNnl (String msg) {
		printErr("ERROR: " + msg);
	}

	// ============LOGGING WITH TAG============

	public static void l (String tag, String msg) {
		l(tag, msg, false);
	}

	public static void l (String tag, String msg, boolean silentOverride) {
		if (silentMode == false || silentOverride) println("[" + tag + "] " + msg);
	}

	public static void w (String tag, String msg) {
		println("[" + tag + "] " + "WARNING: " + msg);
	}

	public static void debug (String tag, String msg) {
		if (DEBUG) println("[" + tag + "] " + "DEBUG: " + msg);
	}

	public static void err (String tag, String msg) {
		printlnErr("[" + tag + "] " + "ERROR: " + msg);
	}

	// ==========================================

	private static void print (String msg) {
		msg = getTimestamp() + msg;
		if (listener != null) listener.log(msg);
		System.out.print(msg);
	}

	private static void println (String msg) {
		msg = getTimestamp() + msg;
		if (listener != null) listener.log(msg);
		System.out.println(msg);
	}

	private static void printErr (String msg) {
		msg = getTimestamp() + msg;
		if (listener != null) listener.err(msg);
		System.err.print(msg);
	}

	private static void printlnErr (String msg) {
		msg = getTimestamp() + msg;
		if (listener != null) listener.err(msg);
		System.err.println(msg);
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
