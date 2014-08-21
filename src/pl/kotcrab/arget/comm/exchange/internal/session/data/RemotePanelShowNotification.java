
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;

/** Notifies that session window has been shown (center panel was changed to matching session that send this notification)
 * @author Pawel Pastuszak */
public class RemotePanelShowNotification extends InternalSessionExchange {
	@Deprecated
	public RemotePanelShowNotification () {
		super(null);
	}

	public RemotePanelShowNotification (UUID id) {
		super(id);
	}
}
