
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

/** Provides 'cipher initialization line' - encrypted symmetric cipher keys, encryption is done using public profile RSA key. Then
 * result is signed using requester private key See: {@link LocalSession}.
 * @author Pawel Pastuszak */
public class SessionCipherKeysTrsanfer extends SessionExchange {
	public String initData;

	@Deprecated
	public SessionCipherKeysTrsanfer () {
		super(null);
	}

	public SessionCipherKeysTrsanfer (UUID id, String initData) {
		super(id);
		this.initData = initData;
	}
}
