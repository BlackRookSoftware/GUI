/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.group;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.GUIInputConstants;

/**
 * A group that binds a series of actions to a set of focusable objects,
 * in such that if one presses certain buttons or keys, they will go forward or
 * back in focus.
 * <p>If the object receives a {@link GUIInputConstants#KEY_LEFT} or {@link GUIInputConstants#KEY_DOWN} key or a positive GAMEPAD_TAP AXIS_POV.
 * @author Matthew Tropiano
 */
public class FocusOrderGroup
{
	public static enum Direction
	{
		HORIZONTAL,
		VERTICAL;
	}
	
	/** Directionality. */
	protected Direction direction;
	/** Object order list. */
	protected GUIObject[] order;
	
	/**
	 * Creates a new {@link FocusOrderGroup} and binds the required actions
	 * to the objects provided.
	 * @param direction the directionality of the focus keys. 
	 * @param objects the objects to include in the group.
	 */
	public FocusOrderGroup(Direction direction, GUIObject ... objects)
	{
		this(direction, false, objects);
	}

	/**
	 * Creates a new {@link FocusOrderGroup} and binds the required actions
	 * to the objects provided.
	 * @param direction the directionality of the focus keys. 
	 * @param loopOrder if true, the order loops back to the beginning, and not if false.
	 * @param objects the objects to include in the group.
	 */
	public FocusOrderGroup(Direction direction, boolean loopOrder, GUIObject ... objects)
	{
		this.direction = direction;
		order = new GUIObject[objects.length];
		System.arraycopy(objects, 0, order, 0, objects.length);
		bind(loopOrder, order);
	}

	/**
	 * Creates a new {@link FocusOrderGroup} and binds the required actions
	 * to the objects provided.
	 * @param direction the directionality of the focus keys. 
	 * @param objects the objects to include in the group.
	 */
	public FocusOrderGroup(Direction direction, Iterable<GUIObject> objects)
	{
		this(direction, false, objects);
	}

	/**
	 * Creates a new {@link FocusOrderGroup} and binds the required actions
	 * to the objects provided.
	 * @param direction the directionality of the focus keys. 
	 * @param loopOrder if true, the order loops back to the beginning, and not if false.
	 * @param objects the objects to include in the group.
	 */
	public FocusOrderGroup(Direction direction, boolean loopOrder, Iterable<GUIObject> objects)
	{
		this.direction = direction;
		int x = 0;
		for (@SuppressWarnings("unused") GUIObject obj : objects)
			x++;
		order = new GUIObject[x];
		
		x = 0;
		for (GUIObject obj : objects)
			order[x++] = obj;
		bind(loopOrder, order);
	}
	
	/**
	 * Binds the actions in this group to the objects in the group.
	 * @param loopOrder if true, the end loops back to the beginning.
	 * @param objects the objects in the group.
	 */
	protected void bind(boolean loopOrder, GUIObject[] objects)
	{
		String[] eventTypes = {
			GUIObject.EVENT_KEY_PRESS, GUIObject.EVENT_GAMEPAD_AXIS, GUIObject.EVENT_GAMEPAD_TAP_PRESS
		};
		
		for (int i = 0; i < objects.length; i++)
		{
			GUIObject back = i > 0 ? objects[i-1] : 
				(loopOrder ? objects[objects.length - 1] : null); 
			GUIObject fwd = i < objects.length - 1 ? objects[i+1] : 
				(loopOrder ? objects[0] : null);
			GroupAction ga = new GroupAction(back, fwd);
			objects[i].bindAction(ga, eventTypes);
		}
	}
	
	/**
	 * Action added to objects for advancing focus. 
	 */
	protected class GroupAction implements GUIAction
	{
		protected GUIObject backwardObject;
		protected GUIObject forwardObject;
		
		public GroupAction(GUIObject backward, GUIObject forward)
		{
			this.backwardObject = backward;
			this.forwardObject = forward;
		}

		private void goForward()
		{
			if (forwardObject != null)
				forwardObject.requestFocus();
		}

		private void goBackward()
		{
			if (backwardObject != null)
				backwardObject.requestFocus();
		}
		
		@Override
		public void call(GUIEvent event)
		{
			if (event.isKeyboardEvent())
			{
				switch (event.getKeyCode())
				{
					case GUIInputConstants.KEY_RIGHT:
						if (direction == Direction.HORIZONTAL)
							goForward();
						break;
					case GUIInputConstants.KEY_DOWN:
						if (direction == Direction.VERTICAL)
							goForward();
						break;
					case GUIInputConstants.KEY_LEFT:
						if (direction == Direction.HORIZONTAL)
							goBackward();
						break;
					case GUIInputConstants.KEY_UP:
						if (direction == Direction.VERTICAL)
							goBackward();
						break;
				}
			}
			else if (event.isGamepadAxisEvent())
			{
				if (event.getGamepadAxisId() == GUIInputConstants.AXIS_POV)
				{
					float aval = event.getGamepadAxisValue();
					if (direction == Direction.HORIZONTAL)
					{
						if (aval >= GUIInputConstants.AXIS_POV_UP_RIGHT && aval <= GUIInputConstants.AXIS_POV_DOWN_RIGHT)
							goForward();
						else if ((aval >= GUIInputConstants.AXIS_POV_DOWN_LEFT && aval <= GUIInputConstants.AXIS_POV_LEFT) || aval == GUIInputConstants.AXIS_POV_UP_LEFT)
							goBackward();
					}
					else if (direction == Direction.VERTICAL)
					{
						if (aval >= GUIInputConstants.AXIS_POV_UP_LEFT && aval <= GUIInputConstants.AXIS_POV_UP_RIGHT)
							goBackward();
						else if (aval >= GUIInputConstants.AXIS_POV_DOWN_RIGHT && aval <= GUIInputConstants.AXIS_POV_DOWN_LEFT)
							goForward();
					}
				}
			}
			else if (event.isGamepadAxisTapEvent())
			{
				if (direction == Direction.HORIZONTAL)
				{
					if (event.getGamepadAxisId() == GUIInputConstants.AXIS_X)
					{
						if (event.getGamepadAxisTapValue())
							goForward();
						else
							goBackward();
					}
				}
				else if (direction == Direction.VERTICAL)
				{
					if (event.getGamepadAxisId() == GUIInputConstants.AXIS_Y)
					{
						if (event.getGamepadAxisTapValue())
							goForward();
						else
							goBackward();
					}
				}
			}
		}
	}
	
}
