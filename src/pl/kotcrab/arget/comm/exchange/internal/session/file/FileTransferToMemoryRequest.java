
package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileTransferToMemoryRequest extends FileTransferExchange {
	public String fileName;
	public long fileSize;

	@Deprecated
	public FileTransferToMemoryRequest () {
		super(null, null);
	}

	public FileTransferToMemoryRequest (UUID sessionId, UUID taskId, String fileName, long fileSize) {
		super(sessionId, taskId);
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

}
