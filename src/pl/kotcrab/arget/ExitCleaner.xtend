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
package pl.kotcrab.arget

import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Set
import javax.swing.JOptionPane
import pl.kotcrab.arget.util.DesktopUtils
import pl.kotcrab.arget.util.ThreadUtils

class ExitCleaner {
	def static void forceExit() {
		val t = new Thread(
			[
				ThreadUtils.sleep(10000)
				writeLog()
				System.exit(-1)
			], "Force Exit");
		t.setDaemon(true);
		t.start();
	}

	private def static writeLog() {
		val PrintWriter writer = new PrintWriter(
			new FileOutputStream(DesktopUtils.getJarPath + "arget-error-log.txt", true));
		writer.println("Arget had to exit abnormally, please report a bug and send this file with it.");
		writer.println(getDate())
		writer.println();
		writer.println("====Dumping thread info:====")

		val Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for (Thread t : threadSet) {
			if(t.isDaemon) {
				writer.println("Skipping daemon thread: " + t.name);
			} else {
				writer.println("Thread: " + t.name);
				for (StackTraceElement e : t.stackTrace)
					writer.println(e.toString())

			}

			writer.println();
		}

		writer.println("============================");
		writer.println();
		writer.close();

		JOptionPane.showMessageDialog(null,
			'Arget had to exit abnormally, please report a bug and send "arget-error-log.txt" file created in Arget directory.',
			"Error", JOptionPane.ERROR_MESSAGE)
	}

	private def static getDate() {
		val DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		val Date date = new Date();
		return dateFormat.format(date);
	}
}
