/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.layout;

import com.blackrook.gui.GUIBounds;
import com.blackrook.gui.GUILayout;
import com.blackrook.gui.GUIObject;

/**
 * A layout that orients objects by resizing them.
 * @author Matthew Tropiano
 */
public class CappedLayout implements GUILayout
{
	/** Cap style enumeration. */
	public static enum Style
	{
		/** Slider moves left to right. */
		HORIZONTAL,
		/** Slider moves top to bottom. */
		VERTICAL
	}
	
	/**
	 * Layout attributes for layout components.
	 */
	public static enum Attrib
	{
		/** Start cap. */
		START,
		/** Middle. */
		MIDDLE,
		/** End cap. */
		END;
	}

	/** Slider style. */
	protected Style style;

	/**
	 * Creates a new capped layout in horizontal style.
	 */
	public CappedLayout()
	{
		this(Style.HORIZONTAL);
	}
	
	/**
	 * Creates a new capped layout.
	 * @param style the style of the layout.
	 */
	public CappedLayout(Style style)
	{
		this.style = style;
	}
	
	@Override
	public void resizeChild(GUIObject object, int index, int childTotal)
	{
		GUIObject parent = object.getParent();
		GUIBounds pb = parent.getBounds();
		float pw = pb.width;
		float ph = pb.height;
		if (object.getLayoutAttrib() != null && object.getLayoutAttrib() instanceof Attrib)
		{
			switch ((Attrib)object.getLayoutAttrib())
			{
				case END:
				{
					switch (style)
					{
						case HORIZONTAL:
							object.setBounds(0, 0, ph, ph);
							break;
						case VERTICAL:
							object.setBounds(0, 0, pw, pw);
							break;
					}
				}
					break;

				case START:
				{
					switch (style)
					{
						case HORIZONTAL:
							object.setBounds(pw-ph, 0, ph, ph);
							break;
						case VERTICAL:
							object.setBounds(0, ph-pw, pw, pw);
							break;
					}
				}
					break;
					
				case MIDDLE:
				{
					switch (style)
					{
						case HORIZONTAL:
							object.setBounds(ph, 0, pw-(ph*2), ph);
							break;
						case VERTICAL:
							object.setBounds(0, pw, pw, ph-(pw*2));
							break;
					}
				}
					break;
			}
		}
	}
}

