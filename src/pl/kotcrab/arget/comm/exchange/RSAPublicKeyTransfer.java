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

package pl.kotcrab.arget.comm.exchange;

import pl.kotcrab.arget.comm.exchange.internal.ProfilePublicKeyTransfer;

/** Transfer of a public RSA key, should by used only when server is sending it's public RSA key. For profile public key transfer
 * {@link ProfilePublicKeyTransfer} should be used.
 * @author Pawel Pastuszak */
public class RSAPublicKeyTransfer implements Exchange {
	public byte[] key;

	@Deprecated
	public RSAPublicKeyTransfer () {
	}

	public RSAPublicKeyTransfer (byte[] key) {
		this.key = key;
	}
}
