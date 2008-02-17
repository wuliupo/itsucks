/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 17.02.2008
 */

package de.phleisch.app.itsucks.gui.common.panel;


public class EditListCallbackPanel extends EditListPanel {

	private static final long serialVersionUID = 7042712273665076919L;
	
	protected EditListCallbackInterface mLogic;

	public interface EditListCallbackInterface {

		abstract ListElement createNewElement();

		abstract void loadEditArea(ListElement pElement);

		abstract void emptyEditArea();

		abstract void enableEditArea(boolean pEnable);
	}
	
	public EditListCallbackPanel(EditListCallbackInterface pLogic) {
		super();
		mLogic = pLogic;
	}
	
	public EditListCallbackPanel() {
		super();
	}
	
	public EditListCallbackInterface getLogic() {
		return mLogic;
	}

	public void setLogic(EditListCallbackInterface pLogic) {
		mLogic = pLogic;
	}

	@Override
	protected ListElement createNewElement() {
		return mLogic.createNewElement();
	}

	@Override
	protected void emptyEditArea() {
		mLogic.emptyEditArea();
	}

	@Override
	protected void enableEditArea(boolean pEnable) {
		mLogic.enableEditArea(pEnable);

	}

	@Override
	protected void loadEditArea(ListElement pElement) {
		mLogic.loadEditArea(pElement);
	}

}
