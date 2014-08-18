
package pl.kotcrab.arget.comm.kryo;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

public class StoppableThreadedListener extends ThreadedListener {
	public StoppableThreadedListener (Listener listener) {
		super(listener);
	}

	public void stop () {
		threadPool.shutdown();
	}
}
