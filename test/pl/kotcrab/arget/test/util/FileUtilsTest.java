
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
