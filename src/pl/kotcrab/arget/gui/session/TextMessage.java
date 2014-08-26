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

import java.awt.Color;
import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.commons.lang3.StringEscapeUtils;

import pl.kotcrab.arget.gui.components.WrapHTMLEditorKit;
import pl.kotcrab.arget.util.DesktopUtils;

public class TextMessage extends MessageComponent {
	private JTextPane textPane;
	private JLabel timeLabel;

	private String originalText;
	private String processedText;

	public TextMessage (int type, String text, boolean markAsRead) {
		super(type);

		textPane = new JTextPane();
		textPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		textPane.setEditorKit(new WrapHTMLEditorKit());
		textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		textPane.setEditable(false);
		textPane.setBackground(null);
		textPane.setBorder(new EmptyBorder(3, 3, 0, 0));
		textPane.setFont(textFont);

		textPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate (HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {

					if (DesktopUtils.openWebsite(hle.getURL()) == false)
						JOptionPane.showMessageDialog(null, "Invalid URL: " + hle.getURL(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		setText(text);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(textPane);

		DateFormat dateFormat = new SimpleDateFormat("[HH:mm] ");
		Date date = new Date();

		String textToShow = dateFormat.format(date);

		if (markAsRead == false) textToShow += "*";

		timeLabel = new JLabel(textToShow);
		timeLabel.setBorder(new EmptyBorder(0, 3, 3, 0));
		timeLabel.setForeground(Color.GRAY);
		timeLabel.setFont(smallTextFont);
		add(timeLabel);
	}

	public TextMessage (int type, String text) {
		this(type, text, true);
	}

	public void setText (String newText) {
		originalText = newText;
		processedText = processText(originalText);
		setLabelText();
	}

	@Override
	public void setRequestWidth (int newWidth) {
		super.setRequestWidth(newWidth);
		setLabelText();
	}

	private void setLabelText () {
		int actualTextWidth = textFontMetrics.stringWidth(originalText);
		actualTextWidth -= actualTextWidth * 3 / 10; // stupid FontMetrics is lying by about 30%

		// TODO niemozna zrobic new line

		if (actualTextWidth > getRequestedWidth())
			textPane.setText(String.format("<html><div style=\"width:%dpx; \">%s</div></html>", getRequestedWidth(), processedText));
		else
			textPane.setText("<html>" + processedText + "</html>");

// TODO font is not set
		// MutableAttributeSet set = textPane.getInputAttributes();
		// StyleConstants.setFontFamily(set, "Tahoma");
		// StyleConstants.setFontSize(set, 13);
		// StyledDocument doc = textPane.getStyledDocument();
		// doc.setCharacterAttributes(0, doc.getLength() + 1, set, false);
	}

	private String processText (String text) {
		StringBuilder builder = new StringBuilder();

		String[] parts = text.split("\\s{1}(?!\\s)"); // split by space but preserve two or more spaces

		for (String item : parts) {
			item = StringEscapeUtils.escapeHtml4(item);

			try {
				URL url = new URL(item);
				item = item.replaceAll("_", "&#95;");
				item = item.replaceAll("\\*", "&#42;");
				url = new URL(item);
				builder.append("<a href=\"" + url + "\">" + url + "</a> ");
			} catch (MalformedURLException e) {
				builder.append(item + " ");
			}
		}

		String result = builder.toString().replace(" ", "&nbsp;");
		result = markdownReplace(result, "*", "\\*", "<b>", "</b>");
		result = markdownReplace(result, "_", "_", "<em>", "</em>");

		return result;
	}

	private String markdownReplace (String text, String searchFor, String regexSearch, String replaceFirstWith,
		String replaceSecondWith) {
		int firstAppearIndex = -1;
		int lastAppearIndex = -1;

		int lastIndex = 0;
		while (lastIndex != -1) {

			lastIndex = text.indexOf(searchFor, lastIndex);

			if (lastIndex != -1) {
				lastIndex++;

				if (firstAppearIndex == -1)
					firstAppearIndex = lastIndex;
				else
					lastAppearIndex = lastIndex;

				if (lastAppearIndex != -1) {
					text = text.replaceFirst(regexSearch, replaceFirstWith).replaceFirst(regexSearch, replaceSecondWith);
					firstAppearIndex = -1;
					lastAppearIndex = -1;
				}
			}
		}

		return text;
	}

	@Override
	public void setRead (boolean read) {
		if (read && isRead() == false && timeLabel != null) {
			timeLabel.setText(timeLabel.getText().replace("*", "")); // remove * which symbolizes unread message
			super.setRead(true);
		}
	}

}
