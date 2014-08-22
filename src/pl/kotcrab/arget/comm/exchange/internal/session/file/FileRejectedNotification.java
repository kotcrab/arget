
package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileRejectedNotification extends FileTransferExchange {

	@Deprecated
	public FileRejectedNotification () {
		super(null, null);
	}

	public FileRejectedNotification (UUID sessionId, UUID taskId) {
		super(sessionId, taskId);
	}

}
