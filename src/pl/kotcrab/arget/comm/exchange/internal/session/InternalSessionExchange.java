
package pl.kotcrab.arget.comm.exchange.internal.session;

import java.util.UUID;

import pl.kotcrab.arget.comm.ExchangeSender;
import pl.kotcrab.arget.comm.exchange.EncryptedTransfer;
import pl.kotcrab.arget.global.GlobalClient;
import pl.kotcrab.arget.global.session.LocalSessionManager;

/** Classes that extends this abstract class will be serialized and encrypted using local session cipher (server won't be able to
 * tell what type and content it is). After encryption it will be enclosed in {@link SessionEncryptedTransfer} and send to server.
 * (Before sending it will be again seriazlied and encrypted using client-server cipher and enclosed in {@link EncryptedTransfer}) <br><br>
 * 
 * For example: MessageTransfer extends InternalSessionExchange: <br>
 * -put MessageTransfer in {@link LocalSessionManager} processing queue <br>
 * -{@link LocalSessionManager} serializes and encrypts it using local session cipher , put it contents in
 * {@link SessionEncryptedTransfer} add it to {@link GlobalClient} sending queue <br>
 * - {@link GlobalClient} using {@link ExchangeSender} serializes and encrypts it using client-server cipher, put it contents in
 * {@link EncryptedTransfer} and send it over TCP kryonet connection
 * 
 * @author Pawel Pastuszak */
public abstract class InternalSessionExchange extends SessionExchange {

	public InternalSessionExchange (UUID id) {
		super(id);
	}

}
