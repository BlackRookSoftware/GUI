/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.struct.ValueUtils;

/**
 * A checkbox object that holds a boolean switch.
 * <p>
 * This object is very much undecorated. You will have to apply colors and stuff to it.
 * Listening to its state will be useful for setting its colors and look.
 * @author Matthew Tropiano
 */
public abstract class GUITogglePanel extends GUIObject implements GUIToggleable
{
	/** Press action. */
	protected static final GUIAction PRESS_ACTION = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			GUITogglePanel tp = (GUITogglePanel)event.getObject();
			tp.requestFocus();
			tp.beingClicked = true;
		};
	};

	/** Release action. */
	protected static final GUIAction RELEASE_ACTION = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			GUITogglePanel tp = (GUITogglePanel)event.getObject();
			tp.beingClicked = false;
		};
	};

	/** If true, this is in the middle of being changed. */
	private boolean beingClicked;
	/** Checked state. */
	private boolean state;
	
	/**
	 * Creates a new toggle panel in the unset, false state.
	 */
	public GUITogglePanel()
	{
		this(false);
		bindAction(PRESS_ACTION, EVENT_MOUSE_PRESS, EVENT_KEY_PRESS);
		bindAction(RELEASE_ACTION, EVENT_MOUSE_RELEASE, EVENT_KEY_RELEASE);
	}
	
	/**
	 * Creates a new toggle panel.
	 * @param startState the starting state (true or false).
	 */
	public GUITogglePanel(boolean startState)
	{
		super();
		setState(startState);
	}
	
	@Override
	public boolean isSet()
	{
		return state;
	}

	@Override
	public GUIToggleable toggle()
	{
		setState(!state);
		return this;
	}

	@Override
	public GUIToggleable setState(boolean state)
	{
		if (this.state != state)
		{
			this.state = state;
			fireEvent(EVENT_VALUE_CHANGE);
		}
		return this;
	}
	
	@Override
	public Boolean getValue()
	{
		return state;
	}

	@Override
	public GUIToggleable setValue(Object value)
	{
		if (value instanceof Boolean)
			setState((Boolean)value);
		else if (value instanceof Number)
			setState(((Number)value).doubleValue() != 0.0);
		else
			setState(ValueUtils.parseBoolean(String.valueOf(value), false));
		return this;
	}

	/**
	 * @return true if this object is in the middle of being clicked.
	 */
	public boolean isBeingClicked()
	{
		return beingClicked;
	}
	
}
