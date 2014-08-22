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

package pl.kotcrab.arget.global.session.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.App;
import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.Msg;
import pl.kotcrab.arget.comm.file.FileTransferTask;
import pl.kotcrab.arget.comm.file.FileTransferTask.Type;
import pl.kotcrab.arget.comm.file.SendFileTask;
import pl.kotcrab.arget.util.FileUitls;

import com.alee.laf.button.WebButton;

//TODO too long name breaks layout
//TODO change sapace in status to empty border
public class FileTransferMessage extends MessageComponent {
	public enum Status {
		REQUEST_SEND, REQUEST_RECEIVED, INPROGRESS, DONE, CANCELED
	};

	private FileTransferMessageListener listener;

	private FileTransferTask task;
	private UUID taskId;

	private JLabel statusLabel;
	private JProgressBar progressBar;
	private WebButton acceptButton;
	private WebButton cancelButton;

	public FileTransferMessage (SendFileTask sendTask) {
		this(sendTask, sendTask.getFile().getName(), sendTask.getFile().length());
	}

	/** @wbp.parser.constructor */
	public FileTransferMessage (FileTransferTask transferTask, String fileName, long fileSize) {
		super(Msg.SYSTEM);
		task = transferTask;
		taskId = task.getId();

		setLayout(new MigLayout("", "[fill][][]", "[][][]"));

		JLabel fileInfoLabel = new JLabel(" File: " + fileName + " Size: " + FileUitls.readableFileSize(fileSize));
		fileInfoLabel.setFont(textFont);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);
		progressBar.setString("Please wait...");

		cancelButton = new WebButton(new ImageIcon(App.getResource("/data/cross.png")));
		cancelButton.setRolloverDecoratedOnly(true);
		cancelButton.setDrawFocus(false);

		acceptButton = new WebButton(new ImageIcon(App.getResource("/data/tick.png")));
		acceptButton.setRolloverDecoratedOnly(true);
		acceptButton.setDrawFocus(false);

		statusLabel = new JLabel(" Waiting...");
		statusLabel.setFont(smallTextFont);
		statusLabel.setForeground(Color.GRAY);

		add(fileInfoLabel, "cell 0 0");
		add(progressBar, "cell 0 1");
		add(cancelButton, "cell 1 0 1 3");
		if (task.getType() == Type.RECEIVE) add(acceptButton, "cell 2 0 1 3");
		add(statusLabel, "cell 0 2");

		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (listener != null) listener.buttonFileAccepted(task);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				if (listener != null) listener.buttonFileRejectedOrCanceled(task);
			}
		});
	}

	public void setListener (FileTransferMessageListener listener) {
		this.listener = listener;
	}

	public void setStatus (Status status) {
		switch (status) {
		case REQUEST_SEND:
			statusLabel.setText(" Request sent...");
			break;
		case REQUEST_RECEIVED:
			statusLabel.setText(" Accept file transfer?");
			break;
		case INPROGRESS:
			statusLabel.setText(" Transfer in progress...");
			progressBar.setIndeterminate(false);
			progressBar.setStringPainted(true);
			progressBar.setString(null);
			progressBar.setValue(0);
			safelyRemove(acceptButton);
			break;
		case DONE:
			statusLabel.setText(" Done.");
			safelyRemove(progressBar);
			safelyRemove(cancelButton);
			break;
		case CANCELED:
			statusLabel.setText(" Canceled or rejected.");
			safelyRemove(progressBar);
			safelyRemove(cancelButton);
			safelyRemove(acceptButton);
			break;
		default:
			Log.w("Unknown FileTransferMessageComponent status! Got: " + status);
			break;

		}
	}

	public void setProgressBarValue (int val) {
		progressBar.setValue(val);
	}

	public UUID getTaskUUID () {
		return taskId;
	}

	private void safelyRemove (final Component comp) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run () {
				remove(comp);
			}
		});
	}

}
