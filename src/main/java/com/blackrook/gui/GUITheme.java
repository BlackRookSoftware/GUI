/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI theme for objects and whole GUI layers. 
 * @author Matthew Tropiano
 */
public class GUITheme
{
	/** Theme name. */
	protected String name;
	/** Key map. */
	protected Map<String, Object> textureMap;
	
	/**
	 * Creates a new theme.
	 * @param name the name of the theme.
	 */
	public GUITheme(String name)
	{
		this.name = name;
		textureMap = new HashMap<String, Object>(8);
	}
	
	/**
	 * Sets a theme skin by key.
	 * Re-setting an existing key replaces it.
	 * @param key the theme key.
	 * @param texture the texture to use. if null, the key is removed.
	 */
	public void set(String key, String texture)
	{
		if (texture == null)
			textureMap.remove(key);
		else
			textureMap.put(key, texture);
	}
	
	/**
	 * Sets a theme font by key.
	 * Re-setting an existing key replaces it.
	 * @param key the theme key.
	 * @param font the skin to use. if null, the key is removed.
	 */
	public void set(String key, GUIFontType font)
	{
		if (font == null)
			textureMap.remove(key);
		else
			textureMap.put(key, font);
	}
	
	/**
	 * Returns a skin associated with a theme key, 
	 * or null if not a skin or none is associated.
	 * @param key the key to use.
	 * @return the corresponding texture name, or null if none found or if the value at this key is not a texture.
	 */
	public String getTexture(String key)
	{
		Object obj = textureMap.get(key);
		return obj != null && obj instanceof String ? (String)obj : null;
	}
	
	/**
	 * Returns a font associated with a theme key, 
	 * or null if not a font or none is associated.
	 * @param key the key to use.
	 * @return the corresponding font, or null if none found or if the value at this key is not a font.
	 */
	public GUIFontType getFont(String key)
	{
		Object obj = textureMap.get(key);
		return obj != null && obj instanceof GUIFontType ? (GUIFontType)obj : null;
	}
	
}
