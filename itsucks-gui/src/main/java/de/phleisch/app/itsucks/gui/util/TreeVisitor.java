/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 29.04.2008
 */

package de.phleisch.app.itsucks.gui.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

public class TreeVisitor {
	
	private List<TreeListener> mRegisteredListener;
	
	public TreeVisitor() {
		mRegisteredListener = new ArrayList<TreeListener>();
	}
	
	public void registerListener(TreeListener pTreeListener) {
		mRegisteredListener.add(pTreeListener);
	}
	
	public void visit(TreeNode pTreeNode) {
		invokeListener(pTreeNode);
		
		Enumeration<?> children = pTreeNode.children();
		while(children.hasMoreElements()) {
			visit((TreeNode) children.nextElement());
		}
	}

	private void invokeListener(TreeNode pTreeNode) {

		for (TreeListener listener : mRegisteredListener) {
			listener.processNode(pTreeNode);
		}
		
	}
	

	public static interface TreeListener {
		void processNode(TreeNode pTreeNode);
	}
	
}
