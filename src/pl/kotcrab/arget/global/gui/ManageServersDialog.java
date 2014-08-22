
package pl.kotcrab.arget.global.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import pl.kotcrab.arget.event.SaveProfileEvent;
import pl.kotcrab.arget.global.ServerInfo;
import pl.kotcrab.arget.gui.ESCClosableDialog;
import pl.kotcrab.arget.profile.Profile;

public class ManageServersDialog extends ESCClosableDialog {
	private ServerInfoListModel listModel;
	private JList<ServerInfo> list;
	private ServerInfo autoconnectInfo;
	private JLabel autoconnectLabel;

	public ManageServersDialog (JFrame frame, Profile profile) {
		super(frame, true);

		this.autoconnectInfo = profile.autoconnectInfo;

		setSize(436, 300);
		setPositionToCenter(frame);
		setMinimumSize(new Dimension(200, 200));
		setTitle("Manage Servers");
		getContentPane().setLayout(new BorderLayout(0, -6));

		listModel = new ServerInfoListModel();
		list = new JList<ServerInfo>();
		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel mainPanel = new JPanel(new MigLayout("", "[341px,grow][65px]", "[23px][23px][][160.00px,grow][]"));
		mainPanel.setBorder(new EmptyBorder(0, 0, -5, 0));
		getContentPane().add(mainPanel, BorderLayout.CENTER);

		JButton addButon = new JButton("Add");
		JButton deleteButton = new JButton("Delete");
		JButton modifyButton = new JButton("Modify");
		JButton autoconnectButton = new JButton("Autoconnect");

		mainPanel.add(addButon, "cell 1 0,growx,aligny top");
		mainPanel.add(deleteButton, "cell 1 1,growx,aligny top");
		mainPanel.add(modifyButton, "cell 1 2,growx,aligny top");
		mainPanel.add(autoconnectButton, "cell 1 3,growx,aligny top");
		mainPanel.add(new JScrollPane(list), "cell 0 0 1 4,grow");

		{
			addButon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					addNewItem();
				}
			});

			// TODO add confirmation
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					deleteSelectedItem();
				}
			});

			autoconnectButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					markAsAutoconnectItem();
				}
			});

			modifyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					modifyItem();
				}
			});
		}

		createBottomPane(profile);

		for (ServerInfo desc : profile.servers)
			listModel.addElement(desc);

		setVisible(true);
	}

	private void createBottomPane (final Profile profile) {
		JPanel bottomPane = new JPanel(new BorderLayout(0, 0));
		bottomPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		getContentPane().add(bottomPane, BorderLayout.SOUTH);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		bottomPane.add(buttonPanel, BorderLayout.EAST);

		JButton cancelButton = new JButton("Cancel");
		JButton okButton = new JButton("OK");

		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		JPanel autoconnectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 9));
		bottomPane.add(autoconnectPanel, BorderLayout.WEST);

		autoconnectLabel = new JLabel();
		autoconnectPanel.add(autoconnectLabel);

		setAutoconnectLabelText();

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				profile.servers = listModel.toArrayList();
				profile.autoconnectInfo = autoconnectInfo;
				MainWindow.eventBus.post(new SaveProfileEvent());
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed (ActionEvent e) {
				dispose();
			}
		});
	}

	private void addNewItem () {
		new CreateServerInfoDialog(this, listModel);
	}

	private void modifyItem () {
		if (isSomethingSelected() == false) return;
		new CreateServerInfoDialog(this, list.getSelectedValue());
		listModel.updateContactsTable();
	}

	private void markAsAutoconnectItem () {
		if (isSomethingSelected() == false) return;

		ServerInfo info = list.getSelectedValue();

		if (info.equals(autoconnectInfo))
			autoconnectInfo = null;
		else
			autoconnectInfo = info;

		setAutoconnectLabelText();
	}

	private void deleteSelectedItem () {
		if (isSomethingSelected() == false) return;

		ServerInfo info = list.getSelectedValue();

		if (info.equals(autoconnectInfo)) {
			autoconnectInfo = null;
			setAutoconnectLabelText();
		}

		listModel.removeElement(info);
	}

	private boolean isSomethingSelected () {
		if (list.getSelectedValue() == null) {
			JOptionPane.showMessageDialog(this, "Nothing selected!", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	private void setAutoconnectLabelText () {
		if (autoconnectInfo == null)
			autoconnectLabel.setText("Autoconnect disabled");
		else
			autoconnectLabel.setText("Autoconnect to: " + autoconnectInfo.name);
	}

	private class ServerInfoListModel extends DefaultListModel<ServerInfo> implements CreateServerDialogFinished {
		public void updateContactsTable () {
			this.fireContentsChanged(this, 0, getSize() - 1);
		}

		public ArrayList<ServerInfo> toArrayList () {
			ArrayList<ServerInfo> list = new ArrayList<ServerInfo>(getSize());

			for (int i = 0; i < getSize(); i++)
				list.add(getElementAt(i));

			return list;
		}

		@Override
		public void finished (ServerInfo desc) {
			addElement(desc);
		}
	}

}
