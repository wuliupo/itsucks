/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 14.01.2007
 */

package de.phleisch.app.itsucks.gui.util;

import javax.swing.DefaultListModel;

public class ExtendedListModel extends DefaultListModel {

	private static final long serialVersionUID = 2986586217026949945L;

	public int moveEntry(int pIndex, int pOffset) {

		//calculate target
		int targetIndex = pIndex + pOffset;
		if(targetIndex < 0) targetIndex = 0;
		else if(targetIndex >= this.getSize()) targetIndex = this.getSize() - 1;
		
		Object source = get(pIndex);
		Object target = get(targetIndex);
		
		//replace the two objects
		set(pIndex, target);
		set(targetIndex, source);
		
		return targetIndex;
	}

	public void fireContentsChanged(int pIndex0, int pIndex1) {
		
		/*
		 * TODO Das ist nicht besonders gut, besser waere es wenn das Model sich auf
		 * Aenderungen vom element registriert und dann das model aktualisiert.
		 */
		
		super.fireContentsChanged(this, pIndex0, pIndex1);
	}
	
}
