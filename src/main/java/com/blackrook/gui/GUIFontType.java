/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

/**
 * Font type interface for use with GUI.
 * @author Matthew Tropiano
 */
public interface GUIFontType
{

	/**
	 * List of font text directionalities.
	 */
	public static enum Directionality
	{
		/** Text will be written left to right. */
		LEFT_TO_RIGHT,
		/** Text will be written right to left. */
		RIGHT_TO_LEFT,
	}
	
	/**
	 * Single character entry for the font, which is essentially a lookup
	 * of these things. 
	 */
	public static class FontChar
	{
		/** Font character. */
		public char c;
		/** Texture location s start. */
		public float s0;
		/** Texture location t start. */
		public float t0;
		/** Texture location s end. */
		public float s1;
		/** Texture location t end. */
		public float t1;
		/** Width of char in relation to overall size (1.0 is equal to fontsize). */
		public float width;
		/** Height of char in relation to overall size (1.0 is equal to fontsize). */
		public float height;
		/** Advancement of char in relation to overall size (1.0 is equal to fontsize). */
		public float advance;
		/** X offset of character in relation to overall size (1.0 is equal to fontsize). */
		public float xofs; 
		/** Y offset of character in relation to overall size (1.0 is equal to fontsize). */
		public float yofs; 
		
		public FontChar(char c, float s0, float t0, float s1, float t1, float w, float h, float advance, float xofs, float yofs)
		{
			this.c = c;
			this.s0 = s0;
			this.t0 = t0;
			this.s1 = s1;
			this.t1 = t1;
			this.width = w;
			this.height = h;
			this.advance = advance;
			this.xofs = xofs;
			this.yofs = yofs;
		}
		
		@Override
		public String toString()
		{
			return Character.toString(c);
		}
	}

	/**
	 * @return the name of this font.
	 */
	public String getName();

	/**
	 * @return the texture to use for this mapping.
	 */
	public String getTexture();

	/**
	 * Gets a character's definition from the map.
	 * @param c the character to retrieve from the map.
	 * @return the font map linked to this character. 
	 * May return the default map if it isn't in the map. 
	 */
	public FontChar getChar(char c);

	/**
	 * @return this font's directionality.
	 */
	public Directionality getDirectionality();

}
