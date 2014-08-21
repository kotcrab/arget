
package pl.kotcrab.arget.global;

public class ServerInfo {
	public String name;
	public String ip;
	public int port;

	public ServerInfo (String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public ServerInfo () {
	}

	@Override
	public String toString () {
		return String.format("%s (%s:%s)", name, ip, port);
	}
}
