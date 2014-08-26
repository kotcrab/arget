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

package pl.kotcrab.arget.util;

import java.io.OutputStream;

public class FastByteArrayOutputStream extends OutputStream {
	/** Buffer and size */
	protected byte[] buf = null;
	protected int size = 0;

	/** Constructs a stream with buffer capacity size 5K */
	public FastByteArrayOutputStream () {
		this(5 * 1024);
	}

	/** Constructs a stream with the given initial size */
	public FastByteArrayOutputStream (int initSize) {
		this.size = 0;
		this.buf = new byte[initSize];
	}

	/** Ensures that we have a large enough buffer for the given size. */
	private void verifyBufferSize (int sz) {
		if (sz > buf.length) {
			byte[] old = buf;
			buf = new byte[Math.max(sz, 2 * buf.length)];
			System.arraycopy(old, 0, buf, 0, old.length);
			old = null;
		}
	}

	public int getSize () {
		return size;
	}

	public int getBufferSize () {
		return buf.length;
	}

	/** Returns the byte array containing the written data. Note that this array will almost always be larger than the amount of
	 * data actually written. */
	public byte[] getByteArray () {
		return buf;
	}

	@Override
	public final void write (byte b[]) {
		verifyBufferSize(size + b.length);
		System.arraycopy(b, 0, buf, size, b.length);
		size += b.length;
	}

	@Override
	public final void write (byte b[], int off, int len) {
		verifyBufferSize(size + len);
		System.arraycopy(b, off, buf, size, len);
		size += len;
	}

	@Override
	public final void write (int b) {
		verifyBufferSize(size + 1);
		buf[size++] = (byte)b;
	}

	public void reset () {
		size = 0;
	}

}
