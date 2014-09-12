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

package pl.kotcrab.arget.test.manual;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.gui.session.SessionPanel;
import pl.kotcrab.arget.gui.session.msg.MsgType;
import pl.kotcrab.arget.gui.session.msg.TextMessage;

public class SessionPanelTest extends JFrame {

	private JPanel contentPane;

	/** Launch the application. */
	public static void main (String[] args) {
		App.init();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run () {
				try {
					SessionPanelTest frame = new SessionPanelTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Create the frame. */
	@SuppressWarnings("deprecation")
	public SessionPanelTest () {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		final SessionPanel panel = new SessionPanel(null, null, null);
		contentPane.add(panel);
		setContentPane(contentPane);

		panel.addMessage(new TextMessage(MsgType.LEFT, "Lorem ipsum dolor sit amet"));
		panel.addMessage(new TextMessage(MsgType.RIGHT, "Lorem ipsum dolor sit amet"));
		panel.addMessage(new TextMessage(MsgType.SYSTEM, "System msg"));
		panel.addMessage(new TextMessage(MsgType.ERROR, "System error msg"));
		// FIXME too long text without spaces break layouts
		panel
			.addMessage(new TextMessage(
				MsgType.RIGHT,
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tincidunt, eros id convallis ultricies, nulla mi eleifend velit,"
					+ " vel placerat ante urna interdum velit. Nunc tincidunt eros ac consectetur varius. Aenean a pretium est, id tincidunt eros."));

		panel.addMessage(new TextMessage(MsgType.RIGHT, "Linki: https://www.kotcrab.pl/ Formatowanie: *Lorem* _ipsum_"));
		panel.addMessage(new TextMessage(MsgType.RIGHT, "Linki: https://www.youtube.com/watch?v=3vI_7os2V_o"));

		panel
			.addMessage(new TextMessage(
				MsgType.RIGHT,
				"Linki: http://mashable.com/2014/08/14/watch-surgery-on-the-oculus-rift-but-maybe-do-it-after-lunch/?utm_cid=mash-com-G+-main-link"));

		panel.addMessage(new TextMessage(MsgType.LEFT, "Obrazki w okienku rozmowy i przesy�anie plik�w"));
		// panel.addMessage(new ImageMessage(Msg.LEFT, ImageUitls.read(new File("avatar.jpg")), ""));

		// panel.addMessage(new FileTransferMessage(new SendFileTask(null, new File("test.txt"), false)));
		// panel.addMessage(new TextMessage(Msg.SYSTEM, "Combined test: _abc *abc* a_"));
		// panel.addMessage(new TextMessage(Msg.SYSTEM, "Error test: *abc ab *c _a*"));

		panel.showTyping();

// new Thread(new Runnable() {
//
// @Override
// public void run () {
// ThreadUtils.sleep(1000);
// panel.addMessage(new TextMessage(Msg.ERROR, "Lorem ipsum dolor sit amet"));
// ThreadUtils.sleep(3000);
// panel.hideTyping();
// }
// }).start();

	}
}
