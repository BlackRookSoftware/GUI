/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

/**
 * Layout class for automatically resizing child components of GUI Objects.
 * @author Matthew Tropiano
 */
@FunctionalInterface
public interface GUILayout
{
	/**
	 * Called when a child object needs resizing.
	 * <b>DO NOT RESIZE THE PARENT OBJECT IN THIS METHOD, AS IT MAY CAUSE AN INFINITE LOOP TO OCCUR.</b>
	 * @param object the object to resize.
	 * @param index the child index while traversing through the list of children.
	 * @param childTotal the total number of children.
	 */
	public void resizeChild(GUIObject object, int index, int childTotal);
	
}
