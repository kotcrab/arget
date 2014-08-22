package pl.kotcrab.arget.test;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.kotcrab.arget.App;

public class AppTest {

	@Test
	public void testPaths () {
		assertFalse(App.APP_DIRECTORY_NAME.contains("SNAPSHOT"));
	}

}
