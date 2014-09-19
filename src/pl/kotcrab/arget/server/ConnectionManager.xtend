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
package pl.kotcrab.arget.server

import pl.kotcrab.arget.App
import pl.kotcrab.arget.comm.exchange.Exchange
import pl.kotcrab.arget.event.ConnectionStatusEvent
import pl.kotcrab.arget.event.Event
import pl.kotcrab.arget.event.EventListener
import pl.kotcrab.arget.gui.MainWindowCallback
import pl.kotcrab.arget.gui.session.SessionWindowManager
import pl.kotcrab.arget.profile.Profile

class ConnectionManager implements EventListener {
	val SessionWindowManager windowManager;
	val MainWindowCallback callback;
	val Profile profile;

	var ArgetClient client;
	var ArgetClient clientRef;

	var ServerDescriptor lastDescriptor;

	new(Profile profile, SessionWindowManager windowManager, MainWindowCallback callback) {
		App.eventBus.register(this)

		this.profile = profile;
		this.windowManager = windowManager
		this.callback = callback
	}

	def stop() {
		App.eventBus.unregister(this)
	}

	def connect(ServerDescriptor info) {
		lastDescriptor = info
		client = new ArgetClient(info, profile, callback, windowManager)
		clientRef = client // client may be changed by set to null by some event

		windowManager.localSessionManager = client.localSessionManager
		if(clientRef.isSuccessfullyInitialized == false) requestDisconnect()
	}

	def isConnected() {
		return client != null
	}

	def requestDisconnect() {
		if (client != null) {
			client.requestDisconnect()
			client = null;
		}
	}

	def createSession(ContactInfo contact) {
		client.createSession(contact)
	}

	def processLastKeychain() {
		client.processLastKeychain()
	}

	def getClientSessionManager() {
		return client.localSessionManager
	}

	def isClientInitialized() {
		return clientRef.isSuccessfullyInitialized()
	}

	def send(Exchange ex) {
		if(client != null) client.send(ex)
	}

	def compareClient(ArgetClient client) {
		return this.client == client
	}

	override onEvent(Event event) {
		if (event instanceof ConnectionStatusEvent) {
			
			if (client == event.eventSender) {
				if (event.status.isReconnectable) {
					connect(lastDescriptor)
				} else if(event.status.isConnectionBroken) requestDisconnect()
			}
			
		}
	}

}