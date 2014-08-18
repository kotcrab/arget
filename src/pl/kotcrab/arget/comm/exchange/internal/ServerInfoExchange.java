
package pl.kotcrab.arget.comm.exchange.internal;

import java.util.ArrayList;

public class ServerInfoExchange implements InternalExchange {
	public String motd = "";
	public String hostedBy = "";
	public ArrayList<String> publicMsg = new ArrayList<String>();

	@Deprecated
	public ServerInfoExchange () {
	}

	public ServerInfoExchange (String motd, String hostedBy, ArrayList<String> publicMsg) {
		this.motd = motd;
		this.hostedBy = hostedBy;
		this.publicMsg = publicMsg;
	}
}
