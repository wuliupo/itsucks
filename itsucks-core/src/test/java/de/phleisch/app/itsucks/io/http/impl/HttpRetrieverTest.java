/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 10.02.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

import junit.framework.TestCase;

public class HttpRetrieverTest extends TestCase {

	public void testRetryBehaviour() {
		
		HttpRetrieverResponseCodeBehaviour behaviour = 
			HttpRetriever.createDefaultHttpRetrieverBehaviour();
		
		assertTrue(behaviour.findActionForResponseCode(200).equals(
				HttpRetrieverResponseCodeBehaviour.Action.OK));
		
		assertTrue(behaviour.findActionForResponseCode(206).equals(
				HttpRetrieverResponseCodeBehaviour.Action.OK));

		assertTrue(behaviour.findActionForResponseCode(503).equals(
				HttpRetrieverResponseCodeBehaviour.Action.FAILED_BUT_RETRYABLE));

		assertTrue(behaviour.findActionForResponseCode(5000).equals(
				HttpRetrieverResponseCodeBehaviour.Action.FAILED));

		assertTrue(behaviour.findActionForResponseCode(5).equals(
				HttpRetrieverResponseCodeBehaviour.Action.FAILED));
	}
	
}
