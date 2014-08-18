
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

	/** @param data in format name:ip:port ex. Local global MyName:localhost:31415, name CANNOT contain ':' */
	@Deprecated
	public ServerInfo (String data) {
		String splited[] = data.split(":");
		if (splited.length > 3)
			throw new IllegalStateException("Failed to parse data for GlobalServerDescriptor, invalid data format: " + data);

		name = splited[0];
		ip = splited[1];
		port = Integer.parseInt(splited[2]);
	}

	@Override
	public String toString () {
		return String.format("%s (%s:%s)", name, ip, port);
	}
}
