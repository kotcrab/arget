
package pl.kotcrab.arget.event;

import java.awt.EventQueue;

import pl.kotcrab.arget.util.ProcessingQueue;

//TODO add executors?
public class EventBus {
	private EventListener listener;
	private ProcessingQueue<Event> queue;

	public EventBus (EventListener listener) {
		this.listener = listener;
		queue = new ProcessingQueue<Event>("EventBus") {

			@Override
			protected void processQueueElement (Event event) {
				processEvent(event);
			}

		};
	}

	public void post (Event event) {
		queue.processLater(event);
	}

	private void processEvent (final Event event) {
		if (event.isExectueOnAWTEventQueue()) {

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run () {
					listener.onEvent(event);
				}
			});

		} else
			listener.onEvent(event);

	}

	public void stop () {
		queue.stop();
	}

}
