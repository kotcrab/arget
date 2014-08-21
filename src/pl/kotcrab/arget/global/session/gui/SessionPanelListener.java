
package pl.kotcrab.arget.global.session.gui;

import java.io.File;

import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;

public interface SessionPanelListener {
	public void send (InternalSessionExchange ex);

	public void sendFile (SessionPanel panel, File file);
}
