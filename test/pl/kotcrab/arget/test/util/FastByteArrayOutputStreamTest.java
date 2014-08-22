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

package pl.kotcrab.arget.test.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import pl.kotcrab.arget.util.FastByteArrayOutputStream;

public class FastByteArrayOutputStreamTest {

	@Test
	public void testFastByteArrayOutputStream () throws IOException {
		int writeTimes = 10;
		int blockSize = 200;

		byte[] bytes = new byte[blockSize];
		new Random().nextBytes(bytes);

		FastByteArrayOutputStream out = new FastByteArrayOutputStream(100);
		for (int i = 0; i < writeTimes; i++)
			out.write(bytes);
		
		byte[] outBytes = out.getByteArray();
		byte[] singleBlock = Arrays.copyOf(outBytes, blockSize);
		
		assertArrayEquals(bytes, singleBlock);
		assertEquals(out.getBufferSize(), outBytes.length);

		out.close();
	}

}
