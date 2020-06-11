/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * GUI font for GUIText objects. Basically a texture-coordinate and size lookup for
 * rendering character meshes.  
 * @author Matthew Tropiano
 */
public class GUIFont implements GUIFontType
{
	/** Name of the font. */
	protected String name;
	/** Texture name for this font. */
	protected String texture;
	/** HashMap for the font map. */
	protected SortedMap<Character, FontChar> fontMap; 
	/** Default character to return if the character is not in the map. */
	protected FontChar defaultChar;
	/** Type of directionality to use. */
	protected Directionality directionality;
	
	// hidden for static factory methods.
	private GUIFont()
	{
		this(null, null);
	}
	
	/**
	 * Creates an GUIFont from a texture.
	 * @param texture the texture.
	 */
	public GUIFont(String texture)
	{
		this("UNNAMED", texture);
	}
	
	/**
	 * Creates an GUIFont from a texture.
	 * @param name the name of the font.
	 * @param texture the texture.
	 */
	public GUIFont(String name, String texture)
	{
		this.name = name;
		this.texture = texture;
		fontMap = new TreeMap<Character, FontChar>();
		defaultChar = new FontChar('\0', 0, 0, 0, 0, 1, 1, 1, 0, 0);
		directionality = Directionality.LEFT_TO_RIGHT;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this font.
	 * @param name the name of the font.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getTexture()
	{
		return texture;
	}

	/**
	 * Sets the texture to use for this mapping.
	 * @param texture the texture name.
	 */
	public void setTexture(String texture)
	{
		this.texture = texture;
	}

	/**
	 * Sets the default character. 
	 * @param s0 the starting texture coordinate, s-axis.
	 * @param t0 the starting texture coordinate, t-axis.
	 * @param s1 the ending texture coordinate, s-axis.
	 * @param t1 the ending texture coordinate, t-axis.
	 * @param width the width ratio scalar for this character. 
	 * The final width of the character is the font size times this scalar. 
	 * @param height the height ratio scalar for this character. 
	 * The final height of the character is the font size times this scalar. 
	 * @param advance the advance ratio scalar for this character, or how much to advance the cursor after this is placed. 
	 * The advance of the character is the font size times this scalar. 
	 * @param xofs the x-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 * @param yofs the y-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 */
	public void setDefaultChar(float s0, float t0, float s1, float t1, float width, float height, float advance, float xofs, float yofs)
	{
		defaultChar.s0 = s0;
		defaultChar.t0 = t0;
		defaultChar.s1 = t0;
		defaultChar.t1 = t0;
		defaultChar.width = width;
		defaultChar.height = height;
		defaultChar.advance = advance;
		defaultChar.xofs = xofs;
		defaultChar.yofs = yofs;
	}
	
	/**
	 * Adds a character to the map.
	 * @param c the character to add to the map.
	 * @param s0 the starting texture coordinate, s-axis.
	 * @param t0 the starting texture coordinate, t-axis.
	 * @param s1 the ending texture coordinate, s-axis.
	 * @param t1 the ending texture coordinate, t-axis.
	 * @param width the width ratio scalar for this character. 
	 * The final width of the character is the font size times this scalar. 
	 * @param height the height ratio scalar for this character. 
	 * The final height of the character is the font size times this scalar. 
	 * @param advance how much to move the cursor for the next cursor rendering.
	 * @param xofs the x-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 * @param yofs the y-offset amount scalar to adjust the cursor for painting this character.
	 * The amount of adjustment is the font size times this scalar. 
	 */
	public void addChar(char c, float s0, float t0, float s1, float t1, 
			float width, float height, float advance, float xofs, float yofs)
	{
		fontMap.put(c, new FontChar(c, s0, t0, s1, t1, width, height, advance, xofs, yofs));
	}
	
	@Override
	public FontChar getChar(char c)
	{
		FontChar out = fontMap.get(c);
		return out != null ? out : defaultChar;
	}
	
	/**
	 * Removes a character from the map.
	 * @param c the character to remove from the map.
	 * @return true if the definition was removed, false otherwise.
	 */
	public boolean removeChar(char c)
	{
		return fontMap.get(c) != null;
	}
	
	@Override
	public Directionality getDirectionality()
	{
		return directionality;
	}

	/**
	 * Sets this font's directionality.
	 * @param directionality the lettering directionality.
	 */
	public void setDirectionality(Directionality directionality)
	{
		this.directionality = directionality;
	}

	/**
	 * Creates a monospace font that maps the first series of unicode characters
	 * from a specified character set distributed evenly across a single texture, 
	 * left to right, top to bottom.
	 * <p>This assumes an "ASCII" alphabet starting from 0x00.
	 * <p>The distribution is square-wise, affected by the parameters <code>charsPerRow</code> and <code>alphabet</code>. 
	 * <p> If <code>charsPerRow</code> is 3, the divisions are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td></tr>
	 * <tr><td>3</td><td>4</td><td>5</td></tr>
	 * <tr><td>6</td><td>7</td><td>8</td></tr>
	 * </table>
	 * <p> If <code>charsPerRow</code> is 4 but <code>alphabet.length() is 15</code>, the divisions are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>11</td></tr>
	 * <tr><td>12</td><td>13</td><td>14</td><td>&nbsp;</td></tr>
	 * </table>
	 * <p> If <code>charsPerRow</code> is 4 but <code>alphabet.length() is 11</code>, the divisions are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>&nbsp;</td></tr>
	 * <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	 * </table>
	 * @param name the name of the font.
	 * @param texture the name of the texture.
	 * @param charsPerRow the amount of characters per row.
	 * @return a new font.
	 */
	public static GUIFont makeMonospaceFont(String name, String texture, int charsPerRow)
	{
		StringBuilder alphabet = new StringBuilder();
		int charAmount = charsPerRow * charsPerRow;
		for (int i = 0; i < charAmount; i++)
			alphabet.append((char)i);
		return makeMonospaceFont(name, texture, charsPerRow, alphabet.toString());
	}

	/**
	 * Creates a monospace font that maps the first series of unicode characters
	 * from a specified character set distributed evenly across a single texture, 
	 * left to right, top to bottom.
	 * <p>The distribution is square-wise, affected by the parameters <code>charsPerRow</code> and <code>alphabet</code>. 
	 * <p> If <code>charsPerRow</code> is 3, the divisions are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td></tr>
	 * <tr><td>3</td><td>4</td><td>5</td></tr>
	 * <tr><td>6</td><td>7</td><td>8</td></tr>
	 * </table>
	 * <p> If <code>charsPerRow</code> is 4 but <code>alphabet.length() is 15</code>, the divisions are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>11</td></tr>
	 * <tr><td>12</td><td>13</td><td>14</td><td>&nbsp;</td></tr>
	 * </table>
	 * <p> If <code>charsPerRow</code> is 4 but <code>alphabet.length() is 11</code>, the divisions are:
	 * <table>
	 * <tr><td>0</td><td>1</td><td>2</td><td>3</td></tr>
	 * <tr><td>4</td><td>5</td><td>6</td><td>7</td></tr>
	 * <tr><td>8</td><td>9</td><td>10</td><td>&nbsp;</td></tr>
	 * <tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	 * </table>
	 * @param name the name of the font.
	 * @param texture the name of the texture.
	 * @param charsPerRow the amount of characters per row.
	 * @param alphabet the texture increment for each character and row. 
	 * @return a new font.
	 */
	public static GUIFont makeMonospaceFont(String name, String texture, int charsPerRow, String alphabet)
	{
		GUIFont out = new GUIFont();
		out.setName(name);
		out.texture = texture;
		
		float inc = 1.0f / charsPerRow;
		
		char[] chararray = alphabet.toCharArray();
		for (char i = 0; i < alphabet.length(); i++)
		{
			int imod = i % charsPerRow;
			int idiv = i / charsPerRow;
			out.addChar(
				chararray[i], 
				inc*imod, inc*idiv, inc*imod+inc, inc*idiv+inc,
				1, 1, 1, 0, 0
			);
		}
		return out;
	}

	/**
	 * Reads in XML-Formatted BM Font (http://www.angelcode.com/products/bmfont/) generated metadata
	 * and creates an OGLGUIFont using the data.
	 * @param file the file that holds the XML data.
	 * @param texture the texture used for this font.
	 * @return the created font.
	 * @throws IOException if a read error happens.
	 */
	public static GUIFont readBMFont(File file, String texture) throws IOException
	{
		FileInputStream fis = new FileInputStream(file);
		GUIFont out = readBMFont(fis, texture);
		fis.close();
		return out;
	}
	
	/**
	 * Reads in XML-Formatted BM Font (http://www.angelcode.com/products/bmfont/) generated metadata
	 * and creates an OGLGUIFont using the data.
	 * @param in the input stream for the XML data.
	 * @param texture the texture used for this font.
	 * @return the created font.
	 * @throws IOException if a read error happens.
	 */
	public static GUIFont readBMFont(InputStream in, String texture) throws IOException
	{
		GUIFont font = new GUIFont(texture);
		try {
			(new BMXMLReader()).read(font, in);
		} catch (ParserConfigurationException | SAXException ex) {
			throw new IOException(ex);
		}
		return font;
	}

	/**
	 * SAX Reader for BMFont.
	 */
	public static class BMXMLReader
	{
		protected static final String ELEMENT_INFO = "info";
		protected static final String ELEMENT_COMMON = "common";
		protected static final String ELEMENT_CHAR = "char";
		
		public synchronized void read(GUIFont font, InputStream in) throws ParserConfigurationException, IOException, SAXException
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			InputSource is = new InputSource(in);
			Handler handler = new Handler(font);
			parser.parse(is, handler);
		}
		
		private class Handler extends DefaultHandler
		{
			GUIFont font;
			float texWidth;
			float texHeight;
			float lineHeight;
			
			public Handler(GUIFont font)
			{
				super();
				this.font = font;
			}
			
			@Override
			public void startDocument() throws SAXException
			{
				font.setDefaultChar(0, 0, 0, 0, 0, 0, 0, 0, 0);
			}

			@Override
			public void endDocument() throws SAXException
			{
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attribs) throws SAXException
			{
				if (localName.equals(ELEMENT_INFO))
					parseInfo(attribs);
				else if (localName.equals(ELEMENT_COMMON))
					parseCommon(attribs);
				else if (localName.equals(ELEMENT_CHAR))
					parseChar(attribs);
			}

			// parses info block.
			private void parseInfo(Attributes attribs) throws SAXException
			{
				for (int i = 0; i < attribs.getLength(); i++)
				{
					String name = attribs.getLocalName(i);
					if (name.equals("face"))
						font.setName(attribs.getValue(i));
					else if (name.equals("size"))
						lineHeight = Float.parseFloat(attribs.getValue(i));
				}
			}
			
			// parses common block.
			private void parseCommon(Attributes attribs) throws SAXException
			{
				for (int i = 0; i < attribs.getLength(); i++)
				{
					String name = attribs.getLocalName(i);
					if (name.equals("scaleW"))
						texWidth = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("scaleH"))
						texHeight = Float.parseFloat(attribs.getValue(i));
				}
			}
			
			// parses common block.
			private void parseChar(Attributes attribs) throws SAXException
			{
				char id = '\0';
				float x = 0f;
				float y = 0f;
				float w = 0f;
				float h = 0f;
				float xofs = 0f;
				float yofs = 0f;
				float adv = 0f;
				
				for (int i = 0; i < attribs.getLength(); i++)
				{
					String name = attribs.getLocalName(i);
					if (name.equals("id"))
						id = (char)Integer.parseInt(attribs.getValue(i));
					else if (name.equals("x"))
						x = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("y"))
						y = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("width"))
						w = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("height"))
						h = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("xadvance"))
						adv = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("xoffset"))
						xofs = Float.parseFloat(attribs.getValue(i));
					else if (name.equals("yoffset"))
						yofs = Float.parseFloat(attribs.getValue(i));
				}
				
				font.addChar(id, 
					x/texWidth, y/texHeight, (x+w)/texWidth, (y+h)/texHeight, 
					w/lineHeight, h/lineHeight, adv/lineHeight, xofs/lineHeight, yofs/lineHeight
				);
			}

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException
			{
			}
			
			@Override
			public void error(SAXParseException e) throws SAXException
			{
				throw new SAXException(e);
			}
			
			@Override
			public void fatalError(SAXParseException e) throws SAXException
			{
				throw new SAXException(e);
			}

			@Override
			public void characters(char[] arg0, int arg1, int arg2) throws SAXException
			{
			}
			
		}
	}
}
