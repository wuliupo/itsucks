/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 05.01.2008
 */

package de.phleisch.app.itsucks.processing;

public class AbortProcessingException extends ProcessingException {

	private static final long serialVersionUID = -1215410293702712908L;

	public AbortProcessingException() {
		super();
	}

	public AbortProcessingException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	public AbortProcessingException(String pMessage) {
		super(pMessage);
	}

	public AbortProcessingException(Throwable pCause) {
		super(pCause);
	}
}
