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
 * A layout that anchors objects to places in a parent object.
 * @author Matthew Tropiano
 */
public class AnchorLayout implements GUILayout
{
	/** Anchor value for automatic alignment. */
	public static final float AUTO = Float.POSITIVE_INFINITY; 
	/** Anchor value for ignoring an anchor point. */
	public static final float UNUSED = Float.NEGATIVE_INFINITY; 
	
	/* Reusable anchors. */
	
	private static Anchor TOP_LEFT_ANCHOR = new Anchor(0, UNUSED, 0, UNUSED);
	private static Anchor TOP_CENTER_ANCHOR = new Anchor(AUTO, UNUSED, 0, UNUSED);
	private static Anchor TOP_RIGHT_ANCHOR = new Anchor(UNUSED, 0, 0, UNUSED);
	private static Anchor MIDDLE_LEFT_ANCHOR = new Anchor(0, UNUSED, AUTO, UNUSED);
	private static Anchor CENTER_ANCHOR = new Anchor(AUTO, UNUSED, AUTO, UNUSED);
	private static Anchor MIDDLE_RIGHT_ANCHOR = new Anchor(UNUSED, 0, AUTO, UNUSED);
	private static Anchor BOTTOM_LEFT_ANCHOR = new Anchor(0, UNUSED, UNUSED, 0);
	private static Anchor BOTTOM_CENTER_ANCHOR = new Anchor(AUTO, UNUSED, UNUSED, 0);
	private static Anchor BOTTOM_RIGHT_ANCHOR = new Anchor(UNUSED, 0, UNUSED, 0);
	
	@Override
	public void resizeChild(GUIObject object, int index, int childTotal)
	{
		GUIBounds parentBounds = object.getParent().getBounds();
		if (object.getLayoutAttrib() instanceof Anchor)
		{
			GUIBounds bounds = object.getBounds();
			Anchor a = (Anchor)object.getLayoutAttrib();
			float x = 0f;
			float y = 0f;
			float width = bounds.width;
			float height = bounds.height;
			
			if (a.left != UNUSED)
			{
				if (a.left == AUTO)
					x = (parentBounds.width / 2f) - (bounds.width / 2f);
				else if (a.right != UNUSED && a.right != AUTO)
				{
					x = a.left;
					width = parentBounds.width - a.left - a.right;
				}
				else
					x = a.left;
			}
			else if (a.right != UNUSED)
			{
				if (a.right == AUTO)
					x = (parentBounds.width / 2f) - (bounds.width / 2f);
				else if (a.left != UNUSED && a.left != AUTO)
				{
					x = a.left;
					width = parentBounds.width - a.left - a.right;
				}
				else
					x = parentBounds.width - bounds.width - a.right;
			}

			if (a.top != UNUSED)
			{
				if (a.top == AUTO)
					y = (parentBounds.height / 2f) - (bounds.height / 2f);
				else if (a.bottom != UNUSED && a.bottom != AUTO)
				{
					y = a.top;
					height = parentBounds.height - a.top - a.bottom;
				}
				else
					y = a.top;
			}
			else if (a.bottom != UNUSED)
			{
				if (a.bottom == AUTO)
					y = (parentBounds.height / 2f) - (bounds.height / 2f);
				else if (a.top != UNUSED && a.top != AUTO)
				{
					y = a.top;
					height = parentBounds.height - a.top - a.bottom;
				}
				else
					y = parentBounds.height - bounds.height - a.bottom;
			}
			object.setBounds(x, y, width, height);
		}
	}

	/**
	 * Creates an anchor attribute for objects in the layout.
	 * Left takes precedence over right, if not UNUSED.
	 * Top takes precedence over bottom, if not UNUSED.
	 * @param left units from the left. If auto, centered between left and right. If unused, ignored.
	 * @param right units from the right. If auto, centered between left and right. If unused, ignored.
	 * @param top units from the top. If auto, centered between top and bottom. If unused, ignored.
	 * @param bottom units from the bottom. If auto, centered between top and bottom. If unused, ignored.
	 * @return the created Anchor.
	 */
	public static Anchor createAnchor(float left, float right, float top, float bottom)
	{
		return new Anchor(left, right, top, bottom);
	}
	
	/**
	 * Returns an Anchor that describes centering an object in its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createCenteredAnchor()
	{
		return CENTER_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the top-left of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createTopLeftAnchor()
	{
		return TOP_LEFT_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the top-center of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createTopCenterAnchor()
	{
		return TOP_CENTER_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the top-right of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createTopRightAnchor()
	{
		return TOP_RIGHT_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the middle-left of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createMiddleLeftAnchor()
	{
		return MIDDLE_LEFT_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the middle-right of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createMiddleRightAnchor()
	{
		return MIDDLE_RIGHT_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the bottom-left of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createBottomLeftAnchor()
	{
		return BOTTOM_LEFT_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the bottom-center of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createBottomCenterAnchor()
	{
		return BOTTOM_CENTER_ANCHOR;
	}
	
	/**
	 * Returns an Anchor that describes anchoring an object in the bottom-right of its parent.
	 * This doesn't actually return a new Anchor - it is a pooled one.
	 * @return the created Anchor.
	 */
	public static Anchor createBottomRightAnchor()
	{
		return BOTTOM_RIGHT_ANCHOR;
	}
	
	/**
	 * The anchor attribute.
	 */
	public static class Anchor
	{
		private float left;
		private float right;
		private float top;
		private float bottom;
		
		private Anchor(float left, float right, float top, float bottom)
		{
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		}
	}
	
}
