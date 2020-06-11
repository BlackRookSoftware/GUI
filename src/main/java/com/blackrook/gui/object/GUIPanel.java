/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIObject;

/**
 * A standard rectangular panel.
 * A theme key can be set on these panels.
 * @author Matthew Tropiano
 */
public class GUIPanel extends GUIObject
{
	/** The current theme key. */
	private String themeKey;
	
	/**
	 * Creates a new GUI panel.
	 */
	public GUIPanel()
	{
		this(0, 0, 0, 0);
	}
	
	/**
	 * Creates a new GUI object.
	 * @param width its width.
	 * @param height its height.
	 */
	public GUIPanel(float width, float height)
	{
		super();
		setBounds(0, 0, width, height);
	}

	/**
	 * Creates a new GUI object.
	 * @param x its position x.
	 * @param y	its position y.
	 * @param width its width.
	 * @param height its height.
	 */
	public GUIPanel(float x, float y, float width, float height)
	{
		super();
		setBounds(x, y, width, height);
	}
	
	/**
	 * Sets this object's theme key. 
	 * This is the key that is used to look up the texture used to render this object
	 * if a theme is applied to this object or the GUI that owns it. 
	 * @param themeKey the new theme key.
	 * @return this panel.
	 */
	public GUIPanel setThemeKey(String themeKey)
	{
		this.themeKey = themeKey;
		updateDirty();
		return this;
	}
	
	@Override
	public String getThemeKey()
	{
		return themeKey;
	}
	
}
