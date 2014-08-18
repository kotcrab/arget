
package pl.kotcrab.arget.global.session.gui;

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
