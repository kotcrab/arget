
package pl.kotcrab.arget;

import java.io.IOException;

/** Interface for listening for exception. Usable if 'throws' cannot be used eg. exception happened in different thread.
 * @author Pawel Pastuszak */
public interface ExceptionListener {
	public void ioException (IOException e);
}
