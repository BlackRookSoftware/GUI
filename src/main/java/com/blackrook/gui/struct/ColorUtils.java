package com.blackrook.gui.struct;

/**
 * Some utility methods for mixing ARGB colors.
 * @author Matthew Tropiano
 */
public final class ColorUtils
{
	// Can't instantiate.
	private ColorUtils(){}
	
	/**
	 * Scales a byte value using a byte-range scalar.
	 * @param incoming the incoming value.
	 * @param scalar 0 to 255.
	 * @return the scaled integer value.
	 */
	private static int scaleByte(int incoming, int scalar)
	{
		// incoming and scalar are 0-255
		return incoming * scalar / 255;
	}

	/**
	 * Converts a componentized color to an ARGB integer.
	 * @param red the red channel scalar (0 to 1).
	 * @param green the green channel scalar (0 to 1).
	 * @param blue the blue channel scalar (0 to 1).
	 * @param alpha the alpha scalar (0 to 1).
	 * @return the resultant integer.
	 */
	public static int argbColor(float red, float green, float blue, float alpha)
	{
		int argb = ((int)(alpha * 255.0) & 0x0ff) << 24 
			| ((int)(red * 255.0) & 0x0ff) << 16 
			| ((int)(green * 255.0) & 0x0ff) << 8 
			| ((int)(blue * 255.0) & 0x0ff);
		return argb;
	}
	
	/**
	 * Converts a hue-saturation-lightness-alpha color to an ARGB integer.
	 * @param hue the color hue (in radians).
	 * @param saturation the saturation scalar (0 to 1).
	 * @param luminance the luminance scalar (0 to 1).
	 * @param alpha the alpha scalar (0 to 1).
	 * @return the resultant ARGB integer.
	 */
	public static int argbHSLA(float hue, float saturation, float luminance, float alpha)
	{
		double c = (1.0 - Math.abs((2.0 * luminance) - 1.0)) * saturation;
		double x = c * (1.0 - Math.abs((hue / (Math.PI / 3.0)) % 2.0 - 1.0));
		double m = luminance - (c / 2.0);
		
		double red, green, blue;
		if (0.0 <= hue && hue < (Math.PI / 3.0))
		{
			red = c;
			green = x;
			blue = 0.0;
		}
		else if ((Math.PI / 3.0) <= hue && hue < (2.0 * Math.PI / 3.0))
		{
			red = x;
			green = c;
			blue = 0.0;
		}
		else if ((2.0 * Math.PI / 3.0) <= hue && hue < Math.PI)
		{
			red = 0.0;
			green = c;
			blue = x;
		}
		else if (Math.PI <= hue && hue < (4.0 * Math.PI / 3.0))
		{
			red = 0.0;
			green = x;
			blue = c;
		}
		else if ((4.0 * Math.PI / 3.0) <= hue && hue < (5.0 * Math.PI / 3.0))
		{
			red = x;
			green = 0.0;
			blue = c;
		}
		else
		{
			red = c;
			green = 0.0;
			blue = x;
		}
		
		int argb = ((int)(alpha * 255.0) & 0x0ff) << 24 
			| ((int)((red + m) * 255.0) & 0x0ff) << 16 
			| ((int)((green + m) * 255.0) & 0x0ff) << 8 
			| ((int)((blue + m) * 255.0) & 0x0ff);
		return argb;
	}
	
	/**
	 * Mixes two ARGB values linearly.
	 * @param argbSource the source ARGB color.
	 * @param argbIncoming the destination ARGB color.
	 * @param scalar transition scalar: 0 to 255. 128 is 50/50.
	 * @return the resultant ARGB color.
	 */
	public static int argbMix(int argbSource, int argbIncoming, int scalar)
	{
		int as = (argbSource >> 24) & 0x0ff;
		int rs = (argbSource >> 16) & 0x0ff;
		int gs = (argbSource >> 8) & 0x0ff;
		int bs = (argbSource) & 0x0ff;

		int ai = (argbIncoming >> 24) & 0x0ff;
		int ri = (argbIncoming >> 16) & 0x0ff;
		int gi = (argbIncoming >> 8) & 0x0ff;
		int bi = (argbIncoming) & 0x0ff;
		
		int iscalar = 255 - scalar;
		
		int ao = (int)(scaleByte(as, iscalar) + scaleByte(ai, scalar));
		int ro = (int)(scaleByte(rs, iscalar) + scaleByte(ri, scalar));
		int go = (int)(scaleByte(gs, iscalar) + scaleByte(gi, scalar));
		int bo = (int)(scaleByte(bs, iscalar) + scaleByte(bi, scalar));
		
		return (ao << 24) | (ro << 16) | (go << 8) | bo;
	}

	/**
	 * Mixes two colors scaled by the incoming color's alpha component. 
	 * @param argbSource the source ARGB color.
	 * @param argbIncoming the destination ARGB color.
	 * @return the resultant ARGB color.
	 */
	public static int argbAlpha(int argbSource, int argbIncoming)
	{
		int as = (argbSource >> 24) & 0x0ff;
		int rs = (argbSource >> 16) & 0x0ff;
		int gs = (argbSource >> 8) & 0x0ff;
		int bs = (argbSource) & 0x0ff;

		int ai = (argbIncoming >> 24) & 0x0ff;
		int ri = (argbIncoming >> 16) & 0x0ff;
		int gi = (argbIncoming >> 8) & 0x0ff;
		int bi = (argbIncoming) & 0x0ff;
		
		int scalar = ai;
		int iscalar = 255 - scalar;
		
		int ao = (int)(scaleByte(as, iscalar) + scaleByte(ai, scalar));
		int ro = (int)(scaleByte(rs, iscalar) + scaleByte(ri, scalar));
		int go = (int)(scaleByte(gs, iscalar) + scaleByte(gi, scalar));
		int bo = (int)(scaleByte(bs, iscalar) + scaleByte(bi, scalar));
		
		return (ao << 24) | (ro << 16) | (go << 8) | bo;
	}

	/**
	 * Adds two colors, with the addition scaled by the incoming color's alpha component. 
	 * @param argbSource the source ARGB color.
	 * @param argbIncoming the destination ARGB color.
	 * @return the resultant ARGB color.
	 */
	public static int argbAddScaledIncomingAlpha(int argbSource, int argbIncoming)
	{
		int as = (argbSource >> 24) & 0x0ff;
		int rs = (argbSource >> 16) & 0x0ff;
		int gs = (argbSource >> 8) & 0x0ff;
		int bs = (argbSource) & 0x0ff;

		int ai = (argbIncoming >> 24) & 0x0ff;
		int ri = (argbIncoming >> 16) & 0x0ff;
		int gi = (argbIncoming >> 8) & 0x0ff;
		int bi = (argbIncoming) & 0x0ff;
		
		int scalar = ai;
		
		int ao = as;
		int ro = Math.min(rs + scaleByte(ri, scalar), 255);
		int go = Math.min(gs + scaleByte(gi, scalar), 255);
		int bo = Math.min(bs + scaleByte(bi, scalar), 255);
		
		return (ao << 24) | (ro << 16) | (go << 8) | bo;
	}

	/**
	 * Subtracts the incoming color from the source, with the subtraction 
	 * scaled by the incoming color's alpha component. 
	 * @param argbSource the source ARGB color.
	 * @param argbIncoming the destination ARGB color.
	 * @return the resultant ARGB color.
	 */
	public static int argbSubtractScaledIncomingAlpha(int argbSource, int argbIncoming)
	{
		int as = (argbSource >> 24) & 0x0ff;
		int rs = (argbSource >> 16) & 0x0ff;
		int gs = (argbSource >> 8) & 0x0ff;
		int bs = (argbSource) & 0x0ff;

		int ai = (argbIncoming >> 24) & 0x0ff;
		int ri = (argbIncoming >> 16) & 0x0ff;
		int gi = (argbIncoming >> 8) & 0x0ff;
		int bi = (argbIncoming) & 0x0ff;
		
		int scalar = ai;
		
		int ao = as;
		int ro = Math.max(rs - scaleByte(ri, scalar), 0);
		int go = Math.max(gs - scaleByte(gi, scalar), 0);
		int bo = Math.max(bs - scaleByte(bi, scalar), 0);
		
		return (ao << 24) | (ro << 16) | (go << 8) | bo;
	}

	/**
	 * Multiply-blends two colors together, with the multiplication 
	 * scaled by the incoming color's alpha component. 
	 * @param argbSource the source ARGB color.
	 * @param argbIncoming the destination ARGB color.
	 * @return the resultant ARGB color.
	 */
	public static int argbMultiplyScaledIncomingAlpha(int argbSource, int argbIncoming)
	{
		int as = (argbSource >> 24) & 0x0ff;
		int rs = (argbSource >> 16) & 0x0ff;
		int gs = (argbSource >> 8) & 0x0ff;
		int bs = (argbSource) & 0x0ff;

		int ai = (argbIncoming >> 24) & 0x0ff;
		int ri = (argbIncoming >> 16) & 0x0ff;
		int gi = (argbIncoming >> 8) & 0x0ff;
		int bi = (argbIncoming) & 0x0ff;
		
		// multiply intensity is inverse
		int scalar = 255 - ai;
		
		int ao = as;
		int ro = rs * scaleByte(ri, scalar) / 255;
		int go = gs * scaleByte(gi, scalar) / 255;
		int bo = bs * scaleByte(bi, scalar) / 255;
		
		return (ao << 24) | (ro << 16) | (go << 8) | bo;
	}
	
}
