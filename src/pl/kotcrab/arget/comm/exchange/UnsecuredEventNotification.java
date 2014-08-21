
package pl.kotcrab.arget.comm.exchange;

/** Notification of a event that don't have to (and probably can't) be encrypted, such as: server is full or server shutting down.
 * @author Pawel Pastuszak */
public class UnsecuredEventNotification implements Exchange {
	public enum Type {
		SERVER_FULL, SERVER_SHUTTING_DOWN, KICKED
	};

	public Type type;

	public UnsecuredEventNotification () {
	}

	public UnsecuredEventNotification (Type type) {
		this.type = type;
	}
}
