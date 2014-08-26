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
