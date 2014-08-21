
package pl.kotcrab.arget.global.gui;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoTransfer;
import pl.kotcrab.arget.gui.CenterPanel;

public class HomePanel extends CenterPanel {
	private JLabel serverInfoLabel;

	public HomePanel (String profileName) {

		String msg = "<html><center>";
		msg += "Welcome back: " + profileName + "!";
		msg += "<br><br>";
		msg += "This is your home screen. It's kinda boring now, sorry :/";

		if (App.DEBUG) {
			msg += "<br><br>";
			msg += "DEBUG MODE IS ENABLED";
		}

		msg += "</center></html>";
		setLayout(new MigLayout("", "[91px,grow]", "[14px,grow][]"));

		JLabel label = new JLabel(msg);
		add(label, "cell 0 0,alignx center,aligny center");

		serverInfoLabel = new JLabel();
		add(serverInfoLabel, "cell 0 1,alignx center,aligny center");
		setServerText(null);
	}

	public void setServerText (ServerInfoTransfer info) {
		String text = "<html><center>";
		text += "Server details:";
		text += "<br>";

		if (info != null) {
			if (info.motd != null) {
				text += "MOTD: " + info.motd;
				text += "<br>";
			}
			if (info.hostedBy != null) {
				text += "Hosted by: " + info.hostedBy;
				text += "<br>";
			}

			text += "<br>";

			if (info.publicMsg != null) { // TODO add show msg button and dialog
				if (info.publicMsg.size() > 0) {
					text += "Server messages: ";
					text += "<br>";
				}

				for (String msg : info.publicMsg) {
					text += msg;
					text += "<br>";
				}
			}
		} else
			text += "Not connected or server details not received";
		text += "</center></html>";

		serverInfoLabel.setText(text);
	}

	@Override
	public String getTitle () {
		return null;
	}
}
