
package pl.kotcrab.arget.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.gui.components.ESCClosableDialog;

public class ViewTextFileDialog extends ESCClosableDialog {

	public ViewTextFileDialog (Window owner, String title, String text) {
		super(owner, ModalityType.APPLICATION_MODAL);
		
		setBounds(100, 100, 450, 300);
		setPositionToCenter(owner);
		setTitle(App.APP_NAME + " - " + title);

		JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JTextArea textArea = new JTextArea(text);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		textArea.setEditable(false);
		
		contentPanel.add(new JScrollPane(textArea));

		JButton okButton = new JButton("OK");
		buttonPane.add(okButton);
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});
		
		setVisible(true);
	}

}
