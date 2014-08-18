
package pl.kotcrab.arget.util;

import java.util.concurrent.ArrayBlockingQueue;

/** Blocking queue that allows processing objects of any type, one by one. Element are processed in different thread. This queue
 * orders elements FIFO. Uses {@link ArrayBlockingQueue}. When no longer needed {@link ProcessingQueue#stop()} must be called to
 * shutdown processing thread.
 * 
 * @author Pawel Pastuszak
 *
 * @param <E> type of objects that will be processed */
public abstract class ProcessingQueue<E> {
	private boolean running = true;
	private Thread processingThread;

	private ArrayBlockingQueue<E> queue;

	/** Creates {@link ProcessingQueue}.
	 * 
	 * @param threadName name of processing thread that will be created
	 * @param capacity queue capacity, if queue is full, {@link ProcessingQueue#processLater()} will block until there is space in
	 *           queue */
	public ProcessingQueue (String threadName, int capacity) {
		queue = new ArrayBlockingQueue<E>(capacity);
		start(threadName);
	}

	/** Creates {@link ProcessingQueue} with fixed 256 objects capacity. If queue is full, {@link ProcessingQueue#processLater()}
	 * will block until there is space in queue
	 * 
	 * @param threadName name of processing thread that will be created */
	public ProcessingQueue (String threadName) {
		queue = new ArrayBlockingQueue<E>(256);
		start(threadName);
	}

	private void start (String threadName) {
		processingThread = new Thread(new Runnable() {
			@Override
			public void run () {
				while (running) {
					try {
						processQueueElement(queue.take());
					} catch (InterruptedException e) {
					}
				}
			}
		}, threadName);
		processingThread.start();
	}

	/** Stop and clear queue. Interrupt and stops processing thread. After calling this method queue becomes unusable. */
	public void stop () {
		running = false;
		processingThread.interrupt();
		queue.clear();
	}

	/** Add element to queue. If queue is full, execution will be blocked until there is empty space in queue.
	 * @param element to be added to queue */
	public void processLater (E element) {
		try {
			queue.put(element);
		} catch (InterruptedException e) { // if queue was stopped, no need to print stack trace
		}
	}

	/** Called by processing thread when queue element should be processed.
	 * @param element to be processed */
	protected abstract void processQueueElement (E element);
}
