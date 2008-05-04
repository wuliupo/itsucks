/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 04.05.2008
 */

package de.phleisch.app.itsucks.gui.util;

import java.util.List;

import de.phleisch.app.itsucks.filter.download.http.impl.Cookie;

public interface CookieParser {

	public abstract List<Cookie> parseCookies(final String pData);

}