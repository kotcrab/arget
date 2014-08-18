
package pl.kotcrab.arget.comm;

import pl.kotcrab.arget.comm.exchange.Exchange;
import pl.kotcrab.arget.comm.exchange.PingRequest;
import pl.kotcrab.arget.comm.exchange.PingResponse;
import pl.kotcrab.arget.util.ThreadUtils;

public class ExchangePinger {
	private static final int MAX_PINGS = 3;

	private ExchangeSender sender;
	private String threadName;
	private TimeoutListener listener;

	private int pingsSent;
	private boolean running;

	public ExchangePinger (ExchangeSender sender, String threadName, final TimeoutListener listener) {
		this.sender = sender;
		this.threadName = threadName;
		this.listener = listener;
	}

	public void stop () {
		running = false;
	}

	public void start () {
		if (running == false) {

			running = true;

			new Thread(new Runnable() {
				@Override
				public void run () {
					while (running) {
						sender.processLater(new PingRequest());
						pingsSent++;
						if (pingsSent > MAX_PINGS) listener.timedOut();
						ThreadUtils.sleep(5000);
					}
				}

			}, threadName).start();
		}
	}

	public void update (Exchange exchange) {
		if (running) {
			if (exchange instanceof PingResponse) pingsSent = 0;
			if (exchange instanceof PingRequest) sender.processLater(new PingResponse());
		}
	}
}
