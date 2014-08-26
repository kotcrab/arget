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

package pl.kotcrab.arget.test.manual;

import java.security.Provider;
import java.security.Security;
import java.util.Enumeration;

import pl.kotcrab.arget.App;

public class ListSecuriryProvidersTest {
	@SuppressWarnings("rawtypes")
	public static void main (String[] args) throws Exception {
		App.init(false);

		try {
			Provider p[] = Security.getProviders();
			for (int i = 0; i < p.length; i++) {
				System.out.println(p[i]);
				for (Enumeration e = p[i].keys(); e.hasMoreElements();)
					System.out.println("\t" + e.nextElement());
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
