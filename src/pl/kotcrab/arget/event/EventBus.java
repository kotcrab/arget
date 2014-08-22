/*******************************************************************************
    Copyright 2014 Pawel Pastuszak
 
    This file is part of Arget.

    Arget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Arget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Arget.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.arget.event;

import java.awt.EventQueue;
import java.util.ArrayList;

import pl.kotcrab.arget.util.ProcessingQueue;

//TODO add executors?
public class EventBus {
	private ArrayList<EventListener> listeners;
	private ProcessingQueue<Event> queue;

	public EventBus () {
		listeners = new ArrayList<EventListener>();

		queue = new ProcessingQueue<Event>("EventBus", true) {

			@Override
			protected void processQueueElement (Event event) {
				processEvent(event);
			}

		};
	}

	public void register (EventListener listener) {
		listeners.add(listener);
	}

	public boolean unregister (EventListener listener) {
		return listeners.remove(listener);
	}

	public void post (Event event) {
		queue.processLater(event);
	}

	public void stop () {
		queue.stop();
	}

	private void processEvent (final Event event) {
		if (event.isExectueOnAWTEventQueue()) {
			for (EventListener listener : listeners)
				processEventOnAWTQueue(listener, event);
		} else {
			for (EventListener listener : listeners)
				listener.onEvent(event);
		}
	}

	private void processEventOnAWTQueue (final EventListener listener, final Event event) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run () {
				listener.onEvent(event);
			}
		});
	}
}
