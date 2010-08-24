/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 21.08.2010
 */

package de.phleisch.app.itsucks;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import de.phleisch.app.itsucks.util.PostConstructListener;

public class ItSucksModule  extends AbstractModule {
	
	@Override 
	protected void configure() {

		//register PostConstructorListener
		bindListener(Matchers.any(),new PostConstructListener());
		
		install(new BaseModule());
		install(new CoreModule());
		
	}
}
