/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 16.12.2007
 */

package de.phleisch.app.itsucks.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.event.impl.SimpleDirectEventSource;
import de.phleisch.app.itsucks.event.impl.SimpleEvent;

public class DispatcherList extends SimpleDirectEventSource {

	private Map<Integer, Dispatcher> mDispatcherList = 
		new ConcurrentHashMap<Integer, Dispatcher>();
	
	private int mNextDispatcherId = 0;
	
	public final static int EVENT_DISPATCHER_ADDED = 1;
	public final static int EVENT_DISPATCHER_REMOVED = 2;
	
	public class DispatcherListEvent extends SimpleEvent {
		
		private int mDispatcherId;
		private Dispatcher mDispatcher;

		public DispatcherListEvent(int pType) {
			super(pType);
		}
				
		public int getDispatcherId() {
			return mDispatcherId;
		}
		
		public void setDispatcherId(int pDispatcherId) {
			mDispatcherId = pDispatcherId;
		}
		public Dispatcher getDispatcher() {
			return mDispatcher;
		}
		public void setDispatcher(Dispatcher pDispatcher) {
			mDispatcher = pDispatcher;
		}
		
	}
	
	public int addDispatcher(Dispatcher pDispatcher) {
		
		int id = generateId();
		mDispatcherList.put(id, pDispatcher);
		
		DispatcherListEvent event = 
			new DispatcherListEvent(EVENT_DISPATCHER_ADDED);
		event.setDispatcher(pDispatcher);
		event.setDispatcherId(id);

		this.fireEvent(event);
		
		return id;
	}

	private synchronized int generateId() {
		return mNextDispatcherId++;
	}

	public Dispatcher getDispatcherById(int pDispatcherId) {
		return mDispatcherList.get(pDispatcherId);
	}
	
	public Dispatcher removeDispatcherById(int pDispatcherId) {
		Dispatcher dispatcher = mDispatcherList.remove(pDispatcherId); 
		
		if(dispatcher != null) {
			DispatcherListEvent event = 
				new DispatcherListEvent(EVENT_DISPATCHER_REMOVED);
			event.setDispatcher(dispatcher);
			event.setDispatcherId(pDispatcherId);
			
			this.fireEvent(event);
		}
		
		return dispatcher;
	}
	
}
