package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileTransferFinishedNotification extends FileTransferExchange{

	@Deprecated
	public FileTransferFinishedNotification () {
		super(null, null);
	}

	public FileTransferFinishedNotification (UUID sessionId, UUID taskId) {
		super(sessionId, taskId);

	}

}
