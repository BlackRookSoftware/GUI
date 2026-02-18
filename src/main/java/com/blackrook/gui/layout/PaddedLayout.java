/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.layout;

import com.blackrook.gui.GUIBounds;
import com.blackrook.gui.GUILayout;
import com.blackrook.gui.GUIObject;

/**
 * A layout for GUIObjects that resizes its children to its parent,
 * with padded space around its edges, defined in screen units. If
 * the units are a value less than one, it is treated as a percentage
 * of the associated axis's width (or height).
 * @author Matthew Tropiano
 */
public class PaddedLayout implements GUILayout
{
	/** Padding on the top, in units. */
	protected float paddingTop;
	/** Padding on the right, in units. */
	protected float paddingRight;
	/** Padding on the bottom, in units. */
	protected float paddingBottom;
	/** Padding on the left, in units. */
	protected float paddingLeft;
	
	/**
	 * Creates a new PaddedLayout with every edge having the same padding.
	 * @param units the padding in units for all edges. 
	 */
	public PaddedLayout(float units)
	{
		this(units, units, units, units);
	}

	/**
	 * Creates a new PaddedLayout with the opposing edges having the same padding.
	 * @param vertical the padding in units for the top and bottom. 
	 * @param horizontal the padding in units for the left and right. 
	 */
	public PaddedLayout(float vertical, float horizontal)
	{
		this(vertical, horizontal, vertical, horizontal);
	}

	/**
	 * Creates a new PaddedLayout with the opposing horzontal edges having the same padding
	 * and the vertical edges different.
	 * @param top the padding in units for the top. 
	 * @param horizontal the padding in units for the left and right. 
	 * @param bottom the padding in units for the bottom.
	 */
	public PaddedLayout(float top, float horizontal, float bottom)
	{
		this(top, horizontal, bottom, horizontal);
	}

	/**
	 * Creates a new PaddedLayout with every edge having the same padding.
	 * @param top the padding in units for the top.
	 * @param right the padding in units for the right.
	 * @param bottom the padding in units for the bottom.
	 * @param left the padding in units for the left.
	 */
	public PaddedLayout(float top, float right, float bottom, float left)
	{
		paddingTop = top;
		paddingRight = right;
		paddingBottom = bottom;
		paddingLeft = left;
	}
	
	@Override
	public void resizeChild(GUIObject object, int index, int childTotal)
	{
		GUIBounds parentBounds = object.getParent().getBounds();
		float pw = parentBounds.width;
		float ph = parentBounds.height;
		
		float x0 = paddingLeft <= 0f ? 0f : 
			(paddingLeft < 1f ? pw * paddingLeft : paddingLeft);
		float y0 = paddingTop <= 0f ? 0f : 
			(paddingTop < 1f ? ph * paddingTop : paddingTop);
		float x1 = paddingRight <= 0f ? pw : 
			(paddingRight < 1f ? pw - (pw * paddingRight) : pw - paddingRight);
		float y1 = paddingBottom <= 0f ? ph : 
			(paddingBottom < 1f ? ph - (ph * paddingBottom) : ph - paddingBottom);
		object.setBounds(x0, y0, x0 < x1 ? x1 - x0 : 0f, y0 < y1 ? y1 - y0 : 0f);
	}

}
