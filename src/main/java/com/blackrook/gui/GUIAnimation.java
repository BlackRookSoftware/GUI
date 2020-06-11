/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

import com.blackrook.gui.GUI.Animation;
import com.blackrook.gui.GUI.AnimationState;
import com.blackrook.gui.struct.MathUtils;

/**
 * Describes an animation to be performed on an object in the GUI system.
 * @author Matthew Tropiano
 */
public abstract class GUIAnimation implements Animation<GUIObject>
{
	/**
	 * Creates a new GUI animation that performs a color transition.
	 * @param color the new color.
	 * @return the created animation.
	 */
	public static GUIAnimation color(GUIColor color)
	{
		return new ColorAnim(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Creates a new GUI animation that performs a color transition.
	 * @param red the red color component (0 to 1).
	 * @param green the green color component (0 to 1).
	 * @param blue the blue color component (0 to 1).
	 * @param alpha the alpha color component (0 to 1).
	 * @return the created animation.
	 */
	public static GUIAnimation color(float red, float green, float blue, float alpha)
	{
		return new ColorAnim(red, green, blue, alpha);
	}

	/**
	 * Creates a new GUI animation that performs an opacity transition.
	 * @param opacity the new opacity (0 to 1).
	 * @return the created animation.
	 */
	public static GUIAnimation opacity(float opacity)
	{
		return new OpacityAnim(opacity);
	}

	/**
	 * Creates a new GUI animation that performs an object position transition.
	 * Either of the following parameters can be null to designate no change.
	 * @param x the new x-coordinate.
	 * @param y the new y-coordinate.
	 * @return the created animation.
	 */
	public static GUIAnimation position(Float x, Float y)
	{
		return new PositionAnim(x, y, null, null);
	}

	/**
	 * Creates a new GUI animation that performs an object dimension transition.
	 * Either of the following parameters can be null to designate no change.
	 * @param width the new width.
	 * @param height the new height.
	 * @return the created animation.
	 */
	public static GUIAnimation dimension(Float width, Float height)
	{
		return new PositionAnim(null, null, width, height);
	}

	/**
	 * Creates a new GUI animation that performs an object bounds transition.
	 * Any of the following parameters can be null to designate no change.
	 * @param x the new x-coordinate.
	 * @param y the new y-coordinate.
	 * @param width the new width.
	 * @param height the new height.
	 * @return the created animation.
	 */
	public static GUIAnimation bounds(Float x, Float y, Float width, Float height)
	{
		return new PositionAnim(x, y, width, height);
	}

	/**
	 * Creates a new GUI animation that performs a rotation transition.
	 * @param rotation the new rotation (in degrees).
	 * @return the created animation.
	 */
	public static GUIAnimation rotation(float rotation)
	{
		return new RotationAnim(rotation);
	}

	/**
	 * Creates a new GUI animation that performs an object visibility change.
	 * @param visible true for visible, false for invisible.
	 * @return the created animation.
	 */
	public static GUIAnimation visible(boolean visible)
	{
		return new VisibleAnim(visible);
	}

	/**
	 * Creates a new GUI animation that sets a texture on the object.
	 * @param textures the textures to set (in order). Can be null.
	 * @return the created animation.
	 */
	public static GUIAnimation texture(String ... textures)
	{
		return new TextureAnim(textures);
	}

	/**
	 * Creates a new GUI animation that calls an action on the object.
	 * @param action the OGLGUIAction to call.
	 * @return the created animation.
	 */
	public static GUIAnimation action(GUIAction action)
	{
		return new ActionAnim(action);
	}

	/**
	 * An action that creates a color transition.
	 */
	private static class ColorAnim extends GUIAnimation
	{
		/** Ending color. */
		private float r1, g1, b1, a1;

		/**
		 * Creates a new GUI action that performs a color transition.
		 */
		private ColorAnim(float red, float green, float blue, float alpha)
		{
			r1 = red;
			g1 = green;
			b1 = blue;
			a1 = alpha;
		}

		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				/** Starting color. */
				private float r0, g0, b0, a0;

				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					if (start)
					{
						GUIColor color = object.getColor();
						r0 = color.getRed();
						g0 = color.getGreen();
						b0 = color.getBlue();
						a0 = color.getAlpha();
					}
					object.setColor(
						(float)MathUtils.linearInterpolate(progressScalar, r0, r1),
						(float)MathUtils.linearInterpolate(progressScalar, g0, g1),
						(float)MathUtils.linearInterpolate(progressScalar, b0, b1),
						(float)MathUtils.linearInterpolate(progressScalar, a0, a1)
					);
				}
			};
		}
	}
	
	/**
	 * An action that creates a color transition.
	 */
	private static class OpacityAnim extends GUIAnimation
	{
		/** Ending opacity. */
		private float op1;

		/**
		 * Creates a new GUI action that performs an opacity transition.
		 */
		private OpacityAnim(float opacity)
		{
			op1 = opacity;
		}

		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				/** Starting color. */
				private float op0;

				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					if (start)
						op0 = object.getOpacity();
					object.setOpacity((float)MathUtils.linearInterpolate(progressScalar, op0, op1));
				}
			};
		}
	}
	
	/**
	 * An action that creates a position transition.
	 */
	private static class PositionAnim extends GUIAnimation
	{
		/** Ending position. */
		private Float x1, y1, width1, height1;

		/**
		 * Creates a new GUI action that performs a position transition.
		 * @param x the new x-coordinate.
		 * @param y the new y-coordinate.
		 * @param width the new width.
		 * @param height the new height.
		 */
		private PositionAnim(Float x, Float y, Float width, Float height)
		{
			x1 = x;
			y1 = y;
			width1 = width;
			height1 = height;
		}

		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				/** Starting position. */
				private float x0, y0, width0, height0;

				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					if (start)
					{
						GUIBounds r = object.getBounds();
						x0 = r.x;
						y0 = r.y;
						width0 = r.width;
						height0 = r.height;
					}
					object.setBounds(
						x1 != null ? (float)MathUtils.linearInterpolate(progressScalar, x0, x1) : x0,
						y1 != null ? (float)MathUtils.linearInterpolate(progressScalar, y0, y1) : y0,
						width1 != null ? (float)MathUtils.linearInterpolate(progressScalar, width0, width1) : width0,
						height1 != null ? (float)MathUtils.linearInterpolate(progressScalar, height0, height1) : height0
					);
				}
			};
		}
	}

	/**
	 * An action that sets visible state.
	 */
	private static class VisibleAnim extends GUIAnimation
	{
		/** Ending flag. */
		private boolean visible;

		/**
		 * Creates a new GUI action that sets visible state or not.
		 * @param visible the new state.
		 */
		private VisibleAnim(boolean visible)
		{
			this.visible = visible;
		}

		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					if (end)
						object.setVisible(visible);
				}
			};
		}
	}
	
	/**
	 * Action that sets a texture.
	 */
	private static class TextureAnim extends GUIAnimation
	{
		/** New skin to set. */
		private String[] textures;

		/**
		 * Creates a new animation that sets skins.
		 */
		private TextureAnim(String ... textures)
		{
			this.textures = new String[textures.length];
			System.arraycopy(textures, 0, this.textures, 0, textures.length);
		}
		
		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					object.setTexture(textures[(int)(progressScalar * textures.length)]);
				}
			};
		}
	}
	
	/**
	 * Action that performs an action on completion.
	 */
	private static class ActionAnim extends GUIAnimation
	{
		/** New skin to set. */
		private GUIAction action;

		/**
		 * Creates a new animation that performs an action on completion.
		 */
		private ActionAnim(GUIAction action)
		{
			this.action = action;
		}
		
		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					if (end)
						object.callAction(action);
				}
			};
		}
	}

	/**
	 * An action that does a rotation.
	 */
	private static class RotationAnim extends GUIAnimation
	{
		/** Ending rotation. */
		private float rotation1;
		
		/**
		 * Creates a new Cinema action that sets rotation.
		 * @param rotation the new rotation.
		 */
		public RotationAnim(float rotation)
		{
			rotation1 = rotation;
		}

		@Override
		public AnimationState<GUIObject> createState(GUIObject object)
		{
			return new AnimationState<GUIObject>(object)
			{
				/** Starting rotation. */
				private float rotation0;

				@Override
				public void update(double progressScalar, boolean start, boolean end) 
				{
					if (start)
						rotation0 = object.getRenderRotationZ();
					object.setRotationZ((float)MathUtils.linearInterpolate(progressScalar, rotation0, rotation1));
				}
			};
		}
	}

}
