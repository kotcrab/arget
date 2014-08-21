
package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;

/** Standard text message
 * @author Pawel Pastuszak */
public class MessageTransfer extends InternalSessionExchange {
	public String msg;

	@Deprecated
	public MessageTransfer () {
		super(null);
	}

	public MessageTransfer (UUID id, String msg) {
		super(id);
		this.msg = msg;
	}

}
