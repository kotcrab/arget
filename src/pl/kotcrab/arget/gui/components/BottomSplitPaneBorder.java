
package pl.kotcrab.arget.gui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;

public class BottomSplitPaneBorder extends AbstractBorder implements UIResource {
	private static final Insets INSETS = new Insets(0, 0, 1, 1);

	@Override
	public void paintBorder (Component c, Graphics g, int x, int y, int w, int h) {
		g.translate(x, y);
		g.setColor(new ColorUIResource(90, 90, 90));
		g.setColor(new ColorUIResource(255, 255, 255));
		g.drawLine(w - 1, 1, w - 1, h - 1);
		g.drawLine(1, h - 1, w - 1, h - 1);
		g.translate(-x, -y);
	}

	@Override
	public Insets getBorderInsets (Component c) {
		return INSETS;
	}

	@Override
	public Insets getBorderInsets (Component c, Insets insets) {
		insets.top = INSETS.top;
		insets.left = INSETS.left;
		insets.bottom = INSETS.bottom;
		insets.right = INSETS.right;
		return insets;
	}
}
