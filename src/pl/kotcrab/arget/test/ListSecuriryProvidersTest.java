
package pl.kotcrab.arget.test;

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
