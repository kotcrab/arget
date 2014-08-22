
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
