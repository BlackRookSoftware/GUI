/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.layout;

import com.blackrook.gui.GUIBounds;
import com.blackrook.gui.GUILayout;
import com.blackrook.gui.GUIObject;

/**
 * A special layout that controls the visibility (and size) of
 * the children on the object using this layout. The children
 * are resized to the parent's width and height 
 * @author Matthew Tropiano
 */
public class CardLayout implements GUILayout
{
	/** Reference to object bound to this layout. */
	protected GUIObject objectRef;
	
	/** Identifier for the active card. */
	protected String activeCard;
	/** Index used for the active card. */
	protected int activeIndex;
	
	/** 
	 * Creates a new CardLayout.
	 * @param objectRef should be the reference to the object using this layout.
	 * Used for triggering child visibility when the active card changes. 
	 */
	public CardLayout(GUIObject objectRef)
	{
		if (objectRef == null)
			throw new IllegalArgumentException("The GUI object reference cannot be null.");
		this.objectRef = objectRef;
		activeCard = null;
		activeIndex = 0;
	}
	
	/**
	 * Sets which card is supposed to be visible by an identifier.
	 * The object that has this identifier as an attribute will be set as visible.
	 * This resets the active index.
	 * @param activeCard the id to set as the identifier of the active object.
	 */
	public void setActiveCard(String activeCard)
	{
		this.activeCard = activeCard;
		this.activeIndex = 0;
		objectRef.resizeChildren();
	}

	/**
	 * Sets which card is supposed to be visible by its child index.
	 * This nullifies the active identifier, if it was in use.
	 * @param activeIndex the index to set as the active object.
	 */
	public void setActiveIndex(int activeIndex)
	{
		this.activeCard = null;
		this.activeIndex = activeIndex;
		objectRef.resizeChildren();
	}

	/**
	 * @return the name of which card is supposed to be visible.
	 */
	public String getActiveCard()
	{
		return activeCard;
	}

	/**
	 * @return the index of which card is supposed to be visible.
	 */
	public int getActiveIndex()
	{
		return activeIndex;
	}

	@Override
	public void resizeChild(GUIObject object, int index, int childTotal)
	{
		GUIObject parent = object.getParent();
		GUIBounds r = parent.getBounds();
		object.setBounds(0, 0, r.width, r.height);
		object.setVisible(
			activeCard != null 
			? activeCard.equals(object.getLayoutAttrib().toString()) 
			: index == activeIndex
			);
	}
	
}
