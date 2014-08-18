
package pl.kotcrab.arget.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.arget.global.gui.MainWindowCallback;

public class ServerMenuItem extends JMenuItem implements ActionListener {
	private MainWindowCallback callback;
	private ServerInfo info;

	public ServerMenuItem (MainWindowCallback callback, ServerInfo info) {
		super(info.toString());
		this.callback = callback;
		this.info = info;
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		callback.connectToServer(info);
	}

}
