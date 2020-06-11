/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.input;

/**
 * A keystroke defined by this GUI for listening for broadcast events.
 * The key masks are defined by the MASK_* constants in this class.
 * @author Matthew Tropiano
 */
public class GUIKeyStroke implements GUIKeyTables
{
	/** Input type, Keyboard. */
	public static final int INPUT_KEY = 			1; 
	/** Input type, Gamepad. */
	public static final int INPUT_GAMEPAD = 		2; 
	/** Input type, Gamepad Axis. */
	public static final int INPUT_GAMEPAD_AXIS = 	3; 
	/** Input type, Generic. */
	public static final int INPUT_GENERIC = 		4; 

	/** Mask for CTRL key. */
	public static final int MASK_CTRL = 			(1 << 0); 
	/** Mask for ALT key. */
	public static final int MASK_ALT = 				(1 << 1); 
	/** Mask for ALT GRAPH key. */
	public static final int MASK_ALT_GRAPH =		(1 << 2); 
	/** Mask for SHIFT key. */
	public static final int MASK_SHIFT = 			(1 << 3); 
	/** Mask for META key. */
	public static final int MASK_META = 			(1 << 4); 
	/** Mask for WINDOW key. */
	public static final int MASK_WIN = 				(1 << 5); 

	/** Input type. */
	private int inputType;
	/** Key mask. */
	private int keyMask;
	/** The keypress itself. */
	private int keyPress;
	/** Is this a key release? */
	private boolean flag;
	
	/**
	 * Creates a keystroke.
	 * @param inputType the input type.
	 * @param keyMask the key mask.
	 * @param keyPress the key press.
	 * @param flag the modifier flag.
	 */
	protected GUIKeyStroke(int inputType, int keyMask, int keyPress, boolean flag)
	{
		this.inputType = inputType;
		this.keyMask = keyMask;
		this.keyPress = keyPress;
		this.flag = flag;
	}
	
	/**
	 * Creates a keystroke by parsing a string that represents the keystroke.
	 * <p>The key is defined by how a keystroke is defined in menus: modifiers
	 * plus the key. Gamepad taps are "PAD" plus an axis plus "+" or "-" to signify the axis direction.
	 * If no direction, assumes button.
	 * The keys and axes are not case-sensitive.
	 * <p>For example:
	 * <ul>
	 * <li><code>"X"</code> -&gt; <code>GUIKeyStroke.create(INPUT_KEY, 0, OGLInputConstants.KEY_X, false)</code></li>
	 * <li><code>"CTRL+X"</code> -&gt; <code>GUIKeyStroke.create(INPUT_KEY, MASK_CTRL, OGLInputConstants.KEY_X, false)</code></li>
	 * <li><code>"CTRL+ALT+X"</code> -&gt; <code>GUIKeyStroke.create(INPUT_KEY, MASK_CTRL | MASK_ALT, OGLInputConstants.KEY_X, false)</code></li>
	 * <li><code>"X release"</code> -&gt; <code>GUIKeyStroke.create(INPUT_KEY, 0, OGLInputConstants.KEY_X, true)</code></li>
	 * <li><code>"PAD 2"</code> -&gt; <code>GUIKeyStroke.create(INPUT_GAMEPAD, 0, OGLInputConstants.GAMEPAD_2)</code></li>
	 * <li><code>"PAD X+"</code> -&gt; <code>GUIKeyStroke.create(INPUT_GAMEPAD_AXIS, 0, OGLInputConstants.AXIS_X, true)</code></li>
	 * <li><code>"PAD Y-"</code> -&gt; <code>GUIKeyStroke.create(INPUT_GAMEPAD_AXIS, 0, OGLInputConstants.AXIS_Y, false)</code></li>
	 * <li><code>"generic 2"</code> -&gt; <code>GUIKeyStroke.create(INPUT_GENERIC, 0, 2, false)</code></li>
	 * </ul> 
	 * @param keystroke the keystroke name.
	 * @return the corresponding key stroke.
	 * @throws IllegalArgumentException if the keystroke can't be parsed.
	 */
	public static GUIKeyStroke create(String keystroke)
	{
		if (keystroke == null)
			throw new IllegalArgumentException("Can't parse a null string.");

		String[] tokens = keystroke.split("\\s+");
		
		if (tokens.length == 0)
			throw new IllegalArgumentException("Can't parse the empty string.");
		else if (tokens.length > 1 && tokens[0].equalsIgnoreCase(GENERIC_PREFIX))
		{
			GUIKeyStroke out = parseGeneric(tokens[1]);
			return out;
		}
		else if (tokens.length > 1 && tokens[0].equalsIgnoreCase(GAMEPAD_PREFIX))
		{
			GUIKeyStroke out = parseGamepad(tokens[1]);
			if (tokens.length > 2)
			{
				if (tokens[2].equalsIgnoreCase(KEY_RELEASE))
					out.flag = true;
				else
					throw new IllegalArgumentException("Expected \"release\".");
			}
			
			return out;
		}
		else
		{
			GUIKeyStroke out = parseKey(tokens[0]);
			if (tokens.length > 1)
			{
				if (tokens[1].equalsIgnoreCase(KEY_RELEASE))
					out.flag = true;
				else
					throw new IllegalArgumentException("Expected \"release\".");
			}
			return out;
		}
	}

	// Parse a generic.
	private static GUIKeyStroke parseGeneric(String numeric)
	{
		Integer i;
		try {
			i = Integer.parseInt(numeric);
		} catch (NumberFormatException e) {
			i = null;
		}
		if (i != null)
			return create(INPUT_GENERIC, 0, i, false);
		else
			throw new IllegalArgumentException("Expected valid number. Token \""+numeric+"\"");
	}

	// Parse a gamepad.
	private static GUIKeyStroke parseGamepad(String keystroke)
	{
		if (GAMEPAD_BUTTON_MAP.containsKey(keystroke))
			return create(INPUT_GAMEPAD, 0, GAMEPAD_BUTTON_MAP.get(keystroke), false);
		else if (GAMEPAD_AXIS_TAP_MAP.containsKey(keystroke))
		{
			return create(INPUT_GAMEPAD_AXIS, 0, 
					GAMEPAD_AXIS_TAP_MAP.get(keystroke), GAMEPAD_AXIS_TAP_FLAG_MAP.get(keystroke));
		}
		else
			throw new IllegalArgumentException("Expected valid key. Token \""+keystroke+"\"");
	}

	// Parse a key.
	private static GUIKeyStroke parseKey(String keystroke)
	{
		int inputMask = 0;
		int inputKey = 0;
		
		String[] keys = keystroke.split("[\\+\\-]");

		for (int i = 0; i < keys.length; i++)
		{
			if (i < keys.length - 1)
			{
				if (KEYBOARD_MASK_MAP.containsKey(keys[i]))
					inputMask |= KEYBOARD_MASK_MAP.get(keys[i]);
				else
					throw new IllegalArgumentException("Expected valid modifier key. Token \""+keys[i]+"\"");
			}
			else
			{
				if (KEYBOARD_KEY_MAP.containsKey(keys[i]))
					inputKey = KEYBOARD_KEY_MAP.get(keys[i]);
				else
					throw new IllegalArgumentException("Expected valid key. Token \""+keys[i]+"\"");
			}
		}

		return create(INPUT_KEY, inputMask, inputKey, false);
	}
	
	/**
	 * Creates a new generic input keystroke.
	 * @param code the input code.
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createGeneric(int code)
	{
		return new GUIKeyStroke(INPUT_GENERIC, 0, code, false);
	}

	/**
	 * Creates a new gamepad button press keystroke with no mask.
	 * @param button the button press (OGLInputContants.*).
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createGamepad(int button)
	{
		return new GUIKeyStroke(INPUT_GAMEPAD, 0, button, false);
	}

	/**
	 * Creates a new gamepad button keystroke with no mask.
	 * @param button the button press (OGLInputContants.*).
	 * @param flag if true, this is a release.
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createGamepad(int button, boolean flag)
	{
		return new GUIKeyStroke(INPUT_GAMEPAD, 0, button, flag);
	}

	/**
	 * Creates a new gamepad button keystroke.
	 * @param keyMask the key mask (GUIKeyStroke.MASK_*).
	 * @param button the button press (OGLInputContants.*).
	 * @param flag if true, this is a release.
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createGamepad(int keyMask, int button, boolean flag)
	{
		return new GUIKeyStroke(INPUT_GAMEPAD, keyMask, button, flag);
	}

	/**
	 * Creates a new gamepad axis tap with no mask.
	 * @param axis the axis (OGLInputContants.*).
	 * @param positive if true, positive axis, if false, negative.
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createGamepadTap(int axis, boolean positive)
	{
		return new GUIKeyStroke(INPUT_GAMEPAD_AXIS, 0, axis, positive);
	}

	/**
	 * Creates a new keyboard press keystroke with no mask.
	 * @param keyPress the key press (OGLInputContants.*).
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createKey(int keyPress)
	{
		return new GUIKeyStroke(INPUT_KEY, 0, keyPress, false);
	}

	/**
	 * Creates a new keyboard keystroke with no mask.
	 * @param keyPress the key press (OGLInputContants.*).
	 * @param flag if keyboard key, this is a release. If a gamepad axis tap: if true, positive axis, if false, negative.
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createKey(int keyPress, boolean flag)
	{
		return new GUIKeyStroke(INPUT_KEY, 0, keyPress, flag);
	}

	/**
	 * Creates a new keyboard keystroke.
	 * @param keyMask the key mask (GUIKeyStroke.MASK_*).
	 * @param keyPress the key press (OGLInputContants.*).
	 * @param flag if keyboard key, this is a release. If a gamepad axis tap: if true, positive axis, if false, negative.
	 * @return the corresponding key stroke.
	 */
	public static GUIKeyStroke createKey(int keyMask, int keyPress, boolean flag)
	{
		return new GUIKeyStroke(INPUT_KEY, keyMask, keyPress, flag);
	}

	/**
	 * Creates a new keystroke.
	 * @param inputType the input type (GUIKeyStroke.INPUT_*).
	 * @param keyMask the key mask (GUIKeyStroke.MASK_*).
	 * @param keyPress the key press (OGLInputContants.*).
	 * @param flag if keyboard key, this is a release. If a gamepad axis tap: if true, positive axis, if false, negative.
	 */
	private static GUIKeyStroke create(int inputType, int keyMask, int keyPress, boolean flag)
	{
		return new GUIKeyStroke(inputType, keyMask, keyPress, flag);
	}

	@Override
	public int hashCode()
	{
		return keyPress;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof GUIKeyStroke)
			return equals((GUIKeyStroke)obj);
		return super.equals(obj);
	}

	/**
	 * Tests if this keystroke equals another.
	 * @param ks the key stroke.
	 * @return true if so, false if not.
	 */
	public boolean equals(GUIKeyStroke ks)
	{
		return 
			this.inputType == ks.inputType
			&& this.keyMask == ks.keyMask
			&& this.keyPress == ks.keyPress
			&& this.flag == ks.flag;
	}
	
}
