/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.01.2008
 */

package de.phleisch.app.itsucks;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import de.phleisch.app.itsucks.processing.download.http.impl.UrlExtractor;

public class ParserTest extends TestCase {

	public void testParser() throws URISyntaxException, IOException {
		
		URI baseURI = new URI("http://test.de/");
		
		//open challenge file
		String challenge = loadFileFromClasspath("/parser_test_challenge.txt");
		
		//parse challenge file
		UrlExtractor urlExtractor = new UrlExtractor(baseURI);
		URI[] extractURLs = urlExtractor.extractURLs(challenge);
		
		//open solution file
		String solution = loadFileFromClasspath("/parser_test_solution.txt");
		
		//check result
		StringTokenizer stringTokenizer = new StringTokenizer(solution.toString(), "\n");
		ArrayList<URI> urlList = new ArrayList<URI>(Arrays.asList(extractURLs));
		
		System.out.println("Links found: " + urlList);
		
		while(stringTokenizer.hasMoreElements()) {
			String uri = (String) stringTokenizer.nextElement();
			URI uri2 = baseURI.resolve(uri);
			
			assertTrue("Url not found in result: '" + uri2 + "'", urlList.contains(uri2));
		}
	}

	private String loadFileFromClasspath(String pFilename)
			throws IOException {
		
		StringBuilder builder = new StringBuilder();
		
		Reader reader = new InputStreamReader(
				getClass().getResourceAsStream(pFilename));
		
		try {
			char buffer[] = new char[1024];
			int bytes;
			while((bytes = reader.read(buffer)) > 0) {
				builder.append(buffer, 0, bytes);
			}
		} finally {
			reader.close();
		}
		
		return builder.toString();
	}
	
}
