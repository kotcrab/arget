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

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.exchange.internal.session.InternalSessionExchange;
import pl.kotcrab.arget.comm.exchange.internal.session.data.MessageTransfer;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelHideNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelShowNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingStartedNotification;
import pl.kotcrab.arget.event.Event;
import pl.kotcrab.arget.event.EventListener;
import pl.kotcrab.arget.gui.CenterPanel;
import pl.kotcrab.arget.gui.MainWindow;
import pl.kotcrab.arget.gui.ScrollLockEvent;
import pl.kotcrab.arget.gui.ScrollLockStatusRequestEvent;
import pl.kotcrab.arget.gui.session.msg.MessageComponent;
import pl.kotcrab.arget.gui.session.msg.MsgType;
import pl.kotcrab.arget.gui.session.msg.TextMessage;
import pl.kotcrab.arget.gui.session.msg.TypingMessage;
import pl.kotcrab.arget.server.ContactInfo;
import pl.kotcrab.arget.util.SwingUtils;
import pl.kotcrab.arget.util.Timer;
import pl.kotcrab.arget.util.TimerListener;

//FIXME you can't send msg with tab only, tab + spaces, and mark down signs only: _ and *
//TODO better scrolllock
public class SessionPanel extends CenterPanel implements EventListener {
	private SessionPanel instance;

	private ContactInfo contact;
	private UUID id;
	private SessionPanelListener listener;

	private InnerMessagePanel innerPanel;
	private JTextArea inputTextArea;
	private JScrollPane pane;

	private TypingMessage typingComponent;
	private boolean typingShowed;

	private Timer typingTimer;
	private boolean typing = false;

	private boolean remoteCenterPanel = false;

	private boolean mouseOnScrollbar = false;
	private boolean mouseOnPane = false;
	private boolean mousePressed;

	private boolean structureChanged = false;
	private MouseWheelEvent lastWheelEvent;
	private MouseEvent lastDragEvent;

	private boolean scrollLockEnabled = false;
	private int lastScrollBarValue;

	private boolean sessionValid;

	private DropTarget dropTarget = new DropTarget() {
		@Override
		public synchronized void drop (DropTargetDropEvent evt) {
			try {
				evt.acceptDrop(DnDConstants.ACTION_COPY);

				// nothing we can do about it, casting to generic list will be unsafe
				@SuppressWarnings("unchecked")
				List<File> droppedFiles = (List<File>)evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				if (droppedFiles.size() > 1) return;

				File file = droppedFiles.get(0);
				listener.sendFile(instance, file);

			} catch (IOException | UnsupportedFlavorException e) {
				Log.exception(e);
			}
		}
	};

	// TODO create this on EDT! (maybe not required)
	public SessionPanel (ContactInfo contact, UUID sessionId, final SessionPanelListener listener) {
		instance = this;
		this.contact = contact;
		this.id = sessionId;
		this.listener = listener;

		typingTimer = new Timer("TypingTimer");

		setLayout(new BorderLayout(0, 0));
		setMinimumSize(new Dimension(300, 0));

		innerPanel = new InnerMessagePanel();
		innerPanel.setBorder(new EmptyBorder(0, 0, 3, 0));
		innerPanel.setBackground(new Color(229, 229, 229));

		pane = new JScrollPane(innerPanel);
		pane.setBorder(null);
		pane.getVerticalScrollBar().setUnitIncrement(26);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		pane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged (AdjustmentEvent e) {
				Adjustable a = e.getAdjustable();

				if (scrollLockEnabled && structureChanged) {
					a.setValue(lastScrollBarValue);
					lastWheelEvent = null;
					lastDragEvent = null;
				}

				if (lastWheelEvent != null) {
					lastScrollBarValue = a.getValue();
				}

				if (lastDragEvent != null) {
					lastScrollBarValue = a.getValue();
				}

				if (scrollLockEnabled && lastWheelEvent == null && lastDragEvent == null && structureChanged == false) {
					a.setValue(lastScrollBarValue);
				}

				if (scrollLockEnabled && lastWheelEvent != null && structureChanged == false) {
					if (lastWheelEvent.getWheelRotation() > 0)
						lastScrollBarValue = a.getValue() + 40;
					else
						lastScrollBarValue = a.getValue() - 40;
				}

				if (scrollLockEnabled == false) a.setValue(a.getMaximum());

				lastWheelEvent = null;
				lastDragEvent = null;
				structureChanged = false;

			}
		});

// /test code that automatically sends msg
// remove this after finishing scroll lock
// Thread t = new Thread(new Runnable() {
// @Override
// public void run () {
// ThreadUtils.sleep(3000);
//
// //for (;;) {
// for (int i = 0; i < 8; i++) {
// System.out.println("add");
// addMessage(new TextMessage(Msg.RIGHT, String.valueOf(new Random().nextFloat()), isRemoteCenterPanel()));
// // send(new MessageTransfer(id, "HAAHA"));
// // ThreadUtils.sleep(300);
// ThreadUtils.sleep(700);
// }
// }
// });
// t.setDaemon(true);
// t.start();

		pane.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved (MouseWheelEvent e) {
				lastWheelEvent = e;
			}
		});

		pane.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited (MouseEvent e) {
				mouseOnPane = false;
			}

			@Override
			public void mouseEntered (MouseEvent e) {
				mouseOnPane = true;
			}

		});

		pane.getVerticalScrollBar().addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved (MouseEvent e) {

			}

			@Override
			public void mouseDragged (MouseEvent e) {
				lastDragEvent = e;
			}
		});

		pane.getVerticalScrollBar().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited (MouseEvent e) {
				mouseOnScrollbar = false;
			}

			@Override
			public void mouseEntered (MouseEvent e) {
				mouseOnScrollbar = true;
			}

			@Override
			public void mousePressed (MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) mousePressed = true;
			}

			@Override
			public void mouseReleased (MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) mousePressed = false;

			}

		});

		add(pane, BorderLayout.CENTER);

		inputTextArea = new JTextArea();

		add(inputTextArea, BorderLayout.SOUTH);

		typingComponent = new TypingMessage();
		inputTextArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		inputTextArea.setBorder(new LineBorder(new Color(192, 192, 192)));
		inputTextArea.setLineWrap(true);
		inputTextArea.setWrapStyleWord(true);
		inputTextArea.setDropTarget(dropTarget);
		SwingUtils.createDefaultPopupMenu(inputTextArea);

		inputTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed (KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String msg = inputTextArea.getText();

					if (msg.length() > 50000) {
						JOptionPane.showMessageDialog(MainWindow.instance, "Hey, you are trying to send message that "
							+ "is over 50000 characters! Thats too much for us, sorry. Try splitting it.");

						stopTypingTimer();
						e.consume();
						return;
					}

					if (msg.equals("") == false && isOnlySpacesInString(msg) == false) {

						if (isOnlySlashInString(msg) == false) {
							msg = removeSlashes(msg);

							addMsg(new TextMessage(MsgType.RIGHT, msg, isRemoteCenterPanel()));
							send(new MessageTransfer(id, msg));
						}
					}

					inputTextArea.setText("");
					revalidate();
					repaint();

					stopTypingTimer();

					e.consume();
					return;
				}

				// TODO ignore others character, accept only letters, upper case letters, symbol, numbers
				if (typing == false) {
					send(new TypingStartedNotification(id));
					typing = true;
				}

				typingTimer.cancel();
			}

			private void stopTypingTimer () {
				send(new TypingFinishedNotification(id));
				typing = false;
				typingTimer.cancel();
			}

			private String removeSlashes (String msg) {
				if (msg.startsWith("/") == false) return msg;

				for (int i = 0; i < msg.length(); i++) {
					if (msg.charAt(i) != '/') return msg.substring(i);
				}

				return msg;
			}

			private boolean isOnlySlashInString (String msg) {
				for (int i = 0; i < msg.length(); i++) {
					if (msg.charAt(i) != '/') return false;
				}

				return true;
			}

			private boolean isOnlySpacesInString (String msg) {
				for (int i = 0; i < msg.length(); i++) {
					if (msg.charAt(i) != ' ') return false;
				}

				return true;
			}

			@Override
			public void keyReleased (KeyEvent e) {
				typingTimer.schedule(new TimerListener() {

					@Override
					public void doTask () {
						send(new TypingFinishedNotification(id));
						typing = false;
					}

				}, 1500);
			}
		});

		App.eventBus.register(this);
		App.eventBus.post(new ScrollLockStatusRequestEvent());
	}

	private void send (InternalSessionExchange ex) {
		if (sessionValid) listener.send(ex);
	}

	public void setUUID (UUID id) {
		this.id = id;
	}

	public UUID getUUID () {
		return id;
	}

	public void disableInput () {
		sessionValid = false;
		inputTextArea.setBackground(UIManager.getColor("TextField.disabledBackground"));
		inputTextArea.setText("");
		inputTextArea.setEnabled(false);
	}

	public void enableInput () {
		sessionValid = true;
		inputTextArea.setBackground(Color.WHITE);
		inputTextArea.setEnabled(true);
	}

	public void showTyping () {

		structureChanged = true;
		lastScrollBarValue = pane.getVerticalScrollBar().getValue();

		innerPanel.add(typingComponent);
		typingShowed = true;

		refreshPanel();
	}

	public void hideTyping () {
		structureChanged = true;
		lastScrollBarValue = pane.getVerticalScrollBar().getValue();

		innerPanel.remove(typingComponent);
		typingShowed = false;

		refreshPanel();
	}

	public void addMsg (final MessageComponent comp) {
		structureChanged = true;
		lastScrollBarValue = pane.getVerticalScrollBar().getValue();

		if (typingShowed)
			innerPanel.add(comp, innerPanel.getComponentCount() - 1);
		else
			innerPanel.add(comp);

		refreshPanel();
	}

	public void clear () {
		structureChanged = true;
		innerPanel.removeAll();

		refreshPanel();
	}

	private void refreshPanel () {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run () {
				innerPanel.revalidate();
				innerPanel.repaint();
			}
		});

	}

	public ContactInfo getContact () {
		return contact;
	}

	@Override
	protected void addImpl (Component comp, Object constraints, int index) {
		if (comp instanceof MessageComponent)
			throw new IllegalArgumentException("MessageComponent can't be added here, use addMsg method!");

		super.addImpl(comp, constraints, index);
	}

	public void setRemoteCenterPanel (boolean isCenter) {
		remoteCenterPanel = isCenter;

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run () {
				if (remoteCenterPanel) {
					innerPanel.markAllAsRead();
					refreshPanel();
				}
			}
		});

	}

	public boolean isRemoteCenterPanel () {
		return remoteCenterPanel;
	}

	@Override
	public void onShow () {
		send(new RemotePanelShowNotification(id));
	}

	@Override
	public void onHide () {
		send(new RemotePanelHideNotification(id));
	}

	@Override
	public String getTitle () {
		return contact.name;
	}

	private class InnerMessagePanel extends JPanel {
		public InnerMessagePanel () {
			setLayout(new LeftRightLayout());
		}

		@Override
		public void addImpl (Component comp, Object constraints, int index) {
			if (comp instanceof MessageComponent == false)
				throw new IllegalArgumentException("Component must be instance of MessageComponent");

			super.addImpl(comp, constraints, index);
		}

		public void markAllAsRead () {
			Component[] componets = getComponents();
			for (int i = 0; i < componets.length; i++) {
				((MessageComponent)componets[i]).setRead(true);
			}
		}

	}

	@Override
	public void onEvent (Event event) {
		if (event instanceof ScrollLockEvent) {
			ScrollLockEvent e = (ScrollLockEvent)event;
			scrollLockEnabled = e.enabled;
		}
	}

}
