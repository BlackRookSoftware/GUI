/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

/**
 * An interface that describes GUI objects that contain a set of objects.
 * @author Matthew Tropiano
 * @param <T> the type of object stored.
 */
public interface GUISelectable<T>
{
	/** Index value for no selection. */
	public static final int NO_SELECTION = -1;
	
	/**
	 * Gets the selected index on the object.
	 * @return the current selected index. 
	 */
	public int getSelectedIndex();
	
	/**
	 * Sets the selected index on the object.
	 * If the index is out of bounds, it should be set to -1, meaning nothing selected. 
	 * @param index the new selected index.
	 * @return this selectable.
	 */
	public GUISelectable<T> setSelectedIndex(int index);
	
	/** 
	 * @return the current selected value, or null if no value selected.
	 */
	public T getSelectedValue();
	
}
