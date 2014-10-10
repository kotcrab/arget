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

package pl.kotcrab.arget.server;

public enum ConnectionStatus {
	CONNECTING, CONNECTED, DISCONNECTED, ERROR, TIMEDOUT, VERSION_MISMATCH, SERVER_FULL, SERVER_SHUTDOWN, KICKED;

	public String toPrettyString () {
		switch (this) {
		case CONNECTED:
			return "Connected";
		case CONNECTING:
			return "Connecting...";
		case DISCONNECTED:
			return "Disconnected";
		case ERROR:
			return "Error";
		case TIMEDOUT:
			return "Connection timed out";
		case VERSION_MISMATCH:
			return "Your client version is incompatible";
		case SERVER_FULL:
			return "Server is full";
		case SERVER_SHUTDOWN:
			return "Server shutdown";
		case KICKED:
			return "Kicked from server";
		default:
			return super.toString();
		}
	}

	public boolean isConnectionBroken () {
		switch (this) {
		case CONNECTED:
		case CONNECTING:
			return false;
		case DISCONNECTED:
		case ERROR:
		case TIMEDOUT:
		case VERSION_MISMATCH:
		case SERVER_FULL:
		case SERVER_SHUTDOWN:
		case KICKED:
			return true;
		default:
			throw new IllegalStateException("Unknown ConnectionStatus, cannot tell if it's broken or not.");
		}
	}

	public boolean isReconnectable () {
		switch (this) {
		case CONNECTED:
		case CONNECTING:
		case DISCONNECTED:
		case ERROR:
		case VERSION_MISMATCH:
		case SERVER_FULL:
		case SERVER_SHUTDOWN:
		case KICKED:
			return false;
		case TIMEDOUT:
			return true;
		default:
			throw new IllegalStateException("Unknown ConnectionStatus, cannot tell if it's reconnectable or not.");
		}
	}
}
