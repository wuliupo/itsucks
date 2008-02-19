/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 05.01.2008
 */

package de.phleisch.app.itsucks.processing;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = 7573704580233642148L;

	public ProcessingException() {
		super();
	}

	public ProcessingException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	public ProcessingException(String pMessage) {
		super(pMessage);
	}

	public ProcessingException(Throwable pCause) {
		super(pCause);
	}

}
