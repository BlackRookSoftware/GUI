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
 * A border layout for OGLGUIObjects similar to {@link java.awt.GridLayout}.
 * @author Matthew Tropiano
 */
public class GridLayout implements GUILayout
{
	/**
	 * The orientation type of how to place the objects
	 * as they are added.
	 */
	public static enum Orientation
	{
		LEFT_RIGHT_TOP_DOWN,
		LEFT_RIGHT_BOTTOM_UP,
		RIGHT_LEFT_TOP_DOWN,
		RIGHT_LEFT_BOTTOM_UP
	}
	
	/** Number of discrete rows in the layout. */
	protected int rows;
	/** Number of discrete columns in the layout. */
	protected int columns;
	/** Padding in units between object. */
	protected float padding;
	/** Orientation of object placement. */
	protected Orientation orientation;
	
	/**
	 * Creates a new GridLayout.
	 * @param rows the rows in this grid layout. 
	 * A value of 0 or less means that the rows do not have a concrete limit.
	 * @param columns the columns in this grid layout. 
	 * A value of 0 or less means that the columns do not have a concrete limit.
	 */
	public GridLayout(int rows, int columns)
	{
		this(rows, columns, Orientation.LEFT_RIGHT_TOP_DOWN);
	}
	
	/**
	 * Creates a new GridLayout.
	 * @param rows the rows in this grid layout. 
	 * A value of 0 or less means that the rows do not have a concrete limit.
	 * @param columns the columns in this grid layout. 
	 * A value of 0 or less means that the columns do not have a concrete limit.
	 * @param padding the amount of padding in units between objects.
	 */
	public GridLayout(int rows, int columns, float padding)
	{
		this(rows, columns, padding, Orientation.LEFT_RIGHT_TOP_DOWN);
	}
	
	/**
	 * Creates a new GridLayout.
	 * @param rows the rows in this grid layout. 
	 * A value of 0 or less means that the rows do not have a concrete limit.
	 * @param columns the columns in this grid layout. 
	 * A value of 0 or less means that the columns do not have a concrete limit.
	 * @param orientation the orientation of the objects in the grid.
	 */
	public GridLayout(int rows, int columns, Orientation orientation)
	{
		this(rows, columns, 0f, orientation);
	}
	
	/**
	 * Creates a new GridLayout.
	 * @param rows the rows in this grid layout. 
	 * A value of 0 or less means that the rows do not have a concrete limit.
	 * @param columns the columns in this grid layout. 
	 * A value of 0 or less means that the columns do not have a concrete limit.
	 * @param padding the amount of padding in units between objects.
	 * @param orientation the orientation of the objects in the grid.
	 */
	public GridLayout(int rows, int columns, float padding, Orientation orientation)
	{
		this.rows = rows;
		this.columns = columns;
		this.padding = padding;
		this.orientation = orientation;
	}
	
	@Override
	public void resizeChild(GUIObject object, int index, int childTotal)
	{
		if (rows <= 0 && columns <= 0)
			return;
		
		if (rows > 0 && columns > 0 && index >= rows*columns)
			return;
		
		GUIBounds parentBounds = object.getParent().getBounds();
		int ro = columns > 0 ? columns : (childTotal / rows) + (childTotal % rows > 0 ? 1 : 0);
		int co = rows > 0 ? rows : (childTotal / columns) + (childTotal % columns > 0 ? 1 : 0);
		float r = ((parentBounds.width - (padding * (ro+1))) / ro);
		float c = ((parentBounds.height - (padding * (co+1))) / co);
		
		float x = 0f;
		float y = 0f;
		float w = r;
		float h = c;
		
		int gridx = index % ro;
		int gridy = index / ro;
		
		switch (orientation)
		{
			case LEFT_RIGHT_TOP_DOWN:
				x = padding + (r * gridx) + (padding * gridx);
				y = padding + (c * gridy) + (padding * gridy);
				break;
			case LEFT_RIGHT_BOTTOM_UP:
				x = r * gridx;
				y = c * ((co - 1) - gridy);
				break;
			case RIGHT_LEFT_TOP_DOWN:
				x = r * ((ro - 1) - gridx);
				y = c * ((co - 1) - gridy);
				break;
			case RIGHT_LEFT_BOTTOM_UP:
				x = r * ((ro - 1) - gridx);
				y = c * gridy;
				break;
		}

		object.setBounds(x, y, w, h);
	}
	
}
