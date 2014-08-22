
package pl.kotcrab.arget.test.util;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import pl.kotcrab.arget.util.ArrayUtils;

public class ArrayUtilsTest {

	@Test
	public void testTrim () {
		byte[] expected = {2, 5, 6, 7, 4, 0, 0, 4, 4};
		byte[] expected2 = {2, 5, 6, 7, 4, 0, 0, 4, 4, 0, 0};

		byte[] array = {2, 5, 6, 7, 4, 0, 0, 4, 4, 0, 0, 0, 0, 0};

		assertArrayEquals(expected, ArrayUtils.trim(array, 0));
		assertArrayEquals(expected2, ArrayUtils.trim(array, 2));
	}

}
