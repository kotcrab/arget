
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

/** Notifies that remote started typing message
 * @author Pawel Pastuszak */
public class TypingStartedNotification extends InternalSessionExchange {
	@Deprecated
	public TypingStartedNotification () {
		super(null);
	}

	public TypingStartedNotification (UUID id) {
		super(id);
	}
}
