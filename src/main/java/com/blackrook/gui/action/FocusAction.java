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
 * An action that just requests focus on the object that it happened on.
 * @author Matthew Tropiano
 */
public class FocusAction implements GUIAction
{
	/** A FocusAction instance. */
	private static final FocusAction INSTANCE = new FocusAction();
	
	private FocusAction() {}
	
	/**
	 * @return the only instance of this action.
	 */
	public static FocusAction get()
	{
		return INSTANCE;
	}
	
	@Override
	public void call(GUIEvent event)
	{
		event.getObject().requestFocus();
	}

}
