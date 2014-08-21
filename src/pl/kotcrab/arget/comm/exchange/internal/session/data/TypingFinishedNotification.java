
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

public class TypingFinishedNotification extends InternalSessionExchange {
	@Deprecated
	public TypingFinishedNotification () {
		super(null);
	}

	public TypingFinishedNotification (UUID id) {
		super(id);
	}
}
