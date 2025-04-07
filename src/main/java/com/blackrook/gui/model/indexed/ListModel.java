/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.model.indexed;

import java.util.ArrayList;
import java.util.List;

import com.blackrook.gui.model.IndexedModel;

/**
 * A model that uses a list backing.
 * @author Matthew Tropiano
 * @param <T> the object type to use.
 */
public class ListModel<T> implements IndexedModel<T>
{
	/** Underlying data set for model. */
	private List<T> dataSet;
	
	/**
	 * Creates a new list model using a set of objects.
	 * @param objects the objects to put in the list.
	 */
	@SafeVarargs
	public ListModel(T ... objects)
	{
		dataSet = new ArrayList<T>(objects.length);
		for (T t : objects)
			dataSet.add(t);
	}
	
	/**
	 * Creates a new list model using an iterable set of objects.
	 * @param iterable the iterable list of objects to put in the list.
	 */
	public ListModel(Iterable<T> iterable)
	{
		dataSet = new ArrayList<T>();
		for (T t : iterable)
			dataSet.add(t);
	}
	
	@Override
	public T getValueByIndex(int index)
	{
		return dataSet.get(index);
	}

	@Override
	public int getIndexByValue(T value)
	{
		return dataSet.indexOf(value);
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}
	
	@Override
	public int size()
	{
		return dataSet.size();
	}

}
