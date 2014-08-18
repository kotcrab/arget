
package pl.kotcrab.arget.comm;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.EncryptedExchange;
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
				connection.sendTCP(new EncryptedExchange(cipher.encrypt(data)));
			} else {
				Log.err(TAG, "Tried to send InteralExchange but sender not ready. Type: " + exchange.getClass());
			}

			return;
		}

		connection.sendTCP(exchange);
	}
}
