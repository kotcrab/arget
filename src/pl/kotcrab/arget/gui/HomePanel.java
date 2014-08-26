/*******************************************************************************
    Copyright 2014 Pawel Pastuszak
 
    This file is part of Arget.

    Arget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Arget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Arget.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package pl.kotcrab.arget.gui;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoTransfer;

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
			text += "Not received server details";
		text += "</center></html>";

		serverInfoLabel.setText(text);
	}

	@Override
	public String getTitle () {
		return null;
	}
}
