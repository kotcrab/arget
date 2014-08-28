
package pl.kotcrab.arget.util.iconflasher;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
		timerTask = new FlashTimerTask();

		frameWindow.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained (FocusEvent e) {
				stop();
			}
		});
	}

	private static final long _NET_WM_STATE_REMOVE = 0L;
	private static final long _NET_WM_STATE_ADD = 1L;
	//private static final long _NET_WM_STATE_TOGGLE = 2L;

	private static final String _NET_WM_STATE = "_NET_WM_STATE";
	private static final String _NET_WM_STATE_DEMANDS_ATTENTION = "_NET_WM_STATE_DEMANDS_ATTENTION";

	@Override
	public void flashIcon () {
		X11 x = X11.INSTANCE;

		if (frameWindow.isFocused() == false && running == false) {
			running = true;
			timer.scheduleAtFixedRate(timerTask, 0, 3000);
		}
	}

	private void stop () {
		timerTask.cancel();
		running = false;
		sendMessage(_NET_WM_STATE_REMOVE, _NET_WM_STATE_DEMANDS_ATTENTION);
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
