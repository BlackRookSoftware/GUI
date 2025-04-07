/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIObject;

/**
 * A special panel that goes not add itself to render sets (but still adds its children), 
 * so it is not rendered itself, but still is a panel otherwise.
 * @author Matthew Tropiano
 */
public class GUIGlassPanel extends GUIPanel
{
	/**
	 * Creates a new GUI "glass" panel.
	 */
	public GUIGlassPanel()
	{
		this(0, 0, 0, 0);
	}
	
	/**
	 * Creates a new GUI "glass" panel.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public GUIGlassPanel(float width, float height)
	{
		super();
		setBounds(0, 0, width, height);
	}

	/**
	 * Creates a new GUI "glass" panel.
	 * @param x its position x.
	 * @param y its position y.
	 * @param width its width.
	 * @param height its height.
	 */
	public GUIGlassPanel(float x, float y, float width, float height)
	{
		super();
		setBounds(x, y, width, height);
	}

	/**
	 * @return false, always, for this object.
	 * @see GUIObject#isRendered()
	 */
	@Override
	public boolean isRendered()
	{
		return false;
	}
	
}
