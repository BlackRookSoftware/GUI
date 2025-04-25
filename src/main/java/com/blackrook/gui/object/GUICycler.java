/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIInputConstants;
import com.blackrook.gui.model.IndexedModel;

/**
 * Option cycler abstract.
 * A cycler changes its value each click.
 * An {@link #EVENT_VALUE_CHANGE} event is fired each value change.
 * @param <T> the object type in the cycler.
 */
public class GUICycler<T> extends GUILabel implements GUIValueField<T>, GUISelectable<T>
{
	/** The model to use. */
	protected IndexedModel<T> model;
	/** The current choice index. */
	protected int currentIndex;
	
	protected static final GUIAction BASIC_ACTION = (event) ->
	{
		GUICycler<?> obj = (GUICycler<?>)event.getObject();
		
		if (event.isMouseEvent())
		{
			if (event.getMouseButton() == GUIInputConstants.MOUSE_LEFT)
				obj.advanceSelection();
		}
		else if (event.isKeyboardEvent())
		{
			int keyCode = event.getKeyCode();
			if (keyCode == GUIInputConstants.KEY_RIGHT)
				obj.advanceSelection();
			else if (keyCode == GUIInputConstants.KEY_LEFT)
				obj.reverseSelection();
		}
		else if (event.isGamepadEvent())
		{
			if (event.getGamepadButton() == GUIInputConstants.GAMEPAD_1)
				obj.advanceSelection();
			else if (event.getGamepadButton() == GUIInputConstants.GAMEPAD_2)
				obj.reverseSelection();
		}
		else if (event.isGamepadAxisTapEvent())
		{
			if (event.getGamepadAxisId() == GUIInputConstants.AXIS_X) // also = XBOX LEFT STICK X
			{
				if (event.getGamepadAxisTapValue())
					obj.advanceSelection();
				else
					obj.reverseSelection();
			}
		}
	};
		
	/**
	 * Creates a new cycler with a set of items to cycle through.
	 * @param model the model that this cycler uses for values.
	 */
	public GUICycler(IndexedModel<T> model)
	{
		super(null, "", Justification.CENTER);
		this.model = model;
		this.currentIndex = NO_SELECTION;

		bindAction(BASIC_ACTION, EVENT_KEY_PRESS, EVENT_MOUSE_CLICK, EVENT_GAMEPAD_PRESS, EVENT_GAMEPAD_TAP_PRESS);
		setSelectedIndex(0);
	}

	@Override
	public GUICycler<T> setSelectedIndex(int index)
	{
		index = index < 0 || index >= model.size() ? NO_SELECTION : index;
		
		if (currentIndex != index)
		{
			currentIndex = index;
			if (currentIndex != NO_SELECTION)
				setText(String.valueOf(model.getValueByIndex(currentIndex)));
			else
				setText("");
			fireEvent(EVENT_VALUE_CHANGE);
		}
		return this;
	}

	@Override
	public int getSelectedIndex()
	{
		return currentIndex;
	}

	@Override
	public T getSelectedValue()
	{
		if (currentIndex == NO_SELECTION)
			return null;
		return model.getValueByIndex(currentIndex);
	}

	@Override
	public T getValue()
	{
		return getSelectedValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public GUICycler<T> setValue(Object value)
	{
		setSelectedIndex(model.getIndexByValue((T)value));
		return this;
	}
	
	/**
	 * Advances the cycler's selected index.
	 */
	protected void advanceSelection()
	{
		setSelectedIndex((currentIndex + 1) % model.size());
	}

	/**
	 * Reverses the cycler's selected index.
	 */
	protected void reverseSelection()
	{
		setSelectedIndex(currentIndex > 0 ? (currentIndex - 1) : model.size() - 1);
	}
	
}

