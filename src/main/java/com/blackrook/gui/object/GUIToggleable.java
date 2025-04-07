/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

/**
 * Describes a GUI object that can be toggled between two states.
 * @author Matthew Tropiano
 */
public interface GUIToggleable extends GUIValueField<Boolean>
{
	/** 
	 * Gets the state of this toggleable object.
	 * @return true if this is in the enabled state, false if not. 
	 */
	public boolean isSet();

	/** 
	 * Changes the state of this toggleable object to the opposite state.
	 * If true, calling this sets it to false. 
	 * If false, calling this sets it to true.
	 * @return itself, to chain calls. 
	 */
	public GUIToggleable toggle();
	
	/** 
	 * Changes the state of this toggleable object to the specified state.
	 * Must fire an {@link GUIValueField#EVENT_VALUE_CHANGE} if the
	 * state of this object changes.
	 * @param state the new state.
	 * @return itself, to chain calls. 
	 */
	public GUIToggleable setState(boolean state);
}
