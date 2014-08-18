
package pl.kotcrab.arget.global.session.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.comm.Msg;

public class TypingMessage extends MessageComponent {
	public TypingMessage () {
		super(Msg.LEFT);

		JLabel image = new JLabel(new ImageIcon(App.getResource("/data/type.gif")));
		image.setBorder(new EmptyBorder(3, 1, 0, 0));
		add(image);
	}
}
