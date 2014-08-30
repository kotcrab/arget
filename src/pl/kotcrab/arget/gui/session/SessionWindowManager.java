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

package pl.kotcrab.arget.gui.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.comm.Msg;
import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionAlreadyExistNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCipherInitError;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionCloseNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionDoesNotExist;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidIDNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionInvalidReciever;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionRejectedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.SessionTargetKeyNotFound;
import pl.kotcrab.arget.comm.exchange.internal.session.data.MessageTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelHideNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelShowNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingStartedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferToFileRequest;
import pl.kotcrab.arget.comm.exchange.internal.session.file.FileTransferToMemoryRequest;
import pl.kotcrab.arget.comm.file.FileTransferManager;
import pl.kotcrab.arget.gui.MainWindowCallback;
import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.server.ContactStatus;
import pl.kotcrab.arget.server.session.LocalSession;
import pl.kotcrab.arget.server.session.LocalSessionListener;
import pl.kotcrab.arget.server.session.LocalSessionManager;
import pl.kotcrab.arget.util.FileUitls;

public class SessionWindowManager implements LocalSessionListener {
	private LocalSessionManager sessionManager;
	private MainWindowCallback mainWindow;

	private List<SessionPanel> panels;

	/** contact to be automatically show when his session has been created */
	private ContactInfo showWhenSessionCreated;

	private FileTransferManager fileTransfer;

	public SessionWindowManager (MainWindowCallback mainWindowCallback) {
		this.mainWindow = mainWindowCallback;
		panels = new ArrayList<SessionPanel>();
	}

	public void setLocalSessionManager (LocalSessionManager sessionManager) {
		this.sessionManager = sessionManager;
		if (fileTransfer != null) fileTransfer.stop();
		fileTransfer = new FileTransferManager(sessionManager, this);
	}

	@Override
	public void sessionCreated (UUID id, String key) {

		SessionPanel panel = null;

		panel = getPanelByKey(key);

		if (panel == null) {
			panel = new SessionPanel(mainWindow.getContactsByKey(key), id, new SessionPanelListener() {

				@Override
				public void sendFile (SessionPanel panel, File file) {
					fileTransfer.sendFile(sessionManager.getSessionByUUID(panel.getUUID()), file);
				}

				@Override
				public void send (InternalSessionExchange ex) {
					sessionManager.sendLater(ex);
				}

			});

			panels.add(panel);
		} else
			panel.setUUID(id);

		panel.disableInput();
		panel.addMessage(new TextMessage(Msg.SYSTEM, "Creating session..."));
		panel.getContact().status = ContactStatus.CONNECTED_SESSION;
		mainWindow.updateContacts();

		if (panel.getContact() == showWhenSessionCreated) {
			mainWindow.setCenterScreenTo(panel);
			showWhenSessionCreated = null;
		}

		// this means that this method is executed when REMTOE is creating session window which means that LOCAL must be current
		// center panel
		if (panel != mainWindow.getCenterScreen()) panel.setRemoteCenterPanel(true);
	}

	@Override
	public void sessionReady (UUID id) {
		SessionPanel panel = getPanelByUUID(id);
		panel.addMessage(new TextMessage(Msg.SYSTEM, "Ready"));
		panel.enableInput();
	}

	@Override
	public void sessionBroken (SessionExchange ex) {
		SessionPanel panel = getPanelByUUID(ex.id);

		if (ex instanceof SessionCloseNotification)
			panel.addMessage(new TextMessage(Msg.SYSTEM, "Session closed"));
		else if (ex instanceof SessionRejectedNotification)
			panel.addMessage(new TextMessage(Msg.ERROR, "Session rejected by remote"));
		else if (ex instanceof SessionCipherInitError)
			panel.addMessage(new TextMessage(Msg.ERROR, "Remote could not initialize cipher"));
		else if (ex instanceof SessionInvalidIDNotification)
			panel.addMessage(new TextMessage(Msg.ERROR, "Error. This UUID is already used by server. Please try again"));
		else if (ex instanceof SessionTargetKeyNotFound)
			panel.addMessage(new TextMessage(Msg.ERROR,
				"Server could not found key for this contact (contact not connected or internal error)"));
		else if (ex instanceof SessionAlreadyExistNotification)
			panel.addMessage(new TextMessage(Msg.ERROR, "Session already exist on server"));
		else if (ex instanceof SessionInvalidReciever)
			panel.addMessage(new TextMessage(Msg.ERROR, "Server said that this client does not belong to this session"));
		else if (ex instanceof SessionDoesNotExist)
			panel.addMessage(new TextMessage(Msg.SYSTEM, "This session does not exist on the server, session closed."));
		else
			panel.addMessage(new TextMessage(Msg.ERROR, "Session closed, error unrecognized: " + ex.getClass()));

		panel.getContact().status = ContactStatus.CONNECTED;
		panel.disableInput();
		mainWindow.updateContacts();
	}

	@Override
	public void sessionClosed (UUID id) {
		SessionPanel panel = getPanelByUUID(id);

		panel.addMessage(new TextMessage(Msg.SYSTEM, "Session closed"));
		panel.getContact().status = ContactStatus.CONNECTED;
		panel.disableInput();
		mainWindow.updateContacts();
	}

	@Override
	public void sessionDataRecieved (InternalSessionExchange ex) {
		SessionPanel panel = getPanelByUUID(ex.id);

		if (ex instanceof MessageTransfer) {
			MessageTransfer msg = (MessageTransfer)ex;

			String msgNotif;
			if (msg.msg.length() > 80)
				msgNotif = msg.msg.substring(0, 80) + "...";
			else
				msgNotif = msg.msg;

			if (mainWindow.getOptions().notifNewMsg)
				App.notificationService.showMessageNotification(panel.getContact().name, msgNotif);

			panel.addMessage(new TextMessage(Msg.LEFT, msg.msg));
			notificationIfNotMainScreen(panel);
		}

		if (ex instanceof TypingStartedNotification) panel.showTyping();
		if (ex instanceof TypingFinishedNotification) panel.hideTyping();
		if (ex instanceof RemotePanelShowNotification) panel.setRemoteCenterPanel(true);
		if (ex instanceof RemotePanelHideNotification) panel.setRemoteCenterPanel(false);

		if (mainWindow.getOptions().notifImageFileTrasnfer && ex instanceof FileTransferToMemoryRequest)
			App.notificationService.showMessageNotification(panel.getContact().name, "Image transfer in progress...");

		if (mainWindow.getOptions().notifFileTrasnfer && ex instanceof FileTransferToFileRequest) {
			FileTransferRequest req = (FileTransferRequest)ex;
			App.notificationService.showMessageNotification(panel.getContact().name, "File transfer request: " + req.fileName
				+ ", size: " + FileUitls.readableFileSize(req.fileSize));
		}

		if (ex instanceof FileTransferExchange)
			fileTransfer.update(sessionManager.getSessionByUUID(panel.getUUID()), (FileTransferExchange)ex);
	}

	private void notificationIfNotMainScreen (SessionPanel panel) {
		// if current panel is not panel that received data
		if (panel != mainWindow.getCenterScreen()) {
			panel.getContact().unreadMessages = true;
			mainWindow.updateContacts();
		}

		mainWindow.starFlasherAndSoundIfNeeded();
	}

	public boolean showPanelForContact (ContactInfo contact) {
		for (SessionPanel panel : panels) {
			if (panel.getContact() == contact) {
				contact.unreadMessages = false;
				mainWindow.updateContacts();
				mainWindow.setCenterScreenTo(panel);
				return true;
			}
		}

		return false;
	}

	private SessionPanel getPanelByKey (String publicKey) {
		for (SessionPanel panel : panels) {
			if (panel.getContact().publicProfileKey.equals(publicKey)) return panel;
		}

		return null;
	}

	public SessionPanel getPanelByUUID (UUID id) {
		for (SessionPanel panel : panels) {
			if (panel.getUUID().compareTo(id) == 0) return panel;
		}

		return null;
	}

	public void addMessage (LocalSession session, MessageComponent comp) {
		SessionPanel panel = getPanelByUUID(session.id);
		if (panel != null) panel.addMessage(comp);
	}

	public void showPanelForContactWhenReady (ContactInfo contact) {
		this.showWhenSessionCreated = contact;
	}

	public void clear () {
		panels.clear();
	}

	public void stop () {
		if (fileTransfer != null) fileTransfer.stop();
	}

}
