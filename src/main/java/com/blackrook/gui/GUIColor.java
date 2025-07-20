/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

import com.blackrook.gui.struct.MathUtils;

/**
 * Contains a set of values that describe a color.
 * @author Matthew Tropiano
 */
public class GUIColor
{
	public static final GUIColor 
	// color hues/components.

	// grays.
	WHITE = 			new GUIColor(1.0f, 1.0f, 1.0f, 1.0f, true),
	LIGHT_GRAY = 		new GUIColor(0.75f, 0.75f, 0.75f, 1.0f, true),
	GRAY = 				new GUIColor(0.5f, 0.5f, 0.5f, 1.0f, true),
	DARK_GRAY = 		new GUIColor(0.25f, 0.25f, 0.25f, 1.0f, true),
	BLACK = 			new GUIColor(0.0f, 0.0f, 0.0f, 1.0f, true),
	
	// primaries, secondaries, tertiaries.
	RED = 				new GUIColor(1.0f, 0.0f, 0.0f, 1.0f, true),
	GREEN = 			new GUIColor(0.0f, 1.0f, 0.0f, 1.0f, true),
	BLUE = 				new GUIColor(0.0f, 0.0f, 1.0f, 1.0f, true),
	
	YELLOW = 			new GUIColor(1.0f, 1.0f, 0.0f, 1.0f, true),
	CYAN = 				new GUIColor(0.0f, 1.0f, 1.0f, 1.0f, true),
	MAGENTA = 			new GUIColor(1.0f, 0.0f, 1.0f, 1.0f, true),
	
	ORANGE = 			new GUIColor(1.0f, 0.5f, 0.0f, 1.0f, true),
	CHARTREUSE = 		new GUIColor(0.5f, 1.0f, 0.0f, 1.0f, true),
	TEAL = 				new GUIColor(0.0f, 1.0f, 0.5f, 1.0f, true),
	AQUAMARINE = 		new GUIColor(0.0f, 0.5f, 1.0f, 1.0f, true),
	VIOLET = 			new GUIColor(0.5f, 0.0f, 1.0f, 1.0f, true),
	FUSCHIA = 			new GUIColor(1.0f, 0.0f, 0.5f, 1.0f, true),

	// other colors
	GARNET = 			new GUIColor(0.5f, 0.0f, 0.0f, 1.0f, true),
	AMBER = 			new GUIColor(0.5f, 0.5f, 0.0f, 1.0f, true),
	PINE = 				new GUIColor(0.0f, 0.5f, 0.0f, 1.0f, true),
	SEA_GREEN = 		new GUIColor(0.0f, 0.5f, 0.5f, 1.0f, true),
	NAVY = 				new GUIColor(0.0f, 0.0f, 0.5f, 1.0f, true),
	ROYAL_PURPLE =		new GUIColor(0.5f, 0.0f, 0.5f, 1.0f, true),
	
	LUMINANCE_BIAS =	new GUIColor(0.3f, 0.59f, 0.11f, 1.0f, true);
	
	/** Flagged as read-only? */
	private boolean readonly;
	
	/** The red component. */
	private float red;
	/** The green component. */
	private float green;
	/** The blue component. */
	private float blue;
	/** The alpha component, commonly used for blending operations. */
	private float alpha;
	
	/**
	 * Makes a new blank Color (RGBA (0,0,0,0)).
	 */
	public GUIColor()
	{
		this(0, 0, 0, 0);
	}
	
	/**
	 * Makes a new color from a 32-bit ARGB integer (like from a BufferedImage).
	 * @param argb 
	 */
	public GUIColor(int argb)
	{
		this.readonly = false;
		set(argb);
	}
	
	/**
	 * Makes a new color from channel components, with the alpha value as 1.
	 * @param red	the red component (0 to 1).
	 * @param green	the green component (0 to 1).
	 * @param blue	the blue component (0 to 1).
	 */
	public GUIColor(float red, float green, float blue)
	{
		this(red,green,blue,1);
	}
	
	/**
	 * Makes a new color from channel components.
	 * @param red	the red component (0 to 1).
	 * @param green	the green component (0 to 1).
	 * @param blue	the blue component (0 to 1).
	 * @param alpha	the alpha component (0 to 1).
	 */
	public GUIColor(float red, float green, float blue, float alpha)
	{
		set(red, green, blue, alpha);
	}

	/**
	 * Makes a new color from channel components.
	 * @param red	the red component (0 to 1).
	 * @param green	the green component (0 to 1).
	 * @param blue	the blue component (0 to 1).
	 * @param alpha	the alpha component (0 to 1).
	 */
	private GUIColor(float red, float green, float blue, float alpha, boolean readonly)
	{
		this(red, green, blue, alpha);
		this.readonly = readonly;
	}

	/**
	 * Makes a new color from an existing color.
	 * @param c	the color to use.
	 */
	public GUIColor(GUIColor c)
	{
		set(c.red, c.green, c.blue, c.alpha);
	}

	/** 
	 * @return the red component. 
	 */
	public float getRed()
	{
		return red;
	}

	/** 
	 * @return the green component. 
	 */
	public float getGreen()
	{
		return green;
	}

	/** 
	 * @return the blue component. 
	 */
	public float getBlue()
	{
		return blue;
	}

	/** 
	 * @return the alpha component, commonly used for blending operations. 
	 */
	public float getAlpha()
	{
		return alpha;
	}

	/**
	 * Sets this color's info using another color.
	 * @param c	the color to use.
	 */
	public void set(GUIColor c)
	{
		set(c.red, c.green, c.blue, c.alpha);
	}

	/**
	 * Sets this color's info using another color.
	 * @param red the red component (0 to 1).
	 * @param green the green component (0 to 1).
	 * @param blue the blue component (0 to 1).
	 * @param alpha the alpha component (0 to 1).
	 */
	public void set(float red, float green, float blue, float alpha)
	{
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}

	/**
	 * Sets this color's info using another color.
	 * @param argb the ARGB formatted 32-bit color value.
	 */
	public void set(int argb)
	{
		setRed(((argb >> 16) & 0x0ff) / 255f);
		setGreen(((argb >> 8) & 0x0ff) / 255f);
		setBlue(((argb >> 0) & 0x0ff) / 255f);
		setAlpha(((argb >> 24) & 0x0ff) / 255f);
	}

	/** 
	 * Sets the red component. 
	 * @param red the new red component (0.0 to 1.0).
	 */
	public void setRed(float red)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this color.");
		this.red = red;
	}

	/** 
	 * Sets the green component. 
	 * @param green the new green component (0.0 to 1.0).
	 */
	public void setGreen(float green)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this color.");
		this.green = green;
	}

	/** 
	 * Sets the blue component. 
	 * @param blue the new blue component (0.0 to 1.0).
	 */
	public void setBlue(float blue)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this color.");
		this.blue = blue;
	}

	/** 
	 * Sets the alpha component, commonly used for blending operations. 
	 * @param alpha the new alpha component (0.0 to 1.0).
	 */
	public void setAlpha(float alpha)
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify this color.");
		this.alpha = alpha;
	}

	/**
	 * Returns this color's RGBA info into a float array.
	 * @param out the output array. Must be at least length 4.
	 * @throws ArrayIndexOutOfBoundsException if out has less than 4 elements.
	 */
	public void getRGBA(float[] out)
	{
		out[0] = red;
		out[1] = green;
		out[2] = blue;
		out[3] = alpha;
	}
	
	/**
	 * @return this color as an ARGB integer value.
	 */
	public int getARGB()
	{
		return getARGB(red, green, blue, alpha);
	}
	
	/**
	 * Multiplies all channels in this color by a scalar. 
	 * @param scalar the scalar value.
	 */
	public void scale(float scalar)
	{
		scale(this,scalar,this);
	}
	
	/**
	 * Adds a color's components to this one.
	 * This changes this color's values.
	 * @param c the incoming color to use.
	 */
	public void add(GUIColor c)
	{
		add(this,c,this);
	}
	
	/**
	 * Subtracts a color's components from this one.
	 * This changes this color's values.
	 * @param c the incoming color to use.
	 */
	public void subtract(GUIColor c)
	{
		subtract(this,c,this);
	}
	
	/**
	 * Multiplies a color's components to this one.
	 * This changes this color's values.
	 * @param c the incoming color to use.
	 */
	public void multiply(GUIColor c)
	{
		multiply(this,c,this);
	}
	
	/**
	 * Blends a color's component's to this one,
	 * using the incoming color's alpha channel as a blending factor.
	 * This changes this color's values.
	 * @param c the incoming color to use.
	 */
	public void alphaBlend(GUIColor c)
	{
		alphaBlend(this, c, this);
	}
	
	/**
	 * Clamps this color's channel values into the range 0 to 1.
	 */
	public void clamp()
	{
		if (readonly)
			throw new IllegalStateException("You cannot modify the output color.");

		red = MathUtils.clampValue(red, 0, 1);
		green = MathUtils.clampValue(green, 0, 1);
		blue = MathUtils.clampValue(blue, 0, 1);
		alpha = MathUtils.clampValue(alpha, 0, 1);
	}
	
	/**
	 * Converts RGBA floats to an ARGB integer.
	 * @param red	the red component value (0 to 1).
	 * @param green	the green component value (0 to 1).
	 * @param blue	the blue component value (0 to 1).
	 * @param alpha	the alpha component value (0 to 1).
	 * @return the resultant ARGB integer.
	 */
	public static int getARGB(float red, float green, float blue, float alpha)
	{
		int out = 0;
		out |= ((int)(MathUtils.clampValue(blue,0,1) * 255)) << 0; 
		out |= ((int)(MathUtils.clampValue(green,0,1) * 255)) << 8; 
		out |= ((int)(MathUtils.clampValue(red,0,1) * 255)) << 16; 
		out |= ((int)(MathUtils.clampValue(alpha,0,1) * 255)) << 24;
		return out;
	}
	
	/**
	 * Scales a color's channel values by a scalar.
	 * This changes "out's" values.
	 * @param a the first color.
	 * @param scalar the scalar value.
	 * @param out the resultant color output.
	 */
	public static void scale(GUIColor a, float scalar, GUIColor out)
	{	
		if (out.readonly)
			throw new IllegalStateException("You cannot modify the output color.");

		out.red = a.red * scalar;
		out.green = a.green * scalar;
		out.blue = a.blue * scalar;
		out.alpha = a.alpha * scalar;
	}

	/**
	 * Adds a color's components to another.
	 * This changes "out's" values.
	 * @param a the first color.
	 * @param b the second color.
	 * @param out the resultant color output.
	 */
	public static void add(GUIColor a, GUIColor b, GUIColor out)
	{
		if (out.readonly)
			throw new IllegalStateException("You cannot modify the output color.");

		out.red = a.red + b.red;
		out.green = a.green + b.green;
		out.blue = a.blue + b.blue;
		out.alpha = a.alpha + b.alpha;
	}

	/**
	 * Subtracts a color's components from another.
	 * This changes "out's" values.
	 * Subtracts b from a.
	 * @param a the first color.
	 * @param b the second color.
	 * @param out the resultant color output.
	 */
	public static void subtract(GUIColor a, GUIColor b, GUIColor out)
	{
		if (out.readonly)
			throw new IllegalStateException("You cannot modify the output color.");

		out.red = a.red - b.red;
		out.green = a.green - b.green;
		out.blue = a.blue - b.blue;
		out.alpha = a.alpha - b.alpha;
	}

	/**
	 * Multiplies a color's components to another.
	 * This changes "out's" values.
	 * @param a the first color.
	 * @param b the second color.
	 * @param out the resultant color output.
	 */
	public static void multiply(GUIColor a, GUIColor b, GUIColor out)
	{
		if (out.readonly)
			throw new IllegalStateException("You cannot modify the output color.");

		out.red = a.red * b.red;
		out.green = a.green * b.green;
		out.blue = a.blue * b.blue;
		out.alpha = a.alpha * b.alpha;
	}
	
	/**
	 * Blends two colors using alpha blending.
	 * This changes "out's" values.
	 * @param a the first color.
	 * @param b the second color.
	 * @param out the resultant color output.
	 */
	public static void alphaBlend(GUIColor a, GUIColor b, GUIColor out)
	{
		if (out.readonly)
			throw new IllegalStateException("You cannot modify the output color.");

		float oma = 1 - b.alpha;
		out.red = oma * a.red + b.red * b.alpha;
		out.green = oma * a.green + b.green * b.alpha;
		out.blue = oma * a.blue + b.blue * b.alpha;
		out.alpha = oma * a.alpha + b.alpha;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof GUIColor)
			return this.equals((GUIColor)obj);
		return super.equals(obj);
	}

	public boolean equals(GUIColor color)
	{
		return 
			red == color.red
			&& green == color.green
			&& blue == color.blue
			&& alpha == color.alpha
		;
	}	
}
