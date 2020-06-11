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
 * A toggle panel that has its own theme keys depending on its state, checkbox style.
 * <p>
 * <table>
 * <tr>
 * 		<td>Checked, Enabled</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_ON}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Enabled</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_OFF}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Focused</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_ON_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Focused</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_OFF_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Disabled</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_ON_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Disabled</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_OFF_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Enabled, Pressed</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_ON_PRESSED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Enabled, Pressed</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_OFF_PRESSED}</td>
 * </tr>
 * <tr>
 * 		<td>Checked, Focused, Pressed</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_ON_FOCUSED_PRESSED}</td>
 * </tr>
 * <tr>
 * 		<td>Unchecked, Focused, Pressed</td>
 * 		<td>{@link GUICheckbox#THEME_KEY_CHECKBOX_OFF_FOCUSED_PRESSED}</td>
 * </tr>
 * </table>
 * @author Matthew Tropiano
 */
public class GUICheckbox extends GUITogglePanel
{
	/** Theme key for a checked checkbox. */
	public static final String THEME_KEY_CHECKBOX_ON = "checkbox_on";
	/** Theme key for an unchecked checkbox. */
	public static final String THEME_KEY_CHECKBOX_OFF = "checkbox_off";
	/** Theme key for a checked, focused checkbox. */
	public static final String THEME_KEY_CHECKBOX_ON_FOCUSED = "checkbox_on_focused";
	/** Theme key for an unchecked, focused checkbox. */
	public static final String THEME_KEY_CHECKBOX_OFF_FOCUSED = "checkbox_off_focused";
	/** Theme key for a checked, disabled checkbox. */
	public static final String THEME_KEY_CHECKBOX_ON_DISABLED = "checkbox_on_disabled";
	/** Theme key for an unchecked, disabled checkbox. */
	public static final String THEME_KEY_CHECKBOX_OFF_DISABLED = "checkbox_off_disabled";
	/** Theme key for a checked checkbox, during press. */
	public static final String THEME_KEY_CHECKBOX_ON_PRESSED = "checkbox_on_pressed";
	/** Theme key for an unchecked checkbox, during press. */
	public static final String THEME_KEY_CHECKBOX_OFF_PRESSED = "checkbox_off_pressed";
	/** Theme key for a checked, focused checkbox, during press. */
	public static final String THEME_KEY_CHECKBOX_ON_FOCUSED_PRESSED = "checkbox_on_focused_pressed";
	/** Theme key for an unchecked, focused checkbox, during press. */
	public static final String THEME_KEY_CHECKBOX_OFF_FOCUSED_PRESSED = "checkbox_off_focused_pressed";

	/** Click action for the checkbox - calls {@link #toggle()} */
	protected static final GUIAction CLICK_ACTION = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			GUICheckbox tp = (GUICheckbox)event.getObject();
			tp.toggle();
		};
	};
		
	protected static final GUIAction KEY_TYPE_ACTION = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			GUICheckbox tp = (GUICheckbox)event.getObject();
			int keyCode = event.getKeyCode();
			if (keyCode == GUIInputConstants.KEY_ENTER)
				tp.toggle();
			else if (keyCode == GUIInputConstants.KEY_SPACE)
				tp.toggle();
		};
	};
	
	/**
	 * Creates a new checkbox in the unset, false state.
	 */
	public GUICheckbox()
	{
		this(false);
	}
	
	/**
	 * Creates a new checkbox.
	 * @param startState the starting state (true or false).
	 */
	public GUICheckbox(boolean startState)
	{
		super(startState);
		bindAction(CLICK_ACTION, EVENT_MOUSE_CLICK);
		bindAction(KEY_TYPE_ACTION, EVENT_KEY_TYPE);
	}
	
	@Override
	public String getThemeKey()
	{
		if (!isEnabled())
			return isSet() ? THEME_KEY_CHECKBOX_ON_DISABLED : THEME_KEY_CHECKBOX_OFF_DISABLED;
		else
		{
			if (isBeingClicked())
			{
				if (isFocused())
					return isSet() ? THEME_KEY_CHECKBOX_ON_FOCUSED_PRESSED : THEME_KEY_CHECKBOX_OFF_FOCUSED_PRESSED;
				else
					return isSet() ? THEME_KEY_CHECKBOX_ON_PRESSED : THEME_KEY_CHECKBOX_OFF_PRESSED;
			}
			else
			{
				if (isFocused())
					return isSet() ? THEME_KEY_CHECKBOX_ON_FOCUSED : THEME_KEY_CHECKBOX_OFF_FOCUSED;
				else
					return isSet() ? THEME_KEY_CHECKBOX_ON : THEME_KEY_CHECKBOX_OFF;
			}
		}
	}

}
