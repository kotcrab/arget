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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pl.kotcrab.arget.util.DesktopUtils;
import pl.kotcrab.arget.util.FileUitls;

public class FileUtilsTest {

	@Test
	public void testReadableFileSize () {
		assertTrue(FileUitls.readableFileSize(500).equals("500 B"));
		assertTrue(FileUitls.readableFileSize(900).equals("900 B"));
		assertTrue(FileUitls.readableFileSize(1024).equals("1 KB"));
		assertTrue(FileUitls.readableFileSize(1025).equals("1 KB"));
		assertTrue(FileUitls.readableFileSize(1500).equals("1.5 KB"));
		assertTrue(FileUitls.readableFileSize(2048).equals("2 KB"));
		assertTrue(FileUitls.readableFileSize(1024 * 1024).equals("1 MB"));
		assertTrue(FileUitls.readableFileSize(1024 * 1024 * 1024).equals("1 GB"));
	}

	@Test
	public void testIsValidFileName () {
		assertTrue(FileUitls.isValidFileName("image.jpg"));
		assertFalse(FileUitls.isValidFileName("abc/abc.jpg")); // this should return false on every OS

		if (DesktopUtils.isWindows()) { // windows specific illegal characters
			assertFalse(FileUitls.isValidFileName("abc*abc.jpg"));
			assertFalse(FileUitls.isValidFileName("abc>abc.jpg"));
			assertFalse(FileUitls.isValidFileName("abc<abc.jpg"));
			assertFalse(FileUitls.isValidFileName("abc;:*abc.jpg"));
			assertFalse(FileUitls.isValidFileName("abc?abc.jpg"));
			assertFalse(FileUitls.isValidFileName("abc\"|abc.jpg"));
			assertFalse(FileUitls.isValidFileName("CON"));
			assertFalse(FileUitls.isValidFileName("COM1"));
		}
	}

}
