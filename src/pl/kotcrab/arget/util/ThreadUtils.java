
package pl.kotcrab.arget.util;

public class ThreadUtils {
	/** Thread that executed this method will sleep for the specific number of milliseconds. If happen InterruptedException will be
	 * ignored.
	 * @param millis length of time to sleep in milliseconds */
	public static void sleep (long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
}
