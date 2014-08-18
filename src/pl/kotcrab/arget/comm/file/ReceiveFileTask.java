
package pl.kotcrab.arget.comm.file;

import java.util.UUID;

import pl.kotcrab.arget.global.session.LocalSession;

public abstract class ReceiveFileTask extends FileTransferTask {

	private boolean blockOkShouldBeSend = false;

	public ReceiveFileTask (Type type, LocalSession session, UUID taskId) {
		super(type, session, taskId);
	}

	public void saveNextBlock (String blockBase64) {
		blockCounter++;

		if (blockCounter >= BLOCKS_IN_BATCH) blockOkShouldBeSend = true;
	}

	public abstract byte[] getData ();

	public boolean isBlockOkShouldBeSend () {
		return blockOkShouldBeSend;
	}

	public void setBlockOkShouldBeSend (boolean blockOkShouldBeSend) {
		this.blockOkShouldBeSend = blockOkShouldBeSend;
		blockCounter = 0;
	}

}
