package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileTransferCancelNotification extends FileTransferExchange{

	@Deprecated
	public FileTransferCancelNotification () {
		super(null, null);
	}

	public FileTransferCancelNotification (UUID sessionId, UUID taskId) {
		super(sessionId, taskId);
	}

}
