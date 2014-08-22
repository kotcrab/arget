
package pl.kotcrab.arget.comm.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import pl.kotcrab.arget.global.session.LocalSession;

public class ReceiveFileToMemoryTask extends ReceiveFileTask {
	public static final int MAX_SIZE = 2048 * 1024; // 2048 KB = 2 MB

	private ByteArrayOutputStream out;
	private byte[] data;
	private long exceptedSize;
	private int currentSize;

	private String fileName;

	// TODO fix stupid argument order
	public ReceiveFileToMemoryTask (long exceptedSize, LocalSession session, UUID taskId, String fileName) {
		super(Type.RECEIVE, session, taskId);

		if (exceptedSize > MAX_SIZE)
			throw new IllegalArgumentException("Excepted size to big, maximum is: " + MAX_SIZE
				+ ". Use ReceivingToFileTask instead!");

		this.exceptedSize = exceptedSize;
		this.fileName = fileName;
	}

	@Override
	public void begin () {
		super.begin();
		out = new ByteArrayOutputStream((int)exceptedSize);
	}

	@Override
	public void saveNextBlock (byte[] block) {
		super.saveNextBlock(block);

		try {
			out.write(block);

			currentSize += block.length;
			if (currentSize > MAX_SIZE) throw new IllegalArgumentException("Too many data received, maximum is: " + MAX_SIZE);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void finish () {
		data = out.toByteArray();

		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.finish();
	}

	@Override
	public byte[] getData () {
		return data;
	}

	public String getFileName () {
		return fileName;
	}

	@Override
	public int getPercentProgress () {
		return (int)(Math.round(currentSize * 100.0 / exceptedSize));
	}
}
