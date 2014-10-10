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

import org.apache.commons.lang3.exception.ExceptionUtils;

/** Log utility, log events are redirected to listener and printed to standard output
 * @author Pawel Pastuszak */
public class Log {
	private static final boolean DEBUG = App.DEBUG;
	private static final boolean LOG_INTERRUPTED = DEBUG;

	private static LoggerListener listener = new DefaultLogListener();

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm] ");

	public static void exception (Exception e) {
		e.printStackTrace();
		listener.exception(ExceptionUtils.getStackTrace(e));
	}

	public static void interruptedEx (InterruptedException e) {
		if (LOG_INTERRUPTED) {
			e.printStackTrace();
			listener.exception(ExceptionUtils.getStackTrace(e));
		}
	}

	// ============STANDARD LOGGING============

	public static void l (String msg) {
		println(msg);
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
		print(msg);
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
		l(tag, msg);
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
		listener.log(msg);
		System.out.print(msg);
	}

	private static void println (String msg) {
		msg = getTimestamp() + msg;
		listener.log(msg);
		System.out.println(msg);
	}

	private static void printErr (String msg) {
		msg = getTimestamp() + msg;
		listener.err(msg);
		System.err.print(msg);
	}

	private static void printlnErr (String msg) {
		msg = getTimestamp() + msg;
		listener.err(msg);
		System.err.println(msg);
	}

	public static void setListener (LoggerListener listener) {
		Log.listener = listener;
	}

	private static String getTimestamp () {
		return dateFormat.format(new Date());
	}
}

class DefaultLogListener implements LoggerListener {
	@Override
	public void log (String msg) {
	}

	@Override
	public void err (String msg) {
	}

	@Override
	public void exception (String stacktrace) {
	}
}
