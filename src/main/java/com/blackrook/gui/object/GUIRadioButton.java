/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;
import com.blackrook.gui.GUIInputConstants;

/**
 * A toggle panel that has its own theme keys depending on its state, radio button style.
 * If this button is set, it will not be unset on input.
 * <p>
 * <table>
 * <tr>
 * 		<td>Checked, Enabled</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_ON}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Enabled</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_OFF}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Focused</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_ON_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Focused</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_OFF_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Disabled</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_ON_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Disabled</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_OFF_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Enabled, Pressed</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_ON_PRESSED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Enabled, Pressed</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_OFF_PRESSED}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Focused, Pressed</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_ON_FOCUSED_PRESSED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Focused, Pressed</td>
 * 		<td>{@link GUIRadioButton#THEME_KEY_RADIO_OFF_FOCUSED_PRESSED}</td>
 * </tr>
 * </table>
 * @author Matthew Tropiano
 */
public class GUIRadioButton extends GUITogglePanel
{
	/** Theme key for a checked radio button. */
	public static final String THEME_KEY_RADIO_ON = "radio_on";
	/** Theme key for an unchecked radio button. */
	public static final String THEME_KEY_RADIO_OFF = "radio_off";
	/** Theme key for a checked, focused radio button. */
	public static final String THEME_KEY_RADIO_ON_FOCUSED = "radio_on_focused";
	/** Theme key for an unchecked, focused radio button. */
	public static final String THEME_KEY_RADIO_OFF_FOCUSED = "radio_off_focused";
	/** Theme key for a checked, disabled radio button. */
	public static final String THEME_KEY_RADIO_ON_DISABLED = "radio_on_disabled";
	/** Theme key for an unchecked, disabled radio button. */
	public static final String THEME_KEY_RADIO_OFF_DISABLED = "radio_off_disabled";
	/** Theme key for a checked radio button, during press. */
	public static final String THEME_KEY_RADIO_ON_PRESSED = "radio_on_pressed";
	/** Theme key for an unchecked radio button, during press. */
	public static final String THEME_KEY_RADIO_OFF_PRESSED = "radio_off_pressed";
	/** Theme key for a checked, focused radio button, during press. */
	public static final String THEME_KEY_RADIO_ON_FOCUSED_PRESSED = "radio_on_focused_pressed";
	/** Theme key for an unchecked, focused radio button, during press. */
	public static final String THEME_KEY_RADIO_OFF_FOCUSED_PRESSED = "radio_off_focused_pressed";

	/** Click action for the radio - calls {@link #toggle()} */
	protected static final GUIAction CLICK_ACTION = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			GUIRadioButton tp = (GUIRadioButton)event.getObject();
			if (tp.isSet())
				return;
			
			tp.toggle();
		};
	};
		
	protected static final GUIAction KEY_TYPE_ACTION = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			GUIRadioButton tp = (GUIRadioButton)event.getObject();
			
			if (tp.isSet())
				return;
			
			int keyCode = event.getKeyCode();
			if (keyCode == GUIInputConstants.KEY_ENTER)
				tp.toggle();
			else if (keyCode == GUIInputConstants.KEY_SPACE)
				tp.toggle();
		};
	};
	
	/**
	 * Creates a new radio button in the unset, false state.
	 */
	public GUIRadioButton()
	{
		this(false);
	}
	
	/**
	 * Creates a new radio button.
	 * @param startState the starting state (true or false).
	 */
	public GUIRadioButton(boolean startState)
	{
		super(startState);
		bindAction(CLICK_ACTION, EVENT_MOUSE_CLICK);
		bindAction(KEY_TYPE_ACTION, EVENT_KEY_TYPE);
	}
	
	@Override
	public String getThemeKey()
	{
		if (!isEnabled())
			return isSet() ? THEME_KEY_RADIO_ON_DISABLED : THEME_KEY_RADIO_OFF_DISABLED;
		else
		{
			if (isBeingClicked())
			{
				if (isFocused())
					return isSet() ? THEME_KEY_RADIO_ON_FOCUSED_PRESSED : THEME_KEY_RADIO_OFF_FOCUSED_PRESSED;
				else
					return isSet() ? THEME_KEY_RADIO_ON_PRESSED : THEME_KEY_RADIO_OFF_PRESSED;
			}
			else
			{
				if (isFocused())
					return isSet() ? THEME_KEY_RADIO_ON_FOCUSED : THEME_KEY_RADIO_OFF_FOCUSED;
				else
					return isSet() ? THEME_KEY_RADIO_ON : THEME_KEY_RADIO_OFF;
			}
		}
	}

}
