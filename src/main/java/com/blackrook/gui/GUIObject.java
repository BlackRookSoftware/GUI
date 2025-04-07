/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.blackrook.gui.GUIEvent.Origin;
import com.blackrook.gui.struct.HashDequeMap;
import com.blackrook.gui.struct.MathUtils;

/**
 * All GUI Objects inherit this class for creating entry points for GUI events.
 * @author Matthew Tropiano
 */
public abstract class GUIObject
{
	/** Unknown type of event. */
	public static final String EVENT_UNKNOWN = "UNKNOWN";
	/** Mouse cursor is (directly) over this object. */
	public static final String EVENT_MOUSE_OVER = "MOUSE_OVER";
	/** Mouse cursor leaves this object (not over it anymore). */
	public static final String EVENT_MOUSE_LEAVE = "MOUSE_LEAVE";
	/** Mouse button is pressed on this object. */
	public static final String EVENT_MOUSE_PRESS = "MOUSE_PRESS";
	/** Mouse button is released on this object. */
	public static final String EVENT_MOUSE_RELEASE = "MOUSE_RELEASE";
	/** Mouse is moved on this object. */
	public static final String EVENT_MOUSE_MOVE = "MOUSE_MOVE";
	/** Mouse is dragged on this object (not moved, button is down). */
	public static final String EVENT_MOUSE_DRAG = "MOUSE_DRAG";
	/** Mouse is clicked on this object. */
	public static final String EVENT_MOUSE_CLICK = "MOUSE_CLICK";
	/** Mouse wheel is scrolled on this object. */
	public static final String EVENT_MOUSE_WHEEL = "MOUSE_WHEEL";
	/** Keyboard key is pressed on this object. */
	public static final String EVENT_KEY_PRESS = "KEY_PRESS";
	/** Keyboard key is released on this object. */
	public static final String EVENT_KEY_RELEASE = "KEY_RELEASE";
	/** Keyboard key is typed on this object. */
	public static final String EVENT_KEY_TYPE = "KEY_TYPE";
	/** Gamepad button is pressed on this object. */
	public static final String EVENT_GAMEPAD_PRESS = "GAMEPAD_PRESS";
	/** Gamepad button is released on this object. */
	public static final String EVENT_GAMEPAD_RELEASE = "GAMEPAD_RELEASE";
	/** Gamepad axis is changed on this object. */
	public static final String EVENT_GAMEPAD_AXIS = "GAMEPAD_AXIS";
	/** Gamepad axis is tapped (press) on this object. */
	public static final String EVENT_GAMEPAD_TAP_PRESS = "GAMEPAD_TAP_PRESS";
	/** Gamepad axis is untapped (release) on this object. */
	public static final String EVENT_GAMEPAD_TAP_RELEASE = "GAMEPAD_TAP_RELEASE";
	/** Some kind of generic input is sent to the GUI in the form of a code. */
	public static final String EVENT_GENERIC_INPUT = "GENERIC_INPUT";
	/** Denotes an event triggered by name. */
	public static final String EVENT_NAMED = "NAMED";
	/** Object is focused. */
	public static final String EVENT_FOCUS = "FOCUS";
	/** Object loses focus. */
	public static final String EVENT_BLUR = "BLUR";

	/** Object is enabled/disabled. */
	public static final String EVENT_CHANGE_ENABLE_STATE = "CHANGE_ENABLE_STATE";
	/** Object is shown/hidden. */
	public static final String EVENT_CHANGE_VISIBLE_STATE = "CHANGE_VISIBLE_STATE";

	/**
	 * Skin scaling type.
	 */
	public static enum ScaleType
	{
		/** 
		 * No scaling. 
		 */
		NORMAL
		{
			@Override
			public float getTextureScaleS(GUIObject object)
			{
				return 1f;
			}
			
			@Override
			public float getTextureScaleT(GUIObject object)
			{
				return 1f;
			}
		},
		
		/** 
		 * Scaling is according to object's dimensional aspect.
		 * Most effective for square textures.
		 * Example: if XY dimensions are [200, 100], ST scaling is [2, 1].
		 * Example: if XY dimensions are [100, 200], ST scaling is [1, 2].
		 */
		ASPECT
		{
			@Override
			public float getTextureScaleS(GUIObject object)
			{
				GUIBounds bounds = object.getBounds();
				return bounds.width > bounds.height 
					? (bounds.width / (bounds.height == 0 ? 1f : bounds.height)) 
					: 1f;
			}
			
			@Override
			public float getTextureScaleT(GUIObject object)
			{
				GUIBounds bounds = object.getBounds();
				return bounds.height > bounds.width 
					? (bounds.height / (bounds.width == 0 ? 1f : bounds.width)) 
					: 1f;
			}
		},
		
		/** 
		 * Scaling is according to object's height.
		 * Most effective for square textures.
		 * Example: if XY dimensions are [200, 100], ST scaling is [2, 1].
		 * Example: if XY dimensions are [100, 200], ST scaling is [.5, 1].
		 */
		ADJUST_X
		{
			@Override
			public float getTextureScaleS(GUIObject object)
			{
				GUIBounds bounds = object.getBounds();
				return bounds.height != 0f ? bounds.width / bounds.height : 0f;
			}
			
			@Override
			public float getTextureScaleT(GUIObject object)
			{
				return 1f;
			}
		},
		
		/** 
		 * Scaling is according to object's width.
		 * Most effective for square textures.
		 * Example: if XY dimensions are [200, 100], ST scaling is [1, .5].
		 * Example: if XY dimensions are [100, 200], ST scaling is [1, 2].
		 */
		ADJUST_Y
		{
			@Override
			public float getTextureScaleS(GUIObject object)
			{
				return 1f;
			}
			
			@Override
			public float getTextureScaleT(GUIObject object)
			{
				GUIBounds bounds = object.getBounds();
				return bounds.width != 0f ? bounds.height / bounds.width : 0f;
			}
		};

		/** 
		 * Get the texture S-scaling according to the object.
		 * @param object the object to use.
		 * @return the texture scaling.
		 */
		public abstract float getTextureScaleS(GUIObject object);

		/** 
		 * Get the texture T-scaling according to the object.
		 * @param object the object to use.
		 * @return the texture scaling.
		 */
		public abstract float getTextureScaleT(GUIObject object);
		
	}
	
	/** Scene reference. */
	private GUI guiRef;
	/** Reference to parent object. */
	private GUIObject parentRef;
	/** Reference to child objects. */
	private final List<GUIObject> children;
	
	/** Object Bounds. */
	private GUIBounds objectBounds;
	/** Object texture name. */
	private String texture;
	/** Object color. */
	private GUIColor color;
	/** Object rotation (around z-axis, pointing out of the screen). */
	private float objectRotation;
	/** Is the object visible? */
	private boolean visible;
	/** Is the object rendered? */
	private boolean rendered;
	/** Does this receive input? */
	private boolean enabled;
	/** Is this considered inert (not receiving input), but not necessarily disabled? */
	private boolean inert;
	/** Constrain position to parent? */
	private boolean constrainToParent;
	/** Layout type for children. */
	private GUILayout layout;
	/** Layout attribute when added to an object. */
	private Object layoutAttrib;
	/** If true, children are not influenced by parent's color. */
	private boolean colorNotInherited;
	
	/** GUI Theme. */
	private GUITheme theme;
	
	/** Object opacity. */
	private float opacity;
	
	/** Scaling type. */
	private ScaleType scaleType;
	
	/** This object's event mapping. */
	private HashDequeMap<String, GUIAction> eventMap;
	/** This object's name list. */
	private Set<String> names;
	
	/** This object's absolute bounds. */
	protected GUIBounds absoluteBounds;

	/**
	 * Creates a new GUI object.
	 */
	protected GUIObject()
	{
		this.children = new ArrayList<GUIObject>(4);
		this.eventMap = new HashDequeMap<String, GUIAction>(2);
		this.names = new HashSet<String>(3);
		this.color = new GUIColor(1, 1, 1, 1);
		this.texture = null;
		this.visible = true;
		this.rendered = true;
		this.enabled = true;
		this.inert = false;
		this.objectBounds = new GUIBounds();
		this.absoluteBounds = new GUIBounds();
		setConstrainToParent(false);
		setLayout(null);
		setLayoutAttrib(null);
		setBounds(0f, 0f, 0f, 0f);
		setScaleType(ScaleType.NORMAL);
		setOpacity(1f);
	}
	
	/**
	 * @return the reference to which GUI scene this belongs to.
	 */
	public final GUI getGUI()
	{
		return guiRef;
	}
	
	/**
	 * Gets the parent object of this one.
	 * Can be null if this is not a child object.
	 * @return the parent object, or null if not added to an object.
	 */
	public final GUIObject getParent()
	{
		return parentRef;
	}

	/**
	 * Add a child object to this one and sets a layout attribute to it, as
	 * though {@link #setLayoutAttrib(Object)}
	 * @param obj the object to add.
	 * @param attrib the layout attribute.
	 * @return itself, to chain calls.
	 */
	public final GUIObject addChild(GUIObject obj, Object attrib)
	{
		obj.setLayoutAttrib(attrib);
		if (!children.contains(obj))
		{
			obj.parentRef = this;
			obj.guiRef = guiRef;
			synchronized (children)
			{
				children.add(obj);
				if (guiRef != null)
					guiRef.addObjectTree(obj);
			}
			resizeChildren();
			updateScenePosition();
		}
		return this;
	}
	
	/**
	 * Add a child object to this one.
	 * @param obj the object to add.
	 * @return itself, to chain calls.
	 */
	public final GUIObject addChild(GUIObject obj)
	{
		return addChild(obj, null);
	}
	
	/**
	 * Removes a child object from this one.
	 * @param obj the object to remove.
	 * @return itself, to chain calls.
	 */
	public final GUIObject removeChild(GUIObject obj)
	{
		if (children.contains(obj))
		{
			obj.parentRef = null;
			obj.guiRef = null;
			synchronized (children)
			{
				if (guiRef != null)
					guiRef.removeObjectTree(obj);
				children.remove(obj);
			}
			resizeChildren();
			updateScenePosition();
		}
		return this;
	}
	
	/**
	 * Removes this object from its GUI.
	 * @return itself, to chain calls.
	 */
	public final GUIObject remove()
	{
		// no parent. may be a root object.
		if (parentRef == null)
		{
			// not attached to anything. 
			if (guiRef == null)
			{
				return this;
			}
			else
			{
				guiRef.removeObject(this);
				return this;
			}
		}
		else
		{
			parentRef.removeChild(this);
			return this;
		}
	}
	
	/**
	 * Moves the order of this object up among its siblings,
	 * so that it is rendered later than the one after it 
	 * (bringing it "closer to the camera").
	 * <p>If this is the last one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 * @return itself, to chain calls.
	 */
	public final GUIObject moveUp()
	{
		List<GUIObject> childList = getParentChildren();
		if (childList == null)
			return this;
		
		int index = childList.indexOf(this);
		if (index == childList.size() - 1)
			return this;
		
		synchronized (childList)
		{
			GUIObject obj = childList.get(index);
			childList.set(index, childList.get(index + 1));
			childList.set(index + 1, obj);
		}
		updateDirty();
		return this;
	}

	/**
	 * Moves the order of this object up among its siblings,
	 * so that it is rendered last (bringing it "closest to the camera").
	 * <p>If this is the last one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 * @return itself, to chain calls.
	 */
	public final GUIObject moveToFront()
	{
		List<GUIObject> childList = getParentChildren();
		if (childList == null)
			return this;
		
		int end = childList.size() - 1;
		
		int index = childList.indexOf(this);
		if (index == end)
			return this;
		
		synchronized (childList)
		{
			childList.add(end, childList.remove(index));
		}
		updateDirty();
		return this;
	}

	/**
	 * Moves the order of this object back among its siblings,
	 * so that it is rendered earlier than the one before it 
	 * (pushing it "further from the camera").
	 * <p>If this is the first one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 * @return itself, to chain calls.
	 */
	public final GUIObject moveDown()
	{
		List<GUIObject> childList = getParentChildren();
		if (childList == null)
			return this;
		
		int index = childList.indexOf(this);
		if (index == 0)
			return this;
		
		synchronized (childList)
		{
			GUIObject obj = childList.get(index);
			childList.set(index, childList.get(index - 1));
			childList.set(index - 1, obj);
		}
		updateDirty();
		return this;
	}

	/**
	 * Moves the order of this object back among its siblings,
	 * so that it is rendered first (pushing it "farthest from the camera").
	 * <p>If this is the first one anyway, nothing happens.
	 * <p>If this is not the child of an object, nor does it belong to a GUI,
	 * nothing happens.
	 * @return itself, to chain calls.
	 */
	public final GUIObject moveToBack()
	{
		List<GUIObject> childList = getParentChildren();
		if (childList == null)
			return this;
		
		int index = childList.indexOf(this);
		if (index == 0)
			return this;
		
		synchronized (childList)
		{
			childList.add(0, childList.remove(index));
		}
		updateDirty();
		return this;
	}

	// Get child list of this object's parent (or GUI, if top of hierarchy).
	private List<GUIObject> getParentChildren()
	{
		GUIObject parent = getParent();
		if (parent == null)
		{
			if (getGUI() == null)
				return null;
			else
				return guiRef.getRootObjects();
		}
		else
			return parent.getChildren();
	}
	
	/**
	 * Called when this object needs adding to a render set for rendering later.
	 * Usually, this should add itself plus its children to the set.
	 * Assumes the render set's GUI bounds are set.
	 * @param set the set to add it to.
	 */
	protected final void addToRenderSet(GUIRenderSet set)
	{
		if (!isVisible())
			return;

		GUIBounds abs = getAbsoluteBounds();
		if (MathUtils.getIntersectionBox(set.getGUIX(), set.getGUIY(), set.getGUIWidth(), set.getGUIHeight(), abs.x, abs.y, abs.width, abs.height) 
				&& isAddedToRenderSet())
			set.addObject(this);
		
		final List<GUIObject> children = this.getChildren();
		synchronized (children)
		{
			for (GUIObject obj : children)
				obj.addToRenderSet(set);
		}
	}
	
	// Tests if this object should be rendered anyway.
	private boolean isAddedToRenderSet()
	{
		return isRendered() 
			&& objectBounds.width > 0
			&& objectBounds.height > 0
			&& color.getAlpha() != 0.0f
		;
	}

	/**
	 * Checks if an action has at least one binding.
	 * @param type the event type.
	 * @return true if so, false if not.
	 */
	public final boolean hasAction(String type)
	{
		Deque<GUIAction> q = eventMap.get(type);
		return q != null && q.size() > 0;
	}
	
	/**
	 * Adds an action bound to an event type.
	 * The action is enqueued, so the added action will 
	 * happen after other actions bound to this event type.
	 * @param action the action to bind.
	 * @param types the event types.
	 * @return itself, to chain calls.
	 */
	public final GUIObject bindAction(GUIAction action, String ... types)
	{
		for (String t : types)
			eventMap.add(t, action);
		return this;
	}
	
	/**
	 * Removes an action bound to an event type.
	 * @param action the action to unbind.
	 * @param types the event types.
	 * @return itself, to chain calls.
	 */
	public final GUIObject unbindAction(GUIAction action, String ... types)
	{
		for (String t : types)
			eventMap.removeValue(t, action);
		return this;
	}
	
	/**
	 * Removes all actions bound to event types.
	 * @param types the event types.
	 * @return itself, to chain calls.
	 */
	public final GUIObject unbindAllActions(String ... types)
	{
		for (String t : types)
			eventMap.remove(t);
		return this;
	}
	
	/**
	 * Calls an action on this object.
	 * @param action the action to call on this object.
	 */
	public final void callAction(GUIAction action)
	{
		GUIEvent event = new GUIEvent();
		event.reset();
		event.setGUI(getGUI());
		event.setObject(this);
		event.setOrigin(Origin.ACTION);
		action.call(event);
	}
	
	/**
	 * Calls all actions bound to this object by type.
	 * If no action attached to this object is associated with that name,
	 * this does nothing and returns false.
	 * @param event the event to pass.
	 */
	final void callEvent(GUIEvent event)
	{
		if (eventMap.containsKey(event.getType()))
		{
			for (GUIAction action : eventMap.get(event.getType()))
				action.call(event);
		}
	}
	
	/**
	 * Fires a non-specific event to the GUI system.
	 * @param type the event type to fire.
	 */
	protected final void fireEvent(String type)
	{
		if (guiRef != null)
			guiRef.fireGUIEvent(this, type);
	}
	
	/**
	 * Adds a single name or series of names to this object, used for selecting objects.
	 * @param names the names to add.
	 */
	public final void addName(String ... names)
	{
		for (String n : names)
		{
			if (!this.names.contains(n))
			{
				this.names.add(n);
				if (guiRef != null)
					guiRef.addObjectName(n, this);
			}
		}
	}

	/**
	 * Removes a single name or series of names from this object.
	 * @param names the names to remove.
	 * @return itself, to chain calls.
	 */
	public final GUIObject removeName(String ... names)
	{
		for (String n : names)
		{
			if (!this.names.contains(n))
			{
				this.names.remove(n);
				if (guiRef != null)
					guiRef.removeObjectName(n, this);
			}
		}
		return this;
	}

	/**
	 * Checks if this object has a particular name.
	 * @param name the name to search for.
	 * @return true if found, false if not. 
	 */
	public final boolean hasName(String name)
	{
		return names.contains(name);
	}

	/**
	 * Returns true if one of this object's names matches the provided pattern, false otherwise.
	 * @param pattern the pattern to use.
	 * @return the name matched, or null if no matches.
	 */
	public final String hasNamePattern(Pattern pattern)
	{
		for (String n : names)
			if (pattern.matcher(n).matches())
				return n;
		return null;
	}

	/**
	 * Returns a copy of this object's bounding rectangle.
	 * Any changes to the returned Rectangle2F will not change the bounds of this object,
	 * and thus will NOT trigger any events triggered by a bounds change.
	 * <p>NOTE: The returned object reference is used by other calls to this method on this thread. 
	 * If you wish to preserve the values returned, they will need to be copied. 
	 * @return a rectangle of this object's bounds.
	 * @see #setBounds(float, float, float, float)
	 */
	public GUIBounds getBounds()
	{
		GUIBounds out = CACHE.get().objectBoundsCallback;
		out.set(objectBounds);
		return out;
	}
	
	/**
	 * Returns a reference to this object's bounds rectangle.
	 * Any changes made to this rectangle will affect the object's bounds,
	 * but will NOT trigger any events triggered by a bounds change. 
	 * @return the rectangle reference.
	 * @see #setBounds(float, float, float, float)
	 */
	protected GUIBounds getNativeBounds()
	{
		return objectBounds;
	}

	/**
	 * Sets this object's rotation in degrees.
	 * @param degrees the new rotation in degrees. 0 is no rotation.
	 * @return itself, to chain calls.
	 */
	public GUIObject setRotationZ(float degrees)
	{
		objectRotation = degrees;
		updateDirty();
		return this;
	}
	
	/**
	 * Rotates this object by a number of degrees.
	 * @param degrees the rotation in degrees to add.
	 * @return itself, to chain calls.
	 */
	public GUIObject rotate(float degrees)
	{
		objectRotation += degrees;
		updateDirty();
		return this;
	}
	
	/**
	 * Sets the color of this object.
	 * @param red the red component value for the color. 
	 * @param green the green component value for the color.
	 * @param blue the blue component value for the color.
	 * @param alpha the alpha component value for the color.
	 * @return itself, to chain calls.
	 */
	public GUIObject setColor(float red, float green, float blue, float alpha)
	{
		color.set(red, green, blue, alpha);
		updateDirty();
		return this;
	}

	/**
	 * Sets the object's color.
	 * @param color the color to set.
	 * @return itself, to chain calls.
	 */
	public GUIObject setColor(GUIColor color)
	{
		this.color.set(color);
		updateDirty();
		return this;
	}

	/**
	 * Gets the object's color.
	 * If the object returned is changed, it will not affect this object's color. 
	 * @return an GUIColor with this object's color components.
	 */
	public GUIColor getColor()
	{
		GUIColor out = CACHE.get().objectColorCallback;
		out.set(color);
		return out;
	}
	
	/**
	 * Sets this object's texture.
	 * @param texture the new texture.
	 * @return itself, to chain calls.
	 */
	public GUIObject setTexture(String texture)
	{
		this.texture = texture;
		updateDirty();
		return this;
	}
	
	/**
	 * Sets if the object is visible (and its children).
	 * Fires an event if the visibility state changed.
	 * This attribute affects this object's descendants - they are not 
	 * visible if this is not visible.
	 * Objects that are not visible are ignored when testing for mouse collisions,
	 * nor are they drawn by the renderer.
	 * @param visible true to set, false to unset.
	 * @return itself, to chain calls.
	 */
	public GUIObject setVisible(boolean visible)
	{
		boolean prev = this.visible;
		this.visible = visible;
		if (visible != prev && guiRef != null)
		{
			if (!visible)
				releaseFocus();
			guiRef.fireGUIEvent(this, EVENT_CHANGE_VISIBLE_STATE);
		}
		updateDirty();
		return this;
	}
	
	/**
	 * Sets if the object is visible (and its children).
	 * Fires an event if the visibility state changed.
	 * This attribute affects this object's descendants - they are not 
	 * visible if this is not visible.
	 * Objects that are not visible are ignored when testing for mouse collisions,
	 * nor are they drawn by the renderer.
	 * @param rendered true to set, false to unset.
	 * @return itself, to chain calls.
	 */
	public GUIObject setRendered(boolean rendered)
	{
		this.rendered = rendered;
		updateDirty();
		return this;
	}
	
	/**
	 * Sets if the object can accept input (and its children).
	 * Fires an event if the enabled state changed.
	 * If this object was focused, it tells the GUI that owns it to unfocus it if it was disabled.
	 * @param enabled true to set, false to clear.
	 * @return itself, to chain calls.
	 */
	public GUIObject setEnabled(boolean enabled)
	{
		boolean prev = this.enabled;
		this.enabled = enabled;
		if (enabled != prev && guiRef != null)
		{
			if (!enabled)
				releaseFocus();
			guiRef.fireGUIEvent(this, EVENT_CHANGE_ENABLE_STATE);
		}
		updateDirty();
		return this;
	}

	/**
	 * Sets if the object can accept/intercept input, but does <b>NOT</b>
	 * affect its "enabled" state, unless its parent is enabled/disabled.
	 * Unlike {@link #isVisible()} and {@link #isEnabled()}, this is not
	 * hierarchically significant (inert-ness is set on this and ONLY this object).
	 * Objects that are inert are ignored when testing for mouse collisions.
	 * This fires NO EVENTS on change.
	 * If this object was focused, it tells the GUI that owns it to unfocus it if it was set to be inert.
	 * @param inert true to set, false to clear.
	 * @return itself, to chain calls.
	 */
	public GUIObject setInert(boolean inert)
	{
		boolean prev = this.inert;
		this.inert = inert;
		if (inert != prev && guiRef != null)
		{
			if (!inert)
				releaseFocus();
		}
		updateDirty();
		return this;
	}

	/**
	 * Sets the object bounds.
	 * @param r	the rectangle to use for bounds.
	 * @return itself, to chain calls.
	 */
	public GUIObject setBounds(GUIBounds r)
	{
		setBounds(r.x, r.y, r.width, r.height);
		return this;
	}
	
	/**
	 * Sets the object bounds.
	 * @param x its position x.
	 * @param y	its position y.
	 * @param width its width.
	 * @param height its height.
	 * @return itself, to chain calls.
	 */
	public GUIObject setBounds(float x, float y, float width, float height)
	{
		objectBounds.x = x;
		objectBounds.y = y;
		objectBounds.width = width;
		objectBounds.height = height;
		correctPosition();
		resizeChildren();
		updateScenePosition();
		return this;
	}

	/**
	 * Gets this object's ABSOLUTE bounds, i.e. the object's current position after
	 * considering its inherited hierarchy.
	 * The Rectangle2F returned can be changed and it will not affect this object's bounds.
	 * @return a Rectangle2F of the current object bounds.
	 */
	public GUIBounds getAbsoluteBounds()
	{
		GUIBounds out = CACHE.get().objectAbsoluteBoundsCallback;
		out.set(absoluteBounds);
		return out;
	}
	
	/**
	 * Sets the object position.
	 * @param x	its position x.
	 * @param y	its position y.
	 * @return itself, to chain calls.
	 */
	public GUIObject setPosition(float x, float y)
	{
		objectBounds.x = x;
		objectBounds.y = y;
		correctPosition();
		updateScenePosition();
		updateDirty();
		return this;
	}

	/**
	 * Sets the object width and height.
	 * @param width its width.
	 * @param height its height.
	 * @return itself, to chain calls.
	 */
	public GUIObject setDimensions(float width, float height)
	{
		objectBounds.width = width;
		objectBounds.height = height;
		correctPosition();
		resizeChildren();
		updateScenePosition();
		return this;
	}

	/**
	 * Changes this object's position by an x or y-coordinate amount.
	 * @param x the x movement.
	 * @param y the y movement. 
	 * @return itself, to chain calls.
	 */
	public GUIObject translate(float x, float y)
	{
		objectBounds.x += x;
		objectBounds.y += y;
		correctPosition();
		updateScenePosition();
		updateDirty();
		return this;
	}

	/**
	 * Changes this object's width/height by an x or y-coordinate amount.
	 * @param width the width amount.
	 * @param height the height amount.
	 * @return itself, to chain calls.
	 */
	public GUIObject stretch(float width, float height)
	{
		objectBounds.width += width;
		objectBounds.height += height;
		correctPosition();
		resizeChildren();
		updateScenePosition();
		return this;
	}

	/**
	 * @return the object's rendering position, x-axis.
	 */
	public final float getRenderPositionX()
	{
		return absoluteBounds.x;
	}

	/**
	 * @return the object's rendering position, y-axis.
	 */
	public final float getRenderPositionY()
	{
		return absoluteBounds.y;
	}

	/**
	 * @return the object's rendering width.
	 */
	public float getRenderWidth()
	{
		return objectBounds.width;
	}

	/**
	 * @return the object's rendering height.
	 */
	public float getRenderHeight()
	{
		return objectBounds.height;
	}

	/**
	 * @return the object's rotation in degrees, z-axis.
	 */
	public float getRenderRotationZ()
	{
		return objectRotation;
	}

	/**
	 * Gets the object's radius, for use with determining what is on the screen or not.
	 * <p>
	 * Since this could be an expensive call, this 
	 * is not always used - it is used if the object's useRenderRadius() 
	 * function returns true, which leaves it in the hands of the implementor.
	 * @return the radius.
	 */
	public float getRenderRadius()
	{
		float rhh = getRenderHeight() / 2f;
		float rhw = getRenderWidth() / 2f;
		return (float)Math.sqrt(rhh * rhh + rhw * rhw);
	}

	/**
	 * Should the object's radius value be used for collision, with the camera,
	 * rather than its half-height or half-width?
	 * @return the radius.
	 */
	public boolean useRenderRadius()
	{
		return getRenderRotationZ() != 0.0f;
	}

	/**
	 * @return the object's red channel value.
	 */
	public float getRenderRed()
	{
		return color.getRed() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getRenderRed() : 1f) ;
	}

	/**
	 * @return the object's green channel value.
	 */
	public float getRenderGreen()
	{
		return color.getGreen() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getRenderGreen() : 1f);
	}

	/**
	 * @return the object's blue channel value.
	 */
	public float getRenderBlue()
	{
		return color.getBlue() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getRenderBlue() : 1f);
	}

	/**
	 * @return the object's alpha channel value.
	 */
	public float getRenderAlpha()
	{
		return color.getAlpha() * getFinalOpacity() * (parentRef != null && !parentRef.getColorNotInherited() ? parentRef.getRenderAlpha() : 1f);
	}

	/**
	 * @return this object's inherited opacity.
	 */
	protected float getFinalOpacity()
	{
		return opacity * (parentRef != null ? parentRef.getFinalOpacity() : 1f);
	}
	
	/**
	 * Gets the object's texture scaling, S-axis (U).
	 * Should, in most cases, return 1f.
	 * @return the scaling value.
	 */
	public float getRenderTextureScaleS()
	{
		if (scaleType != null)
			return scaleType.getTextureScaleS(this);
		else
			return 1f;
	}
	
	/**
	 * Gets the object's texture scaling, T-axis (V).
	 * Should, in most cases, return 1f.
	 * @return the scaling value.
	 */
	public float getRenderTextureScaleT()
	{
		if (scaleType != null)
			return scaleType.getTextureScaleT(this);
		else
			return 1f;
	}
	
	/**
	 * Returns how far down the generation tree this object is.
	 * @return How many parents are above this one.
	 */
	public final int getGeneration()
	{
		return parentRef != null ? 1 + parentRef.getGeneration() : 0;
	}

	/**
	 * Returns this object's texture.
	 * If no texture is set on this, this will take the one from the theme,
	 * if {@link #getThemeKey()} returns a non-null value.
	 * @return the texture name.
	 * @see #getThemeKey() 
	 */
	public String getTexture()
	{
		if (texture != null)
			return texture;
		
		String key = getThemeKey();
		
		if (key != null)
		{
			GUITheme t = getTheme();
			return t != null ? t.getTexture(key) : null;
		}

		return null;
	}
	
	/**
	 * Returns this object's theme key.
	 * This is the key that is used to look up the texture
	 * used to render this object. This may return null,
	 * indicating that this object does not have a default texture.
	 * @return the theme key.
	 */
	public abstract String getThemeKey();
	
	/**
	 * Gets if the object is visible, which means that this object has
	 * its "visible" member set to true, and its opacity is nonzero.
	 * This object is not visible if any of its ancestors are not visible.
	 * Objects that are not visible are ignored when testing for mouse collisions.
	 * @return true if visible, false if not.
	 */
	public boolean isVisible()
	{
		return visible && getOpacity() > 0f && (parentRef != null ? parentRef.isVisible() : true);
	}

	/**
	 * Gets if this object is rendered.
	 * The object's children can still be rendered.
	 * Unlike {@link #isVisible()}, this is not
	 * hierarchically significant ("rendered" affects this and ONLY this object).
	 * @return true if so, false if not.
	 */
	public boolean isRendered()
	{
		return rendered;
	}

	/**
	 * Gets if this object is currently focused in its GUI.
	 * @return true if so. False if not, or this doesn't belong to a GUI.
	 */
	public boolean isFocused()
	{
		return guiRef != null && guiRef.getFocusedObject() == this;
	}

	/**
	 * Gets if this object is currently animating
	 * @return true if so. False if not, or this doesn't belong to a GUI.
	 */
	public boolean isAnimating()
	{
		return guiRef != null && guiRef.isAnimating(this);
	}
	
	/**
	 * Gets if the object can accept/intercept input.
	 * This affects this object's "enabled" state, as the name suggests.
	 * This object is not enabled if any of its ancestors are not enabled.
	 * Objects that are not enabled cannot be focused, and are also ignored
	 * when testing mouse collisions/events. They can still be manipulated
	 * through user-enacted methods that are not direct input.
	 * @return true if enabled, false if not.
	 */
	public boolean isEnabled()
	{
		return enabled && (parentRef != null ? parentRef.isEnabled() : true);
	}

	/**
	 * Gets if the object can accept/intercept input, but does <b>NOT</b>
	 * affect its "enabled" state, unless its parent is enabled/disabled.
	 * Unlike {@link #isVisible()} and {@link #isEnabled()}, this is not
	 * hierarchically significant (inert-ness is set on this and ONLY this object).
	 * Objects that are inert are ignored when testing for mouse collisions.
	 * @return true if inert, false if not.
	 */
	public boolean isInert()
	{
		return inert;
	}

	/**
	 * Checks if this constrains its position to its parent.
	 * @return true of so, false if not.
	 */
	public boolean isConstrainedToParent()
	{
		return constrainToParent;
	}

	/**
	 * Sets if this should constrain its position to its parent.
	 * @param constrainToParent true if so, false if not.
	 * @return itself, to chain calls.
	 */
	public GUIObject setConstrainToParent(boolean constrainToParent)
	{
		this.constrainToParent = constrainToParent;
		correctPosition();
		updateDirty();
		return this;
	}

	/**
	 * Gets this object wrapped in an {@link GUIQuery}.
	 * Convenience method for <code>OGLGUIQuery.wrap(this)</code>.
	 * @return a new OGLGUIQuery with this object in it.
	 */
	public GUIQuery getAsQuery()
	{
		return GUIQuery.wrap(this);
	}

	/**
	 * Gets this object's parent wrapped in an {@link GUIQuery}.
	 * Convenience method for <code>OGLGUIQuery.wrap(this.getParent())</code>.
	 * @return a new OGLGUIQuery with this object's parent in it.
	 */
	public GUIQuery getParentAsQuery()
	{
		return GUIQuery.wrap(this.getParent());
	}
	
	/**
	 * Gets this object's children wrapped in an {@link GUIQuery}.
	 * @return a new OGLGUIQuery with this object's children in it.
	 */
	public GUIQuery getChildrenAsQuery()
	{
		return getAsQuery().getChildren();
	}

	/**
	 * Gets this object's entire descendant tree wrapped in an {@link GUIQuery}.
	 * @return a new OGLGUIQuery with this object's descendants in it.
	 */
	public GUIQuery getDescendantsAsQuery()
	{
		return getAsQuery().getDescendants();
	}

	/**
	 * Gets this object's siblings wrapped in an {@link GUIQuery}.
	 * @return a new OGLGUIQuery with this object's siblings in it.
	 */
	public GUIQuery getSiblingsAsQuery()
	{
		return getAsQuery().getSiblings();
	}

	/**
	 * Gets this object's layout type for resizing object children,
	 * should this object get resized itself. By default, this value is null.
	 * @return the layout, or null if no layout.
	 */
	public GUILayout getLayout()
	{
		return layout;
	}

	/**
	 * Sets this object's layout type for resizing object children,
	 * should this object get resized itself. By default, this value is null.
	 * @param layout the layout to use.
	 * @return itself, to chain calls.
	 */
	public GUIObject setLayout(GUILayout layout)
	{
		this.layout = layout;
		resizeChildren();
		return this;
	}

	/**
	 * Gets this object's layout attribute, used by some layouts in order to
	 * affect the parent's layout behavior when resizing this component (as a child). 
	 * By default, this value is null.
	 * @return the layout attribute on this object, or null if none was set.
	 */
	public Object getLayoutAttrib()
	{
		return layoutAttrib;
	}

	/**
	 * Sets this object's layout attribute, used by some layouts in order to
	 * affect the parent's layout behavior when resizing this component (as a child). 
	 * By default, this value is null.
	 * Changing this value while this object is a part of its parent's layout will
	 * trigger {@link #resizeChildren()} on the parent.
	 * @param attrib the layout attribute to set (or null).
	 * @return itself, to chain calls.
	 */
	public GUIObject setLayoutAttrib(Object attrib)
	{
		this.layoutAttrib = attrib;
		if (parentRef != null)
			parentRef.resizeChildren();
		return this;
	}

	/**
	 * Gets if this object DOESN'T pass on its color to its children.
	 * @return true of so, false if not.
	 */
	public boolean getColorNotInherited()
	{
		return colorNotInherited;
	}

	/**
	 * Sets if this object DOESN'T pass on its color to its children.
	 * @param enabled true if 
	 * @return itself, to chain calls.
	 */
	public GUIObject setColorNotInherited(boolean enabled)
	{
		this.colorNotInherited = enabled;
		updateDirty();
		return this;
	}

	/**
	 * Returns the current theme used by this object.
	 * If no current theme, this returns the parent's theme.
	 * If no parent, this returns the theme attached the owning GUI.
	 * @return the theme to use.
	 */
	public final GUITheme getTheme()
	{
		return theme != null 
			? theme 
			: (parentRef != null 
				? parentRef.getTheme() 
				: (guiRef != null 
					? guiRef.getTheme() 
					: null
				)
			);
	}
	
	/**
	 * Sets the theme used by this object, and its descendants if they do not
	 * have a theme set. Setting this to null allows this object to inherit the current theme
	 * from its parents.
	 * @param theme the theme to use. Can be null.
	 * @return itself, to chain calls.
	 */
	public GUIObject setTheme(GUITheme theme)
	{
		this.theme = theme;
		updateDirty();
		return this;
	}
	
	/**
	 * Gets this object's opacity (0 to 1).
	 * Opacity is always inherited by children, regardless 
	 * of if {@link #getColorNotInherited()} is true or false.
	 * This affects visibility - if this is 0.0, this object is
	 * considered NOT VISIBLE.
	 * @return this object's opacity.
	 * @see #isVisible()
	 */
	public float getOpacity()
	{
		return opacity;
	}

	/**
	 * Gets this object's opacity (0 to 1).
	 * Opacity is always inherited by children, regardless 
	 * of if {@link #getColorNotInherited()} is true or false.
	 * This affects visibility - setting this to 0.0 is like setting
	 * {@link #setVisible(boolean)} to <code>false</code>.
	 * @param opacity the new opacity factor.
	 * @return itself, to chain calls.
	 */
	public GUIObject setOpacity(float opacity)
	{
		this.opacity = opacity;
		updateDirty();
		return this;
	}

	/**
	 * @return this object's texture scaling type.
	 */
	public ScaleType getScaleType()
	{
		return scaleType;
	}

	/**
	 * Sets this object's texture scaling type.
	 * @param scaleType the scaling type to use.
	 * @return itself, to chain calls.
	 */
	public GUIObject setScaleType(ScaleType scaleType)
	{
		this.scaleType = scaleType;
		updateDirty();
		return this;
	}

	/**
	 * Sets this object's width and height based on the bounds of its children.
	 * If it has no children, nothing happens.
	 */
	public void setBoundsByChildren()
	{
		GUIBounds objectBounds = getBounds();

		if (children.size() > 0) 
		{
			float w = 0f;
			float h = 0f;
			for (GUIObject child : children)
			{
				GUIBounds childBounds = child.getBounds();

				float cw = childBounds.x + childBounds.width;
				float ch = childBounds.y + childBounds.height;
				w = Math.max(w, cw);
				h = Math.max(h, ch);
			}
			setBounds(objectBounds.x, objectBounds.y, w, h);
		}
	}
	
	/**
	 * Requests focus on this object in the scene it belongs to.
	 * @return itself, to chain calls.
	 */
	public GUIObject requestFocus()
	{
		if (guiRef != null)
			guiRef.requestObjectFocus(this);
		return this;
	}

	/**
	 * Releases focus on this object if this object currently has focus
	 * in the scene it belongs to.
	 * @return itself, to chain calls.
	 */
	public GUIObject releaseFocus()
	{
		if (guiRef != null)
			guiRef.requestObjectUnfocus(null);
		return this;
	}

	/**
	 * Calls upon the layouts to resize the children.
	 */
	public void resizeChildren()
	{
		if (layout == null)
			return;
		
		int i = 0;
		for (GUIObject child : children)
		{
			layout.resizeChild(child, i++, children.size());
			child.correctPosition();
			child.resizeChildren();
		}
		
		updateScenePosition();
		updateDirty();
	}

	/**
	 * Enqueues an animation on this GUI Object, no duration.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, to chain calls.
	 */
	public GUIObject animate(GUIAnimation ... animations)
	{
		animate(0, GUIEasing.LINEAR, animations);
		return this;
	}

	/**
	 * Enqueues an animation on this GUI Object, linear transition.
	 * @param duration the duration of the action in milliseconds.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, to chain calls.
	 */
	public GUIObject animate(long duration, GUIAnimation ... animations)
	{
		animate(duration, GUIEasing.LINEAR, animations);
		return this;
	}

	/**
	 * Enqueues an animation on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @param transition the transition type for the action.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, to chain calls.
	 */
	public GUIObject animate(long duration, GUIEasingType transition, GUIAnimation ... animations)
	{
		if (guiRef != null)
			guiRef.addAnimation(this, duration, transition, animations);
		return this;
	}

	/**
	 * Enqueues a delay between animations on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @return itself, to chain calls.
	 */
	public GUIObject animateDelay(long duration)
	{
		if (guiRef != null)
			guiRef.addAnimation(this, duration, GUIEasing.LINEAR);
		return this;
	}

	/**
	 * Aborts the animation on this object, abandoning it mid-animation.
	 * @return itself, to chain calls.
	 */
	public GUIObject animateAbort()
	{
		if (guiRef != null)
			guiRef.endAnimation(this, false);
		return this;
	}

	/**
	 * Finishes the animation on this object all the way to the end.
	 * @return itself, to chain calls.
	 */
	public GUIObject animateFinish()
	{
		if (guiRef != null)
			guiRef.endAnimation(this, true);
		return this;
	}

	/**
	 * Corrects the position of this object, if this is constrained to the parent's position.
	 */
	protected void correctPosition()
	{
		if (!constrainToParent || parentRef == null)
			return;
		
		GUIBounds objectBounds = getNativeBounds();
		GUIBounds parentBounds = parentRef.getBounds();
		
		if (objectBounds.x < 0)
			objectBounds.x = 0;
		else if (objectBounds.x + objectBounds.width > parentBounds.width)
			objectBounds.x = parentBounds.width - objectBounds.width;
		
		if (objectBounds.y < 0)
			objectBounds.y = 0;
		else if (objectBounds.y + objectBounds.height > parentBounds.height)
			objectBounds.y = parentBounds.height - objectBounds.height;
	}

	/**
	 * Updates this GUI Object's position and absolute bounds in the scene's collision field.
	 */
	protected void updateScenePosition()
	{
		GUIBounds bounds = getNativeBounds();
		GUIBounds parentAbsoluteBounds = parentRef != null ? parentRef.absoluteBounds : null;
		
		absoluteBounds.x = bounds.x + (parentAbsoluteBounds != null ? parentAbsoluteBounds.x : 0f);
		absoluteBounds.y = bounds.y + (parentAbsoluteBounds != null ? parentAbsoluteBounds.y : 0f);
		absoluteBounds.width = bounds.width;
		absoluteBounds.height = bounds.height;
		
		for (GUIObject obj : children)
			obj.updateScenePosition();
	}

	/**
	 * Update the dirty flag on the GUI that this object belongs to.
	 */
	protected final void updateDirty()
	{
		if (guiRef != null)
			guiRef.setDirty(true);
	}
	
	/**
	 * Called when this object is added to a GUI.
	 * Does nothing, unless overridden.
	 * @param gui the reference to the GUI that this was added to.
	 */
	protected void onGUIChange(GUI gui)
	{
		// Do nothing.
	}
	
	/**
	 * Sets the reference to which GUI scene this belongs to.
	 */
	final void setGUI(GUI ref)
	{
		guiRef = ref;
		onGUIChange(guiRef);
	}

	/**
	 * Returns a reference to the list of this object's children.
	 */
	final List<GUIObject> getChildren()
	{
		return children;
	}

	/** 
	 * Gets a reference to the name hash.
	 */
	final Set<String> getNameHash()
	{
		return names;
	}

	/**
	 * Gets all objects in the tree.
	 * @param query the output query result.
	 */
	final void getAllInTree(GUIQuery query)
	{
		query.add(this);
		for (GUIObject child : getChildren())
			child.getAllInTree(query);
	}

	/**
	 * Gets all name matches in this object's tree.
	 * @param name the name to look for.
	 * @param query the output query result.
	 */
	final void getNameMatchesInTree(String name, GUIQuery query)
	{
		if (hasName(name))
			query.add(this);
		for (GUIObject child : getChildren())
			child.getNameMatchesInTree(name, query);
	}

	/**
	 * Gets all name pattern matches in this object's tree.
	 * @param pattern the pattern to look for.
	 * @param query the output query result.
	 */
	final void getNamePatternMatchesInTree(Pattern pattern, GUIQuery query)
	{
		if (hasNamePattern(pattern) != null)
			query.add(this);
		for (GUIObject child : getChildren())
			child.getNamePatternMatchesInTree(pattern, query);
	}

	/**
	 * Gets all class matches in this object's tree.
	 * @param clazz the class type to look for.
	 * @param query the output query result.
	 */
	final void getTypeMatchesInTree(Class<?> clazz, GUIQuery query)
	{
		if (clazz.isInstance(this))
			query.add(this);
		for (GUIObject child : getChildren())
			child.getTypeMatchesInTree(clazz, query);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(' ');
		GUIBounds bounds = getBounds();
		if (names.size() > 0)
		{
			sb.append('[');
			int x = 0;
			for (String name : names)
			{
				sb.append(name);
				if (x < names.size() - 1)
					sb.append(", ");
				x++;
			}
			sb.append(']');
		}
		else
			sb.append("(UNNAMED)");
		sb.append(' ');
		sb.append(String.format("R[%.03f, %.03f, %.03f, %.03f]", 
			bounds.x, bounds.y, bounds.width, bounds.height));
		sb.append(' ');
		sb.append(String.format("RGBA(%.03f, %.03f, %.03f, %.03f) O: %.03f", 
			color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), opacity));
		sb.append(' ');
		if (!isEnabled())
			sb.append("DISABLED").append(' ');
		if (!isVisible())
			sb.append("INVISIBLE").append(' ');
		if (getLayout() != null)
			sb.append(getLayout().getClass().getSimpleName()).append(' ');
		if (getLayoutAttrib() != null)
			sb.append(getLayoutAttrib().getClass().getSimpleName()).append(' ');
		return sb.toString();
	}
	
	private static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(()->new Cache());

	// Cache.
	private static class Cache
	{
		/** 
		 * Separate object bounds rectangle used for the {@link #getBounds()} call.
		 * This is so that it can be used entirely for feedback, and the manipulation
		 * thereof will not affect this object. 
		 */
		protected GUIBounds objectBoundsCallback;

		/** 
		 * Separate object color used for the {@link #getColor()} call.
		 * This is so that it can be used entirely for feedback, and the manipulation
		 * thereof will not affect this object. 
		 */
		protected GUIColor objectColorCallback;

		/** 
		 * Separate object bounds rectangle used for the {@link #getAbsoluteBounds()} call.
		 * This is so that it can be used entirely for feedback, and the manipulation
		 * thereof will not affect this object. 
		 */
		protected GUIBounds objectAbsoluteBoundsCallback;
		
		public Cache()
		{
			this.objectBoundsCallback = new GUIBounds();
			this.objectColorCallback = new GUIColor();
			this.objectAbsoluteBoundsCallback = new GUIBounds();
		}
	}


}
