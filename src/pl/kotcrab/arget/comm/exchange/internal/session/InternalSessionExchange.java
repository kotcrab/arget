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

import pl.kotcrab.arget.comm.ExchangeSender;
import pl.kotcrab.arget.comm.exchange.EncryptedTransfer;
import pl.kotcrab.arget.server.ArgetClient;
import pl.kotcrab.arget.server.session.LocalSessionManager;

/** Classes that extends this abstract class will be serialized and encrypted using local session cipher (server won't be able to
 * tell what type and content it is). After encryption it will be enclosed in {@link SessionEncryptedTransfer} and send to server.
 * (Before sending it will be again seriazlied and encrypted using client-server cipher and enclosed in {@link EncryptedTransfer}) <br>
 * <br>
 * 
 * For example: MessageTransfer extends InternalSessionExchange: <br>
 * -put MessageTransfer in {@link LocalSessionManager} processing queue <br>
 * -{@link LocalSessionManager} serializes and encrypts it using local session cipher , put it contents in
 * {@link SessionEncryptedTransfer} add it to {@link ArgetClient} sending queue <br>
 * - {@link ArgetClient} using {@link ExchangeSender} serializes and encrypts it using client-server cipher, put it contents in
 * {@link EncryptedTransfer} and send it over TCP kryonet connection
 * 
 * @author Pawel Pastuszak */
public abstract class InternalSessionExchange extends SessionExchange {

	public InternalSessionExchange (UUID id) {
		super(id);
	}

}
