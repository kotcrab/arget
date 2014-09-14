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

package pl.kotcrab.arget.profile;

public class ProfileOptions {
	// main
	public boolean mainPlaySoundNewMsg = true;

	// notifications
	public boolean notifConnectionLost = true;
	public boolean notifUserOnline = true;
	public boolean notifUserOffline = true;
	public boolean notifNewMsg = true;
	public boolean notifImageFileTrasnfer = true;
	public boolean notifFileTrasnfer = true;

	// news server
	public boolean newsEnabled = true;
	public String newsAdress = "khonsu.kotcrab.pl";
	public int newsPort = 31400;
}
