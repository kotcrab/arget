
package pl.kotcrab.arget.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.Exchange;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventNotification;
import pl.kotcrab.arget.comm.exchange.UnsecuredEventNotification.Type;
import pl.kotcrab.arget.comm.exchange.internal.KeyUsedByOtherNotification;
import pl.kotcrab.arget.comm.exchange.internal.KeychainTransfer;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoTransfer;
import pl.kotcrab.arget.comm.kryo.StoppableThreadedListener;
import pl.kotcrab.arget.global.session.GlobalSessionManager;
import pl.kotcrab.arget.global.session.GlobalSessionUpdate;
import pl.kotcrab.arget.util.KryoUtils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

//TODO premium keys, ban list, white list
//TODO check version
public class GlobalServer {
	private static final String TAG = "Global";
	private static final int MAX_CONNECTIONS = 20;

	private File infoFile;
	private GlobalServerInfo info;

	private IDPool idManager;
	private List<String> publicKeys;

	private GlobalSessionManager sessionManager;

	private Server server;
	private StoppableThreadedListener listener;

	private HashMap<Connection, ResponseServer> remotes;

	private GlobalInterface globalInterface;

	public GlobalServer (int port) {
		this(port, "default");
	}

	public GlobalServer (int port, String infoFileName) {
		Log.l(TAG, App.APP_NAME + " " + App.APP_VERSION);
		Log.l(TAG, "Global server started on port: " + port);

		GlobalServerInfoIO.init();

		if (infoFileName.equals("default")) Log.w(TAG, "Configuration file not specified, using default file");
		infoFile = new File(GlobalServerInfoIO.getServersInfoDirectory() + infoFileName);
		info = GlobalServerInfoIO.loadInfo(infoFile);
		Log.l(TAG, String.format("MOTD: '%s', hosted by: '%s'", info.motd, info.hostedBy));

		try {
			init(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1); // TODO command line parser, shutdown
		}

		startConsoleInput();
	}

	private void startConsoleInput () {
		new Thread(new Runnable() {

			@Override
			public void run () {
				try {
					readAndEval();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private void readAndEval () throws IOException {
				while (true) {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					String s = br.readLine();

					// TODO better command line parsing
					if (s.equals("stop")) {
						stop();
						break;
					}

					if (s.startsWith("kick ")) {
						int idToKick = -1;
						try {
							idToKick = Integer.parseInt(s.split(" ")[1]);
						} catch (NumberFormatException e) {
							Log.err(TAG, "Enter valid id number to kick user");
							continue;
						}

						if (findAndKickUser(idToKick) == false) Log.err(TAG, "User with id: " + idToKick + " not found");

						continue;
					}

					if (s.startsWith("motd ")) {
						String newMotd = s.split(" ", 2)[1];
						if (newMotd.length() > 60) {
							Log.err(TAG, "MOTD cannot be longer than 60 characters!");
							continue;
						}

						info.motd = newMotd;
						deliverNewInfo();
						Log.l(TAG, "New MOTD set to: " + info.motd);
						GlobalServerInfoIO.saveInfo(infoFile, info);
						continue;
					}

					if (s.startsWith("hosted-by ")) {
						String newHb = s.split(" ", 2)[1];
						if (newHb.length() > 60) {
							Log.err(TAG, "Hosted-by cannot be longer than 60 characters!");
							continue;
						}

						info.hostedBy = newHb;
						deliverNewInfo();
						Log.l(TAG, "New 'Hosted-by' set to: " + info.hostedBy);
						GlobalServerInfoIO.saveInfo(infoFile, info);
						continue;
					}

					if (s.equals("silent")) {
						Log.setSilentMode(!Log.isSilentMode());
						continue;
					}

					if (s.startsWith("msg")) {
						String[] splited = s.split(" ", 3);
						if (splited.length < 2) {
							Log.err(TAG, "Invalid format, use help!");
							continue;
						}

						String command = splited[1];

						if (command.equals("ls")) {
							Log.l(TAG, "=====Public msg list=====", true);
							for (int i = 0; i < info.publicMsg.size(); i++)
								Log.l(TAG, i + ": " + info.publicMsg.get(i), true);
							Log.l(TAG, "=========================", true);
							continue;
						}

						if (splited.length < 3) {
							Log.err(TAG, "Invalid format, use help!");
							continue;
						}

						String data = splited[2];

						if (command.equals("rm")) {
							int idToRemove;
							try {
								idToRemove = Integer.parseInt(data);
							} catch (NumberFormatException e) {
								Log.err(TAG, "Enter valid id number to remove msg");
								continue;
							}

							try {
								info.publicMsg.remove(idToRemove);
								GlobalServerInfoIO.saveInfo(infoFile, info);
								deliverNewInfo();
								// Log.l(TAG, "Msg removed, remember to call deliver-info to send changes to clients", true);
							} catch (IndexOutOfBoundsException e) {
								Log.err(TAG, "Msg with this id not found.");
								continue;
							}

							continue;
						}

						if (command.equals("add")) {
							if (data.equals("")) {
								Log.err(TAG, "Invalid msg");
								continue;
							}

							info.publicMsg.add(data);
							GlobalServerInfoIO.saveInfo(infoFile, info);
							deliverNewInfo();
							// Log.l(TAG, "Msg added, remember to call deliver-info to send changes to clients", true);
						}

						continue;
					}

					Log.err(TAG, "Command not recognized, use help"); // TODO add help
				}
			}

			private boolean findAndKickUser (int idToKick) {
				for (ResponseServer server : remotes.values()) {
					if (server.getId() == idToKick) {
						server.kickUser();
						return true;
					}
				}

				return false;
			}
		}, "ConsoleInput").start();

	}

	private void init (int port) throws IOException {
		remotes = new HashMap<>();

		idManager = new IDPool();
		publicKeys = Collections.synchronizedList(new ArrayList<String>());

		sessionManager = new GlobalSessionManager(remotes.values());

		server = new Server(200000, 200000);
		KryoUtils.registerNetClasses(server.getKryo());
		server.start();
		server.bind(port);

		// TODO change to normal listener, delete stoppable
		listener = new StoppableThreadedListener(new Listener() {
			@Override
			public void connected (Connection connection) {
				if (remotes.size() >= MAX_CONNECTIONS) {
					connection.sendTCP(new UnsecuredEventNotification(Type.SERVER_FULL));
					connection.close();
					return;
				}

				ResponseServer responseServer = new ResponseServer(connection, globalInterface, idManager.getFreeId());
				remotes.put(connection, responseServer);
			}

			// TODO queue
			@Override
			public void received (Connection connection, Object object) {
				if (object instanceof KeepAlive) return;

				if (object instanceof Exchange) {
					ResponseServer server = remotes.get(connection);
					if (server != null) server.processLater((Exchange)object);
					return;
				}

				System.err.println("Unknown object received! Class:" + object.getClass());
			}

			@Override
			public void disconnected (Connection connection) {
				disconnectConnection(connection);
			}
		});

		server.addListener(listener);

		globalInterface = new GlobalInterface() {

			@Override
			public void addPublicKey (String key) {
				publicKeys.add(key);
				sendKeychainToAllClients();
			}

			@Override
			public void sessionUpdate (GlobalSessionUpdate update) {
				sessionManager.processLater(update);
			}

			// TODO also support AES only mode
			@Override
			public EncryptionMode getEncryptionMode () {
				return EncryptionMode.AES_TWOFISH_SERPENT;
			}

			@Override
			public void disconnect (Connection connection) {
				disconnectConnection(connection);
			}

			@Override
			public void disconnectIfAlreadyConnected (ResponseServer connectionServer, String profilePublicKey) {
				for (ResponseServer server : remotes.values()) {
					if (server != connectionServer) {
						if (server.getProfilePublicKey() != null && server.getProfilePublicKey().equals(profilePublicKey)) {
							server.send(new KeyUsedByOtherNotification());
							server.kickUser();
						}
					}
				}

			}

			@Override
			public ArrayList<String> getKeychain () {
				return new ArrayList<String>(publicKeys);
			}

			@Override
			public ServerInfoTransfer getServerInfoExchange () {
				return createInfoInstance();
			}

		};
	}

	private void deliverNewInfo () {
		for (ResponseServer server : remotes.values())
			server.send(createInfoInstance());
	}

	private ServerInfoTransfer createInfoInstance () {
		return new ServerInfoTransfer(info.motd, info.hostedBy, new ArrayList<String>(info.publicMsg));
	}

	private void disconnectConnection (Connection connection) {
		connection.close();
		ResponseServer respServer = remotes.remove(connection);

		if (respServer != null) {
			respServer.stop();
			publicKeys.remove(respServer.getProfilePublicKey());
			idManager.freeID(respServer.getId());

		}

		sendKeychainToAllClients();
	}

	private void sendKeychainToAllClients () {
		for (ResponseServer server : remotes.values())
			server.send(new KeychainTransfer(new ArrayList<String>(publicKeys)));

	}

	private void stop () {
		Log.l(TAG, "Shutting down server and connections...");

		server.sendToAllTCP(new UnsecuredEventNotification(Type.SERVER_SHUTTING_DOWN));

		sessionManager.stop();
		server.stop();
		listener.stop();

		Log.l(TAG, "Server stopped");
	}

}

interface GlobalInterface {
	public void addPublicKey (String key);

	public ArrayList<String> getKeychain ();

	public ServerInfoTransfer getServerInfoExchange ();

	public void disconnect (Connection connection);

	public void sessionUpdate (GlobalSessionUpdate update);

	public EncryptionMode getEncryptionMode ();

	public void disconnectIfAlreadyConnected (ResponseServer responseServer, String profilePublicKey);
}
