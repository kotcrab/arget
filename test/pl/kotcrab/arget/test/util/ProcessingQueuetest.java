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

import static org.junit.Assert.*;

import org.junit.Test;

import pl.kotcrab.arget.util.ProcessingQueue;
import pl.kotcrab.arget.util.ThreadUtils;

public class ProcessingQueuetest {

	@Test
	public void testProcessingQueue () {
		ProcessingQueue<Integer> queue = new ProcessingQueue<Integer>("Queue") {
			boolean got1;
			boolean got2;
			boolean got3;

			@Override
			protected void processQueueElement (Integer el) {
				int val = el.intValue();

				switch (val) {
				case 1:
					assertFalse(got1);
					assertFalse(got2);
					assertFalse(got3);
					got1 = true;
					break;
				case 2:
					assertTrue(got1);
					assertFalse(got2);
					assertFalse(got3);
					got2 = true;
					break;
				case 3:
					assertTrue(got1);
					assertTrue(got2);
					assertFalse(got3);
					got3 = true;
					break;
				case 4:
					assertTrue(got1);
					assertTrue(got2);
					assertTrue(got3);
					break;
				}

			}
		};

		queue.processLater(new Integer(1));
		queue.processLater(new Integer(2));
		queue.processLater(new Integer(3));
		queue.processLater(new Integer(4));

		ThreadUtils.sleep(10);

		queue.stop();
	}

}
