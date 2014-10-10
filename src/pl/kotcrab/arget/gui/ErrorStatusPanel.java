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

package pl.kotcrab.arget.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import pl.kotcrab.arget.App;
import pl.kotcrab.arget.event.Event;
import pl.kotcrab.arget.event.EventListener;
import pl.kotcrab.arget.event.LoggerPanelEvent;

public class ErrorStatusPanel extends JPanel implements EventListener {
	private static final String WARNING_MSG = " warning message(s)";
	private static final String ERROR_MSG = " error(s) happened";
	private static final String EXCEPTION_MSG = " exception(s) happened";

	private int warningCounter;
	private int errorCounter;
	private int exceptionCounter;

	private JLabel warningLabel;
	private JLabel errorLabel;
	private JLabel exceptionLabel;

	public ErrorStatusPanel () {
		App.eventBus.register(this);
		setMinimumSize(new Dimension(150, 10));
		setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
		setBackground(Color.WHITE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel infoLabel = getJLabel("Please check log! <br>(View -> Show Log)");
		warningLabel = getJLabel(warningCounter + WARNING_MSG);
		errorLabel = getJLabel(errorCounter + ERROR_MSG);
		exceptionLabel = getJLabel(exceptionCounter + EXCEPTION_MSG);

		exceptionLabel.setForeground(Color.RED);
		warningLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		errorLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		exceptionLabel.setBorder(new EmptyBorder(2, 2, 3, 2));

		infoLabel.setAlignmentX(0.5f);
		warningLabel.setAlignmentX(0.5f);
		errorLabel.setAlignmentX(0.5f);
		exceptionLabel.setAlignmentX(0.5f);

		add(infoLabel);
		add(warningLabel);
		add(errorLabel);
		add(exceptionLabel);

		setVisible(false);
	}

	private JLabel getJLabel (String text) {
		return new JLabel(getText(text), SwingConstants.CENTER);
	}

	private String getText (String baseText) {
		return "<html><center>" + baseText + "</center></html>";
	}

	private void setupMsg () {
		warningLabel.setVisible(false);
		errorLabel.setVisible(false);
		exceptionLabel.setVisible(false);

		if (warningCounter > 0) warningLabel.setVisible(true);
		if (errorCounter > 0) errorLabel.setVisible(true);
		if (exceptionCounter > 0) exceptionLabel.setVisible(true);

		warningLabel.setText(warningCounter + WARNING_MSG);
		errorLabel.setText(errorCounter + ERROR_MSG);
		exceptionLabel.setText(exceptionCounter + EXCEPTION_MSG);

		if (warningCounter > 0 || errorCounter > 0 || exceptionCounter > 0) setVisible(true);

	}

	public void clear () {
		warningCounter = 0;
		exceptionCounter = 0;
		errorCounter = 0;
		setVisible(false);
	}

	@Override
	public void onEvent (Event e) {
		if (e instanceof LoggerPanelEvent) {
			LoggerPanelEvent event = (LoggerPanelEvent)e;

			switch (event.type) {
			case WARNING:
				warningCounter++;
				break;
			case ERROR:
				errorCounter++;
				break;
			case EXCEPTION:
				exceptionCounter++;
				break;
			}

			setupMsg();
		}
	}

}
