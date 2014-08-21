
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

/** Notifies that session window has been hidden (center panel was changed)
 * @author Pawel Pastuszak */
public class RemotePanelHideNotification extends InternalSessionExchange {
	@Deprecated
	public RemotePanelHideNotification () {
		super(null);
	}

	public RemotePanelHideNotification (UUID id) {
		super(id);
	}
}
