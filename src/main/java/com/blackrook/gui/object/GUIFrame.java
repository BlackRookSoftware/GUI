/*******************************************************************************
 * Copyright (c) 2014-2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gui.object;

import com.blackrook.gui.GUIAction;
import com.blackrook.gui.GUIEvent;
import com.blackrook.gui.GUILayout;
import com.blackrook.gui.GUIObject;
import com.blackrook.gui.layout.FramedLayout;

/**
 * A "framed" pane object that already adds the frame
 * elements around the object, as well as a means to
 * make the frame resizable around its edges, and draggable
 * at its top.  
 * @author Matthew Tropiano
 */
public class GUIFrame extends GUIGlassPanel
{
	/** Name of top-left panel. */
	public static final String TOP_LEFT_NAME = "top-left"; 
	/** Name of top-center panel. */
	public static final String TOP_CENTER_NAME = "top-center"; 
	/** Name of top-right panel. */
	public static final String TOP_RIGHT_NAME = "top-right"; 
	/** Name of bottom-left panel. */
	public static final String BOTTOM_LEFT_NAME = "bottom-left"; 
	/** Name of bottom-center panel. */
	public static final String BOTTOM_CENTER_NAME = "bottom-center"; 
	/** Name of bottom-right panel. */
	public static final String BOTTOM_RIGHT_NAME = "bottom-right"; 
	/** Name of middle-left panel. */
	public static final String MIDDLE_LEFT_NAME = "middle-left"; 
	/** Name of middle-right panel. */
	public static final String MIDDLE_RIGHT_NAME = "middle-right"; 
	/** Name of content panel. */
	public static final String CONTENT_NAME = "content"; 
	
	/** Top-left Panel. */
	private GUIPanel topLeftPanel;
	/** Top-center Panel. */
	private GUIPanel topCenterPanel;
	/** Top-right Panel. */
	private GUIPanel topRightPanel;
	/** Bottom-left Panel. */
	private GUIPanel bottomLeftPanel;
	/** Bottom-center Panel. */
	private GUIPanel bottomCenterPanel;
	/** Bottom-right Panel. */
	private GUIPanel bottomRightPanel;
	/** Middle-left Panel. */
	private GUIPanel middleLeftPanel;
	/** Middle-right Panel. */
	private GUIPanel middleRightPanel;
	/** Content Panel. */
	private GUIPanel contentPanel;
	
	/** Frame thickness. */
	private float thickness;
	/** Is this frame resizable? */
	private boolean resizable;
	/** Is this frame draggable? */
	private boolean draggable;
	
	/**
	 * Horizontal resize action.
	 */
	private final GUIAction RESIZABLE_RIGHT = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			if (resizable)
			{
				GUIObject obj = event.getObject().getParent();
				float mouseX = event.getMouseMovementX();
				float change = mouseX < 0f ? correctMouseMoveX(mouseX, obj.getBounds().width) : mouseX; 
				obj.stretch(change, 0);
			}
		}
	};
	
	/**
	 * Horizontal resize action.
	 */
	private final GUIAction RESIZABLE_LEFT = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			if (resizable)
			{
				GUIObject obj = event.getObject().getParent();
				float mouseX = event.getMouseMovementX();
				float change = mouseX > 0f ? correctMouseMoveX(mouseX, obj.getBounds().width) : mouseX; 
				obj.stretch(-change, 0);
				obj.translate(change, 0);
			}
		}
	};
	
	/**
	 * Vertical resize action.
	 */
	private final GUIAction RESIZABLE_BOTTOM = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			if (resizable)
			{
				GUIObject obj = event.getObject().getParent();
				float mouseY = event.getMouseMovementY();
				float change = mouseY < 0f ? correctMouseMoveY(mouseY, obj.getBounds().height) : mouseY; 
				obj.stretch(0, change);
			}
		}
	};
	
	/**
	 * Vertical resize action.
	 */
	private final GUIAction RESIZABLE_TOP = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			if (resizable)
			{
				GUIObject obj = event.getObject().getParent();
				float mouseY = event.getMouseMovementY();
				float change = mouseY > 0f ? correctMouseMoveY(mouseY, obj.getBounds().height) : mouseY; 
				obj.stretch(0, -change);
				obj.translate(0, change);
			}
		}
	};
	
	/**
	 * Vertical resize action.
	 */
	private final GUIAction RESIZABLE_TOP_OR_DRAG = new GUIAction()
	{
		@Override
		public void call(GUIEvent event)
		{
			if (draggable)
			{
				GUIObject obj = event.getObject().getParent();
				obj.translate(event.getMouseMovementX(), event.getMouseMovementY());
			}
			else if (resizable)
			{
				GUIObject obj = event.getObject().getParent();
				float mouseY = event.getMouseMovementY();
				float change = mouseY > 0f ? correctMouseMoveY(mouseY, obj.getBounds().height) : mouseY; 
				obj.stretch(0, -change);
				obj.translate(0, change);
			}
		}
	};
	
	/**
	 * Creates a new GUI panel.
	 * @param thickness the thickness of the outer frame in units.
	 */
	public GUIFrame(float thickness)
	{
		this(0, 0, 0, 0, thickness);
	}
	
	/**
	 * Creates a new GUI object.
	 * @param width its width.
	 * @param height its height.
	 * @param thickness 
	 */
	public GUIFrame(float width, float height, float thickness)
	{
		this(0, 0, width, height, thickness);
	}

	/**
	 * Creates a new GUI object.
	 * @param x its position x.
	 * @param y its position y.
	 * @param width its width.
	 * @param height its height.
	 * @param thickness the border thickness.
	 */
	public GUIFrame(float x, float y, float width, float height, float thickness)
	{
		super();
		setBounds(x, y, width, height);
		super.setLayout(new FramedLayout(thickness));
		
		this.thickness = thickness;
		this.resizable = false;
		this.draggable = false;
		
		topLeftPanel = new GUIPanel();
		topLeftPanel.bindAction(RESIZABLE_TOP, EVENT_MOUSE_DRAG);
		topLeftPanel.bindAction(RESIZABLE_LEFT, EVENT_MOUSE_DRAG);
		addChild(topLeftPanel, FramedLayout.Attrib.TOP_LEFT);
		
		topCenterPanel = new GUIPanel();
		topCenterPanel.bindAction(RESIZABLE_TOP_OR_DRAG, EVENT_MOUSE_DRAG);
		addChild(topCenterPanel, FramedLayout.Attrib.TOP_CENTER);

		topRightPanel = new GUIPanel();
		topRightPanel.bindAction(RESIZABLE_TOP, EVENT_MOUSE_DRAG);
		topRightPanel.bindAction(RESIZABLE_RIGHT, EVENT_MOUSE_DRAG);
		addChild(topRightPanel, FramedLayout.Attrib.TOP_RIGHT);
		
		middleLeftPanel = new GUIPanel();
		middleLeftPanel.bindAction(RESIZABLE_LEFT, EVENT_MOUSE_DRAG);
		addChild(middleLeftPanel, FramedLayout.Attrib.MIDDLE_LEFT);
		
		middleRightPanel = new GUIPanel();
		middleRightPanel.bindAction(RESIZABLE_RIGHT, EVENT_MOUSE_DRAG);
		addChild(middleRightPanel, FramedLayout.Attrib.MIDDLE_RIGHT);
		
		bottomLeftPanel = new GUIPanel();
		bottomLeftPanel.bindAction(RESIZABLE_BOTTOM, EVENT_MOUSE_DRAG);
		bottomLeftPanel.bindAction(RESIZABLE_LEFT, EVENT_MOUSE_DRAG);
		addChild(bottomLeftPanel, FramedLayout.Attrib.BOTTOM_LEFT);

		bottomCenterPanel = new GUIPanel();
		bottomCenterPanel.bindAction(RESIZABLE_BOTTOM, EVENT_MOUSE_DRAG);
		addChild(bottomCenterPanel, FramedLayout.Attrib.BOTTOM_CENTER);

		bottomRightPanel = new GUIPanel();
		bottomRightPanel.bindAction(RESIZABLE_BOTTOM, EVENT_MOUSE_DRAG);
		bottomRightPanel.bindAction(RESIZABLE_RIGHT, EVENT_MOUSE_DRAG);
		addChild(bottomRightPanel, FramedLayout.Attrib.BOTTOM_RIGHT);
		
		contentPanel = new GUIPanel();
		addChild(contentPanel, FramedLayout.Attrib.CONTENT);
	}
	
	/**
	 * Gets the corrected X coordinate for a mouse movement.
	 * @param mouseX the mouse X coordinate.
	 * @param width the frame element width.
	 * @return the resultant value.
	 */
	protected float correctMouseMoveX(float mouseX, float width)
	{
		float contentWidth = width - (thickness * 2);
		return (mouseX < 0 ? -1 : 1) * Math.min(contentWidth, Math.abs(mouseX)); 
	}
	
	/**
	 * Gets the corrected Y coordinate for a mouse movement.
	 * @param mouseY the mouse Y coordinate.
	 * @param height the frame element height.
	 * @return the resultant value.
	 */
	protected float correctMouseMoveY(float mouseY, float height)
	{
		float contentHeight = height - (thickness * 2);
		return (mouseY < 0 ? -1 : 1) * Math.min(contentHeight, Math.abs(mouseY)); 
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
	 * Sets if this is resizable by dragging the mouse at the edges.
	 * @param resizable
	 */
	public void setResizable(boolean resizable)
	{
		this.resizable = resizable;
	}
	
	/**
	 * Sets if this is draggable by dragging the mouse at the top center panel.
	 * @param draggable
	 */
	public void setDraggable(boolean draggable)
	{
		this.draggable = draggable;
	}
	
	/**
	 * @return the reference to the top-left panel in the frame.
	 */
	public GUIPanel getTopLeftPanel()
	{
		return topLeftPanel;
	}

	/**
	 * @return the reference to the top-center panel in the frame.
	 */
	public GUIPanel getTopCenterPanel()
	{
		return topCenterPanel;
	}

	/**
	 * @return the reference to the top-right panel in the frame.
	 */
	public GUIPanel getTopRightPanel()
	{
		return topRightPanel;
	}

	/**
	 * @return the reference to the bottom-left panel in the frame.
	 */
	public GUIPanel getBottomLeftPanel()
	{
		return bottomLeftPanel;
	}

	/**
	 * @return the reference to the bottom-center panel in the frame.
	 */
	public GUIPanel getBottomCenterPanel()
	{
		return bottomCenterPanel;
	}

	/**
	 * @return the reference to the bottom-right panel in the frame.
	 */
	public GUIPanel getBottomRightPanel()
	{
		return bottomRightPanel;
	}

	/**
	 * @return the reference to the middle-left panel in the frame.
	 */
	public GUIPanel getMiddleLeftPanel()
	{
		return middleLeftPanel;
	}

	/**
	 * @return the reference to the middle-right panel in the frame.
	 */
	public GUIPanel getMiddleRightPanel()
	{
		return middleRightPanel;
	}

	/**
	 * @return the reference to the content (middle) panel in the frame.
	 */
	public GUIPanel getContentPanel()
	{
		return contentPanel;
	}
	
}
