
package pl.kotcrab.arget.global.session.gui;

import java.io.File;

public interface SessionPanelListener {
	public void send (SessionPanel panel, String data);

	public void messageTyped (SessionPanel panel, String msg);

	public void sendFile (SessionPanel panel, File file);
}
