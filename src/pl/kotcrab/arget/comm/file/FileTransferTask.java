
package pl.kotcrab.arget.comm.file;

import java.util.UUID;

import pl.kotcrab.arget.global.session.LocalSession;

public abstract class FileTransferTask {
	public static final int BLOCKS_IN_BATCH = 8;

	public enum Type {
		SEND, RECEIVE
	}

	public enum Status {
		IDLE, INPROGRESS, DONE
	}

	private FileTransferListener listener;

	private LocalSession session;
	private UUID taskId;

	public Type type;
	private Status status = Status.IDLE;
	private boolean canceled;

	protected int blockCounter = 0;

	public FileTransferTask (Type type, LocalSession session, UUID taskId) {
		this.type = type;
		this.session = session;
		this.taskId = taskId;
	}

	public abstract int getPercentProgress ();

	public void setListener (FileTransferListener listener) {
		this.listener = listener;
	}

	public void begin () {
		if (status == Status.IDLE)
			status = Status.INPROGRESS;
		else
			throw new IllegalStateException("FileTransferTask has been already started!");

	}

	public void finish () {
		if (status == Status.INPROGRESS) {
			status = Status.DONE;
			if (listener != null) listener.operationFinished(this);
		} else
			throw new IllegalStateException("FileTransferTask has been already finished or it is not started yet!");

	}

	public void cancel () {
		finish();
		canceled = true;
	}

	public int getBlockCounter () {
		return blockCounter;
	}

	public boolean isCanceled () {
		return canceled;
	}

	public Status getStatus () {
		return status;
	}

	public LocalSession getSession () {
		return session;
	}

	public Type getType () {
		return type;
	}

	public UUID getId () {
		return taskId;
	}
}
