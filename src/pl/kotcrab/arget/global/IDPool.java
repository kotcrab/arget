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

package pl.kotcrab.arget.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IDPool {
	private List<Integer> freeIDs = Collections.synchronizedList(new ArrayList<Integer>(30));

	private int IDCounter = 0;

	public IDPool () {
		genertesIDs();
	}

	private void genertesIDs () {
		int targetIDCounter = IDCounter + 30;
		for (; IDCounter < targetIDCounter; IDCounter++) {
			freeIDs.add(IDCounter);
		}
	}

	public void freeID (int id) {
		if (freeIDs.contains(id))
			return;
		else
			freeIDs.add(id);

		Collections.sort(freeIDs);
	}

	public int getFreeId () {
		if (freeIDs.size() == 0) genertesIDs(); // we ran out of free id's, generate more!

		int id = freeIDs.get(0);
		freeIDs.remove(0);
		return id;
	}
}
