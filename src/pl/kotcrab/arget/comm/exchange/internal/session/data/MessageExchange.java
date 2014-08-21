package pl.kotcrab.arget.comm.exchange.internal.session.data;

import java.util.UUID;

public class MessageExchange extends InternalSessionExchange
{
	public String msg;
	
	@Deprecated
	public MessageExchange () {
		super(null);
	}
	
	public MessageExchange (UUID id, String msg) {
		super(id);
		this.msg = msg;
	}
	
}