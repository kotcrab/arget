
package pl.kotcrab.arget.global.session.gui;

import java.io.File;

import pl.kotcrab.arget.comm.exchange.internal.session.data.InternalSessionExchange;

public interface SessionPanelListener {
	public void send (InternalSessionExchange ex);

	@Deprecated
	public void send (SessionPanel panel, String data);

	@Deprecated
	public void messageTyped (SessionPanel panel, String msg);

	public void sendFile (SessionPanel panel, File file);
}
