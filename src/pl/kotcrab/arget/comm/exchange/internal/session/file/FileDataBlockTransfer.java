
package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

public class FileDataBlockTransfer extends FileTransferExchange {
	public byte[] block;

	@Deprecated
	public FileDataBlockTransfer () {
		super(null, null);
	}

	public FileDataBlockTransfer (UUID sessionId, UUID taskId, byte[] block) {
		super(sessionId, taskId);
		this.block = block;
	}

}
