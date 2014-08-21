
package pl.kotcrab.arget.comm.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.Msg;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileAcceptedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileDataBlockReceivedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileDataBlockTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileRejectedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferCancelNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferToFileRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferToMemoryRequest;
import pl.kotcrab.arget.comm.file.FileTransferTask.Status;
import pl.kotcrab.arget.global.gui.MainWindow;
import pl.kotcrab.arget.global.session.LocalSession;
import pl.kotcrab.arget.global.session.LocalSessionManager;
import pl.kotcrab.arget.global.session.gui.FileTransferMessage;
import pl.kotcrab.arget.global.session.gui.FileTransferMessageAdapter;
import pl.kotcrab.arget.global.session.gui.FileTransferMessageListener;
import pl.kotcrab.arget.global.session.gui.ImageMessage;
import pl.kotcrab.arget.global.session.gui.SessionWindowManager;
import pl.kotcrab.arget.util.FileUitls;
import pl.kotcrab.arget.util.ImageUitls;
import pl.kotcrab.arget.util.ThreadUtils;

//TODO przewrsjki przesylanie gdy koniec sessji
//TODO gdy osoba ktora przesyla anuluje u celu pokazuje sie komunikat done zamist canceled
//TODO remove illegal character from filename, for example : is allowed on linux, disallowed on windows
//TODO huge cpu usage if sender cancels file
public class FileTransferManager {
	private static final String TAG = "FileTransfer";

	private LocalSessionManager sessionManager;
	private SessionWindowManager windowManager;

	private boolean running = true;

	private Thread fileSender;
	private List<SendFileTask> sendTasks;
	private List<SendFileTask> sendTasksToRemove;
	private List<ReceiveFileTask> receiveTasks;

	private List<FileTransferMessage> guiMessages;

	public FileTransferManager (LocalSessionManager lSessionManager, SessionWindowManager sWindowManager) {
		this.sessionManager = lSessionManager;
		this.windowManager = sWindowManager;

		receiveTasks = new ArrayList<ReceiveFileTask>();
		sendTasks = Collections.synchronizedList(new ArrayList<SendFileTask>());
		sendTasksToRemove = new ArrayList<SendFileTask>();

		guiMessages = new ArrayList<FileTransferMessage>();

		fileSender = new Thread(new Runnable() {

			@Override
			public void run () {

				while (running) {
					if (sendTasks.size() == 0 || allTaskIdle()) {
						ThreadUtils.sleep(1000);
						continue;
					}

					for (final SendFileTask task : sendTasks) {

						if (task.getStatus() == Status.IDLE && task.isRequestSend() == false) {
							LocalSession session = task.getSession();
							File file = task.getFile();

							if (task.isToMemory())
								windowManager.addMessage(session, new ImageMessage(Msg.RIGHT, ImageUitls.read(file), null));

							sendFileTransferRequest(task);
						}

						if (task.getStatus() == Status.INPROGRESS) {
							if (task.isReadyToSendNextBlock()) { // TODO optimize this, if all task all waiting this will do empty loops
								byte[] nextBlock = task.getNextBlock();
								if (nextBlock != null) {
									send(new FileDataBlockTransfer(task.getSession().id, task.getId(), nextBlock));
								}
							}

							setMessageProgress(task);
						}

						if (task.getStatus() == Status.DONE) {
							if (task.isCanceled() == false)
								send(new FileTransferFinishedNotification(task.getSession().id, task.getId()));

							sendTasksToRemove.add(task);
						}

					}

					sendTasks.removeAll(sendTasksToRemove);

				}
			}

			private boolean allTaskIdle () {
				for (final SendFileTask task : sendTasks) {
					if (task.getStatus() != Status.IDLE) return false;
					if (task.isRequestSend() == false) return false;
				}

				return true;
			}

		}, "FileSender");
		fileSender.start();
	}

	public void update (final LocalSession session, FileTransferExchange ex) {
		if (ex instanceof FileTransferToMemoryRequest) {
			FileTransferToMemoryRequest req = (FileTransferToMemoryRequest)ex;

			final ReceiveFileToMemoryTask task = new ReceiveFileToMemoryTask(req.fileSize, session, req.taskId, req.fileName);
			task.begin();
			receiveTasks.add(task);

			task.setListener(new FileTransferListener() {

				@Override
				public void operationFinished (FileTransferTask notNeeded) {
					byte[] data = task.getData();

					// TODO check mimetype, chyba nie potrzebne
					BufferedImage image = ImageUitls.read(data);
					if (image != null)
						windowManager.addMessage(task.getSession(), new ImageMessage(Msg.LEFT, image, task.getFileName()));
					else
						Log.err(TAG, "Received image data but program was unable to build image form it.");
				}
			});

			send(new FileAcceptedNotification(req.id, req.taskId));
			return;
		}

		if (ex instanceof FileTransferToFileRequest) {
			final FileTransferToFileRequest req = (FileTransferToFileRequest)ex;

			final ReceiveFileToFileTask task = new ReceiveFileToFileTask(req.fileSize, session, req.taskId, req.fileName);
			receiveTasks.add(task);

			task.setListener(new FileTransferListener() {

				@Override
				public void operationFinished (FileTransferTask notNeeded) {

				}
			});

			final FileTransferMessage guiMsg = new FileTransferMessage(task, req.fileName, req.fileSize);

			guiMsg.setListener(new FileTransferMessageListener() {

				@Override
				public void buttonFileAccepted (FileTransferTask task) {
					task.begin();
					send(new FileAcceptedNotification(req.id, req.taskId));
					setMessageStatus(task, FileTransferMessage.Status.INPROGRESS);
				}

				@Override
				public void buttonFileRejectedOrCanceled (FileTransferTask task) {
					send(new FileTransferCancelNotification(session.id, task.getId()));
					if (task.getStatus() == Status.INPROGRESS) task.cancel();
					setMessageStatus(task, FileTransferMessage.Status.CANCELED);
				}

			});

			guiMessages.add(guiMsg);
			windowManager.addMessage(task.getSession(), guiMsg);
			guiMsg.setStatus(FileTransferMessage.Status.REQUEST_RECEIVED);

			return;
		}

		if (ex instanceof FileAcceptedNotification) {
			SendFileTask task = getSendTaskByUUID(ex.taskId);
			if (task != null) task.begin();

			setMessageStatus(task, FileTransferMessage.Status.INPROGRESS);

			return;
		}

		if (ex instanceof FileRejectedNotification) {
			SendFileTask task = getSendTaskByUUID(ex.taskId);
			if (task != null) task.cancel();
			setMessageStatus(task, FileTransferMessage.Status.CANCELED);

		}

		if (ex instanceof FileTransferCancelNotification) {
			FileTransferTask task = getTaskByUUID(ex.taskId);
			if (task != null && task.getStatus() == Status.INPROGRESS) task.cancel();
			setMessageStatus(task, FileTransferMessage.Status.CANCELED);

		}

		if (ex instanceof FileTransferFinishedNotification) {
			ReceiveFileTask task = getReceiveTaskByUUID(ex.taskId);
			if (task != null && task.getStatus() == Status.INPROGRESS) task.finish();
			receiveTasks.remove(task);

			setMessageStatus(task, FileTransferMessage.Status.DONE);
		}

		if (ex instanceof FileDataBlockTransfer) {
			FileDataBlockTransfer transfer = (FileDataBlockTransfer)ex;

			ReceiveFileTask task = getReceiveTaskByUUID(transfer.taskId);
			if (task != null) task.saveNextBlock(transfer.block);

			if (task instanceof ReceiveFileToFileTask)
				getMessageByUUID(transfer.taskId).setProgressBarValue((int)task.getPercentProgress());

			if (task.isBlockOkShouldBeSend()) {
				send(new FileDataBlockReceivedNotification(session.id, task.getId()));
				task.setBlockOkShouldBeSend(false);
			}
		}

		if (ex instanceof FileDataBlockReceivedNotification) setTaskReadyToSendNextBlock(ex.taskId);
	}

	private void send (FileTransferExchange exchange) {
		sessionManager.sendLater(exchange);
	}

	public void sendFile (LocalSession session, File file) {
		if (file.isDirectory() || file.exists() == false || file.canRead() == false) {
			JOptionPane.showMessageDialog(MainWindow.instance, "Invalid file.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// send as file to memory or to normal file
		if (file.length() < ReceiveFileToMemoryTask.MAX_SIZE && FileUitls.isImage(file))
			sendTasks.add(new SendFileTask(session, file, true));
		else
			sendTasks.add(new SendFileTask(session, file, false));

	}

	public void sendFileTransferRequest (SendFileTask task) {
		File file = task.getFile();

		if (task.isToMemory())
			send(new FileTransferToMemoryRequest(task.getSession().id, task.getId(), file.getName(), file.length()));
		else
			send(new FileTransferToFileRequest(task.getSession().id, task.getId(), file.getName(), file.length()));

		task.setRequestSend(true);
		if (task.isToMemory() == false) createGUIMessage(task);
	}

	private void createGUIMessage (SendFileTask task) {
		FileTransferMessage guiMsg = new FileTransferMessage(task);

		guiMsg.setListener(new FileTransferMessageAdapter() {

			@Override
			public void buttonFileRejectedOrCanceled (FileTransferTask task) {
				send(new FileTransferCancelNotification(task.getSession().id, task.getId()));
				if (task.getStatus() == Status.INPROGRESS) task.cancel();
				setMessageStatus(task, FileTransferMessage.Status.CANCELED);
			}

		});

		guiMessages.add(guiMsg);
		windowManager.addMessage(task.getSession(), guiMsg);
		guiMsg.setStatus(FileTransferMessage.Status.REQUEST_SEND);

		task.setListener(new FileTransferListener() {
			@Override
			public void operationFinished (FileTransferTask task) {
				setMessageStatus(task, FileTransferMessage.Status.DONE);
			}
		});
	}

	private FileTransferTask getTaskByUUID (UUID id) {
		FileTransferTask task = getSendTaskByUUID(id);
		if (task == null)
			return getReceiveTaskByUUID(id);
		else
			return task;
	}

	private SendFileTask getSendTaskByUUID (UUID id) {
		for (SendFileTask task : sendTasks)
			if (task.getId().compareTo(id) == 0) return task;

		return null;
	}

	private ReceiveFileTask getReceiveTaskByUUID (UUID id) {
		for (ReceiveFileTask task : receiveTasks)
			if (task.getId().compareTo(id) == 0) return task;

		return null;
	}

	private FileTransferMessage getMessageByUUID (UUID id) {
		for (FileTransferMessage msg : guiMessages)
			if (msg.getTaskUUID().compareTo(id) == 0) return msg;

		return null;
	}

	private void setMessageStatus (FileTransferTask task, FileTransferMessage.Status status) {
		if (task == null) return;
		FileTransferMessage msg = getMessageByUUID(task.getId());
		if (msg != null) msg.setStatus(status);
	}

	private void setMessageProgress (FileTransferTask task) {
		FileTransferMessage msg = getMessageByUUID(task.getId());
		if (msg != null) msg.setProgressBarValue(task.getPercentProgress());
	}

	private void setTaskReadyToSendNextBlock (UUID id) {
		SendFileTask task = getSendTaskByUUID(id);
		if (task != null) task.setReadyToSendNextBlock();
	}

	public void stop () {
		running = false;
		// TODO finish all left tasks
		sendTasks.clear();
		receiveTasks.clear();

	}

}
