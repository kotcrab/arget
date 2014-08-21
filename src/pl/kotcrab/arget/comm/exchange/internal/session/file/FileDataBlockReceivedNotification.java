package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileDataBlockReceivedNotification extends FileTransferExchange {

	@Deprecated
	public FileDataBlockReceivedNotification () {
		super(null, null);
	}

	public FileDataBlockReceivedNotification (UUID sessionId, UUID taskId) {
		super(sessionId, taskId);
	}

}
