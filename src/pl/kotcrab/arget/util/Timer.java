
package pl.kotcrab.arget.util;

//TODO change this to some standard class?
public class Timer {
	private String threadName;
	private Thread thread;

	public Timer (String threadName) {
		this.threadName = threadName;
	}

	public void schedule (final TimerListener task, final long delay) {
		if (thread == null) {
			thread = new Thread(new Runnable() {

				@Override
				public void run () {
					ThreadUtils.sleep(delay);
					if (thread != null) {
						task.doTask();
					}
				}
			}, threadName);
			thread.start();
		}
		// throw new IllegalStateException("Other task is already scheduled.");
	}

	public void cancel () {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}
}
