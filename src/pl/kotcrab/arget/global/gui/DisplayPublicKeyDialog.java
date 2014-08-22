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

package pl.kotcrab.arget.global.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import pl.kotcrab.arget.gui.ESCClosableDialog;

public class DisplayPublicKeyDialog extends ESCClosableDialog {

	public DisplayPublicKeyDialog (JFrame frame, final String publicKey) {
		super(frame, true);
		setTitle("Public Key View");
		setSize(450, 300);
		setPositionToCenter(frame);
		setMinimumSize(new Dimension(300, 180));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainPanel.setLayout(new BorderLayout(0, 5));

		JTextArea keyTextArea = new JTextArea();
		keyTextArea.setEditable(false);
		keyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		keyTextArea.setLineWrap(true);
		keyTextArea.setText(publicKey);

		mainPanel.add(new JLabel("This is your public key, you can safely distrubute it.", SwingConstants.CENTER),
			BorderLayout.NORTH);
		mainPanel.add(new JScrollPane(keyTextArea), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		JButton closeButton = new JButton("Close");
		final JButton copyButton = new JButton("Copy");
		buttonPanel.add(copyButton);
		buttonPanel.add(closeButton);

		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				copyButton.setText("Copied");
				StringSelection selection = new StringSelection(publicKey);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});

		setVisible(true);
	}
}
