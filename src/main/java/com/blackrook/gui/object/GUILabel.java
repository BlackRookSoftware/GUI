/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.blackrook.gui.GUI;
import com.blackrook.gui.GUIBounds;
import com.blackrook.gui.GUIFontType;
import com.blackrook.gui.GUIFontType.Directionality;
import com.blackrook.gui.GUIFontType.FontChar;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.GUITheme;

/**
 * GUI Object that holds and displays text data.
 * @author Matthew Tropiano
 */
public class GUILabel extends GUIObject
{
	/**
	 * List of font text alignment/justification types.
	 */
	public static enum Justification
	{
		/** Text will be left-aligned inside the object bounds. */
		LEFT,
		/** Text will be right-aligned inside the object bounds. */
		RIGHT,
		/** Text will be centered inside the object bounds. */
		CENTER,
		/** Font's default justification will be used, depending on its directionality. */
		DEFAULT;
	}
	
	/**
	 * List of font text vertical alignment types.
	 */
	public static enum Alignment
	{
		/** Text will be aligned to the top of the object. */
		TOP,
		/** Text will be aligned to the middle of the object. */
		MIDDLE,
		/** Text will be aligned to the bottom of the object. */
		BOTTOM;
	}
	
	/**
	 * List of auto-resizing methods.
	 */
	public static enum ResizeMode
	{
		/** Y-coordinate bound does not change (shrink down/expand up). */
		PIN_Y,
		/** Y-coordinate bound changes (shrink up/expand down). */
		CHANGE_Y
	}
	
	/** The font definition to use. */
	private GUIFontType font;
	
	/** The text to use for this object. */
	private String text;
	/** The size of the font in GUI units. */
	private float size;
	
	/** The current theme key. */
	private String themeKey;

	/** Use word wrapping. */
	private boolean wordWrap; 
	/** Type of type justification to use. */
	private Justification justification; 
	/** Type of type vertical alignment to use. */
	private Alignment alignment;

	/** Resizing mode to use when text is changed (can be null). */
	private ResizeMode resizeMode;
	/** Maximum width of the resize. */
	private float maxWidth;

	/** Starting line number. */
	private int startingLine;
	
	//==== RESULTANT TEXT DATA ====================

	/** The broken-up text data for generating the polygonal data. */
	private PieceContext textData;
	/** All character pieces. */
	private List<TextPiece> textPieces;
	
	/**
	 * Creates a new GUI Text object with no text nor specific font.
	 */
	public GUILabel()
	{
		this(null, "", Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object (with no specified font).
	 * @param text the initial text.
	 */
	public GUILabel(String text)
	{
		this(null, text, Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 * @param font the specific font to use.
	 */
	public GUILabel(GUIFontType font)
	{
		this(font, "", Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 * @param font the specific font to use.
	 * @param text the initial text.
	 */
	public GUILabel(GUIFontType font, String text)
	{
		this(font, text, Justification.DEFAULT, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 * @param font the specific font to use.
	 * @param text the initial text.
	 * @param justification the text justification.
	 */
	public GUILabel(GUIFontType font, String text, Justification justification)
	{
		this(font, text, justification, Alignment.TOP);
	}
	
	/**
	 * Creates a new GUI Text object.
	 * @param font the specific font to use.
	 * @param text the initial text.
	 * @param justification the text justification.
	 * @param alignment the text line alignment. 
	 */
	public GUILabel(GUIFontType font, String text, Justification justification, Alignment alignment)
	{
		super();
		setJustification(justification);
		setAlignment(alignment);
		setFont(font);
		setText(text);
		setBounds(0f, 0f, 0f, 0f);
		setWordWrapping(true);
		setStartingLine(0);
		setSize(16);
		setResizeMode(null);
	}
	
	/**
	 * Sets the object bounds.
	 * @param x			its position x.
	 * @param y			its position y.
	 * @param width		its width.
	 * @param height	its height.
	 */
	public GUIObject setBounds(float x, float y, float width, float height)
	{
		super.setBounds(x, y, width, height);
		refreshMesh();
		return this;
	}

	/**
	 * Changes this object's width/height by an x or y-coordinate amount.
	 */
	public GUIObject stretch(float width, float height)
	{
		super.stretch(width, height);
		refreshMesh();
		return this;
	}

	/**
	 * Gets this object's texture, which is taken from the selected font.
	 * If no font is set on this, this will take the one from the theme,
	 * if this also has a theme key set on it.
	 * @return the most relevant texture to use, null if none suitable.
	 * @see #getFont()
	 */
	@Override
	public String getTexture()
	{
		GUIFontType font = getFont();
		return font != null ? font.getTexture() : null;
	}
	
	@Override
	public GUIObject setTheme(GUITheme theme)
	{
		super.setTheme(theme);
		refreshMesh();
		return this;
	}

	/**
	 * Gets the font for this text object.
	 * If no font is set on this object, it will attempt to get it from the theme,
	 * if the appropriate theme key is set.
	 * @return the font to use for rendering text.
	 */
	public GUIFontType getFont()
	{
		if (font != null)
			return font;
		
		String key = getThemeKey();
		if (key != null)
		{
			GUITheme t = getTheme();
			return t != null ? t.getFont(key) : null;
		}

		return null;
	}

	/**
	 * Sets the font for this text object.
	 * @param font the font for the label.
	 * @return itself, to chain calls.
	 */
	public GUILabel setFont(GUIFontType font)
	{
		this.font = font;
		refreshMesh();
		return this;
	}

	/**
	 * @return the size of the font in GUI units.
	 */
	public float getSize()
	{
		return size;
	}
	
	/**
	 * Sets the size of the font in GUI units.
	 * @param size the new size.
	 * @return itself, to chain calls.
	 */
	public GUILabel setSize(float size)
	{
		this.size = size;
		refreshMesh();
		return this;
	}
	
	@Override
	public String getThemeKey()
	{
		return themeKey;
	}

	/**
	 * Sets this object's theme key. 
	 * This is the key that is used to look up the font used to render this object
	 * if a theme is applied to this object or the GUI that owns it. 
	 * @param themeKey the theme key to use.
	 * @return itself, to chain calls.
	 */
	public GUILabel setThemeKey(String themeKey)
	{
		this.themeKey = themeKey;
		updateDirty();
		return this;
	}
	
	/**
	 * Gets the text on this text object.
	 * Default is the empty string.
	 * @return the text.
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the text on this text object.
	 * Default is the empty string.
	 * @param text the text to set.
	 * @return itself, to chain calls.
	 */
	public GUILabel setText(String text)
	{
		if (this.text != null && this.text.equals(text))
			return this;
		this.text = text == null ? "" : text;
		refreshMesh();
		return this;
	}
	
	/**
	 * Checks if word wrapping is enabled.
	 * @return true if so, false if not.
	 */
	public boolean isWordWrapping()
	{
		return wordWrap;
	}

	/**
	 * Sets if word wrapping is enabled.
	 * True if so, false if not.
	 * @param wordWrap 
	 * @return itself, to chain calls.
	 */
	public GUILabel setWordWrapping(boolean wordWrap)
	{
		this.wordWrap = wordWrap;
		refreshMesh();
		return this;
	}

	/**
	 * @return the text alignment/justification.
	 */
	public Justification getJustification()
	{
		return justification;
	}

	/**
	 * Sets the text alignment/justification.
	 * @param justification the justification to use.
	 * @return itself, to chain calls.
	 */
	public GUILabel setJustification(Justification justification)
	{
		this.justification = justification;
		refreshMesh();
		return this;
	}

	/**
	 * @return the current style of vertical text alignment.
	 */
	public Alignment getAlignment()
	{
		return alignment;
	}

	/**
	 * Sets the current style of vertical text alignment.
	 * @param alignment the text alignment to use.
	 * @return itself, to chain calls.
	 */
	public GUILabel setAlignment(Alignment alignment)
	{
		this.alignment = alignment;
		refreshMesh();
		return this;
	}

	/**
	 * Gets the current resize mode for this text object.
	 * If this is not null, the object will resize itself 
	 * according to the max width and resize mode parameters.
	 * @return the current resize mode.

	 */
	public ResizeMode getResizeMode()
	{
		return resizeMode;
	}

	/**
	 * Sets the current resize mode for this text object.
	 * If this is not null, the object will resize itself 
	 * according to the max width and resize mode parameters.
	 * @param resizeMode the resize mode.
	 * @return itself, to chain calls.
	 */
	public GUILabel setResizeMode(ResizeMode resizeMode)
	{
		this.resizeMode = resizeMode;
		return this;
	}

	/**
	 * @return the max width in units for the resizing capability of this object.
	 */
	public float getMaxWidth()
	{
		return maxWidth;
	}

	/**
	 * Sets the max width in units for the resizing capability of this object.
	 * @param maxWidth the max width in units.
	 * @return itself, to chain calls.
	 */
	public GUILabel setMaxWidth(float maxWidth)
	{
		this.maxWidth = maxWidth;
		return this;
	}

	/**
	 * Gets the starting line that the mesh data will be generated for
	 * in the text field. By default, this is zero (first line). 
	 * @return the starting line index.
	 */
	public int getStartingLine()
	{
		return startingLine;
	}

	/**
	 * Sets the starting line that the mesh data will be generated for
	 * in the text field. By default, this is zero (first line). 
	 * @param startingLine the starting line index.
	 * @return itself, to chain calls.
	 */
	public GUILabel setStartingLine(int startingLine)
	{
		this.startingLine = startingLine;
		textPieces = constructMesh();
		return this;
	}
	
	/**
	 * Gets a single piece of the rendered text data for rendering.
	 * @param index the character index.
	 * @return a piece of text, or null if no corresponding piece.
	 */
	public TextPiece getTextPiece(int index)
	{
		return textPieces.get(index);
	}

	/**
	 * @return get text piece count. 
	 */
	public int getTextPieceCount()
	{
		return textPieces.size();
	}
	
	@Override
	protected void onGUIChange(GUI gui)
	{
		super.onGUIChange(gui);
		refreshMesh();
	}

	private void refreshMesh()
	{
		refreshTextData(getText());
		textPieces = constructMesh();
		updateDirty();
	}

	/**
	 * Creates and returns a new mesh for the new text.
	 * @param text the text data.
	 */
	private void refreshTextData(String text)
	{	
		GUIFontType font = getFont();
		
		if (font == null)
			return;
		
		float width = resizeMode != null ? maxWidth : getRenderWidth();
		float height = resizeMode != null ? Float.MAX_VALUE : getRenderHeight();

		textData = new PieceContext();
		if (text == null || text.length() == 0 || size <= 0f || width == 0 || height == 0)
			return;
		
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			char c = chars[i];
			FontChar fc = font.getChar(c);
			// newlines
			if (fc == null)
			{
				// Do nothing.
			}
			else if (c == '\n')
			{
				textData.addWordToLine();
				textData.nextLine();
			}
			// spaces and other.
			else if (Character.isWhitespace(c))
			{
				FontChar spacefc = font.getChar(' ');
				float spwidth = (spacefc.width * size);
				textData.addWordToLine();
				if (textData.lineBuffer.lineWidth + spwidth < width)
					textData.lineBuffer.addChar(spacefc);
			}
			// character will breach the line.
			else if (textData.lineBuffer.lineWidth + textData.wordWidth + (fc.advance * size) >= width)
			{
				if (!wordWrap || textData.lineBuffer.words == 0)
					textData.addWordToLine();
				textData.nextLine();
				textData.addChar(fc);
			}
			else
			{
				textData.addChar(fc);
				if (characterIsBreakingDelimiter(c))
					textData.addWordToLine();
			}
		}
		textData.addWordToLine();
		textData.nextLine();
	}

	/**
	 * Checks if a particular character is a breaking delimiter for word wrapping.
	 */
	private boolean characterIsBreakingDelimiter(char c)
	{
		switch (c)
		{
			case '-':
			case '\u2013':
			case '\u2014':
				return true;
		}
		return false;
	}
	
	private static final List<TextPiece> NO_CHARACTERS = new ArrayList<>(1);
	
	// Creates the mesh itself.
	private List<TextPiece> constructMesh()
	{
		GUIFontType font = getFont();
		
		if (font == null || textData.quads == 0)
			return NO_CHARACTERS;

		List<TextPiece> out = new ArrayList<>(getText().length());
		int maxLines = textData.textBlock.size();
		int visibleLines = maxLines - Math.max(startingLine, 0);
		float width = resizeMode != null ? maxWidth : getRenderWidth();
		float height = resizeMode != null ? visibleLines * size : getRenderHeight();
		int totalLines = (int)(size != 0.0f ? height / size : 0);
		float heightBasis = size / height;
		float widthBasis = size / width;

		float y;
		if (alignment == Alignment.BOTTOM)
			y = 1.0f - (visibleLines < totalLines ? visibleLines * heightBasis : totalLines * heightBasis);
		else if (alignment == Alignment.MIDDLE)
			y = 0.5f - ((visibleLines < totalLines ? visibleLines * heightBasis : totalLines * heightBasis) / 2f);
		else // TOP
			y = 0f;
		
		Directionality dir = font.getDirectionality();
		int linenum = 0;
		int linesRendered = 0;
		for (PieceContext.Line line : textData.textBlock)
		{
			if (linesRendered >= totalLines)
				break;

			if (linenum < startingLine)
			{
				linenum++;
				continue;
			}
			
			linenum++;
			
			float x = 0f;

			Justification just;
			if (justification == Justification.DEFAULT)
				just = font.getDirectionality() == Directionality.LEFT_TO_RIGHT ? Justification.LEFT : Justification.RIGHT;
			else
				just = justification;
			
			if (just == Justification.CENTER)
				x = 0.5f - (line.lineWidth / width / 2f);
			else if (just == Justification.RIGHT)
				x = 1.0f - (line.lineWidth / width);
			else // LEFT
				x = 0f;

			if (dir == Directionality.RIGHT_TO_LEFT)
				x += (line.lineWidth / width);
			
			for (FontChar fc : line.line)
			{
				if (dir == Directionality.RIGHT_TO_LEFT)
					x -= fc.advance * widthBasis;
				
				float wx = fc.width * widthBasis;
				float hy = fc.height * heightBasis;
				float xofs = fc.xofs * widthBasis;
				float yofs = fc.yofs * heightBasis;
				
				float x0 = x + xofs;
				float x1 = x+ wx + xofs;
				float y0 = yofs + y;
				float y1 = yofs + (y + hy);
				
				TextPiece c = new TextPiece();
				c.x0 = x0;
				c.x1 = x1;
				c.y0 = y0;
				c.y1 = y1;
				c.s0 = fc.s0;
				c.s1 = fc.s1;
				c.t0 = fc.t0;
				c.t1 = fc.t1;
				out.add(c);
				
				if (dir == Directionality.LEFT_TO_RIGHT)
					x += fc.advance * widthBasis;
			}
			y += heightBasis;
			linesRendered++;
		}
		
		GUIBounds objectBounds = getNativeBounds();
		
		// resize!
		if (resizeMode != null) switch (resizeMode)
		{
			case PIN_Y:
				objectBounds.height = height;
				if (linesRendered <= 1)
					objectBounds.width = textData.textBlock.peekFirst().lineWidth;
				else
					objectBounds.width = maxWidth;
				break;
			case CHANGE_Y:
				float old_height = objectBounds.height;
				objectBounds.height = height;
				objectBounds.y += old_height - objectBounds.height;
				if (linesRendered <= 1)
					objectBounds.width = textData.textBlock.peekFirst().lineWidth;
				else
					objectBounds.width = maxWidth;
				break;
		}

		return out;
	}
	
	/**
	 * A single label character.
	 */
	public static class TextPiece
	{
		/** Scalar across X-axis. */
		public float x0;
		/** Scalar across X-axis. */
		public float x1;
		/** Scalar across Y-axis. */
		public float y0;
		/** Scalar across Y-axis. */
		public float y1;

		/** Texture S coordinate 0. */
		public float s0;
		/** Texture S coordinate 1. */
		public float s1;
		/** Texture T coordinate 0. */
		public float t0;
		/** Texture T coordinate 1. */
		public float t1;
		
		@Override
		public String toString()
		{
			return "TextPiece [ X0:"+(x0)+", X1:"+(x1)+", Y0:"+(y0)+", Y1:"+(y1)+", S0:"+(s0)+", S1:"+(s1)+", T0:"+(t0)+", T1:"+(t1)+"]";
		}
		
	}
	
	/**
	 * Context for creating a text piece.
	 */
	private class PieceContext
	{
		private Deque<Line> textBlock;
		private Line lineBuffer;
		private Deque<FontChar> wordBuffer;
		private float wordWidth;
		private int quads;
		
		PieceContext()
		{
			textBlock = new LinkedList<Line>();
			lineBuffer = new Line();
			wordBuffer = new LinkedList<FontChar>();
			wordWidth = 0f;
			quads = 0;
		}
		
		void nextLine()
		{
			lineBuffer.trim();
			quads += lineBuffer.line.size();
			textBlock.add(lineBuffer);
			lineBuffer = new Line();
		}
		
		void addWordToLine()
		{
			for (FontChar fc : wordBuffer)
				lineBuffer.addChar(fc);
			lineBuffer.words++;
			wordBuffer = new LinkedList<FontChar>();
			wordWidth = 0;
		}
		
		void addChar(FontChar fc)
		{
			wordBuffer.add(fc);
			wordWidth += (fc.advance * getSize());
		}
		
		class Line
		{
			public Deque<FontChar> line;
			public float lineWidth;
			public int words;
			
			public Line()
			{
				line = new LinkedList<FontChar>();
				lineWidth = 0;
				words = 0;
			}
			
			public void addChar(FontChar fc)
			{
				line.add(fc);
				lineWidth += (fc.advance * getSize());
			}
			
			public void trim()
			{
				float size = getSize();
				while (!line.isEmpty() && Character.isWhitespace(line.peekLast().c))
					lineWidth -= line.pollLast().advance * size;
				while (!line.isEmpty() && Character.isWhitespace(line.peekFirst().c))
					lineWidth -= line.pollFirst().advance * size;
			}
			
		}
		
	}
	
}
