
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
