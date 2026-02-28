/*******************************************************************************
 * Copyright (c) 2019-2020 Black Rook Software
 * This program and the accompanying materials are made available under 
 * the terms of the MIT License, which accompanies this distribution.
 ******************************************************************************/
package com.blackrook.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.VolatileImage;

/**
 * A special canvas that writes to itself via buffered frames.
 * The canvas is only changed after a frame is committed, so no partial writes occur.
 * <p>This canvas uses {@link VolatileImage}s for painting.
 * @author Matthew Tropiano
 */
public class RasterCanvas extends Canvas
{
	private static final long serialVersionUID = -894844571993088725L;
	
	private static final ThreadLocal<Dimension> REUSED_DIMENSION = ThreadLocal.withInitial(()->new Dimension());
	private static final ThreadLocal<Rectangle> REUSED_RECTANGLE = ThreadLocal.withInitial(()->new Rectangle());

	/**
	 * Resampling types.
	 * @author Matthew Tropiano
	 */
	public enum ResamplingType
	{
		NEAREST
		{
			@Override
			public void setHints(Graphics2D g)
			{
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			}
		},
		
		LINEAR
		{
			@Override
			public void setHints(Graphics2D g)
			{
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		},
		
		BILINEAR
		{
			@Override
			public void setHints(Graphics2D g)
			{
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		},
		
		TRILINEAR
		{
			@Override
			public void setHints(Graphics2D g)
			{
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		},
		;
		
		/**
		 * Sets the rendering hints for this type.
		 * @param g the graphics context.
		 */
		public abstract void setHints(Graphics2D g);
		
	}
	
	/** The image buffer to write as the canvas contents. */
	private VolatileImage canvasBuffer;
	/** The image buffer to write to during the current frame. */
	private VolatileImage writableBuffer;
	/** Graphics context. */
	private Graphics2D writableGraphics;
	
	/** If true, keeps aspect correct in the renderable area when the canvas resizes. */
	private boolean correctAspect;
	
	/**
	 * Canvas render dimensions.
	 * If null, this uses the dimensions of the buffer.
	 */
	private Dimension renderDimensions;

	/** Resampling type. */
	private ResamplingType resamplingType;
	/** The canvas clear color. */
	private Color clearColor;
	
	/**
	 * Creates a new buffered canvas.
	 * The renderable area is the entirety of the canvas.
	 */
	public RasterCanvas()
	{
		this.canvasBuffer = null;
		this.writableBuffer = null;
		this.correctAspect = false;
		this.renderDimensions = null;
		this.resamplingType = null;
		this.clearColor = Color.BLACK;
	}
	
	/**
	 * Creates a new buffered canvas.
	 * @param x the renderable area width.
	 * @param y the renderable area height.
	 */
	public RasterCanvas(int x, int y)
	{
		this();
		this.renderDimensions = new Dimension(x, y);
	}
	
	/**
	 * Creates a new buffered canvas.
	 * @param x the renderable area width.
	 * @param y the renderable area height.
	 * @param correctAspect if true, keeps the renderable aspect correct.
	 */
	public RasterCanvas(int x, int y, boolean correctAspect)
	{
		this(x, y);
		this.correctAspect = correctAspect;
	}
	
	/**
	 * Sets the resampling type for scaling the screen on aspect correction.
	 * @param resamplingType the new resampling type.
	 */
	public void setResamplingType(ResamplingType resamplingType)
	{
		this.resamplingType = resamplingType;
	}
	
	/**
	 * Generates a new frame for writing and returns a {@link Graphics2D} context
	 * for updating the contents. Any uncommitted data is discarded.
	 * Do not dispose of this context - it is cleaned up later.
	 * @return the {@link Graphics2D} context to manipulate.
	 */
	public Graphics2D startFrame()
	{
		if (writableGraphics != null)
		{
			writableGraphics.dispose();
			writableGraphics = null;
		}
		
		writableBuffer = recreateVolatileImage(writableBuffer, getFrameWidth(), getFrameHeight(), Transparency.TRANSLUCENT);
		
		return writableGraphics = writableBuffer.createGraphics();
	}
	
	/**
	 * Returns a {@link Graphics2D} context for updating the contents of
	 * an already started buffer with {@link #startFrame()}. If {@link #startFrame()}
	 * was not called yet, this will return null.
	 * @return the {@link Graphics2D} context to manipulate, or null if a frame was not started.
	 */
	public Graphics2D continueFrame()
	{
		return writableGraphics;
	}
	
	/**
	 * Commits the new frame and calls {@link #repaint()}.
	 */
	public void finish()
	{
		if (writableGraphics != null)
		{
			writableGraphics.dispose();
			writableGraphics = null;
		}
		
		canvasBuffer = recreateVolatileImage(canvasBuffer, getWidth(), getHeight(), Transparency.OPAQUE);
		if (canvasBuffer != null)
		{
			Dimension size = REUSED_DIMENSION.get();
			Rectangle imageBounds = REUSED_RECTANGLE.get();
			
			size.width = getWidth();
			size.height = getHeight();

			getRenderedCanvasDimensions(size, imageBounds);
			
			Graphics2D g2d = (Graphics2D)canvasBuffer.getGraphics();
			g2d.setColor(clearColor);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			RenderingHints hints = g2d.getRenderingHints();
			
			if (resamplingType == null)
				ResamplingType.NEAREST.setHints(g2d);
			else
				resamplingType.setHints(g2d);
			
			g2d.setRenderingHints(hints);
			
			g2d.drawImage(writableBuffer, imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, null);
			g2d.dispose();
			repaint();
		}
	}
	
	/**
	 * Gets the width in pixels of the target frame created by {@link #startFrame()}.
	 * @return the width in pixels - the defined rendering width or the canvas width. 
	 */
	public int getFrameWidth()
	{
		return renderDimensions != null ? renderDimensions.width : getWidth();
	}
	
	/**
	 * Gets the height in pixels of the target frame created by {@link #startFrame()}.
	 * @return the height in pixels - the defined rendering height or the canvas height. 
	 */
	public int getFrameHeight()
	{
		return renderDimensions != null ? renderDimensions.height : getHeight();
	}
	
	/**
	 * Takes the source canvas dimensions and outputs the canvas coordinates for painting the rendered frame. 
	 * @param dimensionsIn the input dimensions.
	 * @param dimensionsOut the output dimensions.
	 */
	public void getRenderedCanvasDimensions(Dimension dimensionsIn, Rectangle dimensionsOut)
	{
		int frameWidth = getFrameWidth();
		int frameHeight = getFrameHeight();

		int canvasWidth = dimensionsIn.width;
		int canvasHeight = dimensionsIn.height;

		int x = 0;
		int y = 0;
		int width = canvasWidth;
		int height = canvasHeight;

		if (correctAspect)
		{
			double frameAspect = (double)frameWidth / (double)frameHeight;
			double canvasAspect = (double)canvasWidth / (double)canvasHeight;
			
			if (canvasAspect > frameAspect)
			{
				width = (int)(canvasHeight * frameAspect);
				x = (canvasWidth / 2) - (width / 2); 
			}
			else
			{
				height = (int)(canvasWidth * (1.0 / frameAspect));
				y = (canvasHeight / 2) - (height / 2); 
			}
		}
		
		dimensionsOut.setBounds(x, y, width, height);
	}
	
	/**
	 * Converts canvas component coordinates to the pixel coordinates on the rendered image.
	 * If this method returns true, the values in <code>canvasPixelsOut</code> are set.
	 * @param canvasCoordsIn the input coordinates.
	 * @param imageCoordsOut the returned coordinates.
	 * @return true if the input coordinates was inside the bounds of the painted portion, false if not.
	 */
	public boolean getFrameCursorCoordinates(Point canvasCoordsIn, Point imageCoordsOut)
	{
		Dimension size = REUSED_DIMENSION.get();
		Rectangle imageBounds = REUSED_RECTANGLE.get();
		
		size.width = getWidth();
		size.height = getHeight();
		
		getRenderedCanvasDimensions(size, imageBounds);

		if (canvasCoordsIn.x < imageBounds.x)
			return false;
		if (canvasCoordsIn.x >= imageBounds.x + imageBounds.width)
			return false;
		if (canvasCoordsIn.y < imageBounds.y)
			return false;
		if (canvasCoordsIn.y >= imageBounds.y + imageBounds.height)
			return false;
		
		imageCoordsOut.x = (int)((double)(canvasCoordsIn.x - imageBounds.x) / imageBounds.width * getFrameWidth());
		imageCoordsOut.y = (int)((double)(canvasCoordsIn.y - imageBounds.y) / imageBounds.height * getFrameHeight());
				
		return true;
	}
	
	@Override
	public final void update(Graphics g)
	{
		if (canvasBuffer != null)
			((Graphics2D)g).drawImage(canvasBuffer, 0, 0, null);
	}

	/**
	 * Creates/Recreates a new volatile image.
	 * A new VolatileImage, compatible with the current local graphics environment is returned
	 * if the current image passed into the method is invalid or incompatible. The image is deallocated
	 * if it is incompatible. If it is compatible or has different dimensions, nothing is reallocated, and
	 * it is returned.
	 * @param currentImage the previous VolatileImage to validate.
	 * @param width the width of the new image.
	 * @param height the height of the new image.
	 * @param transparency the transparency mode (from {@link Transparency}).
	 * @return a new or same volatile image. if null, can't be created for some reason.
	 */
	private static VolatileImage recreateVolatileImage(VolatileImage currentImage, int width, int height, int transparency)
	{
		GraphicsConfiguration gconfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		if (!revalidate(gconfig, currentImage, width, height))
		{
			if (currentImage != null)
				currentImage.flush();
			currentImage = null; // expedite Garbage Collection maybe why not
			
			if (width > 0 && height > 0)
				currentImage = gconfig.createCompatibleVolatileImage(width, height, transparency);
		}
		
		return currentImage;
	}
	
	// Revalidates a VolatileImage.
	private static boolean revalidate(GraphicsConfiguration gc, VolatileImage currentImage, int width, int height)
	{
		if (currentImage == null)
			return false;
		
		int valid = currentImage.validate(gc);
		
		switch (valid)
		{
			default:
			case VolatileImage.IMAGE_INCOMPATIBLE:
				return false;
			case VolatileImage.IMAGE_RESTORED:
			case VolatileImage.IMAGE_OK:
				return currentImage.getWidth() == width && currentImage.getHeight() == height;
		}
	}
}
