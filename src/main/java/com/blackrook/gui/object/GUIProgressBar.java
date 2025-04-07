/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIEasingType;
import com.blackrook.gui.GUIAnimation;
import com.blackrook.gui.GUIBounds;
import com.blackrook.gui.GUILayout;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.model.RangeModel;

/**
 * Implementation of a progress bar.
 * <p>
 * How much the bar is "filled" is according to the attached model
 * and the current value on the bar. All child components on this bar are
 * inert and do not respond to input, nor can be focused.
 * <p>
 * A transition type can be set on the bar, for animating the bar as it
 * changes.
 * <p>
 * NOTE: The "thumb" object on the bar is NOT VISIBLE (<code>setVisible(false)</code>) by default!
 * <p>
 * This object and its descendants return specific theme keys depending on the current state of the bar.
 * <p>
 * <b>Thumb:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_THUMB_HORIZONTAL}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal Inverse</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_THUMB_HORIZONTAL_INVERSE}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_THUMB_VERTICAL}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical Inverse</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_THUMB_VERTICAL_INVERSE}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Full Bar:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_FULL_HORIZONTAL}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal Inverse</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_FULL_HORIZONTAL_INVERSE}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_FULL_VERTICAL}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical Inverse</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_FULL_VERTICAL_INVERSE}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Empty Bar:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_EMPTY_HORIZONTAL}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal Inverse</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_EMPTY_HORIZONTAL_INVERSE}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_EMPTY_VERTICAL}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical Inverse</td>
 * 		<td>{@link GUIProgressBar#THEME_KEY_PROGRESS_EMPTY_VERTICAL_INVERSE}</td>
 * </tr>
 * </table>
 * @author Matthew Tropiano
 * @param <T>
 */
public class GUIProgressBar<T extends Object> extends GUIGlassPanel implements GUIValueField<T>
{
	/** Theme key for thumb, horizontal. */
	public static final String THEME_KEY_PROGRESS_THUMB_HORIZONTAL = "progress_thumb_horizontal";
	/** Theme key for full bar, horizontal. */
	public static final String THEME_KEY_PROGRESS_FULL_HORIZONTAL = "progress_full_horizontal";
	/** Theme key for empty bar, horizontal. */
	public static final String THEME_KEY_PROGRESS_EMPTY_HORIZONTAL = "progress_empty_horizontal";
	/** Theme key for thumb, horizontal inverse. */
	public static final String THEME_KEY_PROGRESS_THUMB_HORIZONTAL_INVERSE = "progress_thumb_horizontal_inverse";
	/** Theme key for full bar, horizontal inverse. */
	public static final String THEME_KEY_PROGRESS_FULL_HORIZONTAL_INVERSE = "progress_full_horizontal_inverse";
	/** Theme key for empty bar, horizontal inverse. */
	public static final String THEME_KEY_PROGRESS_EMPTY_HORIZONTAL_INVERSE = "progress_empty_horizontal_inverse";
	/** Theme key for thumb, vertical. */
	public static final String THEME_KEY_PROGRESS_THUMB_VERTICAL = "progress_thumb_vertical";
	/** Theme key for full bar, vertical. */
	public static final String THEME_KEY_PROGRESS_FULL_VERTICAL = "progress_full_vertical";
	/** Theme key for empty bar, vertical. */
	public static final String THEME_KEY_PROGRESS_EMPTY_VERTICAL = "progress_empty_vertical";
	/** Theme key for thumb, vertical inverse. */
	public static final String THEME_KEY_PROGRESS_THUMB_VERTICAL_INVERSE = "progress_thumb_vertical_inverse";
	/** Theme key for full bar, vertical inverse. */
	public static final String THEME_KEY_PROGRESS_FULL_VERTICAL_INVERSE = "progress_full_vertical_inverse";
	/** Theme key for empty bar, vertical inverse. */
	public static final String THEME_KEY_PROGRESS_EMPTY_VERTICAL_INVERSE = "progress_empty_vertical_inverse";

	/** The name given to the thumb between the "full" and "empty" bars. */
	public static final String THUMB_NAME = "thumb";
	/** The name given to the full bar. */
	public static final String FULL_NAME = "full";
	/** The name given to the empty bar. */
	public static final String EMPTY_NAME = "empty";

	/** Bar style enumeration. */
	public static enum Style
	{
		/** Bar fills left to right. */
		HORIZONTAL,
		/** Bar fills right to left. */
		HORIZONTAL_INVERSE,
		/** Bar fills bottom to top. */
		VERTICAL,
		/** Bar fills top to bottom. */
		VERTICAL_INVERSE;
	}
	
	/**
	 * Layout attributes for slider components.
	 */
	private static enum LayoutAttrib
	{
		/** Bar thumb. */
		THUMB,
		/** Full bar. */
		FULL,
		/** Empty bar. */
		EMPTY;
	}
	
	/** Reference to itself. */
	private GUIProgressBar<?> thisRef;

	/** Bar style. */
	protected Style style;
	
	/** Transition type. */
	protected GUIEasingType transitionType;
	/** Transition time (milliseconds). */
	protected long transitionTime;
	/** Rectangle to use for bounds in transitions. */
	private GUIBounds transRectangle;

	/** Bar model. */
	protected RangeModel<T> model;
	/** Internal object for the thumb. */
	protected GUIObject thumbObject; 
	/** Internal object for the full bar. */
	protected GUIObject fullBarObject; 
	/** Internal object for the empty bar. */
	protected GUIObject emptyBarObject; 

	/** Current value. */
	protected T currentValue;


	/**
	 * Creates a new progress bar using the provided model and style.
	 * @param style the desired style.
	 * @param model the value model.
	 */
	public GUIProgressBar(RangeModel<T> model, Style style)
	{
		thisRef = this;
		this.style = style;
		this.model = model;
		this.currentValue = model.getValueForScalar(0.0);
		this.transitionTime = 500L;
		this.transitionType = null;
		this.transRectangle = new GUIBounds();
		
		super.setLayout(new ProgressBarLayout());
		
		thumbObject = new Thumb();
		fullBarObject = new FullBar();
		emptyBarObject = new EmptyBar();
		
		addChild(fullBarObject, LayoutAttrib.FULL);
		addChild(emptyBarObject, LayoutAttrib.EMPTY);
		addChild(thumbObject, LayoutAttrib.THUMB);
	}

	/**
	 * This uses a special layout; to preserve it, this throws UnsupportedOperationException.
	 * @throws UnsupportedOperationException if called.
	 */
	@Override
	public GUIObject setLayout(GUILayout layout)
	{
		throw new UnsupportedOperationException("You cannot change the layout.");
	}
	
	/**
	 * @return the current transition type.
	 */
	public GUIEasingType getTransitionType()
	{
		return transitionType;
	}

	/**
	 * Sets the current transition type.
	 * This is the transition used when a value changes.
	 * @param transitionType the transition type.
	 */
	public void setTransitionType(GUIEasingType transitionType)
	{
		this.transitionType = transitionType;
	}

	/**
	 * @return the current transition time in time units.
	 */
	public long getTransitionTime()
	{
		return transitionTime;
	}

	/**
	 * Sets the current transition time in time units.
	 * @param transitionTime the transition time.
	 */
	public void setTransitionTime(long transitionTime)
	{
		this.transitionTime = transitionTime;
	}

	/**
	 * @return a reference to the thumb object.
	 */
	public GUIObject getThumbObject()
	{
		return thumbObject;
	}

	/**
	 * @return a reference to the full bar object.
	 */
	public GUIObject getFullBarObject()
	{
		return fullBarObject;
	}

	/**
	 * @return a reference to the empty bar object.
	 */
	public GUIObject getEmptyBarObject()
	{
		return emptyBarObject;
	}

	/** 
	 * @return the current slider style. 
	 */
	public Style getStyle()
	{
		return style;
	}


	@Override
	public T getValue()
	{
		return currentValue;
	}

	@Override
	@SuppressWarnings("unchecked")
	public GUIValueField<T> setValue(Object value)
	{
		T v = (T)value;
		if (!v.equals(currentValue))
		{
			currentValue = v;
			fireEvent(EVENT_VALUE_CHANGE);
			if (transitionType != null && transitionTime > 0f)
				performTransition();
			else
				resizeChildren();
		}
		return this;
	}

	/**
	 * Perform the transition to the next value.
	 */
	private void performTransition()
	{
		getFullBounds(transRectangle);
		fullBarObject.animateAbort();
		fullBarObject.animate(
			transitionTime, 
			transitionType, 
			GUIAnimation.bounds(
				transRectangle.x, transRectangle.y, 
				transRectangle.width, transRectangle.height
			)
		);
		getEmptyBounds(transRectangle);
		emptyBarObject.animateAbort();
		emptyBarObject.animate(
			transitionTime, 
			transitionType, 
			GUIAnimation.bounds(
				transRectangle.x, transRectangle.y, 
				transRectangle.width, transRectangle.height
			)
		);
		getThumbBounds(transRectangle);
		thumbObject.animateAbort();
		thumbObject.animate( 
			transitionTime, 
			transitionType, 
			GUIAnimation.bounds(
				transRectangle.x, transRectangle.y, 
				transRectangle.width, transRectangle.height
			)
		);
	}
	
	/**
	 * Gets what the "full" bar's bounds should be according to
	 * the current value and model.
	 */
	private void getFullBounds(GUIBounds bounds)
	{
		GUIBounds parentBounds = getBounds();
		float scalar = (float)model.getScalarForValue(currentValue);
		
		switch (style)
		{
			default:
			case HORIZONTAL:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width * scalar;
				bounds.height = parentBounds.height;
			}
			break;
			
			case HORIZONTAL_INVERSE:
			{
				bounds.x = parentBounds.width - (parentBounds.width * scalar);
				bounds.y = 0f;
				bounds.width = parentBounds.width * scalar;
				bounds.height = parentBounds.height;
			}
			break;
			
			case VERTICAL:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height - (parentBounds.height * scalar);
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * scalar;
			}
			break;
			
			case VERTICAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * scalar;
			}
			break;
		}
	}
	
	/**
	 * Gets what the "empty" bar's bounds should be according to
	 * the current value and model.
	 */
	private void getEmptyBounds(GUIBounds bounds)
	{
		GUIBounds parentBounds = getBounds();
		float scalar = (float)model.getScalarForValue(currentValue);
		float antiscalar = 1f - scalar;
		
		switch (style)
		{
			case HORIZONTAL:
			{
				bounds.x = parentBounds.width * scalar;
				bounds.y = 0f;
				bounds.width = parentBounds.width * antiscalar;
				bounds.height = parentBounds.height;
			}
			break;
			
			case HORIZONTAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width * antiscalar;
				bounds.height = parentBounds.height;
			}
			break;
			
			case VERTICAL:
			{
				bounds.x = 0f;
				bounds.y = 0f;
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * antiscalar;
			}
			break;
			
			case VERTICAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height * scalar;
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.height * antiscalar;
			}
			break;
		}
	}
	
	/**
	 * Gets what the thumb's bounds should be according to
	 * the current value and model.
	 */
	private void getThumbBounds(GUIBounds bounds)
	{
		GUIBounds parentBounds = getBounds();
		float scalar = (float)model.getScalarForValue(currentValue);
		
		switch (style)
		{
			case HORIZONTAL:
			{
				bounds.x = parentBounds.width * scalar - (parentBounds.height / 2f);
				bounds.y = 0f;
				bounds.width = parentBounds.height;
				bounds.height = parentBounds.height;
			}
			break;
			
			case HORIZONTAL_INVERSE:
			{
				bounds.x = parentBounds.width - (parentBounds.width * scalar + (parentBounds.height / 2f));
				bounds.y = 0f;
				bounds.width = parentBounds.height;
				bounds.height = parentBounds.height;
			}
			break;
			
			case VERTICAL:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height - (parentBounds.height * scalar + (parentBounds.width / 2f));
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.width;
			}
			break;
			
			case VERTICAL_INVERSE:
			{
				bounds.x = 0f;
				bounds.y = parentBounds.height * scalar - (parentBounds.width / 2f);
				bounds.width = parentBounds.width;
				bounds.height = parentBounds.width;
			}
			break;
		}
	}
	
	/**
	 * Progress bar thumb object. 
	 */
	private class Thumb extends GUIObject
	{
		Thumb()
		{
			setInert(true);
			setVisible(false);
			addName(THUMB_NAME);
		}
		
		@Override
		public String getThemeKey()
		{
			switch (thisRef.style)
			{
				default:
				case HORIZONTAL:
					return THEME_KEY_PROGRESS_THUMB_HORIZONTAL;
				case HORIZONTAL_INVERSE:
					return THEME_KEY_PROGRESS_THUMB_HORIZONTAL_INVERSE;
				case VERTICAL:
					return THEME_KEY_PROGRESS_THUMB_VERTICAL;
				case VERTICAL_INVERSE:
					return THEME_KEY_PROGRESS_THUMB_VERTICAL_INVERSE;
			}
		}
	}

	/**
	 * Progress bar full bar object. 
	 */
	private class FullBar extends GUIObject
	{
		FullBar()
		{
			setInert(true);
			setScaleType(ScaleType.ASPECT);
			addName(FULL_NAME);
		}
		
		@Override
		public String getThemeKey()
		{
			switch (thisRef.style)
			{
				default:
				case HORIZONTAL:
					return THEME_KEY_PROGRESS_FULL_HORIZONTAL;
				case HORIZONTAL_INVERSE:
					return THEME_KEY_PROGRESS_FULL_HORIZONTAL_INVERSE;
				case VERTICAL:
					return THEME_KEY_PROGRESS_FULL_VERTICAL;
				case VERTICAL_INVERSE:
					return THEME_KEY_PROGRESS_FULL_VERTICAL_INVERSE;
			}
		}
	}

	/**
	 * Progress bar empty bar object. 
	 */
	private class EmptyBar extends GUIObject
	{
		EmptyBar()
		{
			setInert(true);
			setScaleType(ScaleType.ASPECT);
			addName(EMPTY_NAME);
		}
		
		@Override
		public String getThemeKey()
		{
			switch (thisRef.style)
			{
				default:
				case HORIZONTAL:
					return THEME_KEY_PROGRESS_EMPTY_HORIZONTAL;
				case HORIZONTAL_INVERSE:
					return THEME_KEY_PROGRESS_EMPTY_HORIZONTAL_INVERSE;
				case VERTICAL:
					return THEME_KEY_PROGRESS_EMPTY_VERTICAL;
				case VERTICAL_INVERSE:
					return THEME_KEY_PROGRESS_EMPTY_VERTICAL_INVERSE;
			}
		}
	}

	/**
	 * Progress bar layout.
	 */
	private class ProgressBarLayout implements GUILayout
	{
		private GUIBounds temp = new GUIBounds();
		
		@Override
		public void resizeChild(GUIObject object, int index, int childTotal)
		{
			object.animateFinish();
			switch ((LayoutAttrib)object.getLayoutAttrib())
			{
				case THUMB:
					getThumbBounds(temp);
					object.setBounds(temp);
					break;
					
				case FULL:
					getFullBounds(temp);
					object.setBounds(temp);
					break;
					
				case EMPTY:
					getEmptyBounds(temp);
					object.setBounds(temp);
					break;
			}
		}
		
	}
	
}
