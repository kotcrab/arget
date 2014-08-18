
package pl.kotcrab.arget.util;

import java.net.Socket;

public class SocketUtils {
	/** Gets remote ip from socket without unnecessary '/'
	 * @param socket
	 * @return remote ip */
	public static String getRemoteIp (Socket socket) {
		return socket.getRemoteSocketAddress().toString().replace("/", "");
	}
}
