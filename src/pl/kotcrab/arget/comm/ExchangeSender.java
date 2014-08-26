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

package pl.kotcrab.arget.comm;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.EncryptedTransfer;
import pl.kotcrab.arget.comm.exchange.Exchange;
import pl.kotcrab.arget.comm.exchange.internal.InternalExchange;
import pl.kotcrab.arget.util.KryoUtils;
import pl.kotcrab.arget.util.ProcessingQueue;
import pl.kotcrab.arget.util.ThreadUtils;
import pl.kotcrab.crypto.SimpleSymmetricCipher;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

public class ExchangeSender extends ProcessingQueue<Exchange> {
	private static final String TAG = "Sender";

	private Connection connection;
	private SimpleSymmetricCipher cipher;
	private Kryo kryo;

	public ExchangeSender (String threadTitle, Connection connection) {
		super(threadTitle, 128);

		this.connection = connection;
	}

	public void enableInternalExchange (Kryo kryo, SimpleSymmetricCipher cipher) {
		this.kryo = kryo;
		this.cipher = cipher;
	}

	@Override
	protected void processQueueElement (Exchange exchange) {
		while (connection.getTcpWriteBufferSize() > 160000) {
			ThreadUtils.sleep(100);
		}

		if (exchange instanceof InternalExchange) {

			if (cipher != null) {
				byte[] data = KryoUtils.writeClassAndObjectToByteArray(kryo, exchange);
				connection.sendTCP(new EncryptedTransfer(cipher.encrypt(data)));
			} else {
				Log.err(TAG, "Tried to send InteralExchange but sender not ready. Type: " + exchange.getClass());
			}

			return;
		}

		connection.sendTCP(exchange);
	}
}
