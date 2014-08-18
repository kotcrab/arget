
package pl.kotcrab.arget.global.session;

import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.global.ResponseServer;

public class GlobalSessionUpdate {
	public ResponseServer reciever;
	public SessionExchange exchange;

	public GlobalSessionUpdate (ResponseServer reciver, SessionExchange exchange) {
		this.reciever = reciver;
		this.exchange = exchange;
	}
}
