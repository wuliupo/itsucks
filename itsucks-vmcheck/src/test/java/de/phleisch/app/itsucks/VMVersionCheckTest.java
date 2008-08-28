package de.phleisch.app.itsucks;

import java.io.IOException;

import junit.framework.TestCase;

public class VMVersionCheckTest extends TestCase {

	public void testRunVMVersionCheck() throws IOException {
		
		VMVersionCheck check = new VMVersionCheck();
		check.loadProperties();
		assertTrue(check.runVMVersionCheck(check));
		
	}

}
