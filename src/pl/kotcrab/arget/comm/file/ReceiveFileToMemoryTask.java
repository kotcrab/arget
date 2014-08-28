/*******************************************************************************
    Copyright 2014 Pawel Pastuszak
 
    This file is part of Arget.

    Arget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Arget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Arget.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.arget.comm.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.server.session.LocalSession;

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
			Log.exception(e);
		}

	}

	@Override
	public void finish () {
		data = out.toByteArray();

		try {
			out.close();
		} catch (IOException e) {
			Log.exception(e);
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
