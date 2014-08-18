
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
					if (sendTasks.size() == 0) {
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
								String nextBlock = task.getNextBlock();
								if (nextBlock != null) {
									sessionManager.sendEncryptedData(task.getSession(), Msg.FILE_DATA_BLOCK + task.getId()
										+ Msg.FILE_DELIMITER + nextBlock);
								}
							}

							setMessageProgress(task);
						}

						if (task.getStatus() == Status.DONE) {
							if (task.isCanceled() == false)
								sessionManager.sendEncryptedData(task.getSession(), Msg.FILE_FINISHED + task.getId());
							sendTasksToRemove.add(task);
						}

					}

					sendTasks.removeAll(sendTasksToRemove);

				}
			}

		}, "FileSender");
		fileSender.start();
	}

	public void update (LocalSession session, String msg) {
		if (msg.startsWith(Msg.FILE_PREFIX) == false) return;

		String[] data = msg.split(Msg.FILE_DELIMITER_REGEX);
		if (data.length < 2) {
			Log.w("Invalid FileTransferUpdate received!");
			return;
		}

		String command = data[0] + "|";
		UUID taskId = UUID.fromString(data[1]);

		if (command.startsWith(Msg.FILE_TRANSFER_REQUEST_TO_MEMORY)) {
			processRequestToMemory(session, data, taskId);
		}

		if (command.startsWith(Msg.FILE_TRANSFER_REQUEST_TO_FILE)) {
			processRequestToFile(session, data, taskId);
		}

		if (command.startsWith(Msg.FILE_ACCEPTED)) {
			SendFileTask task = getSendTaskByUUID(taskId);
			if (task != null) task.begin();

			setMessageStatus(task, FileTransferMessage.Status.INPROGRESS);

			return;
		}

		if (command.startsWith(Msg.FILE_REJECTED)) {
			SendFileTask task = getSendTaskByUUID(taskId);
			if (task != null) task.cancel();
			setMessageStatus(task, FileTransferMessage.Status.CANCELED);

		}

		if (command.startsWith(Msg.FILE_CANCEL)) {
			FileTransferTask task = getTaskByUUID(taskId);
			if (task != null && task.getStatus() == Status.INPROGRESS) task.cancel();
			setMessageStatus(task, FileTransferMessage.Status.CANCELED);

		}

		if (command.startsWith(Msg.FILE_DATA_BLOCK)) {
			if (data.length == 3) {
				ReceiveFileTask task = getReceiveTaskByUUID(taskId);
				if (task != null) task.saveNextBlock(data[2]);

				if (task instanceof ReceiveFileToFileTask)
					getMessageByUUID(taskId).setProgressBarValue((int)task.getPercentProgress());

				if (task.isBlockOkShouldBeSend()) {
					sessionManager.sendEncryptedData(session, Msg.FILE_DATA_BLOCK_RECEIVED + taskId);
					task.setBlockOkShouldBeSend(false);
				}
			} else
				Log.w("Warning! Received corrupted data block!");
		}

		if (command.startsWith(Msg.FILE_DATA_BLOCK_RECEIVED)) setTaskReadyToSendNextBlock(taskId);

		if (command.startsWith(Msg.FILE_FINISHED)) {
			ReceiveFileTask task = getReceiveTaskByUUID(taskId);
			if (task != null && task.getStatus() == Status.INPROGRESS) task.finish();
			receiveTasks.remove(task);

			setMessageStatus(task, FileTransferMessage.Status.DONE);
		}

	}

	private void processRequestToMemory (LocalSession session, String[] data, UUID taskId) {
		if (checkIfValidRequest(data) == false) return;

		String fileName = data[2];
		int exceptedSize = Integer.parseInt(data[3]);

		final ReceiveFileToMemoryTask task = new ReceiveFileToMemoryTask(exceptedSize, session, taskId, fileName);
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
			}
		});

		sessionManager.sendEncryptedData(session, Msg.FILE_ACCEPTED + taskId);
		return;
	}

	private void processRequestToFile (final LocalSession session, String[] data, UUID taskId) {
		if (checkIfValidRequest(data) == false) return;

		String fileName = data[2];
		int exceptedSize = Integer.parseInt(data[3]);

		final ReceiveFileToFileTask task = new ReceiveFileToFileTask(exceptedSize, session, taskId, fileName);
		receiveTasks.add(task);

		task.setListener(new FileTransferListener() {

			@Override
			public void operationFinished (FileTransferTask notNeeded) {

			}
		});

		final FileTransferMessage guiMsg = new FileTransferMessage(task, fileName, exceptedSize);

		guiMsg.setListener(new FileTransferMessageListener() {

			@Override
			public void buttonFileAccepted (FileTransferTask task) {
				task.begin();
				sessionManager.sendEncryptedData(session, Msg.FILE_ACCEPTED + task.getId());
				setMessageStatus(task, FileTransferMessage.Status.INPROGRESS);
			}

			@Override
			public void buttonFileRejectedOrCanceled (FileTransferTask task) {
				sessionManager.sendEncryptedData(session, Msg.FILE_CANCEL + task.getId());
				if (task.getStatus() == Status.INPROGRESS) task.cancel();
				setMessageStatus(task, FileTransferMessage.Status.CANCELED);
			}

		});

		guiMessages.add(guiMsg);
		windowManager.addMessage(task.getSession(), guiMsg);
		guiMsg.setStatus(FileTransferMessage.Status.REQUEST_RECEIVED);

		return;
	}

	private boolean checkIfValidRequest (String[] data) {
		if (data.length < 3) {
			Log.w("Received wrong request, data array too small!");
			return false;
		} else
			return true;
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

		String toSend;

		if (task.isToMemory())
			toSend = Msg.FILE_TRANSFER_REQUEST_TO_MEMORY;
		else
			toSend = Msg.FILE_TRANSFER_REQUEST_TO_FILE;

		toSend += task.getId() + Msg.FILE_DELIMITER + file.getName() + Msg.FILE_DELIMITER + file.length();

		sessionManager.sendEncryptedData(task.getSession(), toSend);
		task.setRequestSend(true);

		if (task.isToMemory() == false) createGUIMessage(task);
	}

	private void createGUIMessage (SendFileTask task) {
		FileTransferMessage guiMsg = new FileTransferMessage(task);

		guiMsg.setListener(new FileTransferMessageAdapter() {

			@Override
			public void buttonFileRejectedOrCanceled (FileTransferTask task) {
				sessionManager.sendEncryptedData(task.getSession(), Msg.FILE_CANCEL + task.getId());
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
		for (SendFileTask task : sendTasks) {
			if (task.getId().compareTo(id) == 0) return task;
		}

		return null;
	}

	private ReceiveFileTask getReceiveTaskByUUID (UUID id) {
		for (ReceiveFileTask task : receiveTasks) {
			if (task.getId().compareTo(id) == 0) return task;
		}

		return null;
	}

	private FileTransferMessage getMessageByUUID (UUID id) {
		for (FileTransferMessage msg : guiMessages) {
			if (msg.getTaskUUID().compareTo(id) == 0) return msg;
		}

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
