
package pl.kotcrab.arget.global.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.binary.Base64;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.Settings;
import pl.kotcrab.arget.comm.exchange.internal.KeychainRequest;
import pl.kotcrab.arget.comm.exchange.internal.ServerInfoExchange;
import pl.kotcrab.arget.event.Event;
import pl.kotcrab.arget.event.EventBus;
import pl.kotcrab.arget.event.EventListener;
import pl.kotcrab.arget.event.MenuEvent;
import pl.kotcrab.arget.event.MenuEventType;
import pl.kotcrab.arget.event.SaveProfileEvent;
import pl.kotcrab.arget.global.ConnectionStatus;
import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.global.ContactStatus;
import pl.kotcrab.arget.global.GlobalClient;
import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.arget.global.session.gui.SessionWindowManager;
import pl.kotcrab.arget.gui.AboutDialog;
import pl.kotcrab.arget.gui.CenterPanel;
import pl.kotcrab.arget.gui.components.BottomSplitPaneBorder;
import pl.kotcrab.arget.gui.components.IconFlasher;
import pl.kotcrab.arget.gui.components.MenuItem;
import pl.kotcrab.arget.gui.components.ServerMenuItem;
import pl.kotcrab.arget.profile.Profile;
import pl.kotcrab.arget.profile.ProfileIO;
import pl.kotcrab.arget.util.SoundUtils;
import pl.kotcrab.arget.util.SwingUtils;

//TODO system tray and/or notifications
//TODO delete contact confirmation not centered
//TODO event bus
//TODO organize package pl.kotcrab.arget.global.gui
public class MainWindow extends JFrame implements MainWindowCallback, EventListener {
	private static final String TAG = "MainWindow";
	public static MainWindow instance;
	public static EventBus eventBus;

	private Profile profile;
	private GlobalClient globalClient;
	private SessionWindowManager sessionWindowManager;

	private JMenu serversMenu;
	private JLabel statusLabel;
	private JSplitPane splitPane;

	private ContactsTab contactsPanel;
	private HomePanel homePanel;
	private CenterPanel logPanel;

	private IconFlasher iconFlasher;

	private boolean painted;

	public MainWindow (Profile profile) {
		if (checkAndSetInstance() == false) return;
		MainWindow.eventBus = new EventBus(this);

		this.profile = profile;

		iconFlasher = new IconFlasher(this, App.loadImage("/data/icon.png"), App.loadImage("/data/iconunread.png"));

		createAndShowGUI();
	}

	private boolean checkAndSetInstance () {
		if (MainWindow.instance != null) {
			System.err.println("MainWindow already exists!");
			dispose();
			return false;
		}

		MainWindow.instance = this;
		return true;
	}

	private void createAndShowGUI () {
		setTitle(App.APP_NAME);
		setBounds(100, 100, 800, 700);
		setMinimumSize(new Dimension(500, 250));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		sessionWindowManager = new SessionWindowManager(this);

		if (profile.mainWindowBounds != null && SwingUtils.isRectangleDisplayableOnScreen(profile.mainWindowBounds))
			setBounds(profile.mainWindowBounds);

		createMenuBars();

		contactsPanel = new ContactsTab(profile, this);

		statusLabel = new JLabel();
		statusLabel.setBorder(new EmptyBorder(1, 3, 2, 0));
		setConnectionStatus(ConnectionStatus.DISCONNECTED);
		getContentPane().add(statusLabel, BorderLayout.SOUTH);

		splitPane = new JSplitPane();
		splitPane.setBorder(new BottomSplitPaneBorder());
		splitPane.setResizeWeight(0);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		homePanel = new HomePanel(profile.fileName);
		logPanel = new LoggerPanel();

		splitPane.setLeftComponent(contactsPanel);
		splitPane.setRightComponent(null);
		setCenterScreenTo(homePanel);

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus (WindowEvent e) {
				iconFlasher.stop();

				instance.validate();
				instance.revalidate();
				instance.repaint();
			}
		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowIconified (WindowEvent e) {
				getCenterScreen().onHide();

			}

			@Override
			public void windowDeiconified (WindowEvent e) {
				getCenterScreen().onShow();

			}

		});

		setVisible(true);

		if (profile.autoconnectInfo != null) {
			new Thread(new Runnable() {

				@Override
				public void run () {
					connectToServer(profile.autoconnectInfo);
				}
			}).start();
		}
	}

	@Override
	public void paint (Graphics g) {
		super.paint(g);

		if (!painted) {
			painted = true;
			splitPane.setDividerLocation(0.25);
		}
	}

	private void createMenuBars () {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		getContentPane().add(menuBar, BorderLayout.NORTH);

		JMenu fileMenu = new JMenu("File");
		serversMenu = new JMenu("Servers");
		JMenu contactsMenu = new JMenu("Contacts");
		JMenu viewMenu = new JMenu("View");
		JMenu helpMenu = new JMenu("Help");

		menuBar.add(fileMenu);
		menuBar.add(serversMenu);
		menuBar.add(contactsMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);

		fileMenu.add(new MenuItem("Logout", MenuEventType.FILE_LOGOUT));
		fileMenu.add(new MenuItem("Exit", MenuEventType.FILE_EXIT));

		serversMenu.add(new MenuItem("Add Server", MenuEventType.SERVERS_ADD));
		serversMenu.add(new MenuItem("Manage Servers", MenuEventType.SERVERS_MANAGE));
		serversMenu.add(new MenuItem("Disconnect", MenuEventType.SERVERS_DISCONNECT));
		serversMenu.add(new JSeparator());

		viewMenu.add(new MenuItem("Show Home Screen", MenuEventType.VIEW_SHOW_HOME));
		viewMenu.add(new MenuItem("Show Log", MenuEventType.VIEW_SHOW_LOG));

		contactsMenu.add(new MenuItem("Show My Public Key", MenuEventType.CONTACTS_SHOW_PUBLIC_KEY));
		contactsMenu.add(new MenuItem("Add Contact", MenuEventType.CONTACTS_ADD));
		contactsMenu.add(new JSeparator());
		contactsMenu.add(new MenuItem("Refresh list", MenuEventType.CONTACTS_REFRESH));

		helpMenu.add(new MenuItem("About", MenuEventType.HELP_ABOUT));

		addServersFromProfile();
	}

	private void rebuildServersList () {
		cleanServersMenuList();
		addServersFromProfile();
	}

	private void addServersFromProfile () {
		for (ServerInfo info : profile.servers)
			serversMenu.add(new ServerMenuItem(this, info));
	}

	private void cleanServersMenuList () {

		Component[] comp = serversMenu.getPopupMenu().getComponents();
		for (int i = 0; i < comp.length; i++) {
			if (comp[i] instanceof ServerMenuItem) serversMenu.getPopupMenu().remove(comp[i]);
		}

	}

	@Override
	public void setConnectionStatus (ConnectionStatus status) {
		setConnectionStatus(status, null);
	}

	@Override
	public void setConnectionStatus (ConnectionStatus status, String msg) {
		String textToSet = null;

		switch (status) {
		case CONNECTED:
			textToSet = "Connected";
			break;
		case CONNECTING:
			textToSet = "Connecting...";
			break;
		case DISCONNECTED:
			textToSet = "Disconnected";
			globalClient = null;
			resetContacts();
			break;
		case ERROR:
			textToSet = "Error";
			globalClient = null;
			resetContacts();
			break;
		case TIMEDOUT:
			textToSet = "Connection timed out";
			globalClient = null;
			resetContacts();
			break;
		case SERVER_FULL:
			textToSet = "Server is full";
			globalClient = null;
			resetContacts();
			break;
		case SERVER_SHUTDOWN:
			textToSet = "Server shutdown";
			globalClient = null;
			resetContacts();
			break;
		case KICKED:
			textToSet = "Kicked from server";
			globalClient = null;
			resetContacts();
			break;
		default:
			break;
		}

		if (msg != null) textToSet += ": " + msg;
		statusLabel.setText(textToSet);
	}

	@Override
	public void dispose () {
		if (globalClient != null) globalClient.requestDisconnect();
		if (sessionWindowManager != null) sessionWindowManager.stop();

		profile.mainWindowBounds = getBounds();
		ProfileIO.saveProfile(profile);

		MainWindow.eventBus.stop();
		MainWindow.instance = null;
		super.dispose();
	}

	@Override
	public void startChat (ContactInfo contact) {
		if (contact.status == ContactStatus.DISCONNECTED) {
			if (sessionWindowManager.showPanelForContact(contact) == false)
				JOptionPane.showMessageDialog(this,
					"Old session not found, new session cannot be created because this contact is not connected!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (contact.status == ContactStatus.CONNECTED_SESSION) {
			sessionWindowManager.showPanelForContact(contact);
			return;
		}

		if (contact.status == ContactStatus.CONNECTED_GLOBAL) {
			sessionWindowManager.showPanelForContactWhenReady(contact);
			globalClient.createSession(contact);
			return;
		}
	}

	@Override
	public void updateContacts () {
		performContactsUpdate();
	}

	// TODO move to contacts table
	private void resetContacts () {
		for (ContactInfo c : profile.contacts)
			c.status = ContactStatus.DISCONNECTED;

		updateContacts();
	}

	private void performContactsUpdate () {
		if (globalClient != null) {
			globalClient.processLastKeychain(); // automatically calls contactsPanel.updateContactsTable();
			contactsPanel.updateContactsTable();
		} else
			contactsPanel.updateContactsTable();

	}

	@Override
	public boolean isKeyInContacts (String key) {
		for (ContactInfo c : profile.contacts) {
			if (c.publicProfileKey.equals(key)) return true;
		}

		return false;
	}

	@Override
	public ContactInfo getContactsByKey (String key) {
		for (ContactInfo c : profile.contacts) {
			if (c.publicProfileKey.equals(key)) return c;
		}

		return null;
	}

	@Override
	public void setCenterScreenTo (CenterPanel panel) {
		if (getCenterScreen() != null) getCenterScreen().onHide();

		// double dl = splitPane.getDividerLocation(); //why the hell this is returning int?
		// "proportional location must be between 0.0 and 1.0."
		splitPane.setRightComponent(panel);
		// splitPane.setDividerLocation(dl);
		splitPane.setDividerLocation(0.25); // TODO rember divider location

		if (panel.getTitle() == null)
			setTitle(App.APP_NAME);
		else
			setTitle(App.APP_NAME + " - " + panel.getTitle());
		panel.onShow();
	}

	@Override
	public CenterPanel getCenterScreen () {
		return (CenterPanel)splitPane.getRightComponent();
	}

	@Override
	public void connectToServer (ServerInfo info) {
		if (globalClient == null) {
			globalClient = new GlobalClient(info, profile, instance, sessionWindowManager);
			sessionWindowManager.setLocalSessionManager(globalClient.getLocalSessionManager());

			if (globalClient.isSuccessfullyInitialized() == false) {
				globalClient.requestDisconnect();
				globalClient = null;
			}
		} else
			JOptionPane.showMessageDialog(instance,
				"Already connected to global server. Disconnect first if you want to change your current server.", "Error",
				JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void starFlasherAndSoundIfNeeded () {
		if (isFocused() == false) {
			iconFlasher.start();
			SoundUtils.playSound("/data/notification.wav");
		}
	}

	@Override
	public void setServerInfo (ServerInfoExchange info) {
		homePanel.setServerText(info);
	}

	@Override
	public void onEvent (Event event) {
		if (event instanceof MenuEvent) processMenuEvent((MenuEvent)event);

		if (event instanceof SaveProfileEvent) {
			ProfileIO.saveProfile(profile);
			rebuildServersList();
		}
	}

	private void processMenuEvent (MenuEvent event) {
		switch (event.type) {
		case FILE_LOGOUT:
			Settings.resetAutoLogin();
			dispose();

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run () {
					new LoginFrame();
				}
			});
			break;
		case FILE_EXIT:
			dispose();
			break;

		case SERVERS_ADD:
			new CreateServerInfoDialog(instance, new CreateServerDialogFinished() {

				@Override
				public void finished (ServerInfo info) {
					profile.servers.add(info);
					ProfileIO.saveProfile(profile);
					rebuildServersList();
				}
			});
			break;
		case SERVERS_MANAGE:
			new ManageServersDialog(instance, profile);
			break;
		case SERVERS_DISCONNECT:
			if (globalClient != null) {
				GlobalClient client = globalClient;
				globalClient = null;
				client.requestDisconnect();
			} else
				JOptionPane.showMessageDialog(instance,
					"You need to be connected to a server to disconnect. Please connect to disconnect.", "Error",
					JOptionPane.ERROR_MESSAGE);
			break;

		case CONTACTS_SHOW_PUBLIC_KEY: {
			String key = Base64.encodeBase64String(profile.rsa.getPublicKey().getEncoded());
			new DisplayPublicKeyDialog(instance, key);
			break;
		}
		case CONTACTS_ADD: {
			String key = Base64.encodeBase64String(profile.rsa.getPublicKey().getEncoded());
			new CreateContactDialog(instance, key, new CreateContactDialogFinished() {

				@Override
				public void finished (ContactInfo contact) {
					profile.contacts.add(contact);
					ProfileIO.saveProfile(profile);
					performContactsUpdate();
				}
			});
			break;
		}
		case CONTACTS_REFRESH:
			if (globalClient != null) {
				globalClient.send(new KeychainRequest());
			}
			break;

		case VIEW_SHOW_HOME:
			setCenterScreenTo(homePanel);
			break;
		case VIEW_SHOW_LOG:
			setCenterScreenTo(logPanel);
			break;

		case HELP_ABOUT:
			new AboutDialog(instance);
			break;

		default:
			Log.err(TAG, "Unknown menu event type: " + event.type);
			break;
		}
	}

}
