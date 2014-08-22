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

package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.arget.global.session.LocalSession;

/** Provides 'cipher initialization line' - encrypted symmetric cipher keys, encryption is done using public profile RSA key. Then
 * result is signed using requester private key See: {@link LocalSession}.
 * @author Pawel Pastuszak */
public class SessionCipherKeysTrsanfer extends SessionExchange {
	public String initData;

	@Deprecated
	public SessionCipherKeysTrsanfer () {
		super(null);
	}

	public SessionCipherKeysTrsanfer (UUID id, String initData) {
		super(id);
		this.initData = initData;
	}
}
