/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 06.01.2007
 */

package de.phleisch.app.itsucks;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

public class TestURI extends TestCase {

	public void testURI() throws MalformedURLException, URISyntaxException {
		
		String baseURL = "http://test.de/bla/test.php?bal=32";
		String baseURI = "bla2/index.html";

		URL url = new URL(baseURL);
		URI uri = new URI(baseURI);
		
		System.out.println(url.toURI().getQuery());
		System.out.println(url.toURI().getPath());
		System.out.println(url.toURI().resolve(uri));
		
	}
	
}
