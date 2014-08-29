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

package pl.kotcrab.arget.server;

public class ContactInfo {
	public String name;
	public String publicProfileKey;

	public transient ContactStatus status;
	public transient boolean unreadMessages;

	public ContactInfo () {
	}

	public ContactInfo (ContactInfo c) {
		this.name = c.name;
		this.publicProfileKey = c.publicProfileKey;
		this.status = c.status;
		this.unreadMessages = c.unreadMessages;
	}

	public ContactInfo (String name, String publicProfileKey) {
		this(name, publicProfileKey, ContactStatus.DISCONNECTED);
	}

	public ContactInfo (String name, String publicProfileKey, ContactStatus status) {
		this.name = name;
		this.publicProfileKey = publicProfileKey;
		this.status = status;
	}
}
