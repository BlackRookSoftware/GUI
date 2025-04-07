/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.model;

/**
 * A data model for use with objects that increment or decrement a standing value.
 * @author Matthew Tropiano
 * @param <T> the object that this model returns.
 */
public interface SpinnerModel<T extends Object>
{
	/**
	 * @return the currently selected value.
	 */
	T getValue();
	
	/**
	 * Sets the currently selected value.
	 * @param value the new value for this model.
	 */
	void setValue(T value);

	/** 
	 * @return the next value after the value that would be returned by {@link #getValue()}.
	 */
	T getNext();
	
	/** 
	 * @return the previous value using what would be returned by #getValue(). 
	 */
	T getPrevious();
	
}
