
package pl.kotcrab.arget.global.gui;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.LoggerListener;
import pl.kotcrab.arget.gui.CenterPanel;

public class LoggerPanel extends CenterPanel {
	private JTextArea textArea;

	public LoggerPanel () {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		Log.setListener(new LoggerListener() {

			@Override
			public void log (String msg) {
				appendToLog(msg);
			}

			@Override
			public void err (String msg) {
				appendToLog("ERROR: " + msg);
			}
		});
	}

	@Override
	public String getTitle () {
		return "Log";
	}

	private void appendToLog (String msg) {
		try {
			textArea.getDocument().insertString(textArea.getDocument().getLength(), msg + "\n", null);
		} catch (BadLocationException e) {
		}

	}

}
