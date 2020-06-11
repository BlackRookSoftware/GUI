/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;
/**
 * Interface for fields that contain a value of some kind.
 * Contains methods for getting and setting that value.
 * @author Matthew Tropiano
 *
 * @param <T> an object type.
 */
public interface GUIValueField<T>
{
	/** Event type for a value/slider changing. */
	public static final String EVENT_VALUE_CHANGE = "VALUE_CHANGE";

	/** @return this field's current value. */
	T getValue();
	
	/** 
	 * Sets this field's value.
	 * This must accept any object, and attempt to set the correct value based on it.
	 * This object must fire an {@link #EVENT_VALUE_CHANGE} event if it changes from its current value.
	 * @param value the value to set.
	 * @return itself, to chain calls.
	 */
	GUIValueField<T> setValue(Object value);
	
}
