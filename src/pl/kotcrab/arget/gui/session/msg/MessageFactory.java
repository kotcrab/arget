
package pl.kotcrab.arget.gui.session.msg;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.file.FileTransferTask;
import pl.kotcrab.arget.comm.file.SendFileTask;

public class MessageFactory {

	public ImageMessage image(final MsgType type, final BufferedImage image, final String fileName) {
		final AtomicReference<ImageMessage> msg = new AtomicReference<ImageMessage>();

		runOnEDT(new Runnable() {
			@Override
			public void run () {
				msg.set(new ImageMessage(type, image, fileName));
			}
		});

		return msg.get();
	}

	
	public FileTransferMessage fileTransfer (SendFileTask sendTask) {
		return fileTransfer(sendTask, sendTask.getFile().getName(), sendTask.getFile().length());
	}

	public FileTransferMessage fileTransfer (final FileTransferTask transferTask, final String fileName, final long fileSize) {
		final AtomicReference<FileTransferMessage> msg = new AtomicReference<FileTransferMessage>();

		runOnEDT(new Runnable() {
			@Override
			public void run () {
				msg.set(new FileTransferMessage(transferTask, fileName, fileSize));
			}
		});

		return msg.get();
	}

	public TypingMessage typing () {
		final AtomicReference<TypingMessage> msg = new AtomicReference<TypingMessage>();

		runOnEDT(new Runnable() {
			@Override
			public void run () {
				msg.set(new TypingMessage());
			}
		});

		return msg.get();
	}
	
	public TextMessage text (MsgType type, String text) {
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
