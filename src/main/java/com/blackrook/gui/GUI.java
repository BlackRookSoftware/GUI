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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.KeyStroke;

import com.blackrook.gui.GUIEvent.Origin;
import com.blackrook.gui.input.GUIKeyStroke;
import com.blackrook.gui.struct.HashDequeMap;

/**
 * Main GUI structure.
 * @author Matthew Tropiano
 */
public class GUI
{
	/** GUI Viewport. */
	private Viewport viewport;

	/** GUI Theme. */
	private GUITheme theme;
	
	/** Current moused-over object. */
	private GUIObject currentObjectMouseOver;
	/** Current object the mouse button is pressed on. */
	private GUIObject currentObjectMouseDown;
	/** Current pressed mouse button. */
	private int currentObjectMouseDownButton;
	/** Current "drag state." */
	private boolean currentObjectMouseDragState;
	/** Current focused object. */
	private GUIObject currentObjectFocus;
	/** Current modifiers for keys (used with broadcast). */
	private int currentKeyModifiers;
	/** Current mouse enter state. */
	private boolean currentMouseEnterState;
	/** Current mouse position X. */
	private int currentMousePositionX;
	/** Current mouse position Y. */
	private int currentMousePositionY;
	/** Current mouse movement X. */
	private int currentMouseMovementX;
	/** Current mouse movement Y. */
	private int currentMouseMovementY;
	/** Current mouse current object X. */
	private int currentMouseObjectX;
	/** Current mouse current object Y. */
	private int currentMouseObjectY;
	
	/** Mutex object for object operations. */
	private Object objectsMutex;
	/** List of Root GUI Objects. */
	private List<GUIObject> rootObjects;
	/** Set of all objects. */
	private Set<GUIObject> allObjects;
	/** Name to objects. */
	private HashDequeMap<String, GUIObject> namedObjectMap;
	/** The action queue. */
	private Map<GUIObject, AnimationQueue<GUIObject>> actionQueueMap;

	/** List of broadcast actions - keystrokes. */
	private HashDequeMap<GUIKeyStroke, GUIAction> keystrokeBroadcastMap;
	/** List of broadcast actions - names. */
	private HashDequeMap<String, GUIAction> namedBroadcastMap;
	
	/** If this changed and needs scene rebuilding. */
	private boolean dirty;

	/**
	 * Creates a new OGLGUI instance.
	 */
	public GUI()
	{
		// Current GUI
		this.viewport = new Viewport(0f, 0f, 0f, 0f);
		
		// Current Theme
		this.theme = null;

		// GUI Element State
		this.currentObjectFocus = null;
		this.currentObjectMouseOver = null;
		this.currentObjectMouseDown = null;
		this.currentObjectMouseDownButton = 0;
		this.currentObjectMouseDragState = false;
		this.currentMouseEnterState = false;
		this.currentMousePositionX = Integer.MIN_VALUE;
		this.currentMousePositionY = Integer.MIN_VALUE;
		this.currentMouseMovementX = 0;
		this.currentMouseMovementY = 0;
		this.currentMouseObjectX = -1;
		this.currentMouseObjectY = -1;

		// Element Hierarchy
		this.objectsMutex = new Object();
		this.rootObjects = new ArrayList<>(8);
		this.allObjects = new HashSet<>(128);
		this.namedObjectMap = new HashDequeMap<>(24);
		this.actionQueueMap = new HashMap<>();
		
		this.keystrokeBroadcastMap = new HashDequeMap<>(4);
		this.namedBroadcastMap = new HashDequeMap<>(4);
	}

	/**
	 * Gets the root objects.
	 */
	List<GUIObject> getRootObjects()
	{
		return rootObjects;
	}

	/**
	 * Adds the object tree to the GUI.
	 * @param obj the object to add.
	 */
	void addObjectTree(GUIObject obj)
	{
		synchronized (objectsMutex)
		{
			allObjects.add(obj);
			for (String n : obj.getNameSet())
				addObjectName(n, obj);
			for (GUIObject child : obj.getChildren())
			{
				child.setGUI(this);
				addObjectTree(child);
			}
		}
	}
	
	void addObjectName(String name, GUIObject object)
	{
		synchronized (namedObjectMap)
		{
			namedObjectMap.add(name, object);
		}
	}

	/**
	 * Removes an the object tree from the GUI.
	 */
	void removeObjectTree(GUIObject obj)
	{
		synchronized (objectsMutex)
		{
			allObjects.remove(obj);
			for (String n : obj.getNameSet())
				removeObjectName(n, obj);
			for (GUIObject child : obj.getChildren())
			{
				child.setGUI(null);
				removeObjectTree(child);
			}
		}
	}

	void removeObjectName(String name, GUIObject object)
	{
		synchronized (namedObjectMap)
		{
			namedObjectMap.removeValue(name, object);
		}
	}

	/**
	 * Adds an action to the action queue.
	 * @param object the object that called this request.
	 * @param duration the duration of the actions in milliseconds.
	 * @param type the transition/easing type.
	 * @param actions the action to add.
	 */
	void addAnimation(GUIObject object, long duration, GUIEasingType type, GUIAnimation ... actions)
	{
		synchronized (actionQueueMap)
		{
			AnimationQueue<GUIObject> animQueue = actionQueueMap.get(object);
			if (animQueue == null)
			{
				animQueue = new AnimationQueue<GUIObject>(object);
				actionQueueMap.put(object, animQueue);
			}
			animQueue.add(duration, type, actions);
		}
	}

	/**
	 * Adds an action to the action queue.
	 * @param object the object that called this request.
	 * @param complete if true, completes the animation.
	 */
	void endAnimation(GUIObject object, boolean complete)
	{
		synchronized (actionQueueMap)
		{
			AnimationQueue<GUIObject> animQueue = actionQueueMap.get(object);
			if (animQueue != null)
			{
				if (complete)
					animQueue.finish();
				else
					animQueue.abort();
				actionQueueMap.remove(object);
			}
		}
	}

	/**
	 * @return true if the object is in the middle of an animation.
	 */
	boolean isAnimating(GUIObject object)
	{
		boolean out = false;
		synchronized (actionQueueMap)
		{
			out = actionQueueMap.containsKey(object);
		}
		return out;
	}

	/**
	 * Fires a GUI-specific event.
	 * @param object the object central to the event.
	 * @param type the event type.
	 */
	void fireGUIEvent(GUIObject object, String type)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(type);
		guiEvent.setOrigin(Origin.GUI);
		object.callEvent(guiEvent);
	}

	/**
	 * Checks if the GUI has potentially changed, and will need to be redrawn.
	 * @return true if so, false if not.
	 */
	public boolean isDirty()
	{
		return dirty;
	}

	/**
	 * Sets if this GUI has potentially changed, and will need to be redrawn.
	 * @param dirty true if so, false if not.
	 */
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
	}

	/**
	 * Adds a GUI Object to this GUI (plus children).
	 * @param obj the object to add.
	 */
	public void addObject(GUIObject obj)
	{
		synchronized (objectsMutex)
		{
			obj.setGUI(this);
			rootObjects.add(obj);
			addObjectTree(obj);
			setDirty(true);
		}
	}

	/**
	 * Removes a GUI Object from this GUI (plus children).
	 * @param obj the object to remove.
	 * @return true if removed, false if not.
	 */
	public boolean removeObject(GUIObject obj)
	{
		synchronized (objectsMutex)
		{
			if (rootObjects.remove(obj))
			{
				obj.setGUI(null);
				removeObjectTree(obj);
				return true;
			}
			setDirty(true);
		}
		return false;
	}

	/**
	 * Adds all objects in the GUI to a render set for rendering later.
	 * Depending on some object flags or their bounds, some objects or sets of objects are not rendered.
	 * @param set the set to add it to.
	 */
	public void addToRenderSet(GUIRenderSet set)
	{
		set.setGUIX(viewport.guiX);
		set.setGUIY(viewport.guiY);
		set.setGUIWidth(viewport.guiWidth);
		set.setGUIHeight(viewport.guiHeight);
		
		for (GUIObject object : rootObjects)
			object.addToRenderSet(set);
	}
	
	/**
	 * Adds a broadcast key event to this GUI.
	 * All keystrokes made to this GUI perform the associated {@link KeyStroke}.
	 * Actions are called in the order that they were bound. 
	 * @param keystroke the keystroke to add.
	 * @param action the associated action.
	 */
	public void bindBroadcastAction(GUIKeyStroke keystroke, GUIAction action)
	{
		keystrokeBroadcastMap.add(keystroke, action);
	}
	
	/**
	 * Removes a specific broadcast key event from this GUI.
	 * @param keystroke the associated keystroke.
	 * @param action the action to remove.
	 * @return true if removed successfully, false otherwise.
	 */
	public boolean unbindBroadcastAction(GUIKeyStroke keystroke, GUIAction action)
	{
		return keystrokeBroadcastMap.removeValue(keystroke, action);
	}
	
	/**
	 * Removes all specific broadcast key events from this GUI for a keystroke.
	 * @param keystroke the keystroke to remove.
	 * @return true if removed successfully, false otherwise.
	 */
	public boolean unbindAllBroadcastActions(GUIKeyStroke keystroke)
	{
		return keystrokeBroadcastMap.remove(keystroke) != null;
	}
	
	/**
	 * Removes all specific broadcast key events from this GUI for a keystroke.
	 */
	public void unbindAllBroadcastActions()
	{
		keystrokeBroadcastMap.clear();
	}
	
	/**
	 * Requests an object focus and fires object events.
	 * Fires events if and only if <code>object</code> is not
	 * the currently-focused object.
	 * @param object the object requesting focus.
	 */
	public void requestObjectFocus(GUIObject object)
	{
		if (object != currentObjectFocus)
		{
			if (currentObjectFocus != null)
				fireGUIEvent(currentObjectFocus, GUIObject.EVENT_BLUR);
			
			currentObjectFocus = object;
		
			if (currentObjectFocus != null)
				fireGUIEvent(currentObjectFocus, GUIObject.EVENT_FOCUS);
		}
	}
	
	/**
	 * Requests that an object be unfocused, but only if it
	 * was the one currently in focus. 
	 * @param object the object to unfocus.
	 */
	public void requestObjectUnfocus(GUIObject object)
	{
		if (getFocusedObject() == object)
			requestObjectFocus(null);
	}
	
	/**
	 * @return the object that currently has focus in this GUI. Can be null if no object has focus.
	 */
	public GUIObject getFocusedObject()
	{
		return currentObjectFocus;
	}
	
	/**
	 * Gets this GUI's viewport (this affects what is displayed).
	 * @return the GUI's current viewport.
	 */
	public Viewport getViewport()
	{
		return viewport;
	}
	
	/**
	 * @return the current theme used by the GUI. If no current theme, this returns null.
	 */
	public GUITheme getTheme()
	{
		return theme;
	}
	
	/**
	 * Sets the theme used by all objects.
	 * @param theme the theme to set.
	 */
	public void setTheme(GUITheme theme)
	{
		this.theme = theme;
	}
	
	/**
	 * Gets all GUI Objects. 
	 * @return a query result.
	 */
	public GUIQuery getAll()
	{
		synchronized (objectsMutex)
		{
			GUIQuery out = new GUIQuery();
			for (GUIObject obj : rootObjects)
				obj.getAllInTree(out);
			return out;
		}
	}
	
	/**
	 * Gets all GUI Objects that are in an animation. 
	 * @return a query result.
	 */
	public GUIQuery getAnimating()
	{
		synchronized (objectsMutex)
		{
			GUIQuery out = new GUIQuery();
			synchronized (actionQueueMap)
			{
				for (Map.Entry<GUIObject, AnimationQueue<GUIObject>> entry : actionQueueMap.entrySet())
					out.add(entry.getKey());
			}
			return out;
		}
	}
	
	/**
	 * Gets all GUI Objects with a matching name. 
	 * @param name the name to use.
	 * @return a query result.
	 */
	public GUIQuery getByName(String name)
	{
		synchronized (objectsMutex)
		{
			GUIQuery out = new GUIQuery();
			for (GUIObject obj : rootObjects)
				obj.getNameMatchesInTree(name, out);
			return out;
		}
	}
	
	/**
	 * Gets all GUI Objects with a matching name RegEx pattern. 
	 * @param pattern the pattern to use.
	 * @return a query result.
	 */
	public GUIQuery getByPattern(Pattern pattern)
	{
		synchronized (objectsMutex)
		{
			GUIQuery out = new GUIQuery();
			for (GUIObject obj : rootObjects)
				obj.getNamePatternMatchesInTree(pattern, out);
			return out;
		}
	}
	
	/**
	 * Gets all GUI Objects with a matching name.
	 * @param clazz the class type to search on. 
	 * @return a query result.
	 */
	public GUIQuery getByType(Class<?> clazz)
	{
		synchronized (objectsMutex)
		{
			GUIQuery out = new GUIQuery();
			for (GUIObject obj : rootObjects)
				obj.getTypeMatchesInTree(clazz, out);
			return out;
		}
	}
	
	/**
	 * Resets/clears the GUI state.
	 * This does NOT fire events.
	 * This consists of:
	 * <ul>
	 * <li>Current moused-over object.</li>
	 * <li>Current object the mouse button is pressed on.</li>
	 * <li>Current pressed mouse button.</li>
	 * <li>Current "drag state."</li>
	 * <li>Current focused object.</li>
	 * <li>Current modifiers for keys (used with broadcast).</li>
	 * <li>Current mouse enter state.</li>
	 * <li>Current mouse position X.</li>
	 * <li>Current mouse position Y.</li>
	 * <li>Current mouse movement X.</li>
	 * <li>Current mouse movement Y.</li>
	 * <li>Current mouse current object X.</li>
	 * <li>Current mouse current object Y.</li>
	 * </ul>
	 */
	public void clearState()
	{
		currentObjectMouseOver = null;
		currentObjectMouseDown = null;
		currentObjectMouseDownButton = 0;
		currentObjectMouseDragState = false;
		currentObjectFocus = null;
		currentKeyModifiers = 0;
		currentMouseEnterState = false;
		currentMousePositionX = -1;
		currentMousePositionY = -1;
		currentMouseMovementX = -1;
		currentMouseMovementY = -1;
		currentMouseObjectX = -1;
		currentMouseObjectY = -1;
	}
	
	/**
	 * Called to send a broadcast event by name.
	 * The event is only broadcast.
	 * @param name the generic code to send.
	 * @return true if handled, false if not.
	 */
	public boolean sendName(String name)
	{
		return fireBroadcastNamedEvent(name);
	}
	
	/**
	 * Called to send a generic input event.
	 * The event is first broadcast, then sent to the currently-focused object.
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * @param code the generic code to send.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendGenericInput(int code)
	{
		boolean handled = fireBroadcastGenericInputEvent(code);
		
		if (currentObjectFocus != null)
		{
			fireGenericInputEvent(currentObjectFocus, code);
			handled = true;
		}
		return handled;
	}
	
	/**
	 * Called to send a key press event.
	 * The key press is first broadcast, then sent to the currently-focused object.
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * This also sets the current key's mask internally, if the key has an associated mask. 
	 * @param keycode a key code to send.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendKeyPress(int keycode)
	{
		setKeyMask(keycode, false);
		
		boolean handled = fireBroadcastKeyEvent(keycode, false);
		
		if (currentObjectFocus != null)
		{
			fireKeyboardKeyEvent(currentObjectFocus, keycode, false);
			handled = true;
		}
		return handled;
	}
	
	/**
	 * Called to send a key release event.
	 * The key release is first broadcast, then sent to the currently-focused object. 
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * This also clears the current key's mask internally, if the key has an associated mask. 
	 * @param keycode a key code to send.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendKeyRelease(int keycode)
	{
		setKeyMask(keycode, true);

		boolean handled = fireBroadcastKeyEvent(keycode, true);

		if (currentObjectFocus != null)
		{
			fireKeyboardKeyEvent(currentObjectFocus, keycode, true);
			handled = true;
		}
		return handled;
	}
	
	/**
	 * Called to send a key type event.
	 * The key type is only sent to the currently-focused object. 
	 * @param keycode a key code to send.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendKeyType(int keycode)
	{
		if (currentObjectFocus != null)
		{
			fireKeyboardKeyTypedEvent(currentObjectFocus, keycode);
			return true;
		}
		return false;
	}
	
	/**
	 * Called to send a mouse button press type event.
	 * A button press is handled by the currently moused-over object. 
	 * @param mousebutton the mouse button id.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendMousePress(int mousebutton)
	{
		currentObjectMouseDragState = false;
		
		if (currentObjectMouseOver != null)
		{
			fireMouseButtonEvent(currentObjectMouseOver, mousebutton, false);
			currentObjectMouseDown = currentObjectMouseOver;
			currentObjectMouseDownButton = mousebutton;
			return true;
		}
		currentObjectMouseDown = null;
		return false;
	}

	/**
	 * Called to send a mouse button press type event.
	 * A button release is handled by the currently moused-over object.
	 * If the same object was previously the subject of a mouse press, a CLICK event is also sent to that object. 
	 * @param mousebutton the mouse button id.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendMouseRelease(int mousebutton)
	{
		if (currentObjectMouseDown != null)
		{
			if (currentObjectMouseOver != null)
				fireMouseButtonEvent(currentObjectMouseOver, mousebutton, true);
			if (currentObjectMouseDown == currentObjectMouseOver && !currentObjectMouseDragState)
				fireMouseClickEvent(currentObjectMouseDown, mousebutton);
			currentObjectMouseDown = null;
			currentObjectMouseDownButton = -1;
			currentObjectMouseDragState = false;
			return true;
		}
		return false;
	}

	/**
	 * Called to send a mouse button press type event.
	 * A mouse wheel change is handled by the currently focused object.
	 * @param units the amount of wheel units.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendMouseWheel(int units)
	{
		if (currentObjectFocus != null)
		{
			fireMouseWheelEvent(currentObjectFocus, units);
			return true;
		}
		return false;
	}

	/**
	 * Called to send a mouse move event.
	 * This sets the current mouse coordinates of this GUI, 
	 * which affects what the currently moused-over object is, and may also send MOUSE LEAVE or MOUSE ENTER events. 
	 * If a mouse button is already pressed on an object, a DRAG event is sent to it, but only if it handles it.
	 * A mouse move is also handled by the currently moused-over object.
	 * <p>NOTE: This is not affected by viewport. You need to calculate that.
	 * @param mouseX the mouse position, X-axis.
	 * @param mouseY the mouse position, Y-axis.
	 */
	public void sendMousePosition(int mouseX, int mouseY)
	{
		updateMouseCoordinateFields(mouseX, mouseY);
		updateMouseObjectEvents();
		
		if (currentObjectMouseDown != null)
		{
			currentObjectMouseDragState = currentObjectMouseDown.hasAction(GUIObject.EVENT_MOUSE_DRAG);
			if (currentObjectMouseDragState)
				fireMouseDragEvent(currentObjectMouseDown, currentObjectMouseDownButton);
		}
		else if (currentObjectMouseOver != null)
			fireMouseMoveEvent(currentObjectMouseOver);
	}

	/**
	 * Called to send a "mouse entered the GUI" event.
	 * A mouse enter sets the initial mouse position.
	 * @param mouseX the entry x-coordinate.
	 * @param mouseY the entry y-coordinate.
	 */
	public void sendMouseEnter(int mouseX, int mouseY)
	{
		updateMouseCoordinateFields(mouseX, mouseY);
		updateMouseObjectEvents();
		currentMouseEnterState = true;
	}

	/**
	 * Called to send a "mouse exited the GUI" event.
	 * A mouse exit is handled by the currently moused-over object, then clears the currently-moused over object.
	 */
	public void sendMouseExit()
	{
		if (currentObjectMouseOver != null)
			fireMouseLeaveEvent(currentObjectMouseOver);
	
		currentObjectMouseOver = null;
		currentObjectMouseDown = null;
		currentMouseEnterState = false;
	}

	/**
	 * Called to send a joystick button press type event.
	 * The button press is first broadcast, then sent to the currently-focused object. 
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * @param joystickId the game pad id.
	 * @param joystickButton the joystick button.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendGamepadPress(int joystickId, int joystickButton)
	{
		boolean handled = fireBroadcastGamepadButtonEvent(joystickId, joystickButton, false);

		if (currentObjectFocus != null)
		{
			fireGamepadButtonEvent(currentObjectFocus, joystickId, joystickButton, false);
			handled = true;
		}
		
		return handled;
	}

	/**
	 * Called to send a joystick button release type event.
	 * The button release is first broadcast, then sent to the currently-focused object. 
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * @param joystickId the game pad id.
	 * @param joystickButton the joystick button.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendGamepadRelease(int joystickId, int joystickButton)
	{
		boolean handled = fireBroadcastGamepadButtonEvent(joystickId, joystickButton, true);

		if (currentObjectFocus != null)
		{
			fireGamepadButtonEvent(currentObjectFocus, joystickId, joystickButton, true);
			handled = true;
		}
		
		return handled;
	}

	/**
	 * Called to send a joystick axis change type event.
	 * The axis change is only sent to the currently-focused object. 
	 * @param joystickId the game pad id.
	 * @param joystickAxisId the axis id.
	 * @param value the axis value.
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendGamepadAxisChange(int joystickId, int joystickAxisId, float value)
	{
		if (currentObjectFocus != null)
		{
			fireGamepadAxisEvent(currentObjectFocus, joystickId, joystickAxisId, value);
			return true;
		}
		return false;
	}

	/**
	 * Called to send a joystick axis tap type event.
	 * An "axis tap" is when a joystick axis is brought from a value in its deadzone to outside of it.
	 * The axis tap is first broadcast, then sent to the currently-focused object. 
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * @param joystickId the game pad id.
	 * @param joystickAxisId the axis id.
	 * @param position the axis position (true = not in deadzone).
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendGamepadAxisTapPress(int joystickId, int joystickAxisId, boolean position)
	{
		boolean handled = fireBroadcastGamepadTapEvent(joystickId, joystickAxisId, position, false);

		if (currentObjectFocus != null)
		{
			fireGamepadAxisTapEvent(currentObjectFocus, joystickId, joystickAxisId, position, false);
			handled = true;
		}
		
		return handled;
	}
	
	/**
	 * Called to send a joystick axis tap type event.
	 * An "axis tap" is when a joystick axis is brought from a value in its deadzone to outside of it.
	 * The axis tap is first broadcast, then sent to the currently-focused object. 
	 * If this is handled by an existing broadcast event, it is still sent to the focused object.
	 * @param joystickId the game pad id.
	 * @param joystickAxisId the axis id.
	 * @param position the axis position (true = not in deadzone).
	 * @return true if handled by a component, false if not.
	 */
	public boolean sendGamepadAxisTapRelease(int joystickId, int joystickAxisId, boolean position)
	{
		boolean handled = fireBroadcastGamepadTapEvent(joystickId, joystickAxisId, position, true);

		if (currentObjectFocus != null)
		{
			fireGamepadAxisTapEvent(currentObjectFocus, joystickId, joystickAxisId, position, true);
			handled = true;
		}
		
		return handled;
	}
	
	/**
	 * Updates this GUI.
	 * @param timeslice the time slice amount to update the animations by.
	 */
	public void update(long timeslice)
	{
		updateAnimations(timeslice);
	}

	/**
	 * Updates all of the animation nodes in the GUI by an amount of time.
	 * @param timeslice amount of time in time units.
	 */
	private void updateAnimations(long timeslice)
	{
		if (timeslice <= 0L) 
			return;
		
		synchronized (actionQueueMap)
		{
			Iterator<Map.Entry<GUIObject, AnimationQueue<GUIObject>>> it = actionQueueMap.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<GUIObject, AnimationQueue<GUIObject>> entry = it.next();
				if (entry.getValue().isDone())
					it.remove();
			}
		}
	}

	/**
	 * Calculates mouse coordinates for events.
	 */
	private void updateMouseCoordinateFields(int positionX, int positionY)
	{
		if (currentMouseEnterState)
		{
			currentMouseMovementX = positionX - currentMousePositionX;
			currentMouseMovementY = positionY - currentMousePositionY;
		}
		else
		{
			currentMouseMovementX = 0;
			currentMouseMovementY = 0;
		}

		currentMousePositionX = positionX;
		currentMousePositionY = positionY;

		GUIObject obj = currentObjectMouseDown != null 
			? currentObjectMouseDown
			: (currentObjectMouseOver != null ? currentObjectMouseOver : null);
		
		if (obj != null)
		{
			GUIBounds rect = obj.getBounds();
			currentMouseObjectX = positionX - (int)rect.x; 
			currentMouseObjectY = positionY - (int)rect.y; 
		}
		else
		{
			currentMouseObjectX = -1;
			currentMouseObjectY = -1;
		}
		
	}
	
	/**
	 * Sets/releases a key mask, if it is a maskable key (CTRL, SHIFT, ALT, ALTGRAPH, MASK).
	 * @param keycode the input keycode.
	 * @param release true if key released, false otherwise.
	 */
	private void setKeyMask(int keycode, boolean release)
	{
		if (release) switch (keycode)
		{
			case GUIInputConstants.KEY_CONTROL:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_CTRL; 
				break;
			case GUIInputConstants.KEY_ALT:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_ALT; 
				break;
			case GUIInputConstants.KEY_ALT_GRAPH:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_ALT_GRAPH; 
				break;
			case GUIInputConstants.KEY_META:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_META; 
				break;
			case GUIInputConstants.KEY_SHIFT:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_SHIFT; 
				break;
			case GUIInputConstants.KEY_WINDOWS:
				currentKeyModifiers &= ~GUIKeyStroke.MASK_WIN; 
				break;
		}
		else switch (keycode)
		{
			case GUIInputConstants.KEY_CONTROL:
				currentKeyModifiers |= GUIKeyStroke.MASK_CTRL; 
				break;
			case GUIInputConstants.KEY_ALT:
				currentKeyModifiers |= GUIKeyStroke.MASK_ALT; 
				break;
			case GUIInputConstants.KEY_ALT_GRAPH:
				currentKeyModifiers |= GUIKeyStroke.MASK_ALT_GRAPH; 
				break;
			case GUIInputConstants.KEY_META:
				currentKeyModifiers |= GUIKeyStroke.MASK_META; 
				break;
			case GUIInputConstants.KEY_SHIFT:
				currentKeyModifiers |= GUIKeyStroke.MASK_SHIFT; 
				break;
			case GUIInputConstants.KEY_WINDOWS:
				currentKeyModifiers |= GUIKeyStroke.MASK_WIN; 
				break;
		}
		
	}
	
	/**
	 * Fires a broadcast named event.
	 * @param name the name used.
	 * @return true if a broadcast event was handled, false if not.
	 */
	private boolean fireBroadcastNamedEvent(String name)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(GUIObject.EVENT_NAMED);
		guiEvent.setOrigin(Origin.GUI);
		
		Deque<GUIAction> queue = namedBroadcastMap.get(name);
		if (queue != null && !queue.isEmpty()) 
		{
			for (GUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}

	/**
	 * Fires a broadcast generic input event.
	 * @param code the code used.
	 * @return true if a broadcast event was handled, false if not.
	 */
	private boolean fireBroadcastGenericInputEvent(int code)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(GUIObject.EVENT_GENERIC_INPUT);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setCode(code);
		
		Deque<GUIAction> queue = keystrokeBroadcastMap.get(GUIKeyStroke.createGeneric(code));
		if (queue != null && !queue.isEmpty()) 
		{
			for (GUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}

	/**
	 * Fires a broadcast key event.
	 * @param keycode the keycode used.
	 * @param release true if release, false if not.
	 * @return true if a broadcast event was handled, false if not.
	 */
	private boolean fireBroadcastKeyEvent(int keycode, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(release ? GUIObject.EVENT_KEY_RELEASE : GUIObject.EVENT_KEY_PRESS);
		guiEvent.setRelease(release);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setKeyCode(keycode);
		
		Deque<GUIAction> queue = keystrokeBroadcastMap.get(GUIKeyStroke.createKey(currentKeyModifiers, keycode, release));
		if (queue != null && !queue.isEmpty()) 
		{
			for (GUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}

	/**
	 * Fires a broadcast joystick button event.
	 * @param id the joystick id used.
	 * @param button the button used.
	 * @param release true if release, false if not.
	 * @return true if a broadcast event was handled, false if not.
	 */
	private boolean fireBroadcastGamepadButtonEvent(int id, int button, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(release ? GUIObject.EVENT_GAMEPAD_RELEASE : GUIObject.EVENT_GAMEPAD_PRESS);
		guiEvent.setRelease(release);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadButton(id, button);
		
		Deque<GUIAction> queue = keystrokeBroadcastMap.get(GUIKeyStroke.createGamepad(currentKeyModifiers, button, release));
		if (queue != null && !queue.isEmpty()) 
		{
			for (GUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}

	/**
	 * Fires a broadcast joystick axis tap event.
	 * @param id the joystick id used.
	 * @param axisId the axis used.
	 * @param positive true if axis tap was in a "positive" valued direction, false if not.
	 * @param release true if release, false if not.
	 * @return true if the event was handled, false if not.
	 */
	private boolean fireBroadcastGamepadTapEvent(int id, int axisId, boolean positive, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(null);
		guiEvent.setType(release ? GUIObject.EVENT_GAMEPAD_TAP_RELEASE : GUIObject.EVENT_GAMEPAD_TAP_PRESS);
		guiEvent.setRelease(release);
		guiEvent.setRelease(release);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadAxisTap(id, axisId, positive);
		
		Deque<GUIAction> queue = keystrokeBroadcastMap.get(GUIKeyStroke.createGamepadTap(axisId, positive));
		if (queue != null && !queue.isEmpty()) 
		{
			for (GUIAction action : queue)
				action.call(guiEvent);
			return true;
		}
		return false;
	}

	/**
	 * Fires a generic input event to an object.
	 * @param object the object central to the event.
	 * @param code the input code.
	 */
	private void fireGenericInputEvent(GUIObject object, int code)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_GENERIC_INPUT);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setCode(code);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a keyboard typed event to an object.
	 * @param object the object central to the event.
	 * @param keyCode the key code.
	 * @param release true if this is a key release, false if a press. 
	 */
	private void fireKeyboardKeyEvent(GUIObject object, int keyCode, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(release ? GUIObject.EVENT_KEY_RELEASE : GUIObject.EVENT_KEY_PRESS);
		guiEvent.setRelease(release);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setKeyCode(keyCode);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a keyboard typed event to an object.
	 * @param object the object central to the event.
	 * @param keyCode the key code.
	 */
	private void fireKeyboardKeyTypedEvent(GUIObject object, int keyCode)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_KEY_TYPE);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setKeyCode(keyCode);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse over event to an object.
	 * @param object the object central to the event.
	 */
	private void fireMouseMoveEvent(GUIObject object)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_MOUSE_MOVE);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse over event to an object.
	 * @param object the object central to the event.
	 */
	private void fireMouseOverEvent(GUIObject object)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_MOUSE_OVER);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse leave event to an object.
	 * @param object the object central to the event.
	 */
	private void fireMouseLeaveEvent(GUIObject object)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_MOUSE_LEAVE);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse button click event to an object.
	 * @param object the object central to the event.
	 * @param button the mouse button id.
	 */
	private void fireMouseClickEvent(GUIObject object, int button)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_MOUSE_CLICK);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseButton(button);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse dragged event to an object.
	 * @param object the object central to the event.
	 * @param button the mouse button id.
	 */
	private void fireMouseDragEvent(GUIObject object, int button)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_MOUSE_DRAG);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseButton(button);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse pressed event to an object.
	 * @param object the object central to the event.
	 * @param button the mouse button id.
	 * @param release true if this is a button release, false if a press. 
	 */
	private void fireMouseButtonEvent(GUIObject object, int button, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(release ? GUIObject.EVENT_MOUSE_RELEASE : GUIObject.EVENT_MOUSE_PRESS);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseButton(button);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}
	
	/**
	 * Fires a mouse wheel event to an object.
	 * @param object the object central to the event.
	 * @param units the amount of wheel movement (can be positive or negative). 
	 */
	private void fireMouseWheelEvent(GUIObject object, int units)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_MOUSE_WHEEL);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setKeyModifier(currentKeyModifiers);
		guiEvent.setMouseWheelUnits(units);
		guiEvent.setMouseUnits(
			currentMousePositionX,
			currentMousePositionY,
			currentMouseMovementX,
			currentMouseMovementY,
			currentMouseObjectX,
			currentMouseObjectY
		);
		object.callEvent(guiEvent);
	}

	/**
	 * Fires a joystick button pressed event to an object.
	 * @param object the object central to the event.
	 * @param joystickId the joystick id.
	 * @param buttonCode the joystick button code.
	 * @param release true if a release, false if not.
	 */
	private void fireGamepadButtonEvent(GUIObject object, int joystickId, int buttonCode, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(release ? GUIObject.EVENT_GAMEPAD_TAP_RELEASE : GUIObject.EVENT_GAMEPAD_TAP_PRESS);
		guiEvent.setRelease(release);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadButton(joystickId, buttonCode);
		object.callEvent(guiEvent);
	}

	/**
	 * Fires a joystick axis change event to an object.
	 * @param object the object central to the event.
	 * @param joystickId the joystick id.
	 * @param axisTypeId the axis type id. 
	 * @param value the value.
	 */
	private void fireGamepadAxisEvent(GUIObject object, int joystickId, int axisTypeId, float value)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(GUIObject.EVENT_GAMEPAD_AXIS);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadAxis(joystickId, axisTypeId, value);
		object.callEvent(guiEvent);
	}

	/**
	 * Fires a joystick axis tap event to an object.
	 * @param object the object central to the event.
	 * @param joystickId the joystick id.
	 * @param axisTypeId the axis type id. 
	 * @param positive if true, positive, false if negative.
	 * @param release true if a release, false if not.
	 */
	private void fireGamepadAxisTapEvent(GUIObject object, int joystickId, int axisTypeId, boolean positive, boolean release)
	{
		GUIEvent guiEvent = new GUIEvent();
		guiEvent.setGUI(this);
		guiEvent.setObject(object);
		guiEvent.setType(release ? GUIObject.EVENT_GAMEPAD_TAP_RELEASE : GUIObject.EVENT_GAMEPAD_TAP_PRESS);
		guiEvent.setRelease(release);
		guiEvent.setOrigin(Origin.INPUT);
		guiEvent.setGamepadAxisTap(joystickId, axisTypeId, positive);
		object.callEvent(guiEvent);
	}

	/**
	 * Updates objects affected by mouse movement.
	 */
	private void updateMouseObjectEvents()
	{
		GUIObject finalObject = null;
		
		for (GUIObject object : allObjects)
		{
			if (!object.isVisible())
				continue;

			if (!object.isEnabled())
				continue;
			
			if (object.isInert())
				continue;

			if (!doMouseTest(object))
				continue;
			
			if (finalObject == null)
				finalObject = object;
		}
		
		if (finalObject != currentObjectMouseOver && currentObjectMouseOver != null)
			fireMouseLeaveEvent(currentObjectMouseOver);
	
		if (finalObject != currentObjectMouseOver && finalObject != null)
			fireMouseOverEvent(finalObject);
	
		currentObjectMouseOver = finalObject;
	}

	/**
	 * Checks if the mouse cursor is inside a particular object (rendered area).
	 * @param object the object to test.
	 * @return <code>true</code> if inside, <code>false</code> if not.
	 */
	private boolean doMouseTest(GUIObject object)
	{
		float mx = currentMousePositionX;
		float my = currentMousePositionY;
		return mx > object.getRenderPositionX() 
			&& mx < object.getRenderPositionX() + object.getRenderWidth()
			&& my > object.getRenderPositionY()
			&& my < object.getRenderPositionY() + object.getRenderHeight();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (GUIObject object : rootObjects)
			toStringRecurse(sb, "", object);
		return sb.toString();
	}
	
	private void toStringRecurse(StringBuilder sb, String tabString, GUIObject object)
	{
		sb.append(tabString).append(object.toString()).append('\n');
		for (GUIObject child : object.getChildren())
			toStringRecurse(sb, tabString + "\t", child);
	}

	/**
	 * A viewport "camera" that describes how or what is displayed of the GUI.
	 * <p>This affects what is visible on screen as well as how to render it in the canvas.
	 * <p>REMEMBER: In GUIs, (0,0) is upper left.
	 */
	public static class Viewport
	{
		/** GUI Origin X-coordinate (in GUI coordinates). */
		private float guiX;
		/** GUI Origin Y-coordinate (in GUI coordinates). */
		private float guiY;
		/** GUI Viewport width (in GUI coordinates). */
		private float guiWidth;
		/** GUI Viewport width (in GUI coordinates). */
		private float guiHeight;
		
		/**
		 * Creates a new GUI viewport.
		 * @param guiX GUI Origin X-coordinate (in GUI coordinates).
		 * @param guiY GUI Origin Y-coordinate (in GUI coordinates).
		 * @param guiWidth GUI Viewport width (in GUI coordinates).
		 * @param guiHeight GUI Viewport width (in GUI coordinates).
		 */
		public Viewport(float guiX, float guiY, float guiWidth, float guiHeight)
		{
			setBounds(guiX, guiY, guiWidth, guiHeight);
		}

		public void setBounds(float guiX, float guiY, float guiWidth, float guiHeight)
		{
			this.guiX = guiX;
			this.guiY = guiY;
			this.guiWidth = guiWidth;
			this.guiHeight = guiHeight;
		}
		
		public float getGUIX()
		{
			return guiX;
		}

		public void setGUIX(float guiX)
		{
			this.guiX = guiX;
		}

		public float getGUIY()
		{
			return guiY;
		}

		public void setGUIY(float guiY)
		{
			this.guiY = guiY;
		}

		public float getGUIWidth()
		{
			return guiWidth;
		}

		public void setGUIWidth(float guiWidth)
		{
			this.guiWidth = guiWidth;
		}

		public float getGUIHeight()
		{
			return guiHeight;
		}

		public void setGUIHeight(float guiHeight)
		{
			this.guiHeight = guiHeight;
		}

	}

	/**
	 * Describes an animation to be performed on an object.
	 * @param <G> the GUI object type.
	 */
	public interface Animation<G extends GUIObject>
	{
		/** 
		 * Returns a mutable action state that is used by action queues.
		 * Requires pairing with an object. 
		 * @param object the object to affect.
		 * @return a new action state.
		 */
		public abstract AnimationState<G> createState(G object);
	}

	/**
	 * Animation state.
	 * @param <T> the GUI object type.
	 */
	public static abstract class AnimationState<T extends GUIObject>
	{
		protected GUIObject object;
		
		protected AnimationState(GUIObject object)
		{
			this.object = object;
		}
		
		/**
		 * Updates the object on this state using the progress factor provided.
		 * This factor is already calculated from easing.
		 * @param progressScalar progress scalar (0 to 1).
		 * @param start if this is the first call.
		 * @param end if this is the last call.
		 */
		public abstract void update(double progressScalar, boolean start, boolean end);
		
	}

	/**
	 * Describes an animation to be performed on an object.
	 * @param <G> the GUI object type.
	 */
	private static class AnimationGroup<G extends GUIObject>
	{
		/** Object reference. */
		protected G object;
		/** Animation duration in milliseconds. */
		protected long duration;
		/** Animation easing type. */
		protected GUIEasingType easing;
		/** Animation list. */
		protected Animation<G>[] animations;

		/**
		 * Creates a new animation group.
		 * @param object the object being animated.
		 * @param duration the duration in time units.
		 * @param easing the easing function.
		 * @param animations the animations to perform.
		 */
		@SafeVarargs
		AnimationGroup(G object, long duration, GUIEasingType easing, Animation<G> ... animations)
		{
			this.object = object;
			this.duration = duration;
			this.easing = easing;
			this.animations = animations;
		}
		
		/**
		 * @return the object associated with this animation state.
		 */
		public G getObject()
		{
			return object;
		}

		/**
		 * @return the duration of this animation state in time units.
		 */
		public long getDuration()
		{
			return duration;
		}

		/**
		 * @return the easing of this animation state.
		 */
		public GUIEasingType getEasing()
		{
			return easing;
		}

		/**
		 * @return the animations on this animation state.
		 */
		public Animation<G>[] getAnimations()
		{
			return animations;
		}

	}

	/**
	 * Describes an animation to be performed on an object.
	 * @param <T> the GUI object type.
	 */
	private static class AnimationGroupState<T extends GUIObject>
	{
		/** Animation group. */
		protected AnimationGroup<T> animationGroup;
		/** Animation states. */
		protected AnimationState<T>[] animationStates;
		/** Animation progress in time units. */
		protected long progress;
		
		/** Start flag. */
		protected boolean start;
		
		/** 
		 * Returns a mutable action state that is used by action queues.
		 * Requires pairing with an object. 
		 */
		@SuppressWarnings("unchecked")
		AnimationGroupState(AnimationGroup<T> animationGroup)
		{
			this.progress = 0;
			this.animationGroup = animationGroup;

			Animation<T>[] anims = animationGroup.getAnimations();
			this.animationStates = new AnimationState[anims.length];
			int x = 0;
			for (Animation<T> anim : anims)
				this.animationStates[x++] = anim.createState(animationGroup.getObject());
		}
		
		/**
		 * Updates the object animations on this group state.
		 * @param timeslice the time slice in time units.
		 * @return the leftover time.
		 */
		public long update(long timeslice)
		{
			long duration = animationGroup.getDuration();
			long next = Math.min(progress + timeslice, duration);
			long leftover = (progress + timeslice) - next;
			progress = next;
			
			GUIEasingType trans = animationGroup.getEasing();
			double p = trans.getScaling(duration > 0.0 ? Math.min((double)progress / duration, 1.0) : 1.0);
			
			for (AnimationState<T> state : animationStates)
				state.update(p, !start, p == 1.0);
			
			if (!start)
				start = !start;
			
			return leftover;
		}

	}

	/**
	 * Queue that holds the current action state plus the rest in the list.
	 * @param <G> the GUI object type.
	 */
	private static class AnimationQueue<G extends GUIObject>
	{
		/** Object reference. */
		protected G object;
		/** Current action. */
		protected AnimationGroupState<G> currentAction;
		/** Rest of actions. */
		protected Deque<AnimationGroup<G>> animationList;
		
		/**
		 * Creates the action state queue.
		 */
		AnimationQueue(G object)
		{
			this.object = object;
			animationList = new LinkedList<AnimationGroup<G>>();
			currentAction = null;
		}
		
		/**
		 * Adds an animation state to this animation queue.
		 * @param duration
		 * @param type
		 * @param actions
		 */
		@SuppressWarnings("unchecked")
		public void add(long duration, GUIEasingType type, Animation<G> ... actions)
		{
			animationList.add(new AnimationGroup<G>(object, duration, type, actions));
		}
		
		/**
		 * Updates action states.
		 * @param timeslice the timeslice amount to update.
		 */
		public synchronized void update(long timeslice)
		{
			long leftover = timeslice;
			while (leftover > 0L && !isDone())
			{
				if (currentAction == null)
				{
					if (!animationList.isEmpty())
						currentAction = new AnimationGroupState<G>(animationList.pollFirst());
				}
				leftover = currentAction.update(timeslice);
				if (leftover > 0L)
					currentAction = null;
			}
		}
		
		/**
		 * Finishes out the animation.
		 */
		public synchronized void finish()
		{
			update(Integer.MAX_VALUE);
		}
		
		/**
		 * Stops the animation.
		 */
		public synchronized void abort()
		{
			currentAction = null;
			animationList.clear();
		}
		
		/**
		 * Is this done?
		 * @return true if so, false if not.
		 */
		public boolean isDone()
		{
			return currentAction == null && animationList.isEmpty();
		}
		
	}

}
