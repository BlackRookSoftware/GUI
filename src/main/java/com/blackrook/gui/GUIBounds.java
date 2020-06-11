/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

/**
 * Object that describes a rectangular area (single-precision coordinates).
 * @author Matthew Tropiano
 */
public class GUIBounds
{
	/** Starting X-coordinate. */
	public float x;
	/** Starting Y-coordinate. */
	public float y;
	/** Rectangle width. */
	public float width;
	/** Rectangle height. */
	public float height;
	
	/**
	 * Creates a rectangle at (0,0) with width and height of 0.
	 */
	public GUIBounds()
	{
		this(0f, 0f, 0f, 0f);
	}
	
	/**
	 * Creates a copy of a rectangle.
	 * @param r the source rectangle to copy.
	 */
	public GUIBounds(GUIBounds r)
	{
		this(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Creates a new rectangle.
	 * @param x Starting X-coordinate.
	 * @param y Starting Y-coordinate.
	 * @param width Rectangle width.
	 * @param height Rectangle height.
	 */
	public GUIBounds(float x, float y, float width, float height)
	{
		set(x, y, width, height);
	}

	/**
	 * Sets the values in this rectangle using another rectangle.
	 * @param r the other rectangle.
	 */
	public void set(GUIBounds r)
	{
		set(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Sets the values in this rectangle.
	 * @param x Starting X-coordinate.
	 * @param y Starting Y-coordinate.
	 * @param width Rectangle width.
	 * @param height Rectangle height.
	 */
	public void set(float x, float y, float width, float height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString()
	{
		return String.format("R[X%f, Y%f, W%f, H%f]", x, y, width, height);
	}
	
}
