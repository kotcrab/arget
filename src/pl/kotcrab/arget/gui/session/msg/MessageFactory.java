
package pl.kotcrab.arget.gui.session.msg;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import pl.kotcrab.arget.Log;

public class MessageFactory {

	public TextMessage text (final MsgType type, final String text) {
		return text(type, text, true);
	}

	public TextMessage text (final MsgType type, final String text, final boolean markAsRead) {
		final AtomicReference<TextMessage> msg = new AtomicReference<TextMessage>();

		runOnEDT(new Runnable() {
			@Override
			public void run () {
				msg.set(new TextMessage(type, text, markAsRead));
			}
		});

		return msg.get();
	}

	private void runOnEDT (Runnable runnable) {

		if (EventQueue.isDispatchThread())
			runnable.run();
		else {
			try {
				EventQueue.invokeAndWait(runnable);
			} catch (InvocationTargetException | InterruptedException e) {
				Log.exception(e);
			}
		}
	}
}
