
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

public class RemotePanelHideNotification extends InternalSessionExchange {
	@Deprecated
	public RemotePanelHideNotification () {
		super(null);
	}

	public RemotePanelHideNotification (UUID id) {
		super(id);
	}
}
