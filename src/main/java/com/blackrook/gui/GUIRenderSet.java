/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

import com.blackrook.gui.object.GUILabel;
import com.blackrook.gui.object.GUILabel.TextPiece;
import com.blackrook.gui.struct.ColorUtils;

/**
 * The render set for rendering the GUI.
 * @author Matthew Tropiano
 */
public class GUIRenderSet
{
	/** GUI Viewport Origin X-coordinate (in GUI coordinates). */
	private float guiX;
	/** GUI Viewport Origin Y-coordinate (in GUI coordinates). */
	private float guiY;
	/** GUI Viewport width (in GUI coordinates). */
	private float guiWidth;
	/** GUI Viewport width (in GUI coordinates). */
	private float guiHeight;
	
	/** List of pieces to draw. */
	private Piece[] pieceList;
	/** Piece count. */
	private int pieceCount;
	
	/**
	 * Creates a new GUI Render Set.
	 */
	public GUIRenderSet()
	{
		this.guiX = 0f;
		this.guiY = 0f;
		this.guiWidth = 0f;
		this.guiHeight = 0f;
		setPieceCapacity(1024);
	}
	
	// Sets the internal capacity for the surface list.
	private void setPieceCapacity(int capacity)
	{
		int oldCapacity = pieceList != null ? pieceList.length : 0;
		Piece[] oldArray = pieceList;
				
		if (oldCapacity == capacity)
			return;
		
		Piece[] newArray = new Piece[capacity];
		if (oldArray != null)
			System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldCapacity, capacity));
		
		for (int i = oldCapacity; i < capacity; i++)
			newArray[i] = new Piece();

		if (capacity < oldCapacity)
			pieceCount = capacity;
		
		pieceList = newArray;
	}

	/**
	 * Clears the piece list.
	 */
	public void clear()
	{
		pieceCount = 0;
	}
	
	/**
	 * Adds the renderable characteristics for an object to this.
	 * @param object the object to add. 
	 */
	public void addObject(GUIObject object)
	{
		if (object instanceof GUILabel)
		{
			addObject((GUILabel)object);
			return;
		}
		
		if (pieceCount >= pieceList.length)
			setPieceCapacity(pieceList.length * 2);

		Piece p = pieceList[pieceCount++];
		p.boundsX = object.getRenderPositionX();
		p.boundsY = object.getRenderPositionY();
		p.boundsWidth = object.getRenderWidth();
		p.boundsHeight = object.getRenderHeight();
		
		p.textureCoordinates[0] = 0f; // s0
		p.textureCoordinates[1] = 0f; // t0
		p.textureCoordinates[2] = object.getRenderTextureScaleS(); // s1
		p.textureCoordinates[3] = object.getRenderTextureScaleT(); // t1

		p.colorARGB = ColorUtils.argbColor(object.getRenderRed(), object.getRenderGreen(), object.getRenderBlue(), object.getRenderAlpha());
		p.rotation = object.getRenderRotationZ();
		p.texture = object.getTexture();
	}
	
	/**
	 * Adds the renderable characteristics for a text object to this.
	 * @param label the object to add. 
	 */
	public void addObject(GUILabel label)
	{
		float origX = label.getRenderPositionX();
		float origY = label.getRenderPositionY();
		float width = label.getRenderWidth();
		float height = label.getRenderHeight();

		for (int i = 0; i < label.getTextPieceCount(); i++)
		{
			if (pieceCount >= pieceList.length)
				setPieceCapacity(pieceList.length * 2);

			TextPiece tp = label.getTextPiece(i);
			Piece p = pieceList[pieceCount++];

			p.boundsX = origX + (tp.x0 * width);
			p.boundsY = origY + (tp.y0 * height);
			p.boundsWidth = (tp.x1 - tp.x0) * width;
			p.boundsHeight = (tp.y1 - tp.y0) * height;

			p.textureCoordinates[0] = tp.s0;
			p.textureCoordinates[1] = tp.t0;
			p.textureCoordinates[2] = tp.s1;
			p.textureCoordinates[3] = tp.t1;
			
			p.colorARGB = ColorUtils.argbColor(label.getRenderRed(), label.getRenderGreen(), label.getRenderBlue(), label.getRenderAlpha());
			p.rotation = label.getRenderRotationZ();
			p.texture = label.getTexture();
		}
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
	
	public Piece[] getPieces()
	{
		return pieceList;
	}
	
	public int getPieceCount()
	{
		return pieceCount;
	}
	
	/**
	 * A single renderable piece for the GUI.
	 */
	public static class Piece
	{
		/** Absolute bounds: X. */
		public float boundsX;
		/** Absolute bounds: Y. */
		public float boundsY;
		/** Absolute bounds: width. */
		public float boundsWidth;
		/** Absolute bounds: height. */
		public float boundsHeight;
		/** Color. */
		public int colorARGB;
		/** Rotation. */
		public float rotation;
		/** Texture to use. */
		public String texture;
		/** Texture coordinates. */
		public float[] textureCoordinates;
		
		private Piece()
		{
			this.textureCoordinates = new float[4];
		}
	}
		
}
