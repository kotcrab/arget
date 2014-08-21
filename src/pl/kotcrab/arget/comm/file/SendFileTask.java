
package pl.kotcrab.arget.comm.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import pl.kotcrab.arget.global.session.LocalSession;

public class SendFileTask extends FileTransferTask {
	private static final int BLOCK_SIZE = 8196;

	private File fileToSend;
	private FileInputStream in;
	private boolean toMemory;

	private boolean requestSend;
	private boolean readyToSendNextBlock = true;

	private long length;
	private long readBytes;

	public SendFileTask (LocalSession session, File file, boolean toMemory) {
		super(Type.SEND, session, UUID.randomUUID());
		this.fileToSend = file;
		this.toMemory = toMemory;

		length = file.length();
	}

	@Override
	public void begin () {
		try {
			in = new FileInputStream(fileToSend);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		super.begin(); // THIS IS IMPORTANT (begin is called not from file
		// sender thread super.begin changes status to
		// INPROGRESS
		// and filesender will start sending file before stream is ready
	}

	public byte[] getNextBlock () {

		if (getStatus() == Status.INPROGRESS) {
			try {
				byte[] bytes = getNextByteArray(in);

				int read = in.read(bytes);

				if (read != -1 && read != 0) {
					readBytes += read;

					if (in.available() != 0) {
						bytes = getNextByteArray(in);
					}

					blockCounter++;

					if (blockCounter >= BLOCKS_IN_BATCH) readyToSendNextBlock = false;
					return bytes;
				} else {
					finish();
					return null;
				}

			} catch (IOException e) {
				// TODO file transfer failed
				e.printStackTrace();
			}
		} else
			throw new IllegalStateException("Task is not started or already finished!");

		return null;

	}

	public byte[] getNextByteArray (FileInputStream in) {
		try {
			if (in.available() > BLOCK_SIZE)
				return new byte[BLOCK_SIZE];
			else
				return new byte[in.available()];
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void finish () {
		super.finish();

		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isToMemory () {
		return toMemory;
	}

	public File getFile () {
		return fileToSend;
	}

	public boolean isRequestSend () {
		return requestSend;
	}

	public void setRequestSend (boolean requestSend) {
		this.requestSend = requestSend;
	}

	@Override
	public int getPercentProgress () {
		return (int)(readBytes * 100.0 / length + 0.5);// http://stackoverflow.com/a/10415638/1950897
	}

	public boolean isReadyToSendNextBlock () {
		return readyToSendNextBlock;
	}

	public void setReadyToSendNextBlock () {
		readyToSendNextBlock = true;
		blockCounter = 0;
	}
}
