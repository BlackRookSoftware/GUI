/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.model.range;

import com.blackrook.gui.model.RangeModel;
import com.blackrook.gui.struct.MathUtils;

/**
 * An implementation of {@link RangeModel} that returns an integer range.
 * @author Matthew Tropiano
 */
public class IntRangeModel implements RangeModel<Integer>
{
	/** Min value. */
	private int min;
	/** Max value. */
	private int max;
	
	/**
	 * Creates a new model with the provided minimum and maximum values.
	 * @param min the minimum value.
	 * @param max the maximum value.
	 */
	public IntRangeModel(int min, int max)
	{
		this.min = min;
		this.max = max;
	}

	@Override
	public Integer getMinValue()
	{
		return min;
	}

	@Override
	public Integer getMaxValue()
	{
		return max;
	}

	@Override
	public Integer getValueForScalar(double scalar)
	{
		return (int)MathUtils.linearInterpolate(scalar, min, max);
	}

	@Override
	public double getScalarForValue(Integer value)
	{
		return MathUtils.getInterpolationFactor(value, min, max);
	}

	@Override
	public boolean isInRange(Integer value)
	{
		return value != null && value >= min && value <= max;
	}

}
