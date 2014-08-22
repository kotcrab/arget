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

package pl.kotcrab.arget.server.session.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JPanel;

import pl.kotcrab.arget.Log;
import pl.kotcrab.arget.comm.Msg;
import pl.kotcrab.arget.gui.components.ShadowBorder;

public abstract class MessageComponent extends JPanel {
	protected static FontMetrics textFontMetrics;
	protected static Font textFont;
	protected static Font smallTextFont;

	private int type;
	private int requestedWidth = 150;

	/** Has this message been read already */
	private boolean read;

	static {
		String fontName = "Tahoma";
		textFont = new Font(fontName, Font.PLAIN, 13);
		smallTextFont = new Font(fontName, Font.PLAIN, 10);

		Canvas c = new Canvas();
		textFontMetrics = c.getFontMetrics(textFont);
	}

	public MessageComponent (int type) {
		this.type = type;

		switch (type) {
		case Msg.LEFT:
			setBackground(Color.WHITE);
			break;
		case Msg.RIGHT:
			setBackground(new Color(204, 255, 204)); // green
			break;
		case Msg.SYSTEM:
			setBackground(new Color(204, 255, 255)); // blue
			break;
		case Msg.ERROR:
			setBackground(new Color(255, 153, 153)); // red
			break;
		default:
			Log.w("Unknown MessageComponent type! Got: " + type);
			setBackground(Color.WHITE);
			break;
		}

		setBorder(ShadowBorder.getSharedInstance());
	}

	public void setRequestWidth (int newWidth) {
		this.requestedWidth = newWidth;
	}

	public int getRequestedWidth () {
		return requestedWidth;
	}

	public int getSide () {
		return type;
	}

	public boolean isRead () {
		return read;
	}

	public void setRead (boolean read) {
		this.read = read;
	}

}
