
package pl.kotcrab.arget.global.session;

import java.util.UUID;

import pl.kotcrab.arget.global.ResponseServer;

public class GlobalSession {
	public UUID id;
	public ResponseServer requester;
	public ResponseServer target;

	public GlobalSession (UUID id, ResponseServer requester, ResponseServer target) {
		this.id = id;
		this.requester = requester;
		this.target = target;
	}
}
