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

import java.awt.Component;

import pl.kotcrab.arget.comm.exchange.internal.ServerInfoTransfer;
import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ServerDescriptor;

//TODO sort this maybe?
public interface MainWindowCallback {
	public void startChat (ContactInfo contact);

	public void updateContacts ();

	public boolean isKeyInContacts (String key);

	public ContactInfo getContactsByKey (String key);

	public void setCenterScreenTo (CenterPanel panel);

	public Component getCenterScreen ();

	public void starFlasherAndSoundIfNeeded ();

	public void connectToServer (ServerDescriptor info);

	public void setServerInfo (ServerInfoTransfer info);
}
