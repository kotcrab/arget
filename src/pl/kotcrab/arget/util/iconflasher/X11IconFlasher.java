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

package pl.kotcrab.arget.util.iconflasher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import pl.kotcrab.arget.util.ThreadUtils;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.Window;
import com.sun.jna.platform.unix.X11.XClientMessageEvent;
import com.sun.jna.platform.unix.X11.XEvent;

public class X11IconFlasher extends IconFlasher {

	private long frameId;
	private Window window;

	private Timer timer;
	private FlashTimerTask timerTask;
	private boolean running;

	public X11IconFlasher (JFrame frameWindow) {
		super(frameWindow);
		System.loadLibrary("jawt");

		frameId = Native.getWindowID(frameWindow);
		window = new Window(frameId);

		timer = new Timer("IconFlasher", true);

		frameWindow.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus (WindowEvent e) {
				stop();
			}
		});

	}

	private static final long _NET_WM_STATE_REMOVE = 0L;
	private static final long _NET_WM_STATE_ADD = 1L;
	// private static final long _NET_WM_STATE_TOGGLE = 2L;

	private static final String _NET_WM_STATE = "_NET_WM_STATE";
	private static final String _NET_WM_STATE_DEMANDS_ATTENTION = "_NET_WM_STATE_DEMANDS_ATTENTION";

	@Override
	public void flashIcon () {
		if (frameWindow.isFocused() == false && running == false) {
			running = true;
			timer.purge();
			timerTask = new FlashTimerTask();
			timer.scheduleAtFixedRate(timerTask, 0, 3000);
		}
	}

	private void stop () {
		if (running) {
			timerTask.cancel();
			running = false;
			sendMessage(_NET_WM_STATE_REMOVE, _NET_WM_STATE_DEMANDS_ATTENTION);
		}
	}

	private class FlashTimerTask extends TimerTask {

		@Override
		public void run () {
			sendMessage(_NET_WM_STATE_REMOVE, _NET_WM_STATE_DEMANDS_ATTENTION);
			ThreadUtils.sleep(1000);
			sendMessage(_NET_WM_STATE_ADD, _NET_WM_STATE_DEMANDS_ATTENTION);

			if (running == false) sendMessage(_NET_WM_STATE_REMOVE, _NET_WM_STATE_DEMANDS_ATTENTION);
		}

	}

	private void sendMessage (long mode, String msg) {
		X11 x = X11.INSTANCE;

		Display display = null;
		try {
			display = x.XOpenDisplay(null);
			sendMessage(display, mode, msg);
		} finally {
			if (display != null) {
				x.XCloseDisplay(display);
			}
		}
	}

	private void sendMessage (Display display, long mode, String msg) {
		X11 x = X11.INSTANCE;

		XEvent event = new XEvent();
		event.type = X11.ClientMessage;
		event.setType(XClientMessageEvent.class);
		event.xclient.type = X11.ClientMessage;
		event.xclient.display = display;
		event.xclient.message_type = x.XInternAtom(display, _NET_WM_STATE, false);
		event.xclient.window = window;
		event.xclient.format = 32;

		event.xclient.data.setType(NativeLong[].class);
		event.xclient.data.l[0] = new NativeLong(mode);
		event.xclient.data.l[1] = x.XInternAtom(display, msg, false);

		NativeLong mask = new NativeLong(X11.SubstructureNotifyMask | X11.SubstructureRedirectMask);
		x.XSendEvent(display, x.XDefaultRootWindow(display), 0, mask, event);
	}
}
