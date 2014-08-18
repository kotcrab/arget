
package pl.kotcrab.arget.global.session.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import pl.kotcrab.arget.comm.Msg;

public class LeftRightLayout implements LayoutManager {
	private int vgap = 2;

	@Override
	public void layoutContainer (Container parent) {

		Insets insets = parent.getInsets();
		int maxWidth = parent.getWidth() - (insets.left + insets.right);
		int previousHeight = 0;
		int x = 0, y = insets.top;

		for (int i = 0; i < parent.getComponentCount(); i++) {
			MessageComponent c = (MessageComponent)parent.getComponent(i);

			int width = (int)(parent.getWidth() * 0.65f);
			c.setRequestWidth(width);

			if (c.isVisible()) {
				Dimension d = c.getPreferredSize();

				if (c.getSide() == Msg.LEFT)
					x = 5;
				else if (c.getSide() == Msg.RIGHT)
					x = maxWidth - d.width - 3;
				else if (c.getSide() == Msg.SYSTEM || c.getSide() == Msg.ERROR) x = (maxWidth - d.width) / 2;

				y += vgap + previousHeight;

				c.setBounds(x, y, d.width, d.height);

				previousHeight = d.height;
			}
		}
	}

	@Override
	public Dimension preferredLayoutSize (Container parent) {
		return calcSize(parent);
	}

	@Override
	public Dimension minimumLayoutSize (Container parent) {
		return calcSize(parent);
	}

	private Dimension calcSize (Container parent) {
		Insets insets = parent.getInsets();

		int totalY = 0;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			totalY += vgap + parent.getComponent(i).getHeight();
		}
		return new Dimension(0, totalY + insets.top + insets.bottom);
	}

	@Override
	public void addLayoutComponent (String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent (Component comp) {
	}
}
