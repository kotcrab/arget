
package pl.kotcrab.arget.global.session;

import java.util.UUID;

import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.data.InternalSessionExchange;

public interface LocalSessionListener {
	public void sessionCreated (UUID id, String key);

	public void sessionReady (UUID id);

	public void sessionBroken (SessionExchange ex);

	public void sessionClosed (UUID id);

	public void sessionDataRecieved (InternalSessionExchange ex);

	@Deprecated
	public void sessionDataRecieved (UUID id, String decryptedData);
}
