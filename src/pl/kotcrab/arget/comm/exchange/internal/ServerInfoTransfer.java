
package pl.kotcrab.arget.comm.exchange.internal;

import java.util.ArrayList;

/** Transfer of server information: current MOTD, hosted-by text and public server messages
 * @author Pawel Pastuszak */
public class ServerInfoTransfer implements InternalExchange {
	public String motd = "";
	public String hostedBy = "";
	public ArrayList<String> publicMsg = new ArrayList<String>();

	@Deprecated
	public ServerInfoTransfer () {
	}

	public ServerInfoTransfer (String motd, String hostedBy, ArrayList<String> publicMsg) {
		this.motd = motd;
		this.hostedBy = hostedBy;
		this.publicMsg = publicMsg;
	}
}
