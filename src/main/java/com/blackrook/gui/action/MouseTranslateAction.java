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
 * An action that translates the object by mouse movement.
 * @author Matthew Tropiano
 */
public class MouseTranslateAction implements GUIAction
{
	/** A MouseTranslateAction instance. */
	private static final MouseTranslateAction INSTANCE = new MouseTranslateAction();
	
	private MouseTranslateAction() {}
	
	/**
	 * @return the only instance of this action.
	 */
	public static MouseTranslateAction get()
	{
		return INSTANCE;
	}
	
	@Override
	public void call(GUIEvent event)
	{
		event.getObject().translate(event.getMouseMovementX(), event.getMouseMovementY());
	}

}
