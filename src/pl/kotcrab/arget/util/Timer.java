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
