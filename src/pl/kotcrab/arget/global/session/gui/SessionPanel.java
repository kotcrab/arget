
package pl.kotcrab.arget.global.session.gui;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelHideNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.RemotePanelShowNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingFinishedNotification;
import pl.kotcrab.arget.comm.exchange.internal.session.data.TypingStartedNotification;
import pl.kotcrab.arget.global.ContactInfo;
import pl.kotcrab.arget.gui.CenterPanel;
import pl.kotcrab.arget.util.Timer;
import pl.kotcrab.arget.util.TimerListener;

//TODO you can't send msg with tab only, tab + spaces, and mark down signs only: _ and *
public class SessionPanel extends CenterPanel {
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

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public SessionPanel (ContactInfo contact, final UUID id, final SessionPanelListener listener) {
		instance = this;
		this.contact = contact;
		this.id = id;
		this.listener = listener;

		typingTimer = new Timer("TypingTimer");

		setLayout(new BorderLayout(0, 0));
		setMinimumSize(new Dimension(300, 0));

		innerPanel = new InnerMessagePanel();
		innerPanel.setBorder(new EmptyBorder(0, 0, 3, 0));
		innerPanel.setBackground(new Color(229, 229, 229));

		pane = new JScrollPane(innerPanel);
		pane.setBorder(null);
		pane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		pane.getVerticalScrollBar().setUnitIncrement(26);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		pane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged (AdjustmentEvent e) {

				if (mouseOnScrollbar == false) {
					Adjustable a = e.getAdjustable();
					a.setValue(a.getMaximum());
				}
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

		});

		add(pane, BorderLayout.CENTER);

		inputTextArea = new JTextArea();

// JPanel inputTextAreaPanel = new JPanel(new BorderLayout());
// inputTextAreaPanel.add(inputTextArea, BorderLayout.CENTER);
// inputTextAreaPanel.setBackground(new Color(229, 229, 229));

		add(inputTextArea, BorderLayout.SOUTH);

		typingComponent = new TypingMessage();
		inputTextArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		inputTextArea.setBorder(new LineBorder(new Color(192, 192, 192)));
		inputTextArea.setLineWrap(true);
		inputTextArea.setWrapStyleWord(true);

		inputTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed (KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String msg = inputTextArea.getText();

					if (msg.equals("") == false && isOnlySpacesInString(msg) == false) {

						if (isOnlySlashInString(msg) == false) {
							msg = removeSlashes(msg);

							listener.messageTyped(instance, msg);
						}
					}

					inputTextArea.setText("");
					revalidate();
					repaint();

					listener.send(new TypingFinishedNotification(id));
					typing = false;
					typingTimer.cancel();

					e.consume();
					return;
				}

				// TODO ignore others character, accept only letters, upper case letters, symbol, numbers
				if (typing == false) {
					listener.send(new TypingStartedNotification(id));
					typing = true;
				}

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
						listener.send(new TypingFinishedNotification(id));
						typing = false;
					}

				}, 1500);
			}
		});

		inputTextArea.setDropTarget(dropTarget);
	}

	public void setUUID (UUID id) {
		this.id = id;
	}

	public UUID getUUID () {
		return id;
	}

	public void disableInput () {
		inputTextArea.setBackground(UIManager.getColor("TextField.disabledBackground"));
		inputTextArea.setText("");
		inputTextArea.setEnabled(false);
	}

	public void enableInput () {
		inputTextArea.setBackground(Color.WHITE);
		inputTextArea.setEnabled(true);
	}

	public void showTyping () {
		innerPanel.add(typingComponent);
		typingShowed = true;
		refreshPanel();
	}

	public void hideTyping () {
		innerPanel.remove(typingComponent);
		typingShowed = false;
		refreshPanel();
	}

	public void addMessage (MessageComponent comp) {

		if (typingShowed)
			innerPanel.add(comp, innerPanel.getComponentCount() - 1);
		else
			innerPanel.add(comp);

		refreshPanel();

	}

	private void refreshPanel () {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run () { // TODO wiadomosci sie nie pokazujo (maybe fixed)
				innerPanel.validate();
				innerPanel.revalidate();
				innerPanel.repaint();
			}
		});
	}

	public void clear () {
		innerPanel.removeAll();
	}

	public ContactInfo getContact () {
		return contact;
	}

	@Override
	protected void addImpl (Component comp, Object constraints, int index) {
		if (comp instanceof MessageComponent)
			throw new IllegalArgumentException("MessageComponent can't be added here, use addMessage function!");

		super.addImpl(comp, constraints, index);
	}

	public void setRemoteCenterPanel (boolean isCenter) {
		remoteCenterPanel = isCenter;

		if (remoteCenterPanel) innerPanel.markAllAsRead();
	}

	public boolean isRemoteCenterPanel () {
		return remoteCenterPanel;
	}

	@Override
	public void onShow () {
		listener.send(new RemotePanelShowNotification(id));
	}

	@Override
	public void onHide () {
		listener.send(new RemotePanelHideNotification(id));
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

}
