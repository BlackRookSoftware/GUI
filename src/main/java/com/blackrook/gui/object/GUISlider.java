/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIBounds;
import com.blackrook.gui.GUILayout;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.GUIInputConstants;
import com.blackrook.gui.action.FocusAction;
import com.blackrook.gui.model.RangeModel;
import com.blackrook.gui.struct.MathUtils;

/**
 * A slider class that alters an internal value depending on where
 * the slider sits on the slider bar.
 * <p>
 * This object already contains its own layout and a child object,
 * its movement constrained to that of its parent, that acts as the slider thumb.
 * <p>
 * This object fires an EVENT_VALUE_CHANGE event when the slider changes.
 * <p>
 * This object and its descendants return specific theme keys depending on the current state of the slider.
 * <p>
 * <b>Thumb:</b>
 * <table>
 * <tr>
 * 		<td>Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_THUMB}</td>
 * </tr>
 * <tr>
 * 		<td>Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_THUMB_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_THUMB_DISABLED}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Track:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal, Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal, Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal, Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_DISABLED}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Track Start:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal, Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_START}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal, Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_START_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal, Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_START_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_START}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_START_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_START_DISABLED}</td>
 * </tr>
 * </table>
 * <p>
 * <b>Track End:</b>
 * <table>
 * <tr>
 * 		<td>Horizontal, Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_END}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal, Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_END_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Horizontal, Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_HORIZONTAL_TRACK_END_DISABLED}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Enabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_END}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Focused</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_END_FOCUSED}</td>
 * </tr>
 * <tr>
 * 		<td>Vertical, Disabled</td>
 * 		<td>{@link GUISlider#THEME_KEY_SLIDER_VERTICAL_TRACK_END_DISABLED}</td>
 * </tr>
 * </table>
 * @author Matthew Tropiano
 * @param <T> the value type.
 */
public class GUISlider<T> extends GUIGlassPanel implements GUIValueField<T>
{
	/** Theme key for slider thumb. */
	public static final String THEME_KEY_SLIDER_THUMB = "slider_thumb";
	/** Theme key for slider thumb, disabled. */
	public static final String THEME_KEY_SLIDER_THUMB_DISABLED = "slider_thumb_disabled";
	/** Theme key for slider thumb, focused. */
	public static final String THEME_KEY_SLIDER_THUMB_FOCUSED = "slider_thumb_focused";
	/** Theme key for horizontal slider track. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK = "slider_horizontal_track";
	/** Theme key for horizontal slider track start. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_START = "slider_horizontal_track_start";
	/** Theme key for horizontal slider track end. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_END = "slider_horizontal_track_end";
	/** Theme key for horizontal slider track, disabled. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_DISABLED = "slider_horizontal_track_disabled";
	/** Theme key for horizontal slider track start, disabled. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_START_DISABLED = "slider_horizontal_track_start_disabled";
	/** Theme key for horizontal slider track end, disabled. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_END_DISABLED = "slider_horizontal_track_end_disabled";
	/** Theme key for horizontal slider track, focused. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_FOCUSED = "slider_horizontal_track_focused";
	/** Theme key for horizontal slider track start, focused. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_START_FOCUSED = "slider_horizontal_track_start_focused";
	/** Theme key for horizontal slider track end, focused. */
	public static final String THEME_KEY_SLIDER_HORIZONTAL_TRACK_END_FOCUSED = "slider_horizontal_track_end_focused";
	/** Theme key for vertical slider track. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK = "slider_vertical_track";
	/** Theme key for vertical slider track start. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_START = "slider_vertical_track_start";
	/** Theme key for vertical slider track end. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_END = "slider_vertical_track_end";
	/** Theme key for vertical slider track, disabled. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_DISABLED = "slider_vertical_track_disabled";
	/** Theme key for vertical slider track start, disabled. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_START_DISABLED = "slider_vertical_track_start_disabled";
	/** Theme key for vertical slider track end, disabled. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_END_DISABLED = "slider_vertical_track_end_disabled";
	/** Theme key for vertical slider track, focused. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_FOCUSED = "slider_vertical_track_focused";
	/** Theme key for vertical slider track start, focused. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_START_FOCUSED = "slider_vertical_track_start_focused";
	/** Theme key for vertical slider track end, focused. */
	public static final String THEME_KEY_SLIDER_VERTICAL_TRACK_END_FOCUSED = "slider_vertical_track_end_focused";
	
	/** The name given to the thumb object. */
	public static final String THUMB_NAME = "thumb";
	/** The name given to the track object. */
	public static final String TRACK_NAME = "track";
	/** The name given to the track start object. */
	public static final String TRACK_START_NAME = "track-start";
	/** The name given to the track end object. */
	public static final String TRACK_END_NAME = "track-end";
	
	/** Slider style enumeration. */
	public static enum Style
	{
		/** Slider moves left to right. */
		HORIZONTAL,
		/** Slider moves top to bottom. */
		VERTICAL
	}
	
	/**
	 * Layout attributes for slider components.
	 */
	private static enum LayoutAttrib
	{
		/** Slider thumb. */
		THUMB,
		/** Slider thumb track. */
		TRACK,
		/** Slider thumb track start. */
		TRACK_START,
		/** Slider thumb track end. */
		TRACK_END;
	}
	
	/** Key press event on the thumb. */
	protected static final GUIAction THUMB_PRESS = (event) ->
	{
		GUIObject thumb = event.getObject();
		GUISlider<?> slider = (GUISlider<?>)thumb.getParent();
		GUIBounds bounds = thumb.getBounds();
		
		if (event.isKeyboardEvent())
		{
			switch (event.getKeyCode())
			{
				case GUIInputConstants.KEY_LEFT:
					thumb.translate(-bounds.width/4, 0f);
					slider.setValuesByThumbBounds();
					break;
				case GUIInputConstants.KEY_RIGHT:
					thumb.translate(bounds.width/4, 0f);
					slider.setValuesByThumbBounds();
					break;
				case GUIInputConstants.KEY_UP:
					thumb.translate(0f, -bounds.height/4);
					slider.setValuesByThumbBounds();
					break;
				case GUIInputConstants.KEY_DOWN:
					thumb.translate(0f, bounds.height/4);
					slider.setValuesByThumbBounds();
					break;
			}
		}
		else if (event.isGamepadAxisEvent())
		{
			if (event.getGamepadAxisId() == GUIInputConstants.AXIS_X)
			{
				if (event.getGamepadAxisValue() > 0f)
				{
					thumb.translate(bounds.width/4, 0f);
					slider.setValuesByThumbBounds();
				}
				else
				{
					thumb.translate(-bounds.width/4, 0f);
					slider.setValuesByThumbBounds();
				}
			}
			else if (event.getGamepadAxisId() == GUIInputConstants.AXIS_Y)
			{
				if (event.getGamepadAxisValue() > 0f)
				{
					thumb.translate(0f, -bounds.height/4);
					slider.setValuesByThumbBounds();
				}
				else
				{
					thumb.translate(0f, bounds.height/4);
					slider.setValuesByThumbBounds();
				}
			}
		}
	};
		
	/** Mouse press event on the thumb. */
	protected static final GUIAction THUMB_MOUSE_PRESS = (event) ->
	{
		event.getObject().getParent().requestFocus();
	};
		
	/** Mouse drag event on the thumb. */
	protected static final GUIAction THUMB_MOUSE_DRAG = (event) ->
	{
		event.getObject().translate(event.getMouseMovementX(), event.getMouseMovementY());
		GUISlider<?> slider = (GUISlider<?>)event.getObject().getParent();
		slider.setValuesByThumbBounds();
	};
	
	/** Reference to itself. */
	private GUISlider<?> thisRef;
		
	/** Slider style. */
	protected Style style;
	/** Slider model. */
	protected RangeModel<T> model;
	/** Internal object for the thumb. */
	protected GUIObject thumbObject; 
	/** Internal object for the track. */
	protected GUIObject trackObject; 
	/** Internal object for the track start. */
	protected GUIObject trackStartObject; 
	/** Internal object for the track end. */
	protected GUIObject trackEndObject; 
	/** Thumb scalar. */
	protected float thumbScalar;
	
	/** Current value. */
	protected T currentValue;
	
	/**
	 * Creates a new GUI Slider object in horizontal style.
	 * @param model the range model to use for defining values.
	 */
	public GUISlider(RangeModel<T> model)
	{
		this(model, Style.HORIZONTAL);
	}
	
	/**
	 * Creates a new GUI Slider object.
	 * @param model the range model to use for defining values.
	 * @param style the slider's style.
	 */
	public GUISlider(RangeModel<T> model, Style style)
	{
		this(model, style, 1f);
	}
	
	/**
	 * Creates a new GUI Slider object.
	 * @param model the range model to use for defining values.
	 * @param style the slider's style.
	 * @param thumbScalar the thumb scalar (from 0 to 1).  
	 */
	public GUISlider(RangeModel<T> model, Style style, float thumbScalar)
	{
		super();
		thisRef = this;
		this.style = style; 
		this.model = model;
		this.thumbScalar = thumbScalar;
		this.currentValue = model.getValueForScalar(0.0);
		super.setLayout(new SliderLayout());
		
		thumbObject = new Thumb();
		trackObject = new Track();
		trackStartObject = new TrackStart();
		trackEndObject = new TrackEnd();

		addChild(trackObject, LayoutAttrib.TRACK);
		addChild(trackStartObject, LayoutAttrib.TRACK_START);
		addChild(trackEndObject, LayoutAttrib.TRACK_END);
		addChild(thumbObject, LayoutAttrib.THUMB);
		
		bindAction(FocusAction.get(), EVENT_MOUSE_CLICK);
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
	 * @return the current slider style. 
	 */
	public Style getStyle()
	{
		return style;
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
			resizeChildren();
		}
		return this;
	}
	
	@Override
	public T getValue()
	{
		return currentValue;
	}

	/**
	 * @return the reference to the thumb object.
	 */
	public GUIObject getThumb()
	{
		return thumbObject;
	}

	/**
	 * @return the reference to the track start object.
	 */
	public GUIObject getTrackStart()
	{
		return trackStartObject;
	}

	/**
	 * @return the reference to the track end object.
	 */
	public GUIObject getTrackEnd()
	{
		return trackEndObject;
	}

	/**
	 * @return the reference to the track object.
	 */
	public GUIObject getTrack()
	{
		return trackObject;
	}

	/** 
	 * Gets the current thumb scalar.
	 * It's a value from 0 to 1 that describes the width of the thumb relative to the width/height of the entire slider. 
	 * Affected by slider style.
	 * @return the current scalar.
	 */
	public float getThumbScalar()
	{
		return thumbScalar;
	}

	/** 
	 * Sets the current thumb scalar.
	 * It's a value from 0 to 1 that describes the width of the thumb relative to the width/height of the entire slider. 
	 * Affected by slider style.
	 * @param thumbScalar the new scalar.
	 */
	public void setThumbScalar(float thumbScalar)
	{
		this.thumbScalar = thumbScalar;
	}

	/**
	 * @return the interpolation value between 0 and 1 using the position of the slider.
	 */
	protected float getPositionScalar()
	{
		switch (style)
		{
			default:
			case HORIZONTAL:
			{
				float pw = getBounds().width;
				return (float)MathUtils.getInterpolationFactor(thumbObject.getBounds().x, 0, pw - thumbObject.getBounds().width);
			}
			case VERTICAL:
			{
				float ph = getBounds().height;
				return (float)MathUtils.getInterpolationFactor(thumbObject.getBounds().y, ph - thumbObject.getBounds().height, 0);
			}
		}
	}
	
	/**
	 * Sets the internal values by the thumb position.
	 */
	protected void setValuesByThumbBounds()
	{
		T value = model.getValueForScalar(getPositionScalar());
		if (!value.equals(currentValue))
		{
			currentValue = value;
			fireEvent(EVENT_VALUE_CHANGE);
		}
	}

	/**
	 * Slider thumb object. 
	 */
	private class Thumb extends GUIObject
	{
		Thumb()
		{
			addName(THUMB_NAME);
			setConstrainToParent(true);
			bindAction(THUMB_MOUSE_PRESS, EVENT_MOUSE_PRESS);
			bindAction(THUMB_MOUSE_DRAG, EVENT_MOUSE_DRAG);
			bindAction(THUMB_PRESS, EVENT_KEY_PRESS, EVENT_GAMEPAD_TAP_PRESS);
		}
		
		@Override
		public String getThemeKey()
		{
			if (!thisRef.isEnabled())
				return THEME_KEY_SLIDER_THUMB_DISABLED;
			else if (thisRef.isFocused() || this.isFocused())
				return THEME_KEY_SLIDER_THUMB_FOCUSED;
			else
				return THEME_KEY_SLIDER_THUMB;
		}
	}
	
	/**
	 * Slider track.
	 */
	private class Track extends GUIObject
	{
		Track()
		{
			setInert(true);
			setScaleType(ScaleType.ASPECT);
			addName(TRACK_NAME);
		}

		@Override
		public String getThemeKey()
		{
			if (thisRef.style == Style.VERTICAL)
			{
				if (!thisRef.isEnabled())
					return THEME_KEY_SLIDER_VERTICAL_TRACK_DISABLED;
				else if (thisRef.isFocused() || this.isFocused())
					return THEME_KEY_SLIDER_VERTICAL_TRACK_FOCUSED;
				else
					return THEME_KEY_SLIDER_VERTICAL_TRACK;
			}
			else
			{
				if (!thisRef.isEnabled())
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_DISABLED;
				else if (thisRef.isFocused() || this.isFocused())
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_FOCUSED;
				else
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK;
			}
		}
		
	}
	
	/**
	 * Slider track start.
	 */
	private class TrackStart extends GUIObject
	{
		TrackStart()
		{
			setInert(true);
			addName(TRACK_START_NAME);
		}

		@Override
		public String getThemeKey()
		{
			if (thisRef.style == Style.VERTICAL)
			{
				if (!thisRef.isEnabled())
					return THEME_KEY_SLIDER_VERTICAL_TRACK_START_DISABLED;
				else if (thisRef.isFocused() || this.isFocused())
					return THEME_KEY_SLIDER_VERTICAL_TRACK_START_FOCUSED;
				else
					return THEME_KEY_SLIDER_VERTICAL_TRACK_START;
			}
			else
			{
				if (!thisRef.isEnabled())
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_START_DISABLED;
				else if (thisRef.isFocused() || this.isFocused())
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_START_FOCUSED;
				else
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_START;
			}
		}
		
	}
	
	/**
	 * Slider track end.
	 */
	private class TrackEnd extends GUIObject
	{
		TrackEnd()
		{
			setInert(true);
			addName(TRACK_END_NAME);
		}

		@Override
		public String getThemeKey()
		{
			if (thisRef.style == Style.VERTICAL)
			{
				if (!thisRef.isEnabled())
					return THEME_KEY_SLIDER_VERTICAL_TRACK_END_DISABLED;
				else if (thisRef.isFocused() || this.isFocused())
					return THEME_KEY_SLIDER_VERTICAL_TRACK_END_FOCUSED;
				else
					return THEME_KEY_SLIDER_VERTICAL_TRACK_END;
			}
			else
			{
				if (!thisRef.isEnabled())
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_END_DISABLED;
				else if (thisRef.isFocused() || this.isFocused())
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_END_FOCUSED;
				else
					return THEME_KEY_SLIDER_HORIZONTAL_TRACK_END;
			}
		}
		
	}
	
	/**
	 * The slider's layout.
	 */
	private class SliderLayout implements GUILayout
	{
		@Override
		public void resizeChild(GUIObject object, int index, int childTotal)
		{
			GUIObject parent = object.getParent();
			float pw = parent.getBounds().width;
			float ph = parent.getBounds().height;
			if (object.getLayoutAttrib() != null && object.getLayoutAttrib() instanceof LayoutAttrib)
			{
				double scalar = model.getScalarForValue(currentValue);
				switch ((LayoutAttrib)object.getLayoutAttrib())
				{
					case THUMB:
					{
						switch (style)
						{
							case HORIZONTAL:
							{
								float tw = ph * thumbScalar;
								object.setBounds((float)MathUtils.linearInterpolate(scalar, 0f, pw-tw), 0f, tw, ph);
							}
							break;
							
							case VERTICAL:
							{
								float tw = pw * thumbScalar;
								object.setBounds(0f, (float)MathUtils.linearInterpolate(scalar, ph-tw, 0f), pw, tw);
							}
							break;
						}
					} // THUMB
					break;
						
					case TRACK_END:
					{
						switch (style)
						{
							case HORIZONTAL:
								object.setBounds(pw-ph, 0, ph, ph);
								break;
							case VERTICAL:
								object.setBounds(0, 0, pw, pw);
								break;
						}
					} // TRACK_START
					break;

					case TRACK_START:
					{
						switch (style)
						{
							case HORIZONTAL:
								object.setBounds(0, 0, ph, ph);
								break;
							case VERTICAL:
								object.setBounds(0, ph-pw, pw, pw);
								break;
						}
					} // TRACK_END
					break;
					
					case TRACK:
					{
						switch (style)
						{
							case HORIZONTAL:
								object.setBounds(ph, 0, pw-(ph*2), ph);
								break;
							case VERTICAL:
								object.setBounds(0, pw, pw, ph-(pw*2));
								break;
						}
					} // TRACK
					break;
				}
			}
		}
	}

}
