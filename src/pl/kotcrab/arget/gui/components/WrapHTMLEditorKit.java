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

package pl.kotcrab.arget.gui.components;

import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;

public class WrapHTMLEditorKit extends HTMLEditorKit {
	@Override
	public ViewFactory getViewFactory () {

		return new HTMLFactory() {
			@Override
			public View create (Element e) {
				View v = super.create(e);
				if (v instanceof InlineView) {
					return new InlineView(e) {

						@Override
						public int getBreakWeight (int axis, float pos, float len) {
							return GoodBreakWeight;
						}

						@Override
						public View breakView (int axis, int p0, float pos, float len) {
							if (axis == View.X_AXIS) {
								checkPainter();
								int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
								if (p0 == getStartOffset() && p1 == getEndOffset()) {
									return this;
								}
								return createFragment(p0, p1);
							}
							return this;
						}
					};
				} else if (v instanceof ParagraphView) {
					return new ParagraphView(e) {
						@Override
						protected SizeRequirements calculateMinorAxisRequirements (int axis, SizeRequirements r) {
							if (r == null) {
								r = new SizeRequirements();
							}
							float pref = layoutPool.getPreferredSpan(axis);
							float min = layoutPool.getMinimumSpan(axis);
							// Don't include insets, Box.getXXXSpan will include them.
							r.minimum = (int)min;
							r.preferred = Math.max(r.minimum, (int)pref);
							r.maximum = Integer.MAX_VALUE;
							r.alignment = 0.5f;
							return r;
						}

					};
				}
				return v;
			}
		};

	}
}
