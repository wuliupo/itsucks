/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 17.11.2009
 */

package de.phleisch.app.itsucks.io.http;

public interface HttpResponseCodes {

	//200 - ok
	public final static int OK_200 = 200;
	
	//300 - redirect
	public final static int PARTIAL_CONTENT_206 = 206;
	public final static int MOVED_PERMANENTLY_301 = 301;
	public final static int FOUND_302 = 302;
	public final static int SEE_OTHER_303 = 303;
	public final static int TEMPORARY_REDIRECT_307 = 307;
	
	//400 - client error
	public final static int NOT_FOUND_404 = 404;
	public final static int REQUESTED_RANGE_NOT_SATISFIABLE_416 = 416;
	
	//500 - server error
}
