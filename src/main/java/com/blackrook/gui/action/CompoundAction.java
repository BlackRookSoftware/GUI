/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.action;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;

/**
 * An action encapsulates and calls other actions.
 * Actions can be added to or removed from this one at will.
 * @author Matthew Tropiano
 */
public class CompoundAction implements GUIAction
{
	/** Action list. */
	private GUIAction[] actionList;
	
	/**
	 * Creates a new compound action with no actions attached.
	 */
	private CompoundAction(GUIAction ... actions)
	{
		this.actionList = new GUIAction[actions.length];
		System.arraycopy(actions, 0, actionList, 0, actions.length);
	}
	
	/**
	 * Creates a new compound action from a set of actions. 
	 * @param actions the actions to add.
	 * @return the created action.
	 */
	public static CompoundAction create(GUIAction ... actions)
	{
		return new CompoundAction(actions);
	}
	
	@Override
	public final void call(GUIEvent event)
	{
		for (GUIAction action : actionList)
			action.call(event);
	}

}
