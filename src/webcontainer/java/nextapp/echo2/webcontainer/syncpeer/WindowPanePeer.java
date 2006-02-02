/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo2.webcontainer.syncpeer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImageBorder;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Pane;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.propertyrender.FillImageRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.ImageReferenceRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

/**
 * Synchronization peer for <code>nextapp.echo2.app.WindowPane</code>
 * components.
 * <p>
 * This class should not be extended or used by classes outside of the Echo
 * framework.
 */
public class WindowPanePeer 
implements ActionProcessor, DomUpdateSupport, ImageRenderSupport, PropertyUpdateProcessor, ComponentSynchronizePeer {

    /**
     * A boolean property which may be assigned to <code>WindowPane</code>s
     * in order to enable the proprietary Internet Explorer transparent PNG
     * alpha renderer for rendering the <code>border</code> property of the
     * window pane.
     */
    public static final String PROPERTY_IE_ALPHA_RENDER_BORDER
            = "nextapp.echo2.webcontainer.syncpeer.WindowPanePeer.ieAlphaRenderBorder";
    
    private static final Insets DEFAULT_CONTENT_INSETS = new Insets(3);
    private static final Insets DEFAULT_TITLE_INSETS = new Insets(4);
    private static final FillImageBorder DEFAULT_BORDER 
            = new FillImageBorder(new Color(0x00007f), new Insets(20), DEFAULT_CONTENT_INSETS);
    private static final Color DEFAULT_TITLE_BACKGROUND = new Color(0x005faf);
    private static final Color DEFAULT_TITLE_FOREGROUND = Color.WHITE;
    private static final Extent DEFAULT_POSITION_X = new Extent(64, Extent.PX);
    private static final Extent DEFAULT_POSITION_Y = new Extent(64, Extent.PX);
    private static final String DEFAULT_WIDTH = "512px";
    private static final String DEFAULT_HEIGHT = "256px";
    private static final String IMAGE_ID_TITLE_BACKGROUND = "titleBackground";
    private static final String IMAGE_ID_CLOSE_ICON = "close";
    private static final String IMAGE_ID_ICON = "icon";
    private static final String IMAGE_ID_BORDER_TOP_LEFT = "borderTopLeft";
    private static final String IMAGE_ID_BORDER_TOP = "borderTop";
    private static final String IMAGE_ID_BORDER_TOP_RIGHT = "borderTopRight";
    private static final String IMAGE_ID_BORDER_LEFT = "borderLeft";
    private static final String IMAGE_ID_BORDER_RIGHT = "borderRight";
    private static final String IMAGE_ID_BORDER_BOTTOM_LEFT = "borderBottomLeft";
    private static final String IMAGE_ID_BORDER_BOTTOM = "borderBottom";
    private static final String IMAGE_ID_BORDER_BOTTOM_RIGHT = "borderBottomRight";

    private static final ImageReference DEFAULT_CLOSE_ICON = new ResourceImageReference(
            "/nextapp/echo2/webcontainer/resource/image/DefaultCloseButton.gif");

    /**
     * Service to provide supporting JavaScript library.
     */
    public static final Service WINDOW_PANE_SERVICE = JavaScriptService.forResource("Echo.WindowPane",
            "/nextapp/echo2/webcontainer/resource/js/WindowPane.js");

    static {
        WebRenderServlet.getServiceRegistry().add(WINDOW_PANE_SERVICE);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        return ContainerInstance.getElementId(child.getParent()) + "_content";
    }

    /**
     * @see nextapp.echo2.webcontainer.image.ImageRenderSupport#getImage(nextapp.echo2.app.Component,
     *      java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_TITLE_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage = (FillImage) component.getRenderProperty(WindowPane.PROPERTY_TITLE_BACKGROUND_IMAGE);
            return backgroundImage == null ? null : backgroundImage.getImage();
        } else if (IMAGE_ID_ICON.equals(imageId)) {
            return (ImageReference) component.getRenderProperty(WindowPane.PROPERTY_ICON);
        } else if (IMAGE_ID_CLOSE_ICON.equals(imageId)) {
            return (ImageReference) component.getRenderProperty(WindowPane.PROPERTY_CLOSE_ICON, DEFAULT_CLOSE_ICON);
        } else if (IMAGE_ID_BORDER_TOP_LEFT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.TOP_LEFT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_TOP.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.TOP);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_TOP_RIGHT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.TOP_RIGHT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_LEFT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.LEFT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_RIGHT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.RIGHT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_BOTTOM_LEFT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.BOTTOM_LEFT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_BOTTOM.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.BOTTOM);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_BOTTOM_RIGHT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(WindowPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.BOTTOM_RIGHT);
            return fillImage == null ? null : fillImage.getImage();
        } else {
            return null;
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance,
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        WindowPane windowPane = (WindowPane) component;
        boolean closable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_CLOSABLE, Boolean.TRUE)).booleanValue();
        if (closable) {
            ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, WindowPane.INPUT_CLOSE, null);
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(
     *      nextapp.echo2.webcontainer.ContainerInstance,
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
        WindowPane windowPane = (WindowPane) component;
        boolean movable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_MOVABLE, Boolean.TRUE)).booleanValue();
        boolean resizable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_RESIZABLE, Boolean.TRUE)).booleanValue();
        if (WindowPane.PROPERTY_POSITION_X.equals(propertyName)) {
            if (movable) {
                ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, WindowPane.PROPERTY_POSITION_X,
                        ExtentRender.toExtent(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
            }
        } else if (WindowPane.PROPERTY_POSITION_Y.equals(propertyName)) {
            if (movable) {
                ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, WindowPane.PROPERTY_POSITION_Y,
                        ExtentRender.toExtent(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
            }
        } else if (WindowPane.PROPERTY_WIDTH.equals(propertyName)) {
            if (resizable) {
                ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, WindowPane.PROPERTY_WIDTH,
                        ExtentRender.toExtent(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
            }
        } else if (WindowPane.PROPERTY_HEIGHT.equals(propertyName)) {
            if (resizable) {
                ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, WindowPane.PROPERTY_HEIGHT,
                        ExtentRender.toExtent(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
            }
        } else if (WindowPane.Z_INDEX_CHANGED_PROPERTY.equals(propertyName)) {
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, WindowPane.Z_INDEX_CHANGED_PROPERTY,
                    new Integer(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
     *      nextapp.echo2.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
    }

    /**
     * Renders the <code>FillImageBorder</code> as HTML.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param windowDivElement the main DIV element of the window
     * @param windowPane the <code>WindowPane</code> instance
     */
    private void renderBorder(RenderContext rc, Element windowDivElement, WindowPane windowPane) {
        FillImageBorder border = (FillImageBorder) windowPane.getRenderProperty(WindowPane.PROPERTY_BORDER, DEFAULT_BORDER);
        Color borderColor = border.getColor();
        Insets borderInsets = border.getBorderInsets() == null ? new Insets(0) : border.getBorderInsets();
        Document document = rc.getServerMessage().getDocument();
        String elementId = ContainerInstance.getElementId(windowPane);
        boolean resizable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_RESIZABLE, Boolean.TRUE)).booleanValue();

        boolean renderPositioningBothSides = !rc.getContainerInstance().getClientProperties().getBoolean(
                ClientProperties.QUIRK_CSS_POSITIONING_ONE_SIDE_ONLY);
        boolean renderSizeExpression = !renderPositioningBothSides
                && rc.getContainerInstance().getClientProperties().getBoolean(
                        ClientProperties.PROPRIETARY_IE_CSS_EXPRESSIONS_SUPPORTED);
        int borderTopPixels = ExtentRender.toPixels(borderInsets.getTop(), 0);
        int borderLeftPixels = ExtentRender.toPixels(borderInsets.getLeft(), 0);
        int borderRightPixels = ExtentRender.toPixels(borderInsets.getRight(), 0);
        int borderBottomPixels = ExtentRender.toPixels(borderInsets.getBottom(), 0);
        int borderVerticalPixels = borderTopPixels + borderBottomPixels;
        int borderHorizontalPixels = borderLeftPixels + borderRightPixels;
        
        int fillImageRenderFlags = ((Boolean) windowPane.getRenderProperty(PROPERTY_IE_ALPHA_RENDER_BORDER, 
                Boolean.FALSE)).booleanValue() ? FillImageRender.FLAG_ENABLE_IE_PNG_ALPHA_FILTER : 0;

        // Top Left Corner
        Element borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_tl");
        CssStyle borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("top", "0px");
        borderCssStyle.setAttribute("left", "0px");
        borderCssStyle.setAttribute("height", borderTopPixels + "px");
        borderCssStyle.setAttribute("width", borderLeftPixels + "px");
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "nw-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_TOP_LEFT, border
                .getFillImage(FillImageBorder.TOP_LEFT), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Top Side
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_t");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("top", "0px");
        borderCssStyle.setAttribute("left", borderLeftPixels + "px");
        borderCssStyle.setAttribute("height", borderTopPixels + "px");
        if (renderPositioningBothSides) {
            borderCssStyle.setAttribute("right", borderRightPixels + "px");
        } else if (renderSizeExpression) {
            borderCssStyle.setAttribute("width", "expression((document.getElementById('" + elementId + "').offsetWidth-"
                    + (borderHorizontalPixels) + ")+'px')");
        }
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "n-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_TOP, border
                .getFillImage(FillImageBorder.TOP), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Top Right Corner
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_tr");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("top", "0px");
        borderCssStyle.setAttribute("right", "0px");
        borderCssStyle.setAttribute("height", borderTopPixels + "px");
        borderCssStyle.setAttribute("width", borderRightPixels + "px");
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "ne-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_TOP_RIGHT, border
                .getFillImage(FillImageBorder.TOP_RIGHT), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Left Side
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_l");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("top", borderTopPixels + "px");
        borderCssStyle.setAttribute("left", "0px");
        borderCssStyle.setAttribute("width", borderLeftPixels + "px");
        if (renderPositioningBothSides) {
            borderCssStyle.setAttribute("bottom", borderRightPixels + "px");
        } else if (renderSizeExpression) {
            borderCssStyle.setAttribute("height", "expression((document.getElementById('" + elementId + "').offsetHeight-"
                    + (borderVerticalPixels) + ")+'px')");

        }
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "w-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_LEFT, border
                .getFillImage(FillImageBorder.LEFT), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Right Side
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_r");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("top", borderTopPixels + "px");
        borderCssStyle.setAttribute("right", "0px");
        borderCssStyle.setAttribute("width", borderRightPixels + "px");
        if (renderPositioningBothSides) {
            borderCssStyle.setAttribute("bottom", borderRightPixels + "px");
        } else if (renderSizeExpression) {
            borderCssStyle.setAttribute("height", "expression((document.getElementById('" + elementId + "').offsetHeight-"
                    + (borderVerticalPixels) + ")+'px')");

        }
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "e-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_RIGHT, border
                .getFillImage(FillImageBorder.RIGHT), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Bottom Left Corner
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_bl");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("bottom", "0px");
        borderCssStyle.setAttribute("left", "0px");
        borderCssStyle.setAttribute("height", borderBottomPixels + "px");
        borderCssStyle.setAttribute("width", borderLeftPixels + "px");
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "sw-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_BOTTOM_LEFT, border
                .getFillImage(FillImageBorder.BOTTOM_LEFT), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Bottom Side
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_b");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("bottom", "0px");
        borderCssStyle.setAttribute("left", borderLeftPixels + "px");
        borderCssStyle.setAttribute("height", borderBottomPixels + "px");
        if (renderPositioningBothSides) {
            borderCssStyle.setAttribute("right", borderRightPixels + "px");
        } else if (renderSizeExpression) {
            borderCssStyle.setAttribute("width", "expression((document.getElementById('" + elementId + "').offsetWidth-"
                    + (borderHorizontalPixels) + ")+'px')");

        }
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "s-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_BOTTOM, border
                .getFillImage(FillImageBorder.BOTTOM), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);

        // Bottom Right Side
        borderDivElement = document.createElement("div");
        borderDivElement.setAttribute("id", elementId + "_border_br");
        borderCssStyle = new CssStyle();
        borderCssStyle.setAttribute("font-size", "1px");
        ColorRender.renderToStyle(borderCssStyle, null, borderColor);
        borderCssStyle.setAttribute("position", "absolute");
        borderCssStyle.setAttribute("bottom", "0px");
        borderCssStyle.setAttribute("right", "0px");
        borderCssStyle.setAttribute("height", borderBottomPixels + "px");
        borderCssStyle.setAttribute("width", borderRightPixels + "px");
        if (resizable) {
            borderCssStyle.setAttribute("cursor", "se-resize");
        }
        FillImageRender.renderToStyle(borderCssStyle, rc, this, windowPane, IMAGE_ID_BORDER_BOTTOM_RIGHT, border
                .getFillImage(FillImageBorder.BOTTOM_RIGHT), fillImageRenderFlags);
        borderDivElement.setAttribute("style", borderCssStyle.renderInline());
        windowDivElement.appendChild(borderDivElement);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate,
     *      nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        rc.getServerMessage().addLibrary(WINDOW_PANE_SERVICE.getId());
        renderDisposeDirective(rc, (WindowPane) component);
    }
    
    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to 
     * dispose the state of a window pane, performing tasks such as unregistering
     * event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param windowPane the <code>WindowPane</code>
     */
    private void renderDisposeDirective(RenderContext rc, WindowPane windowPane) {
        ServerMessage serverMessage = rc.getServerMessage();
        Element disposeElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE,
                "EchoWindowPane.MessageProcessor", "dispose");
        disposeElement.setAttribute("eid", ContainerInstance.getElementId(windowPane));
    }

    /**
     * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Node, nextapp.echo2.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
        WindowPane windowPane = (WindowPane) component;
        String elementId = ContainerInstance.getElementId(windowPane);
        String bodyElementId = elementId + "_body";
        ImageReference icon = (ImageReference) windowPane.getRenderProperty(WindowPane.PROPERTY_ICON);
        String title = (String) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE);
        Extent width = (Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_WIDTH);
        Extent height = (Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_HEIGHT);
        int titleHeightPixels = ExtentRender.toPixels((Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE_HEIGHT), 28);
        boolean movable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_MOVABLE, Boolean.TRUE)).booleanValue();
        boolean renderPositioningBothSides = !rc.getContainerInstance().getClientProperties().getBoolean(
                ClientProperties.QUIRK_CSS_POSITIONING_ONE_SIDE_ONLY);
        boolean renderSizeExpression = !renderPositioningBothSides
                && rc.getContainerInstance().getClientProperties().getBoolean(
                        ClientProperties.PROPRIETARY_IE_CSS_EXPRESSIONS_SUPPORTED);
        Component child = null;
        if (windowPane.getComponentCount() != 0) {
            child = windowPane.getComponent(0);
        }

        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(WINDOW_PANE_SERVICE.getId());
        Document document = serverMessage.getDocument();
        Element windowDivElement = document.createElement("div");
        windowDivElement.setAttribute("id", elementId);

        // Create main window DIV element.
        CssStyle windowDivCssStyle = new CssStyle();
        windowDivCssStyle.setAttribute("z-index", Integer.toString(windowPane.getZIndex()));
        windowDivCssStyle.setAttribute("padding", "0px");
        windowDivCssStyle.setAttribute("position", "absolute");
        windowDivCssStyle.setAttribute("top", ExtentRender.renderCssAttributePixelValue((Extent) windowPane.getRenderProperty(
                WindowPane.PROPERTY_POSITION_Y, DEFAULT_POSITION_Y)));
        windowDivCssStyle.setAttribute("left", ExtentRender.renderCssAttributePixelValue((Extent) windowPane.getRenderProperty(
                WindowPane.PROPERTY_POSITION_X, DEFAULT_POSITION_X)));
        if (width == null) {
            windowDivCssStyle.setAttribute("width", DEFAULT_WIDTH);
        } else {
            windowDivCssStyle.setAttribute("width", ExtentRender.renderCssAttributePixelValue(width));
        }
        if (height == null) {
            windowDivCssStyle.setAttribute("height", DEFAULT_HEIGHT);
        } else {
            windowDivCssStyle.setAttribute("height", ExtentRender.renderCssAttributePixelValue(height));
        }
        windowDivElement.setAttribute("style", windowDivCssStyle.renderInline());
        parentNode.appendChild(windowDivElement);

        // Render window border.
        renderBorder(rc, windowDivElement, windowPane);

        // Create window body DIV element (container of title and content
        // areas).
        Element windowBodyDivElement = document.createElement("div");
        windowBodyDivElement.setAttribute("id", bodyElementId);
        CssStyle windowBodyDivCssStyle = new CssStyle();
        ColorRender.renderToStyle(windowBodyDivCssStyle, (Color) windowPane.getRenderProperty(WindowPane.PROPERTY_FOREGROUND),
                (Color) windowPane.getRenderProperty(WindowPane.PROPERTY_BACKGROUND));
        FontRender.renderToStyle(windowBodyDivCssStyle, (Font) windowPane.getRenderProperty(WindowPane.PROPERTY_FONT));
        if (windowBodyDivCssStyle.getAttribute("background-color") == null) {
            windowBodyDivCssStyle.setAttribute("background-color", "white");
        }
        windowBodyDivCssStyle.setAttribute("position", "absolute");
        windowBodyDivCssStyle.setAttribute("z-index", "2");
        FillImageBorder border = (FillImageBorder) windowPane.getRenderProperty(WindowPane.PROPERTY_BORDER, DEFAULT_BORDER);
        Insets contentInsets = border.getContentInsets() == null ? new Insets(0) : border.getContentInsets();
        int left = ExtentRender.toPixels(contentInsets.getLeft(), 0);
        int right = ExtentRender.toPixels(contentInsets.getRight(), 0);
        int top = ExtentRender.toPixels(contentInsets.getTop(), 0);
        int bottom = ExtentRender.toPixels(contentInsets.getBottom(), 0);
        windowBodyDivCssStyle.setAttribute("top", top + "px");
        windowBodyDivCssStyle.setAttribute("left", left + "px");
        if (renderPositioningBothSides) {
            windowBodyDivCssStyle.setAttribute("bottom", bottom + "px");
            windowBodyDivCssStyle.setAttribute("right", right + "px");
        } else if (renderSizeExpression) {
            windowBodyDivCssStyle.setAttribute("height", "expression((document.getElementById('" + elementId + "').offsetHeight-"
                    + (top + bottom) + ")+'px')");
            windowBodyDivCssStyle.setAttribute("width", "expression((document.getElementById('" + elementId + "').offsetWidth-"
                    + (left + right) + ")+'px')");
        }
        windowBodyDivElement.setAttribute("style", windowBodyDivCssStyle.renderInline());
        windowDivElement.appendChild(windowBodyDivElement);

        // Create Internet Explorer Select-Element blocking IFRAME (if required).
        if (rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_IE_SELECT_Z_INDEX)) {
            Element iframeQuirkDivElement = document.createElement("div");
            // Reuse/modify windowBodyDivCssStyle.
            windowBodyDivCssStyle.setAttribute("z-index", "1");
            iframeQuirkDivElement.setAttribute("style", windowBodyDivCssStyle.renderInline());
            windowDivElement.appendChild(iframeQuirkDivElement);
            Element iframeQuirkIframeElement = document.createElement("iframe");
            iframeQuirkIframeElement.setAttribute("width", "100%");
            iframeQuirkIframeElement.setAttribute("height", "100%");
            iframeQuirkDivElement.appendChild(iframeQuirkIframeElement);
        }

        // Create title DIV container element.
        Element titleContainerDivElement = document.createElement("div");
        titleContainerDivElement.setAttribute("id", elementId + "_title");
        CssStyle titleContainerDivCssStyle = new CssStyle();
        if (movable) {
            titleContainerDivCssStyle.setAttribute("cursor", "move");
        }
        ColorRender.renderToStyle(titleContainerDivCssStyle, 
                (Color) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE_FOREGROUND, DEFAULT_TITLE_FOREGROUND), 
                (Color) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE_BACKGROUND, DEFAULT_TITLE_BACKGROUND));
        FontRender.renderToStyle(titleContainerDivCssStyle, (Font) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE_FONT));
        FillImageRender.renderToStyle(titleContainerDivCssStyle, rc, this, component, IMAGE_ID_TITLE_BACKGROUND,
                (FillImage) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE_BACKGROUND_IMAGE), 0);
        titleContainerDivCssStyle.setAttribute("position", "absolute");
        titleContainerDivCssStyle.setAttribute("top", "0px");
        titleContainerDivCssStyle.setAttribute("height", titleHeightPixels + "px");
        titleContainerDivCssStyle.setAttribute("width", "100%");
        titleContainerDivCssStyle.setAttribute("overflow", "hidden");
        titleContainerDivElement.setAttribute("style", titleContainerDivCssStyle.renderInline());
        windowBodyDivElement.appendChild(titleContainerDivElement);
        
        Insets titleInsets = (Insets) windowPane.getRenderProperty(WindowPane.PROPERTY_TITLE_INSETS, DEFAULT_TITLE_INSETS);
        
        // Add Title Icon.
        if (icon != null) {
            Insets titleIconInsets = (Insets) windowPane.getRenderProperty(WindowPane.PROPERTY_ICON_INSETS, titleInsets);
            Element titleIconDivElement = document.createElement("div");
            CssStyle titleIconDivCssStyle = new CssStyle();
            titleIconDivCssStyle.setAttribute("position", "absolute");
            titleIconDivCssStyle.setAttribute("left", "0px");
            titleIconDivCssStyle.setAttribute("top", "0px");
            titleIconDivCssStyle.setAttribute("white-space", "nowrap");
            titleIconDivCssStyle.setAttribute("overflow", "hidden");
            InsetsRender.renderToStyle(titleIconDivCssStyle, "padding", titleIconInsets);
            titleIconDivElement.setAttribute("style", titleIconDivCssStyle.renderInline());
            titleContainerDivElement.appendChild(titleIconDivElement);

            Element imgElement = ImageReferenceRender.renderImageReferenceElement(rc, this, windowPane, IMAGE_ID_ICON);
            titleIconDivElement.appendChild(imgElement);
        }
        
        // Add Title Text.
        if (title != null) {
            int titlePosition = icon == null ? 0 : 32;
            Element titleTextDivElement = document.createElement("div");
            CssStyle titleTextDivCssStyle = new CssStyle();
            titleTextDivCssStyle.setAttribute("position", "absolute");
            titleTextDivCssStyle.setAttribute("left", titlePosition + "px");
            titleTextDivCssStyle.setAttribute("top", "0px");
            titleTextDivCssStyle.setAttribute("white-space", "nowrap");
            titleTextDivCssStyle.setAttribute("overflow", "hidden");
            InsetsRender.renderToStyle(titleTextDivCssStyle, "padding", titleInsets);
            titleTextDivElement.setAttribute("style", titleTextDivCssStyle.renderInline());
            if (title != null) {
                DomUtil.setElementText(titleTextDivElement, title);
            }
            titleContainerDivElement.appendChild(titleTextDivElement);
        }
        
        // Add Close Icon.
        boolean closable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_CLOSABLE, Boolean.TRUE)).booleanValue();
        if (closable) {
            Insets closeIconInsets = (Insets) windowPane.getRenderProperty(WindowPane.PROPERTY_CLOSE_ICON_INSETS, titleInsets);
            Element closeIconDivElement = document.createElement("div");
            closeIconDivElement.setAttribute("id", elementId + "_close");
            CssStyle closeIconDivCssStyle = new CssStyle();
            closeIconDivCssStyle.setAttribute("cursor", "pointer");
            closeIconDivCssStyle.setAttribute("position", "absolute");
            closeIconDivCssStyle.setAttribute("right", "0px");
            closeIconDivCssStyle.setAttribute("top", "0px");
            InsetsRender.renderToStyle(closeIconDivCssStyle, "padding", closeIconInsets);
            closeIconDivElement.setAttribute("style", closeIconDivCssStyle.renderInline());
            titleContainerDivElement.appendChild(closeIconDivElement);
            
            Element imgElement = ImageReferenceRender.renderImageReferenceElement(rc, this, windowPane, IMAGE_ID_CLOSE_ICON);
            closeIconDivElement.appendChild(imgElement);
        }

        // Create outer content DIV Element.
        Element outerContentDivElement = document.createElement("div");
        outerContentDivElement.setAttribute("id", elementId + "_outercontent");
        CssStyle outerContentDivCssStyle = new CssStyle();
        outerContentDivCssStyle.setAttribute("overflow", "auto");
        outerContentDivCssStyle.setAttribute("padding", "0px");
        outerContentDivCssStyle.setAttribute("position", "absolute");
        outerContentDivCssStyle.setAttribute("width", "100%");
        outerContentDivCssStyle.setAttribute("top", titleHeightPixels + "px");
        if (renderPositioningBothSides) {
            outerContentDivCssStyle.setAttribute("bottom", "0px");
        }
        if (renderSizeExpression) {
            outerContentDivCssStyle.setAttribute("height", "expression((document.getElementById('" + bodyElementId
                    + "').offsetHeight-" + titleHeightPixels + ")+'px')");
        }
        outerContentDivElement.setAttribute("style", outerContentDivCssStyle.renderInline());
        windowBodyDivElement.appendChild(outerContentDivElement);

        // Create inset content DIV Element.
        Element contentDivElement = document.createElement("div");
        contentDivElement.setAttribute("id", elementId + "_content");
        CssStyle contentDivCssStyle = new CssStyle();
        if (!(child instanceof Pane)) {
            // Render inset padding only if pane content is not itself a Pane.
            InsetsRender.renderToStyle(contentDivCssStyle, "padding", 
                    (Insets) windowPane.getRenderProperty(WindowPane.PROPERTY_INSETS));
        }
        contentDivElement.setAttribute("style", contentDivCssStyle.renderInline());
        outerContentDivElement.appendChild(contentDivElement);

        // Render initialization directive.
        renderInitDirective(rc, windowPane);
        
        // Render child.
        if (child != null) {
            ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
            if (syncPeer instanceof DomUpdateSupport) {
                ((DomUpdateSupport) syncPeer).renderHtml(rc, update, contentDivElement, child);
            } else {
                syncPeer.renderAdd(rc, update, getContainerId(child), child);
            }
        }
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to 
     * initialize the state of a window pane, performing tasks such as 
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param windowPane the <code>WindowPane</code>
     */
    private void renderInitDirective(RenderContext rc, WindowPane windowPane) {
        String elementId = ContainerInstance.getElementId(windowPane);
        ServerMessage serverMessage = rc.getServerMessage();
        boolean closable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_CLOSABLE, Boolean.TRUE)).booleanValue();
        boolean movable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_MOVABLE, Boolean.TRUE)).booleanValue();
        boolean resizable = ((Boolean) windowPane.getRenderProperty(WindowPane.PROPERTY_RESIZABLE, Boolean.TRUE)).booleanValue();
        String minimumWidth = ExtentRender.renderCssAttributePixelValue(
                (Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_MINIMUM_WIDTH));
        String minimumHeight = ExtentRender.renderCssAttributePixelValue(
                (Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_MINIMUM_HEIGHT));
        String maximumWidth = ExtentRender.renderCssAttributePixelValue(
                (Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_MAXIMUM_WIDTH));
        String maximumHeight = ExtentRender.renderCssAttributePixelValue(
                (Extent) windowPane.getRenderProperty(WindowPane.PROPERTY_MAXIMUM_HEIGHT));
        
        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, 
                "EchoWindowPane.MessageProcessor", "init");
        initElement.setAttribute("eid", elementId);
        initElement.setAttribute("closable", closable ? "true" : "false");
        initElement.setAttribute("movable", movable ? "true" : "false");
        initElement.setAttribute("resizable", resizable ? "true" : "false");
        initElement.setAttribute("container-id", windowPane.getParent().getRenderId());
        if (minimumWidth != null) {
            initElement.setAttribute("minimum-width", minimumWidth);
        }
        if (minimumHeight != null) {
            initElement.setAttribute("minimum-height", minimumHeight);
        }
        if (maximumWidth != null) {
            initElement.setAttribute("maximum-width", maximumWidth);
        }
        if (maximumHeight != null) {
            initElement.setAttribute("maximum-height", maximumHeight);
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
        renderAdd(rc, update, targetId, update.getParent());
        return true;
    }
}