
package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileAcceptedNotification extends FileTransferExchange {

	@Deprecated
	public FileAcceptedNotification () {
		super(null, null);
	}

	public FileAcceptedNotification (UUID sessionId, UUID taskId) {
		super(sessionId, taskId);
	}

}
