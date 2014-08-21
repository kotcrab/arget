
package pl.kotcrab.arget.global.gui;

import java.awt.Component;

import pl.kotcrab.arget.comm.exchange.internal.ServerInfoTransfer;
import pl.kotcrab.arget.global.ConnectionStatus;
import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.arget.gui.CenterPanel;

//TODO sort this maybe?
public interface MainWindowCallback {
	public void setConnectionStatus (ConnectionStatus status);

	public void setConnectionStatus (ConnectionStatus status, String msg);

	public void startChat (ContactInfo contact);

	public void updateContacts ();

	public boolean isKeyInContacts (String key);

	public ContactInfo getContactsByKey (String key);

	public void setCenterScreenTo (CenterPanel panel);

	public Component getCenterScreen ();

	public void starFlasherAndSoundIfNeeded ();

	public void connectToServer (ServerInfo info);

	public void setServerInfo (ServerInfoTransfer info);
}
