/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.group;

import java.util.Arrays;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.object.GUIToggleable;

/**
 * This is a set that describes a set of toggleable buttons that
 * are associated in a group such that when one gets set, the others are unset.
 * @author Matthew Tropiano
 */
public class ToggleGroup
{
	/** Current panel. */
	protected GUIToggleable current;
	
	/**
	 * Creates a new {@link ToggleGroup} and binds the required actions
	 * to the objects provided.
	 * @param objects the objects to include in the group.
	 */
	public ToggleGroup(GUIObject ... objects)
	{
		this(Arrays.asList(objects));
	}

	/**
	 * Creates a new {@link ToggleGroup} and binds the required actions
	 * to the objects provided.
	 * @param objects the objects to include in the group.
	 */
	public ToggleGroup(Iterable<GUIObject> objects)
	{
		GUIAction action = (event) -> toggle(event.getObject());
			
		for (GUIObject obj : objects)
			obj.bindAction(action, GUIToggleable.EVENT_VALUE_CHANGE);
	}
	
	/**
	 * Performs toggle.
	 */
	private void toggle(GUIObject object)
	{
		if (object != null && object instanceof GUIToggleable)
		{
			GUIToggleable tog = (GUIToggleable)object;
			if (current != null && current != tog) 
				current.setState(false);
			current = tog;
		}
	}
	
}
