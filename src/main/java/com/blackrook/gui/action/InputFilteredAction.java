/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.action;

import java.util.HashSet;
import java.util.Set;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;
import com.blackrook.gui.GUIInputConstants;

/**
 * An action that validates HOW it was activated before it is actually called. 
 * This is meant to be attached to more than one type of event, especially if 
 * mouse, gamepad, and keyboard keys are used for validation.
 * @author Matthew Tropiano
 */
public class InputFilteredAction implements GUIAction
{
	/** The action to call. */
	private GUIAction action;
	/** Set of valid generic codes. */
	private Set<Integer> validCodes;
	/** Set of valid keys. */
	private Set<Integer> validKeyCodes;
	/** Set of valid mouse buttons. */
	private Set<Integer> validMouseButtons;
	/** Set of valid gamepad buttons. */
	private Set<Integer> validGamepadButtons;
	/** Set of valid gamepad buttons. */
	private Set<AxisTap> validGamepadTaps;
	
	private InputFilteredAction(GUIAction action)
	{
		this.action = action;
		this.validCodes = null;
		this.validKeyCodes = null;
		this.validMouseButtons = null;
		this.validGamepadButtons = null;
		this.validGamepadTaps = null;
	}
	
	/**
	 * Creates a new input-filtered action.
	 * @param action the action to call after the input is validated.
	 * @return a new filtered action.
	 */
	public static InputFilteredAction create(GUIAction action)
	{
		return new InputFilteredAction(action);
	}
	
	/**
	 * Creates a new axis tap.
	 * @param axisId the axis id.
	 * @param positive true if positive axis direction, false if negative.
	 * @return a new axis tap.
	 */
	public static AxisTap axisTap(int axisId, boolean positive)
	{
		return new AxisTap(axisId, positive);
	}

	/**
	 * Adds a "valid" generic code to this action.
	 * Codes are user defined and can be anything.
	 * @param codes the generic codes to add.
	 * @return itself, for call chaining.
	 */
	public InputFilteredAction setCodes(int ... codes)
	{
		if (codes.length == 0)
		{
			validCodes = null;
			return this;
		}
		
		if (validCodes == null)
			validCodes = new HashSet<>(2);
		else
			validCodes.clear();
		
		for (int i = 0; i < codes.length; i++)
			validCodes.add(codes[i]);

		return this;
	}
	
	/**
	 * Adds a "valid" key code to this action.
	 * Key codes are defined by the ones in {@link GUIInputConstants}.
	 * @param keyCodes the key codes to add.
	 * @return itself, for call chaining.
	 */
	public InputFilteredAction setKeyCodes(int ... keyCodes)
	{
		if (keyCodes.length == 0)
		{
			validKeyCodes = null;
			return this;
		}
		
		if (validKeyCodes == null)
			validKeyCodes = new HashSet<>(2);
		else
			validKeyCodes.clear();

		for (int i = 0; i < keyCodes.length; i++)
			validKeyCodes.add(keyCodes[i]);

		return this;
	}
	
	/**
	 * Adds a "valid" mouse button to this action.
	 * Mouse buttons are defined by the ones in {@link GUIInputConstants}.
	 * @param buttons the mouse buttons to add.
	 * @return itself, for call chaining.
	 */
	public InputFilteredAction setMouseButtons(int ... buttons)
	{
		if (buttons.length == 0)
		{
			validMouseButtons = null;
			return this;
		}
		
		if (validMouseButtons == null)
			validMouseButtons = new HashSet<>(2);
		else
			validMouseButtons.clear();

		for (int i = 0; i < buttons.length; i++)
			validMouseButtons.add(buttons[i]);

		return this;
	}
	
	/**
	 * Adds a "valid" gamepad button to this action.
	 * Gamepad buttons are defined by the ones in {@link GUIInputConstants}.
	 * the equivalents in {@link GUIInputConstants}.  
	 * @param buttons the gamepad buttons to add.
	 * @return itself, for call chaining.
	 */
	public InputFilteredAction setGamepadButtons(int ... buttons)
	{
		if (buttons.length == 0)
		{
			validGamepadButtons = null;
			return this;
		}
		
		if (validGamepadButtons == null)
			validGamepadButtons = new HashSet<>(2);
		else
			validGamepadButtons.clear();

		for (int i = 0; i < buttons.length; i++)
			validGamepadButtons.add(buttons[i]);

		return this;
	}
	
	/**
	 * Adds a "valid" gamepad axis tap to this action.
	 * Gamepad axes are defined by the ones in {@link GUIInputConstants}.
	 * the equivalents in {@link GUIInputConstants}.
	 * @param axisTaps the axis taps to set.  
	 * @return itself, for call chaining.
	 */
	public InputFilteredAction setGamepadAxisTaps(AxisTap ... axisTaps)
	{
		if (axisTaps.length == 0)
		{
			validGamepadTaps = null;
			return this;
		}
		
		if (validGamepadTaps == null)
			validGamepadTaps = new HashSet<>(2);
		else
			validGamepadTaps.clear();

		for (int i = 0; i < axisTaps.length; i++)
			validGamepadTaps.add(axisTaps[i]);

		return this;
	}
	
	@Override
	public final void call(GUIEvent event)
	{
		if (event.isGenericInputEvent())
		{
			if (validCodes != null && validCodes.contains(event.getKeyCode()))
				action.call(event);
		}
		else if (event.isKeyboardEvent())
		{
			if (validKeyCodes != null && validKeyCodes.contains(event.getKeyCode()))
				action.call(event);
		}
		else if (event.isMouseEvent())
		{
			if (validMouseButtons != null && validMouseButtons.contains(event.getMouseButton()))
				action.call(event);
		}
		else if (event.isGamepadEvent())
		{
			if (validGamepadButtons != null && validGamepadButtons.contains(event.getGamepadButton()))
				action.call(event);
		}
		else if (event.isGamepadAxisTapEvent())
		{
			if (validGamepadTaps != null && validGamepadTaps.contains(new AxisTap(event.getGamepadAxisId(), event.getGamepadAxisTapValue())))
				action.call(event);
		}
	}
	
	/**
	 * A single Axis Tap.
	 */
	public static class AxisTap
	{
		private int key;
		private boolean value;
		
		private AxisTap(int key, boolean value) 
		{
			this.key = key;
			this.value = value;
		}
		
		@Override
		public int hashCode() 
		{
			return Integer.hashCode(key) ^ Boolean.hashCode(value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AxisTap)
				return equals(((AxisTap)obj));
			return super.equals(obj);
		}
		
		public boolean equals(AxisTap obj) 
		{
			return key == obj.key && value == obj.value;
		}

	}
	
}
