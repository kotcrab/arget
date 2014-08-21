
package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileTransferToFileRequest extends FileTransferExchange {
	public String fileName;
	public long fileSize;

	@Deprecated
	public FileTransferToFileRequest () {
		super(null, null);
	}

	public FileTransferToFileRequest (UUID sessionId, UUID taskId, String fileName, long fileSize) {
		super(sessionId, taskId);
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

}
