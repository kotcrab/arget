
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

public class RemotePanelShowNotification extends InternalSessionExchange {
	@Deprecated
	public RemotePanelShowNotification () {
		super(null);
	}

	public RemotePanelShowNotification (UUID id) {
		super(id);
	}
}
