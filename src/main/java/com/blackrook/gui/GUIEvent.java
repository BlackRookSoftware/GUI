/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

/**
 * Event class for things that happen in the GUI.
 * Be warned that event objects may be pooled - this should only be used
 * for feedback purposes. Do not store references to these objects, 
 * as they be may be reused by the GUI system. They are also NOT THREAD-SAFE.
 * @see GUIInputConstants
 * @author Matthew Tropiano
 */
public final class GUIEvent implements GUIInputConstants
{
	/**
	 * Origin of event.
	 */
	public static enum Origin
	{
		/** Event has unknown origin. */
		UNKNOWN,
		/** Event originated from an action call. */
		ACTION,
		/** Event originated from Input. */
		INPUT,
		/** Event originated from GUI System. */
		GUI,
	}
	
	/**
	 * Input type on event.
	 */
	private static enum InputType
	{
		/** Not an input event. */
		NONE,
		/** Keyboard event. */
		KEYBOARD,
		/** Mouse event. */
		MOUSE,
		/** Mouse wheel event. */
		MOUSE_WHEEL,
		/** Gamepad event. */
		GAMEPAD,
		/** Gamepad axis event. */
		GAMEPAD_AXIS,
		/** Gamepad axis tap event. */
		GAMEPAD_AXIS_TAP,
		/** Generic event. */
		GENERIC;
	}
	
	/** GUI system node that this event occurred on. */
	private GUI gui;
	/** Object that this event occurred on. */
	private GUIObject object;
	/** Event type name. */
	private String type;
	/** Event API origin. */
	private Origin origin;
	/** Input type for event. */
	private InputType inputType;
	/** Button press/release. */
	private boolean release;
	/** Keyboard modifier. */
	private int imod;
	/** Integer argument 0. */
	private int iarg0;
	/** Integer argument 1. */
	private int iarg1;
	/** Integer argument 2. */
	private int iarg2;
	/** Integer argument 3. */
	private int iarg3;
	/** Integer argument 4. */
	private int iarg4;
	/** Integer argument 5. */
	private int iarg5;
	/** Float argument 0. */
	private float farg0;
	
	/** Creates a completely new event. */
	GUIEvent()
	{
		reset();
	}
	
	/** Resets this event's fields. */
	void reset()
	{
		this.gui = null;
		this.object = null;
		this.type = GUIObject.EVENT_UNKNOWN;
		this.origin = Origin.UNKNOWN;
		this.release = false;
		this.iarg0 = -1;
		this.iarg1 = -1;
		this.iarg2 = -1;
		this.iarg3 = -1;
		this.iarg4 = -1;
		this.iarg5 = -1;
		this.farg0 = 0f;
	}
	
	/**
	 * @return a reference to the GUI node that this event happened on. 
	 */
	public GUI getGUI()
	{
		return gui;
	}
	
	/** 
	 * Returns a reference to the object that is the source of this event.
	 * May be null if this event was not fired by an object. 
	 * @return the object.
	 */
	public GUIObject getObject()
	{
		return object;
	}
	
	/**
	 * @return the type of event that this is.
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * @return what system originated this event.
	 */
	public Origin getOrigin()
	{
		return origin;
	}
		
	/**
	 * @return true if this is a key/button release event, false if not, or it's a press.
	 */
	public boolean isRelease() 
	{
		return release;
	}
	
	/**
	 * Is this event a generic input event?
	 * @return true if so, false if not.
	 */
	public boolean isGenericInputEvent()
	{
		return inputType == InputType.GENERIC;
	}
	
	/**
	 * Is this event a keyboard event?
	 * @return true if so, false if not.
	 */
	public boolean isKeyboardEvent()
	{
		return inputType == InputType.KEYBOARD;
	}
	
	/**
	 * Is this event a mouse event?
	 * @return true if so, false if not.
	 */
	public boolean isMouseEvent()
	{
		return inputType == InputType.MOUSE;
	}
	
	/**
	 * Is this event a mouse wheel event?
	 * @return true if so, false if not.
	 */
	public boolean isMouseWheelEvent()
	{
		return inputType == InputType.MOUSE_WHEEL;
	}
	
	/**
	 * Is this event a gamepad axis event?
	 * @return true if so, false if not.
	 */
	public boolean isGamepadEvent()
	{
		return inputType == InputType.GAMEPAD;
	}
	
	/**
	 * Is this event a gamepad axis event?
	 * @return true if so, false if not.
	 */
	public boolean isGamepadAxisEvent()
	{
		return inputType == InputType.GAMEPAD_AXIS;
	}
	
	/**
	 * Is this event a gamepad axis tap event?
	 * @return true if so, false if not.
	 */
	public boolean isGamepadAxisTapEvent()
	{
		return inputType == InputType.GAMEPAD_AXIS_TAP;
	}
	
	/**
	 * Is this event an object event?
	 * @return true if so, false if not.
	 */
	public boolean isObjectEvent()
	{
		return inputType == InputType.NONE;
	}
	
	/**
	 * Returns the code associated with this event, if this was a generic event.
	 * If this is NOT a GENERIC event, this returns -1.
	 * @return the input code.
	 */
	public int getCode()
	{
		if (!isGenericInputEvent())
			return -1;
		return iarg0;
	}
	
	/**
	 * @return the key modifier mask associated with this event.
	 */
	public int getKeyModifier()
	{
		return imod;
	}
	
	/**
	 * Returns the key code associated with this event, if this was a keyboard event.
	 * If this is NOT a keyboard event, this returns -1.
	 * @return the key code.
	 */
	public int getKeyCode()
	{
		if (!isKeyboardEvent())
			return -1;
		return iarg0;
	}
	
	/**
	 * Returns the mouse button associated with this event, if this was a mouse event.
	 * If this is NOT a mouse event, this returns -1.
	 * @return the mouse button.
	 */
	public int getMouseButton()
	{
		if (!isMouseEvent())
			return -1;
		return iarg0;
	}

	/**
	 * Returns the mouse cursor position, x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 * @return the mouse position.
	 */
	public int getMousePositionX()
	{
		if (!isMouseEvent())
			return -1;
		return iarg0;
	}

	/**
	 * Returns the mouse cursor position, y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 * @return the mouse position.
	 */
	public int getMousePositionY()
	{
		if (!isMouseEvent())
			return -1;
		return iarg1;
	}

	/**
	 * Returns the amount of units that the mouse moved, x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 * @return the mouse movement units.
	 */
	public int getMouseMovementX()
	{
		if (!isMouseEvent())
			return -1;
		return iarg2;
	}

	/**
	 * Returns the amount of units that the mouse moved, y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 * @return the mouse movement units.
	 */
	public int getMouseMovementY()
	{
		if (!isMouseEvent())
			return -1;
		return iarg3;
	}

	/**
	 * Returns the mouse cursor position inside the current object that it is over, 
	 * x-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 * @return the mouse object position.
	 */
	public int getMouseObjectPositionX()
	{
		if (!isMouseEvent())
			return -1;
		return iarg4;
	}

	/**
	 * Returns the mouse cursor position inside the current object that it is over, 
	 * y-axis, associated with this event, if this was a mouse event.
	 * This is in units native to the GUI system!
	 * @return the mouse object position.
	 */
	public int getMouseObjectPositionY()
	{
		if (!isMouseEvent())
			return -1;
		return iarg5;
	}

	/**
	 * Returns the amount of units that the mouse wheel moved, if this is a mouse wheel event.
	 * If this is NOT a mouse wheel event, this returns 0.
	 * @return the mouse wheel movement amount.
	 */
	public int getMouseWheelMovement()
	{
		if (!isMouseWheelEvent())
			return 0;
		return iarg0;
	}
	
	/**
	 * Returns the id of the controller that this event happened on.
	 * If this is NOT a gamepad event, the returns -1.
	 * @return the mouse wheel movement amount.
	 */
	public int getGamepadId()
	{
		if (!isGamepadEvent() && !isGamepadAxisEvent() && !isGamepadAxisTapEvent())
			return -1;
		return iarg0;
	}
	
	/**
	 * Returns the axis on the controller that this event happened on.
	 * If this is NOT a gamepad event, the returns {@link GUIInputConstants#AXIS_UNDEFINED}.
	 * @return the axis id.
	 */
	public int getGamepadAxisId()
	{
		if (!isGamepadAxisEvent() && !isGamepadAxisTapEvent())
			return AXIS_UNDEFINED;
		return iarg1;
	}
	
	/**
	 * Returns the axis on the controller that this event happened on.
	 * If this is NOT a gamepad event, the returns {@link GUIInputConstants#GAMEPAD_UNDEFINED}.
	 * @return the gamepad button.
	 */
	public int getGamepadButton()
	{
		if (!isGamepadEvent())
			return GAMEPAD_UNDEFINED;
		return iarg1;
	}
	
	/**
	 * Returns the value on the controller axis that this event happened on.
	 * If this is NOT a gamepad axis event, the returns 0f.
	 * @return the gamepad axis value.
	 */
	public float getGamepadAxisValue()
	{
		if (!isGamepadAxisEvent())
			return 0f;
		return farg0;
	}
	
	/**
	 * Returns the positivity on the controller axis that a tap event happened on.
	 * If this is NOT a gamepad axis tap event, the returns false.
	 * @return the gamepad axis tap value.
	 */
	public boolean getGamepadAxisTapValue()
	{
		if (!isGamepadAxisTapEvent())
			return false;
		return farg0 > 0f;
	}
	
	/** 
	 * Sets the GUI reference. 
	 */
	void setGUI(GUI gui)
	{
		this.gui = gui;
	}
	
	/** 
	 * Sets the object reference. 
	 */
	void setObject(GUIObject object)
	{
		this.object = object;
	}
	
	/**
	 * Sets the type. 
	 */
	void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 * Sets if this is a button release (false is press).
	 */
	void setRelease(boolean release)
	{
		this.release = release;
	}
	
	/** 
	 * Sets the origin. 
	 */
	void setOrigin(Origin origin)
	{
		this.origin = origin;
	}
	
	/** 
	 * Sets the key modifier. 
	 */
	void setKeyModifier(int keyModifier)
	{
		this.imod = keyModifier;
	}
	
	/** Sets the generic code. */
	void setCode(int code)
	{
		inputType = InputType.GENERIC;
		this.iarg0 = code;
	}
	
	/** 
	 * Sets the key code. 
	 */
	void setKeyCode(int keyCode)
	{
		inputType = InputType.KEYBOARD;
		this.iarg0 = keyCode;
	}
	
	/** 
	 * Sets the mouse button.
	 */
	void setMouseButton(int button)
	{
		inputType = InputType.MOUSE;
		this.iarg0 = button;
	}
	
	/** 
	 * Sets the mouse wheel movement units. 
	 */
	void setMouseWheelUnits(int units)
	{
		inputType = InputType.MOUSE_WHEEL;
		this.iarg0 = units;
	}

	/**
	 * Sets the mouse movement/location units. 
	 * @param positionX position X in GUI units.
	 * @param positionY position Y in GUI units.
	 * @param movementX mouse movement X in GUI units.
	 * @param movementY mouse movement Y in GUI units.
	 * @param objectX object position X in GUI units.
	 * @param objectY object position Y in GUI units.
	 */
	void setMouseUnits(int positionX, int positionY, int movementX, int movementY, int objectX, int objectY)
	{
		inputType = InputType.MOUSE;
		iarg0 = positionX;
		iarg1 = positionY;
		iarg2 = movementX;
		iarg3 = movementY;
		iarg4 = objectX;
		iarg5 = objectY;
	}
	
	/**
	 * Sets the gamepad button.
	 * @param gamepadId the id of the gamepad that this happened on.
	 * @param gamepadButton the gamepad button pressed.
	 */
	void setGamepadButton(int gamepadId, int gamepadButton)
	{
		inputType = InputType.GAMEPAD;
		iarg0 = gamepadId;
		iarg1 = gamepadButton;
	}
	
	/**
	 * Sets the gamepad axis value.
	 * @param gamepadId the id of the gamepad that this happened on.
	 * @param gamepadAxisId the axis id.
	 * @param value the new axis value.
	 */
	void setGamepadAxes(int gamepadId, int gamepadAxisId, float value)
	{
		inputType = InputType.GAMEPAD_AXIS;
		iarg0 = gamepadId;
		iarg1 = gamepadAxisId;
		farg0 = value;
	}
	
	/**
	 * Sets the gamepad axis value.
	 * @param gamepadId the id of the gamepad that this happened on.
	 * @param gamepadAxisId the axis id.
	 * @param positive if true, tap was at positive axis.
	 */
	void setGamepadAxisTap(int gamepadId, int gamepadAxisId, boolean positive)
	{
		inputType = InputType.GAMEPAD_AXIS_TAP;
		iarg0 = gamepadId;
		iarg1 = gamepadAxisId;
		farg0 = positive ? 1f : -1f;
	}
	
}
