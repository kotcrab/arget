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

package pl.kotcrab.arget.test.manual;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.LoggerListener;
import pl.kotcrab.arget.util.ThreadUtils;

public class LoggerTest {
	private static boolean logOk;
	private static boolean errOk;

	public static void main (final String[] args) {
		Log.setListener(new LoggerListener() {

			@Override
			public void log (String msg) {
				logOk = true;
			}

			@Override
			public void err (String msg) {
				errOk = true;
			}

			@Override
			public void exception (String stacktrace) {
				
			}
		});

		Log.l("Normal out test");
		Log.w("Warning out test");
		Log.err("Error test");

		ThreadUtils.sleep(10);

		Log.l("some-lib", "Lib normal out test");
		Log.w("some-lib", "Lib warning out test");
		Log.err("some-lib", "Lib error test");

		if (logOk && errOk)
			System.out.println("Success!");
		else
			System.out.println("Failure!");
	}
}
