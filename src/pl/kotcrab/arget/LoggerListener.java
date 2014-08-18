
package pl.kotcrab.arget;

/** Interface for listening for log events
 * @author Pawel Pastuszak */
public interface LoggerListener {
	public void log (String msg);

	public void err (String msg);
}
