
package pl.kotcrab.arget.comm.exchange.internal.session.file;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;

public abstract class FileTransferExchange extends InternalSessionExchange {
	public UUID taskId;
	
	public FileTransferExchange (UUID sessionId, UUID taskId) {
		super(sessionId);
		this.taskId = taskId;
	}

}
