/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.action;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;

/**
 * An action that just moves this object to the front of its siblings.
 * @author Matthew Tropiano
 */
public class MoveToFrontAction implements GUIAction
{
	/** A MoveToFrontAction instance. */
	private static final MoveToFrontAction INSTANCE = new MoveToFrontAction();
	
	private MoveToFrontAction() {}
	
	/**
	 * @return the only instance of this action.
	 */
	public static MoveToFrontAction get()
	{
		return INSTANCE;
	}
	
	@Override
	public void call(GUIEvent event)
	{
		event.getObject().moveToFront();
	}

}
