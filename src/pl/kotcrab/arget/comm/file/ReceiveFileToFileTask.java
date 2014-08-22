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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.global.session.LocalSession;

public class ReceiveFileToFileTask extends ReceiveFileTask {
	private FileOutputStream out;
	private long exceptedSize;
	private long currentSize;

	private String fileName;

	// TODO fix stupid argument order
	public ReceiveFileToFileTask (long exceptedSize, LocalSession session, UUID taskId, String fileName) {
		super(Type.RECEIVE, session, taskId);

		this.exceptedSize = exceptedSize;
		this.fileName = fileName;
	}

	@Override
	public void begin () {
		super.begin();

		File target = new File(App.DOWNLOAD_FOLDER_PATH + fileName);

		if (target.exists()) target = new File(App.DOWNLOAD_FOLDER_PATH + Math.random() + ") " + fileName);

		try {
			target.createNewFile();
			out = new FileOutputStream(target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveNextBlock (byte[] block) {
		super.saveNextBlock(block);
		if (getStatus() == Status.INPROGRESS) {
			try {
				out.write(block);

				currentSize += block.length;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void finish () {
		super.finish();
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] getData () {
		throw new NotImplementedException("Not implemented yet!");
	}

	@Override
	public int getPercentProgress () {
		return (int)(Math.round(currentSize * 100.0 / exceptedSize));
	}
}
