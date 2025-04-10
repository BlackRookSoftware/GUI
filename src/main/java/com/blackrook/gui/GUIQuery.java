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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import com.blackrook.gui.object.GUILabel;
import com.blackrook.gui.object.GUIToggleable;
import com.blackrook.gui.object.GUIValueField;
import com.blackrook.gui.struct.HashDequeMap;

/**
 * The results of a scene query.
 * @author Matthew Tropiano
 */
public final class GUIQuery implements Iterable<GUIObject>
{
	/** Hash of objects. */
	private Set<GUIObject> objectHash;
	/** Map of name to object. */
	private HashDequeMap<String, GUIObject> nameMap;
	/** List of objects. */
	private List<GUIObject> objectList;

	/**
	 * Creates a new query set. 
	 */
	GUIQuery()
	{
		this(4);
	}

	/**
	 * Creates a new query set.
	 * @param capacity the initial capacity.
	 */
	GUIQuery(int capacity)
	{
		this.objectHash = new HashSet<>(capacity, 1f);
		this.objectList = new ArrayList<>(capacity);
		this.nameMap = new HashDequeMap<>(capacity, 1f);
	}

	/** 
	 * Adds an object to the result, but only if it isn't in the set.
	 * @return true if added, false if not. 
	 */
	final boolean add(GUIObject object)
	{
		if (object == null || objectHash.contains(object))
			return false;
		objectHash.add(object);
		objectList.add(object);
		for (String name : object.getNameSet())
			nameMap.add(name, object);
		return true;
	}

	/**
	 * @return the amount of objects in this query.
	 */
	public int size()
	{
		return objectList.size();
	}

	/**
	 * @return true if there are no objects in this query, false if one or more.
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public Iterator<GUIObject> iterator()
	{
		return objectList.iterator();
	}

	/**
	 * Wraps a set of objects in a new query, where its contents
	 * consist of the provided objects, in the same order.
	 * @param object the object to include in the query selection.
	 * @return a new query result with all of the provided objects.
	 */
	public static GUIQuery wrap(GUIObject object)
	{
		GUIQuery out = new GUIQuery();
		out.add(object);
		return out;
	}
	
	/**
	 * Wraps a set of objects in a new query, where its contents
	 * consist of the provided objects, in the same order.
	 * @param objects the objects to include in the query selection.
	 * @return a new query result with all of the provided objects.
	 */
	public static GUIQuery wrap(GUIObject ... objects)
	{
		GUIQuery out = new GUIQuery(objects.length);
		if (objects != null) for (GUIObject object : objects)
			out.add(object);
		return out;
	}
	
	/**
	 * Wraps a set of objects in a new query, where its contents
	 * consist of the provided objects, in the same order.
	 * @param objects the objects to include in the query selection.
	 * @return a new query result with all of the provided objects.
	 */
	public static GUIQuery wrap(Iterable<GUIObject> objects)
	{
		GUIQuery out = new GUIQuery();
		if (objects != null) for (GUIObject object : objects)
			out.add(object);
		return out;
	}
	
	/**
	 * Wraps a set of objects in a new query, where its contents
	 * consist of the provided objects, in the same order.
	 * @param query the query of objects to include in the query selection.
	 * @return a new query result with all of the provided objects.
	 */
	public static GUIQuery wrap(GUIQuery query)
	{
		return query.copy();
	}
	
	/**
	 * Copies this query, returning a new query with the same objects.
	 * @return a new query.
	 */
	public GUIQuery copy()
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
			out.add(object);
		return out;
	}
	
	/**
	 * Gets an object in this query wrapped in a query.
	 * The order of the objects in the query is the order in which they were added.
	 * @param index the index of the desired object.
	 * @return a new query result that is the desired object.
	 */
	public GUIQuery get(int index)
	{
		return wrap(getObject(index));
	}
	
	/**
	 * Gets an GUIObject in this query.
	 * The order of the objects in the query is the order in which they were added.
	 * @param index the index of the desired object.
	 * @return the GUIObject at the desired index, or null if no object at that index.
	 */
	public GUIObject getObject(int index)
	{
		return objectList.get(index);
	}
	
	/**
	 * Gets every n-th object in this query as a new query.
	 * For example, if <code>n</code> is 3, this returns every multiple-of-3-indexed object.
	 * Conveience method for <code>getOffsetAndModulo(0, n)</code>. 
	 * @param n the multiple stepping.
	 * @return a new query result.
	 */
	public GUIQuery getEveryMultiple(int n)
	{
		return getOffsetAndModulo(0, n);
	}
	
	/**
	 * Gets each even-indexed object in this query as a new query.
	 * Conveience method for <code>getOffsetAndModulo(0, 2)</code>. 
	 * @return a new query result.
	 */
	public GUIQuery getEvens()
	{
		return getOffsetAndModulo(0, 2);
	}
	
	/**
	 * Gets each odd-indexed object in this query as a new query.
	 * Conveience method for <code>getOffsetAndModulo(1, 2)</code>. 
	 * @return a new query result.
	 */
	public GUIQuery getOdds()
	{
		return getOffsetAndModulo(1, 2);
	}
	
	/**
	 * Gets every object at the index pattern specified in this query as a new query.
	 * For example, if <code>offset</code> is 1 and <code>step</code> is 3, this returns every 3rd object
	 * shifted after the first object.
	 * @param offset the starting offset.
	 * @param modulo the modulo after the offset.
	 * @return a new query result.
	 */
	public GUIQuery getOffsetAndModulo(int offset, int modulo)
	{
		GUIQuery out = new GUIQuery((size() - offset) / modulo);
		for (int i = 0; i < size(); i++)
			if ((i - offset) % modulo == 0)
				out.add(getObject(i));
		return out;
	}
	
	/**
	 * Returns the first object in this query wrapped in a query.
	 * Convenience method for <code>get(0)</code>.
	 * If there are no objects in this query, it will return an empty query. 
	 * @return a new query result with the first object in this query.
	 */
	public GUIQuery getFirst()
	{
		return get(0);
	}
	
	/**
	 * Returns the last object in this query wrapped in a query.
	 * Convenience method for <code>get(size()-1)</code>. 
	 * @return a new query result with the last object in this query.
	 */
	public GUIQuery getLast()
	{
		return get(size() - 1);
	}
	
	/**
	 * Gets all GUI Objects that match all of the names provided in this query.
	 * IF no names are provided, this returns itself. 
	 * @param names the names to search for.
	 * @return a new query result.
	 */
	public GUIQuery getByName(String ... names)
	{
		GUIQuery out = this;
		for (String name : names)
			 out = wrap(out.nameMap.get(name));
		return out;
	}

	/**
	 * Gets all GUI Objects with a matching name regex pattern in this query. 
	 * @param pattern the pattern to use.
	 * @return a new query result.
	 */
	public GUIQuery getByPattern(Pattern pattern)
	{
		GUIQuery out = new GUIQuery();
		
		for (Map.Entry<String, Deque<GUIObject>> entry : nameMap.entrySet())
		{
			String name = entry.getKey();
			if (pattern.matcher(name).matches()) for (GUIObject object : nameMap.get(name))
				out.add(object);
		}
		return out;
	}
	
	/**
	 * Gets a new query result of all objects in the query with the same class.
	 * @param clazz the class type to scan for.
	 * @return a new query result where all of the results are castable as <code>clazz</code>.
	 */
	public GUIQuery getByType(Class<?> clazz)
	{
		GUIQuery out = new GUIQuery();
		for (GUIObject object : this)
			if (clazz.isInstance(object))
				out.add(object);
		return out;
	}

	/**
	 * Gets a new query result of all objects in the query with a specific layout attribute.
	 * @param layoutAttribute the layout attribute to test for.
	 * @return a new query result.
	 */
	public GUIQuery getByAttrib(Object layoutAttribute)
	{
		GUIQuery out = new GUIQuery();
		for (GUIObject object : this)
		{
			boolean test = layoutAttribute == null || object.getLayoutAttrib() == null 
					? layoutAttribute == object.getLayoutAttrib()
					: layoutAttribute.equals(object.getLayoutAttrib());
			if (test)
				out.add(object);
		}
		return out;		
	}

	/**
	 * Gets a new query result containing objects that are currently animating.
	 * @return a new query result that has objects that are in an animation.
	 */
	public GUIQuery getAnimating()
	{
		GUIQuery out = new GUIQuery();
		for (GUIObject object : this)
			if (object.isAnimating())
				out.add(object);
		return out;
	}

	/**
	 * Gets all children of all of the objects in this query.
	 * @return a new query result.
	 */
	public GUIQuery getChildren()
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
			for (GUIObject child : object.getChildren())
				out.add(child);
		return out;
	}
	
	/**
	 * Gets all descendants of all of the objects in this query.
	 * @return a new query result.
	 */
	public GUIQuery getDescendants()
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
			getDescendantsRecurse(out, object);
		return out;
	}
	
	// recursive accumulator for getDescendants.
	private void getDescendantsRecurse(GUIQuery query, GUIObject parent)
	{
		query.add(parent);
		for (GUIObject child : parent.getChildren())
			getDescendantsRecurse(query, child);
	}
	
	/**
	 * Gets all siblings of all of the objects in this query.
	 * Does not get siblings of objects without parents.
	 * @return a new query result.
	 */
	public GUIQuery getSiblings()
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
		{
			if (object.getParent() != null)
			{
				for (GUIObject child : object.getParent().getChildren())
					if (child != object) 
						out.add(child);
			}
		}
		return out;
	}
	
	/**
	 * Gets all parents of all of the objects in this query.
	 * @return a new query result.
	 */
	public GUIQuery getParents()
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
			out.add(object.getParent());
		return out;
	}
	
	/**
	 * Creates a new query result that is the union of two queries.
	 * NOTE: There are never duplicates in GUIQueries.
	 * @param query the query to unify with this one.
	 * @return a new query result with both sets of objects.
	 */
	public GUIQuery getUnion(GUIQuery query)
	{
		GUIQuery out = new GUIQuery(size() + query.size());
		for (GUIObject object : this)
			out.add(object);
		for (GUIObject object : query)
			out.add(object);
		return out;
	}

	/**
	 * Creates a new query result that is the intersection of two queries
	 * (both objects must exist in this query and the other to be included).
	 * @param query the query to intersect with this one.
	 * @return a new query result consisting of objects that are in both queries.
	 */
	public GUIQuery getIntersection(GUIQuery query)
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
			if (query.objectHash.contains(object))
				out.add(object);
		return out;
	}

	/**
	 * Creates a new query result that is the objects in this query
	 * minus the ones present in another.
	 * @param query the query to subtract from this one.
	 * @return a new query result with objects in this query minus the provided query.
	 */
	public GUIQuery getDifference(GUIQuery query)
	{
		GUIQuery out = new GUIQuery(size());
		for (GUIObject object : this)
			if (!query.objectHash.contains(object))
				out.add(object);
		return out;
	}

	/**
	 * Creates a new query result that is the union of the objects in this query and another,
	 * minus the ones present in both. The is essentially the union minus the intersection.
	 * @param query the query to XOR with this one.
	 * @return a new query result with objects in this query and the provided one, but not both.
	 */
	public GUIQuery getXOr(GUIQuery query)
	{
		GUIQuery out = new GUIQuery(size() + query.size());
		for (GUIObject object : this)
			if (!query.objectHash.contains(object))
				out.add(object);
		for (GUIObject object : query)
			if (!this.objectHash.contains(object))
				out.add(object);
		return out;
	}

	/**
	 * Gets a sublist of this query result as another query.
	 * @param startIndex the starting index of this query, INCLUSIVE.
	 * @param endIndex the ending index, EXCLUSIVE. If this is past the end of the query, this is {@link #size()}.
	 * @return a new query result that is a sublist of the objects in this result.
	 */
	public GUIQuery getSubQuery(int startIndex, int endIndex)
	{
		int end = Math.min(endIndex, size());
		int start = Math.max(startIndex, 0);
		GUIQuery out = new GUIQuery(end - start);
		for (int i = start; i < end; i++)
			out.add(getObject(i));
		return out;
	}

	/**
	 * Gets a new query result that is a random sub-selection of 
	 * objects in this query. 
	 * @param random the random number generator to use.
	 * @return a new query result that is a random sub-selection of objects
	 * in this query. 
	 */
	public GUIQuery getRandom(Random random)
	{
		return getRandomSample(random, 0.5f);
	}

	/**
	 * Gets a new query result that is a random sub-selection of 
	 * objects in this query, weighted by a chance to select each one.
	 * @param random the random number generator to use.
	 * @param chance the random chance factor. if 0 or less, picks nothing.
	 * if 1 or greater, picks EVERYTHING. No random numbers are generated in these cases.
	 * @return a new query result that is a random sub-selection of objects
	 * in this query. 
	 */
	public GUIQuery getRandomSample(Random random, float chance)
	{
		if (chance <= 0f)
			return wrap();
		else if (chance >= 1f)
			return wrap(this);
		
		GUIQuery out = new GUIQuery();
		for (int i = 0; i < size(); i++)
			if (random.nextFloat() < chance) 
				out.add(getObject(i));
		return out;
	}

	/**
	 * Gets a new query result that is a random sub-selection of 
	 * a specific amount of objects in this query. 
	 * @param random the random number generator to use.
	 * @param objects the number of objects to pick at random.
	 * @return a new query result that is a random sub-selection of objects
	 * in this query, with size <code>objects</code>.
	 */
	public GUIQuery getRandomAmount(Random random, int objects)
	{
		GUIQuery out = wrap(this);
		if (objects <= 0)
			return new GUIQuery();

		out.shuffle(random);
		
		int start = random.nextInt((size() - objects) + 1);
		return out.getSubQuery(start, start + objects);
	}

	/**
	 * Gets a new query result containing objects with a matching toggle state. 
	 * Only works on GUIToggleable objects.
	 * @param state the set state to test for.
	 * @return a new query result that has objects with a matching toggle state.
	 */
	public GUIQuery getHavingToggleState(boolean state)
	{
		GUIQuery out = new GUIQuery();
		for (int i = 0; i < size(); i++)
		{
			GUIObject obj = getObject(i);
			if (obj instanceof GUIToggleable && ((GUIToggleable)obj).isSet() == state)
				out.add(obj);
		}
		return out;
	}
	
	/**
	 * Gets a new query result containing objects that are visible.
	 * @return a new query result that has objects that are visible.
	 * @see GUIObject#isVisible()
	 */
	public GUIQuery getVisible()
	{
		GUIQuery out = new GUIQuery();
		for (GUIObject object : this)
			if (object.isVisible())
				out.add(object);
		return out;
	}
	
	/**
	 * Randomizes the order of the objects in this query.
	 * @param random the random number generator to use.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery shuffle(Random random)
	{
		int len = objectList.size();
		for (int i = 0; i < len - 1; i++)
		{
			int src = random.nextInt(len - i - 1) + 1;
			GUIObject temp = objectList.get(i);
			objectList.set(i, objectList.get(src));
			objectList.set(src, temp);
		}
		return this;
	}

	/**
	 * Requests focus on the first object in the query.
	 * If the set is empty, this does nothing.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery requestFocus()
	{
		if (size() > 0)
			getObject(0).requestFocus();
		return this;
	}
	
	/**
	 * Requests release of focus on all of the objects in the query.
	 * Only one object at a time can be focused, so this will actually
	 * unfocus at most one object in the query set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery releaseFocus()
	{
		for (GUIObject object : this)
			object.releaseFocus();
		return this;
	}
	
	/**
	 * Sets if the object can accept input on all objects in this query result.
	 * @param enabled true to enable, false to disable.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setEnabled(boolean enabled)
	{
		for (GUIObject object : this)
			object.setEnabled(enabled);
		return this;
	}

	/**
	 * Gets if the first object in the result can accept input.
	 * Note that this can be affected by parents in the object's lineage.
	 * If the set is empty, this returns false.
	 * @return true if so, false otherwise.
	 * @see GUIObject#isEnabled()
	 */
	public boolean isEnabled()
	{
		return size() > 0 ? getObject(0).isEnabled() : false;
	}

	/**
	 * Sets if the object is visible on all objects in this query result.
	 * @param visible true to set visible, false to not.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setVisible(boolean visible)
	{
		for (GUIObject object : this)
			object.setVisible(visible);
		return this;
	}

	/**
	 * Gets if the first object in the result is visible.
	 * Note that this can be affected by parents in the object's lineage.
	 * If the set is empty, this returns false.
	 * @return true if so, false otherwise.
	 * @see GUIObject#isVisible()
	 */
	public boolean isVisible()
	{
		return size() > 0 ? getObject(0).isVisible() : false;
	}

	/**
	 * Sets the theme on all objects in this query result.
	 * @param theme the theme to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setTheme(GUITheme theme)
	{
		for (GUIObject object : this)
			object.setTheme(theme);
		return this;
	}

	/**
	 * Gets the theme on the first object in the query result.
	 * Note that this can be affected by the GUI that the object is attached to.
	 * If the set is empty, this returns null.
	 * @return the attached theme or null if no theme.
	 * @see GUIObject#getTheme()
	 */
	public GUITheme getTheme()
	{
		return size() > 0 ? getObject(0).getTheme() : null;
	}

	/**
	 * Gets the current theme key on the first object in the query.
	 * If the set is empty, this returns null.
	 * @return the key name or null if no key is set.
	 * @see GUIObject#getThemeKey()
	 */
	public String getThemeKey()
	{
		return size() > 0 ? getObject(0).getThemeKey() : null;
	}
	
	/**
	 * Sets this object's texture on all objects in this query result.
	 * @param texture the texture to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setTexture(String texture)
	{
		for (GUIObject object : this)
			object.setTexture(texture);
		return this;
	}

	/**
	 * Gets the texture on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return the set texture or null if no texture is set.
	 */
	public String getTexture()
	{
		return size() > 0 ? getObject(0).getTexture() : null;
	}

	/**
	 * Sets the color on all objects in this query result.
	 * @param red the red component value for the color. 
	 * @param green the green component value for the color.
	 * @param blue the blue component value for the color.
	 * @param alpha the alpha component value for the color.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setColor(float red, float green, float blue, float alpha)
	{
		for (GUIObject object : this)
			object.setColor(red, green, blue, alpha);
		return this;
	}

	/**
	 * Sets the color on all objects in this query result.
	 * @param color the color to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setColor(GUIColor color)
	{
		return setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * Gets the color on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return the color.
	 */
	public GUIColor getColor()
	{
		return size() > 0 ? getObject(0).getColor() : null;
	}

	/**
	 * Gets the red color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the red color component. 
	 * @see GUIObject#getRenderRed()
	 * @see GUIObject#getColorNotInherited()
	 */
	public float getRed()
	{
		return size() > 0 ? getObject(0).getRenderRed() : 0f;
	}
	
	/**
	 * Gets the green color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the green color component. 
	 * @see GUIObject#getRenderGreen()
	 * @see GUIObject#getColorNotInherited()
	 */
	public float getGreen()
	{
		return size() > 0 ? getObject(0).getRenderGreen() : 0f;
	}
	
	/**
	 * Gets the blue color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the blue color component. 
	 * @see GUIObject#getRenderBlue()
	 * @see GUIObject#getColorNotInherited()
	 */
	public float getBlue()
	{
		return size() > 0 ? getObject(0).getRenderBlue() : 0f;
	}
	
	/**
	 * Gets the alpha color component of the first object in this query result.
	 * This color is a calculated color - it is affected by its parent's color components.
	 * If the set is empty, this returns 0.
	 * @return the alpha color component. 
	 * @see GUIObject#getRenderAlpha()
	 * @see GUIObject#getColorNotInherited()
	 */
	public float getAlpha()
	{
		return size() > 0 ? getObject(0).getRenderAlpha() : 0f;
	}
	
	/**
	 * Sets the opacity on all objects in this query result.
	 * @param opacity the opacity to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setOpacity(float opacity)
	{
		for (GUIObject object : this)
			object.setOpacity(opacity);
		return this;
	}

	/**
	 * Gets the opacity on the first object in this query result.
	 * If the set is empty, this returns 0.
	 * @return the object's opacity value.
	 */
	public float getOpacity()
	{
		return size() > 0 ? getObject(0).getOpacity() : 0f;
	}

	/**
	 * Sets the object bounds on all objects in this query result.
	 * @param x its position x.
	 * @param y its position y.
	 * @param width its width.
	 * @param height its height.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setBounds(float x, float y, float width, float height)
	{
		for (GUIObject object : this)
			object.setBounds(x, y, width, height);
		return this;
	}

	/**
	 * Gets the object bounds on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return a GUIBounds containing the object bounds. If the contents of the GUIBounds change,
	 * it will not affect the object.
	 */
	public GUIBounds getBounds()
	{
		return size() > 0 ? getObject(0).getBounds() : null;
	}
	
	/**
	 * Gets the object absolute bounds on the first object in this query result.
	 * If the set is empty, this returns null.
	 * @return a GUIBounds containing the object bounds. If the contents of the GUIBounds change,
	 * it will not affect the object.
	 */
	public GUIBounds getAbsoluteBounds()
	{
		return size() > 0 ? getObject(0).getAbsoluteBounds() : null;
	}
	
	/**
	 * Sets the object position on all objects in this query result.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setPosition(float x, float y)
	{
		for (GUIObject object : this)
			object.setPosition(x, y);
		return this;
	}

	/**
	 * Sets the object width and height on all objects in this query result.
	 * @param width its width.
	 * @param height its height.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setDimensions(float width, float height)
	{
		for (GUIObject object : this)
			object.setDimensions(width, height);
		return this;
	}

	/**
	 * Changes this object's position by an x or y-coordinate amount on all objects in this query result.
	 * @param x the x movement.
	 * @param y the y movement. 
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery translate(float x, float y)
	{
		for (GUIObject object : this)
			object.translate(x, y);
		return this;
	}

	/**
	 * Changes this object's width/height by an x or y-coordinate amount on all objects in this query result.
	 * @param width the width amount.
	 * @param height the height amount.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery stretch(float width, float height)
	{
		for (GUIObject object : this)
			object.stretch(width, height);
		return this;
	}

	/**
	 * Sets the text of all objects in this query result.
	 * Only works on GUILabel objects.
	 * @param text the text message to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setText(String text)
	{
		for (GUIObject object : this)
			if (object instanceof GUILabel) 
				((GUILabel)object).setText(text);
		return this;
	}
	
	/**
	 * Gets the text on the first object in this query result,
	 * or null if the object is not a GUILabel type.
	 * If the set is empty, this returns null.
	 * @return the text as a String.
	 */
	public String getText()
	{
		if (size() > 0 && getObject(0) instanceof GUILabel)
			return ((GUILabel)getObject(0)).getText();
		return null;
	}	
	
	/**
	 * Sets the value on all objects in this query result.
	 * Only works on {@link GUIValueField} objects.
	 * @param value the value to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setValue(Object value)
	{
		for (GUIObject object : this)
			if (object instanceof GUIValueField<?>) 
				((GUIValueField<?>)object).setValue(value);
		return this;
	}
	
	/**
	 * Gets the value on the first object in this query result,
	 * or null if the object is not a GUIValueField type.
	 * If the set is empty, this returns null.
	 * @return the text as a String.
	 */
	public Object getValue()
	{
		if (size() > 0 && getObject(0) instanceof GUIValueField)
			return ((GUIValueField<?>)getObject(0)).getValue();
		return null;
	}	
	
	/**
	 * Toggles the state of all objects in this query result.
	 * Only works on {@link GUIToggleable} objects.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery toggle()
	{
		for (GUIObject object : this)
			if (object instanceof GUIToggleable) 
				((GUIToggleable)object).toggle();
		return this;
	}
	
	/**
	 * Sets the state of all objects in this query result.
	 * Only operates on {@link GUIToggleable} objects.
	 * @param state the new toggle state.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setState(boolean state)
	{
		for (GUIObject object : this)
			if (object instanceof GUIToggleable) 
				((GUIToggleable)object).setState(state);
		return this;
	}
	
	/**
	 * Gets if the toggle state on the first object in this query result is "set,"
	 * or false if the object is not an GUIToggleable type.
	 * If the set is empty, this returns false.
	 * @return if the toggleable is set or false otherwise.
	 * @see #setState(boolean)
	 */
	public boolean isSet()
	{
		if (size() > 0 && getObject(0) instanceof GUIToggleable)
			return ((GUIToggleable)getObject(0)).isSet();
		return false;
	}	
	
	/**
	 * Sets this object's rotation in degrees on all objects in this query result.
	 * @param rotation the rotation to set.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery setRotationZ(float rotation)
	{
		for (GUIObject object : this)
			object.setRotationZ(rotation);
		return this;
	}

	/**
	 * Rotates this object by an amount of degrees on all objects in this query result.
	 * @param rotation the rotation to add.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery rotate(float rotation)
	{
		for (GUIObject object : this)
			object.rotate(rotation);
		return this;
	}

	/**
	 * Adds a name (or several) to the objects in this query.
	 * @param names the names to add to the objects.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery addName(String ... names)
	{
		for (GUIObject object : this)
			object.addName(names);
		return this;
	}
	
	/**
	 * Removes a name (or several) from the objects in this query.
	 * @param names the names to remove from the objects.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery removeName(String ... names)
	{
		for (GUIObject object : this)
			object.removeName(names);
		return this;
	}
	
	/**
	 * Enqueues an animation on this GUI Object, no duration.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animate(GUIAnimation ... animations)
	{
		return animate(0, GUIEasing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object, linear transition.
	 * @param duration the duration of the action in milliseconds.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animate(long duration, GUIAnimation ... animations)
	{
		return animate(duration, GUIEasing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @param transition the transition type for the action.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animate(long duration, GUIEasingType transition, GUIAnimation ... animations)
	{
		for (GUIObject object : this)
			object.animate(duration, transition, animations);
		return this;
	}

	/**
	 * Enqueues an animation on this GUI Object, linear transition, and with
	 * a cumulative delay added to each object.<p>
	 * For example, if <code>cumulativeDelay</code> is 200, then the first object
	 * will receive 0 delay, but the next will receive 200ms, then the next will
	 * get 400ms, then the next 600ms, and so on.
	 * @param cumulativeDelay the cumulative delay to add to each object beyond the first.
	 * @param duration the duration of the action in milliseconds.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animate(long cumulativeDelay, long duration, GUIAnimation ... animations)
	{
		return animate(cumulativeDelay, duration, GUIEasing.LINEAR, animations);
	}

	/**
	 * Enqueues an animation on this GUI Object, and with
	 * a cumulative delay added to each object.<p>
	 * For example, if <code>cumulativeDelay</code> is 200, then the first object
	 * will receive 0 delay, but the next will receive 200ms, then the next will
	 * get 400ms, then the next 600ms, and so on.
	 * @param cumulativeDelay the cumulative delay to add to each object beyond the first.
	 * @param duration the duration of the action in milliseconds.
	 * @param transition the transition type for the action.
	 * @param animations the animations to perform (at once) for this animation.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animate(long cumulativeDelay, long duration, GUIEasingType transition, GUIAnimation ... animations)
	{
		long delay = 0L;
		for (GUIObject object : this)
		{
			object.animateDelay(delay);
			object.animate(duration, transition, animations);
			delay += cumulativeDelay;
		}
		return this;
	}

	/**
	 * Enqueues a delay between animations on this GUI Object.
	 * @param duration the duration of the action in milliseconds.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animateDelay(long duration)
	{
		for (GUIObject object : this)
			object.animateDelay(duration);
		return this;
	}

	/**
	 * Aborts the animation on this object, abandoning it mid-animation.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animateAbort()
	{
		for (GUIObject object : this)
			object.animateAbort();
		return this;
	}

	/**
	 * Finishes the animation on this object all the way to the end.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery animateFinish()
	{
		for (GUIObject object : this)
			object.animateFinish();
		return this;
	}
	
	/**
	 * Binds an action of a particular type to the objects inside this query.
	 * @param action the action to bind.
	 * @param types the action type to bind.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery bindAction(GUIAction action, String ... types)
	{
		for (GUIObject object : this)
			object.bindAction(action, types);
		return this;
	}
	
	/**
	 * Unbinds an action of a particular type from the objects inside this query.
	 * @param action the action to bind.
	 * @param types the action type to bind.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery unbindAction(GUIAction action, String ... types)
	{
		for (GUIObject object : this)
			object.unbindAction(action, types);
		return this;
	}
	
	/**
	 * Unbinds all actions of a particular type from the objects inside this query.
	 * @param type the action type to bind.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery unbindAllActions(String type)
	{
		for (GUIObject object : this)
			object.unbindAllActions(type);
		return this;
	}
	
	/**
	 * Calls an action on the objects inside the query.
	 * @param action the action to call.
	 * @return itself, in order to chain queries.
	 */
	public GUIQuery callAction(GUIAction action)
	{
		for (GUIObject object : this)
			object.callAction(action);
		return this;
	}
	
	@Override
	public String toString()
	{
		return objectList.toString();
	}
	
}
