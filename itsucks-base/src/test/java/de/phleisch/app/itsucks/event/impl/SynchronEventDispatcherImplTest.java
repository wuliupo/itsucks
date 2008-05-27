package de.phleisch.app.itsucks.event.impl;

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventDispatcher;
import de.phleisch.app.itsucks.event.EventObserver;
import junit.framework.TestCase;

public class SynchronEventDispatcherImplTest extends TestCase {

	private boolean mEventReceived;
	
	public void testDispatcher() {
		
		EventDispatcher dispatcher = new SynchronEventDispatcherImpl();
		
		dispatcher.fireEvent(new SimpleEvent(5));

		mEventReceived = false;
		
		dispatcher.registerObserver(new EventObserver() {

			public void processEvent(Event event) {
				mEventReceived = true;
			}
		});
		
		dispatcher.fireEvent(new SimpleEvent(6));
		assertTrue(mEventReceived);
	}
}
