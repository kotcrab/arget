
package pl.kotcrab.arget.global.session.gui;

import pl.kotcrab.arget.comm.file.FileTransferTask;

public interface FileTransferMessageListener {
	public void buttonFileAccepted (FileTransferTask task);

	public void buttonFileRejectedOrCanceled (FileTransferTask task);
}
