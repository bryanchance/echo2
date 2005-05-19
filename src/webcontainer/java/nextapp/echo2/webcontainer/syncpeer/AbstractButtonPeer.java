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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.CheckBox;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.RadioButton;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.app.button.AbstractButton;
import nextapp.echo2.app.button.ButtonGroup;
import nextapp.echo2.app.button.ToggleButton;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.SynchronizePeer;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.image.ImageTools;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FillImageRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.ImageReferenceRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webrender.clientupdate.DomPropertyStore;
import nextapp.echo2.webrender.clientupdate.DomUpdate;
import nextapp.echo2.webrender.clientupdate.EventUpdate;
import nextapp.echo2.webrender.clientupdate.ServerMessage;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.server.Service;
import nextapp.echo2.webrender.server.WebRenderServlet;
import nextapp.echo2.webrender.services.JavaScriptService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Synchronization peer for 
 * <code>nextapp.echo2.app.AbstractButton</code>-derived components.
 */
public class AbstractButtonPeer 
implements ActionProcessor, DomUpdateSupport, ImageRenderSupport, PropertyUpdateProcessor, SynchronizePeer {
    
    //BUGBUG. support rollover and pressed properties where base property value is null (currently renders wrong).

    private static final Alignment DEFAULT_TEXT_POSITION = new Alignment(Alignment.TRAILING, Alignment.DEFAULT);
    private static final Alignment DEFAULT_STATE_POSITION = new Alignment(Alignment.LEADING, Alignment.DEFAULT);
    private static final Extent DEFAULT_ICON_TEXT_MARGIN = new Extent(3);
    private static final ImageReference DEFAULT_CHECKBOX_ICON
            = new ResourceImageReference("/nextapp/echo2/webcontainer/resource/image/CheckBoxOff.gif");
    private static final ImageReference DEFAULT_SELECTED_CHECKBOX_ICON 
            = new ResourceImageReference("/nextapp/echo2/webcontainer/resource/image/CheckBoxOn.gif");
    private static final ImageReference DEFAULT_RADIOBUTTON_ICON
            = new ResourceImageReference("/nextapp/echo2/webcontainer/resource/image/RadioButtonOff.gif");
    private static final ImageReference DEFAULT_SELECTED_RADIOBUTTON_ICON 
            = new ResourceImageReference("/nextapp/echo2/webcontainer/resource/image/RadioButtonOn.gif");
    
    private static final String IMAGE_ID_BACKGROUND = "background";
    private static final String IMAGE_ID_ICON = "icon";
    private static final String IMAGE_ID_ROLLOVER_BACKGROUND = "rolloverBackground";
    private static final String IMAGE_ID_ROLLOVER_ICON = "rolloverIcon";
    private static final String IMAGE_ID_PRESSED_BACKGROUND = "pressedBackground";
    private static final String IMAGE_ID_PRESSED_ICON = "pressedIcon";
    private static final String IMAGE_ID_STATE_ICON = "stateIcon";
    private static final String IMAGE_ID_SELECTED_STATE_ICON = "selectedStateIcon";
    
    /**
     * Service to provide supporting JavaScript library.
     */
    public static final Service BUTTON_SERVICE = JavaScriptService.forResource("Echo.Button", 
            "/nextapp/echo2/webcontainer/resource/js/Button.js");

    static {
        WebRenderServlet.getServiceRegistry().add(BUTTON_SERVICE);
    }
    
    private static final String[] SET_GROUP_KEYS = new String[]{"groupid"};
    
    /**
     * Converts the value of the <code>alignment</code> property of an 
     * <code>AbstractButton</code> into a value suitable to be passed to a
     * <code>TriCellTable</code> as an <code>orientation</code> constructor
     * parameter.
     * 
     * @param alignment the <code>Alignment</code>
     * @param component the button being rendered
     * @return the <code>orientation</code> value
     */
    private int convertIconTextPositionToOrientation(Alignment alignment, Component component) {
        if (alignment.getVertical() == Alignment.DEFAULT) {
            if (alignment.getRenderedHorizontal(component) == Alignment.LEFT) {
                return TriCellTable.LEFT_RIGHT;
            } else {
                return TriCellTable.RIGHT_LEFT;
            }
        } else {
            if (alignment.getVertical() == Alignment.TOP) {
                return TriCellTable.TOP_BOTTOM;
            } else {
                return TriCellTable.BOTTOM_TOP;
            }
        }
    }
    
    /**
     * Converts the value of the <code>stateAlignment</code> property of a 
     * <code>ToggleButton</code> into a value suitable to be passed to a
     * <code>TriCellTable</code> as an <code>orientation</code> constructor
     * parameter.
     * 
     * @param alignment the state <code>Alignment</code>
     * @param component the button being rendered
     * @return the <code>orientation</code> value
     */
    private int convertStatePositionToOrientation(Alignment alignment, Component component) {
        if (alignment.getVertical() == Alignment.DEFAULT) {
            if (alignment.getRenderedHorizontal(component) == Alignment.LEFT) {
                return TriCellTable.RIGHT_LEFT;
            } else {
                return TriCellTable.LEFT_RIGHT;
            }
        } else {
            if (alignment.getVertical() == Alignment.TOP) {
                return TriCellTable.BOTTOM_TOP;
            } else {
                return TriCellTable.TOP_BOTTOM;
            }
        }
    }
    
    /**
     * Appends a directive to the outgoing <code>ServerMessage</code> to 
     * assign the specified button element identifier to the specified 
     * radio group identifier.
     * 
     * @param serverMessage the <code>serverMessage</code>
     * @param elementId the HTML id of the button
     * @param groupId the unique radio button group identifier
     */
    private void createSetGroup(ServerMessage serverMessage, String elementId, String groupId) {
        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_UPDATE, 
                "EchoButton.MessageProcessor", "setgroup", SET_GROUP_KEYS, new String[]{groupId});
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        itemizedUpdateElement.appendChild(itemElement);
    }
    
    private void createInit(ServerMessage serverMessage, String elementId) {
        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_UPDATE,
                "EchoButton.MessageProcessor", "init",  new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        itemizedUpdateElement.appendChild(itemElement);
    }
    
    private void createDispose(ServerMessage serverMessage, String elementId) {
        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_REMOVE,
                "EchoButton.MessageProcessor", "dispose",  new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        itemizedUpdateElement.appendChild(itemElement);
    }
    
    /**
     * Determines the selected state icon of the specified
     * <code>ToggleButton</code>.
     * 
     * @param toggleButton the <code>ToggleButton</code>
     * @return the selected state icon
     */
    private ImageReference getSelectedStateIcon(ToggleButton toggleButton) {
        ImageReference selectedStateIcon 
                = (ImageReference) toggleButton.getRenderProperty(ToggleButton.PROPERTY_SELECTED_STATE_ICON);
        if (selectedStateIcon == null) {
            if (toggleButton instanceof CheckBox) {
                selectedStateIcon = DEFAULT_SELECTED_CHECKBOX_ICON;
            } else if (toggleButton instanceof RadioButton) {
                selectedStateIcon = DEFAULT_SELECTED_RADIOBUTTON_ICON;
            }
        }
        return selectedStateIcon;
    }
    
    /**
     * Determines the default (non-selected) state icon of the specified
     * <code>ToggleButton</code>.
     * 
     * @param toggleButton the <code>ToggleButton</code>
     * @return the state icon
     */
    private ImageReference getStateIcon(ToggleButton toggleButton) {
        ImageReference stateIcon = (ImageReference) toggleButton.getRenderProperty(ToggleButton.PROPERTY_STATE_ICON);
        if (stateIcon == null) {
            if (toggleButton instanceof CheckBox) {
                stateIcon = DEFAULT_CHECKBOX_ICON;
            } else if (toggleButton instanceof RadioButton) {
                stateIcon = DEFAULT_RADIOBUTTON_ICON;
            }
        }
        return stateIcon;
    }
    
    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
    }
    
    /**
     * @see nextapp.echo2.webcontainer.image.ImageRenderSupport#getImage(nextapp.echo2.app.Component, 
     *      java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_ICON.equals(imageId)) {
            return (ImageReference) component.getRenderProperty(AbstractButton.PROPERTY_ICON);
        } else if (IMAGE_ID_ROLLOVER_ICON.equals(imageId)) {
            return (ImageReference) component.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_ICON);
        } else if (IMAGE_ID_PRESSED_ICON.equals(imageId)) {
            return (ImageReference) component.getRenderProperty(AbstractButton.PROPERTY_PRESSED_ICON);
        } else if (IMAGE_ID_STATE_ICON.equals(imageId)) {
            return getStateIcon((ToggleButton) component);
        } else if (IMAGE_ID_SELECTED_STATE_ICON.equals(imageId)) {
            return getSelectedStateIcon((ToggleButton) component);
        } else if (IMAGE_ID_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage 
                    = (FillImage) component.getRenderProperty(AbstractButton.PROPERTY_BACKGROUND_IMAGE);
            if (backgroundImage == null) {
                return null;
            } else {
                return backgroundImage.getImage();
            }
        } else if (IMAGE_ID_ROLLOVER_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage 
                    = (FillImage) component.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_BACKGROUND_IMAGE);
            if (backgroundImage == null) {
                return null;
            } else {
                return backgroundImage.getImage();
            }
        } else if (IMAGE_ID_PRESSED_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage 
                    = (FillImage) component.getRenderProperty(AbstractButton.PROPERTY_PRESSED_BACKGROUND_IMAGE);
            if (backgroundImage == null) {
                return null;
            } else {
                return backgroundImage.getImage();
            }
        } else {
            return null;
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, 
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        ci.getUpdateManager().setClientAction(component, AbstractButton.INPUT_CLICK, null);
    }

    /**
     * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(
     *      nextapp.echo2.webcontainer.ContainerInstance, nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        if (ToggleButton.SELECTED_CHANGED_PROPERTY.equals(propertyElement.getAttribute(PROPERTY_NAME))) {
            ToggleButton toggleButton = (ToggleButton) component;
            toggleButton.setSelected("true".equals(propertyElement.getAttribute("value")));
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String, nextapp.echo2.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element contentElement = DomUpdate.createDomAdd(rc.getServerMessage(), targetId);
        renderHtml(rc, update, contentElement, component);
    }
    
    /**
     * Renders the content of the button, i.e., its text, icon, and/or state icon.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param buttonContainerElement the <code>Element</code> which will 
     *        contain the content
     * @param button the <code>AbstractButton</code> being rendered
     */
    private void renderButtonContent(RenderContext rc, Element buttonContainerElement, AbstractButton button) {
        Node contentNode;
        Document document = rc.getServerMessage().getDocument();
        ToggleButton toggleButton = button instanceof ToggleButton ? (ToggleButton) button : null;
        String elementId = ContainerInstance.getElementId(button);
        
        String text = (String) button.getRenderProperty(AbstractButton.PROPERTY_TEXT);
        ImageReference icon = (ImageReference) button.getRenderProperty(AbstractButton.PROPERTY_ICON);
        
        // Create entities.
        Text textNode = text == null ? null : rc.getServerMessage().getDocument().createTextNode(
                (String) button.getRenderProperty(AbstractButton.PROPERTY_TEXT));
        
        Element iconElement;
        if (icon == null) {
            iconElement = null;
        } else {
            iconElement = ImageReferenceRender.renderImageReferenceElement(rc, AbstractButtonPeer.this, button, 
                    IMAGE_ID_ICON);
            iconElement.setAttribute("id", elementId + "_icon");
        }

        Element stateIconElement;
        if (toggleButton == null) {
            stateIconElement = null;
        } else {
            stateIconElement = ImageReferenceRender.renderImageReferenceElement(rc, AbstractButtonPeer.this, button, 
                    toggleButton.isSelected() ? IMAGE_ID_SELECTED_STATE_ICON : IMAGE_ID_STATE_ICON);
            stateIconElement.setAttribute("id", elementId + "_stateicon");
        }
        
        int entityCount = (textNode == null ? 0 : 1) + (iconElement == null ? 0 : 1) + (stateIconElement == null ? 0 : 1);
        
        Extent iconTextMargin;
        Alignment textPosition;
        
        switch (entityCount) {
        case 1:
            if (textNode != null) {
                contentNode = textNode;
            } else if (iconElement != null) {
                contentNode = iconElement;
            } else { // stateIconElement must not be null.
                contentNode = stateIconElement;
            }
            break;
        case 2:
            iconTextMargin = (Extent) button.getRenderProperty(AbstractButton.PROPERTY_ICON_TEXT_MARGIN, 
                    DEFAULT_ICON_TEXT_MARGIN);
            TriCellTable tct;
            textPosition = (Alignment) button.getRenderProperty(AbstractButton.PROPERTY_TEXT_POSITION, 
                    DEFAULT_TEXT_POSITION);
            if (stateIconElement == null) {
                // Not rendering a ToggleButton.
                int orientation = convertIconTextPositionToOrientation(textPosition, button);
                tct = new TriCellTable(document, elementId, orientation, iconTextMargin);
                
                renderCellText(tct, textNode, button);
                renderCellIcon(tct, iconElement, 1);
                
                Element tableElement = tct.getTableElement();
                tableElement.setAttribute("id", elementId + "_table");
                contentNode = tableElement;
            } else {
                 // Rendering a ToggleButton.
                Extent stateMargin = (Extent) button.getRenderProperty(ToggleButton.PROPERTY_STATE_MARGIN, 
                        DEFAULT_ICON_TEXT_MARGIN);
                Alignment statePosition = (Alignment) button.getRenderProperty(ToggleButton.PROPERTY_STATE_POSITION,
                        DEFAULT_STATE_POSITION);
                int orientation = convertStatePositionToOrientation(statePosition, button);
                tct = new TriCellTable(document, elementId, orientation, stateMargin);

                if (textNode == null) {
                    renderCellIcon(tct, iconElement, 0);
                } else {
                    renderCellText(tct, textNode, button);
                }
                renderCellState(tct, stateIconElement, 1, button);
                
                Element tableElement = tct.getTableElement();
                tableElement.setAttribute("id", elementId + "_table");
                contentNode = tableElement;
            }
            break;
        case 3:
            iconTextMargin = (Extent) button.getRenderProperty(AbstractButton.PROPERTY_ICON_TEXT_MARGIN, 
                    DEFAULT_ICON_TEXT_MARGIN);
            textPosition = (Alignment) button.getRenderProperty(AbstractButton.PROPERTY_TEXT_POSITION, 
                    DEFAULT_TEXT_POSITION);
            Extent stateMargin = (Extent) button.getRenderProperty(ToggleButton.PROPERTY_STATE_MARGIN, 
                    DEFAULT_ICON_TEXT_MARGIN);
            Alignment statePosition = (Alignment) button.getRenderProperty(ToggleButton.PROPERTY_STATE_POSITION,
                    DEFAULT_STATE_POSITION);
            int stateOrientation = convertStatePositionToOrientation(statePosition, button);
            int orientation = convertIconTextPositionToOrientation(textPosition, button);
            tct = new TriCellTable(document, elementId, orientation, iconTextMargin, stateOrientation, stateMargin);

            renderCellText(tct, textNode, button);
            renderCellIcon(tct, iconElement, 1);
            renderCellState(tct, stateIconElement, 2, button);
            
            Element tableElement = tct.getTableElement();
            tableElement.setAttribute("id", elementId + "_table");
            contentNode = tableElement;
            break;
        default:
            // 0 element button.
            contentNode = null;
        }
        
        if (contentNode != null) {
            buttonContainerElement.appendChild(contentNode);
        }
    }
    
    /**
     * Renders the content of the <code>TriCellTable</code> cell which 
     * contains the button's icon.
     * 
     * @param tct the <code>TriCellTable</code> to update
     * @param iconElement the icon element
     * @param cellIndex the index of the cell in the <code>TriCellTable</code>
     *        that should contain the icon
     */
    private void renderCellIcon(TriCellTable tct, Element iconElement, int cellIndex) {
        Element iconTdElement = tct.getTdElement(cellIndex);
        iconTdElement.setAttribute("style", "padding: 0px");
        iconTdElement.appendChild(iconElement);
    }
    
    /**
     * Renders the content of the <code>TriCellTable</code> cell which 
     * contains the button's state icon.
     * 
     * @param tct the <code>TriCellTable</code> to update
     * @param stateIconElement the state icon element
     * @param cellIndex the index of the cell in the <code>TriCellTable</code>
     *        that should contain the state icon
     * @param button the <code>AbstractButton</code> being rendered
     */
    private void renderCellState(TriCellTable tct, Element stateIconElement, int cellIndex, AbstractButton button) {
        Element stateTdElement = tct.getTdElement(cellIndex);
        CssStyle stateTdCssStyle = new CssStyle();
        stateTdCssStyle.setAttribute("padding", "0px");
        AlignmentRender.renderToStyle(stateTdCssStyle, button, 
                (Alignment) button.getRenderProperty(ToggleButton.PROPERTY_STATE_ALIGNMENT));
        stateTdElement.setAttribute("style", stateTdCssStyle.renderInline());
        stateTdElement.appendChild(stateIconElement);
    }
    
    /**
     * Renders the content of the <code>TriCellTable</code> cell which 
     * contains the button's text.
     * Text is always rendered in cell #0 of the table.
     * 
     * @param tct the <code>TriCellTable</code> to update
     * @param textNode the text
     * @param button the <code>AbstractButton</code> being rendered
     */
    private void renderCellText(TriCellTable tct, Text textNode, AbstractButton button) {
        Element textTdElement = tct.getTdElement(0);
        CssStyle textTdCssStyle = new CssStyle();
        textTdCssStyle.setAttribute("padding", "0px");
        AlignmentRender.renderToStyle(textTdCssStyle, button, 
                (Alignment) button.getRenderProperty(AbstractButton.PROPERTY_TEXT_ALIGNMENT));
        textTdElement.setAttribute("style", textTdCssStyle.renderInline());
        textTdElement.appendChild(textNode);
    }
    
    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        ServerMessage serverMessage = rc.getServerMessage();
        String id = ContainerInstance.getElementId(component);
//BUGBUG testing new method.        
//        EventUpdate.createEventRemove(serverMessage, "click,mouseover,mousedown,mouseout,mouseup", id);
        createDispose(serverMessage, id);
    }

    /**
     * @see nextapp.echo2.webcontainer.DomUpdateSupport#renderHtml(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, org.w3c.dom.Element, nextapp.echo2.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Element parent, Component component) {
        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(BUTTON_SERVICE.getId(), true);
        AbstractButton button = (AbstractButton) component;
        FillImage backgroundImage = (FillImage) button.getRenderProperty(AbstractButton.PROPERTY_BACKGROUND_IMAGE);
        
        Document document = parent.getOwnerDocument();
        Element divElement = document.createElement("div");
        
        String id = ContainerInstance.getElementId(button);
        
        divElement.setAttribute("id", id);
        
        CssStyle cssStyle = new CssStyle();
        cssStyle.setAttribute("cursor", "pointer");
        cssStyle.setAttribute("margin", "0px");
        cssStyle.setAttribute("border-spacing", "0px");
        ExtentRender.renderToStyle(cssStyle, "width", (Extent) button.getRenderProperty(AbstractButton.PROPERTY_WIDTH));
        Extent height = (Extent) button.getRenderProperty(AbstractButton.PROPERTY_HEIGHT);
        if (height != null) {
            ExtentRender.renderToStyle(cssStyle, "height", height);
            cssStyle.setAttribute("overflow", "hidden");
        }
        BorderRender.renderToStyle(cssStyle, (Border) button.getRenderProperty(AbstractButton.PROPERTY_BORDER));
        ColorRender.renderToStyle(cssStyle, (Color) button.getRenderProperty(AbstractButton.PROPERTY_FOREGROUND), 
                (Color) button.getRenderProperty(AbstractButton.PROPERTY_BACKGROUND));
        InsetsRender.renderToStyle(cssStyle, "padding", (Insets) button.getRenderProperty(AbstractButton.PROPERTY_INSETS));
        FontRender.renderToStyle(cssStyle, (Font) button.getRenderProperty(AbstractButton.PROPERTY_FONT));
        FillImageRender.renderToStyle(cssStyle, rc, this, button, IMAGE_ID_BACKGROUND, backgroundImage, true);
        
        divElement.setAttribute("style", cssStyle.renderInline());
        
        parent.appendChild(divElement);
        
        boolean rolloverEnabled = ((Boolean) button.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_ENABLED, 
                Boolean.FALSE)).booleanValue();
        boolean pressedEnabled = ((Boolean) button.getRenderProperty(AbstractButton.PROPERTY_PRESSED_ENABLED, 
                Boolean.FALSE)).booleanValue();
        boolean addRolloverListener = false;
        
        if (rolloverEnabled || pressedEnabled) {
            CssStyle baseCssStyle = new CssStyle();
            BorderRender.renderToStyle(baseCssStyle, (Border) button.getRenderProperty(AbstractButton.PROPERTY_BORDER));
            ColorRender.renderToStyle(baseCssStyle, (Color) button.getRenderProperty(AbstractButton.PROPERTY_FOREGROUND), 
                    (Color) button.getRenderProperty(AbstractButton.PROPERTY_BACKGROUND));
            FontRender.renderToStyle(baseCssStyle, (Font) button.getRenderProperty(AbstractButton.PROPERTY_FONT));
            FillImageRender.renderToStyle(baseCssStyle, rc, this, button, IMAGE_ID_BACKGROUND,
                    (FillImage) button.getRenderProperty(AbstractButton.PROPERTY_BACKGROUND_IMAGE), true);
            DomPropertyStore.createDomPropertyStore(serverMessage, id, "baseStyle", 
                    baseCssStyle.renderInline());
            
            if (rolloverEnabled) {
                CssStyle rolloverCssStyle = new CssStyle();
                BorderRender.renderToStyle(rolloverCssStyle, 
                        (Border) button.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_BORDER));
                ColorRender.renderToStyle(rolloverCssStyle, 
                        (Color) button.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_FOREGROUND),
                        (Color) button.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_BACKGROUND));
                FontRender.renderToStyle(rolloverCssStyle, 
                        (Font) button.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_FONT));
                if (backgroundImage != null) {
                    FillImageRender.renderToStyle(rolloverCssStyle, rc, this, button, IMAGE_ID_ROLLOVER_BACKGROUND,
                            (FillImage) button.getRenderProperty(AbstractButton.PROPERTY_ROLLOVER_BACKGROUND_IMAGE), true);
                }
                if (rolloverCssStyle.hasAttributes()) {
                    DomPropertyStore.createDomPropertyStore(serverMessage, id, "rolloverStyle", 
                            rolloverCssStyle.renderInline());
                    addRolloverListener = true;
                }
            }
            
            if (pressedEnabled) {
                CssStyle pressedCssStyle = new CssStyle();
                BorderRender.renderToStyle(pressedCssStyle, 
                        (Border) button.getRenderProperty(AbstractButton.PROPERTY_PRESSED_BORDER));
                ColorRender.renderToStyle(pressedCssStyle, 
                        (Color) button.getRenderProperty(AbstractButton.PROPERTY_PRESSED_FOREGROUND),
                        (Color) button.getRenderProperty(AbstractButton.PROPERTY_PRESSED_BACKGROUND));
                FontRender.renderToStyle(pressedCssStyle, 
                        (Font) button.getRenderProperty(AbstractButton.PROPERTY_PRESSED_FONT));
                if (backgroundImage != null) {
                    FillImageRender.renderToStyle(pressedCssStyle, rc, this, button, IMAGE_ID_PRESSED_BACKGROUND,
                            (FillImage) button.getRenderProperty(AbstractButton.PROPERTY_PRESSED_BACKGROUND_IMAGE), true);
                }
                if (pressedCssStyle.hasAttributes()) {
                    DomPropertyStore.createDomPropertyStore(serverMessage, id, "pressedStyle", 
                            pressedCssStyle.renderInline());
                }
            }
        }
        
        //BUGBUG. testing new method.
        /*
        if (addRolloverListener) {
            EventUpdate.createEventAdd(serverMessage, "click,mousedown,mouseover", id, 
                    "EchoButton.doAction,EchoButton.doPressed,EchoButton.doRolloverEnter");
        } else {
            EventUpdate.createEventAdd(serverMessage, "click,mousedown", id, 
                    "EchoButton.doAction,EchoButton.doPressed");
        }
        */
        createInit(serverMessage, id);

        renderButtonContent(rc, divElement, button);

        if (button instanceof ToggleButton) {
            ToggleButton toggleButton = (ToggleButton) button;
            DomPropertyStore.createDomPropertyStore(serverMessage, id, "toggle", "true");
            DomPropertyStore.createDomPropertyStore(serverMessage, id, "selected", 
                    toggleButton.isSelected() ? "true" : " false");
            DomPropertyStore.createDomPropertyStore(serverMessage, id, "stateIcon", 
                    ImageTools.getUri(rc, this, toggleButton, IMAGE_ID_STATE_ICON));
            DomPropertyStore.createDomPropertyStore(serverMessage, id, "selectedStateIcon", 
                    ImageTools.getUri(rc, this, toggleButton, IMAGE_ID_SELECTED_STATE_ICON));

            if (button instanceof RadioButton) {
                ButtonGroup buttonGroup = ((RadioButton) toggleButton).getGroup();
                if (buttonGroup != null) {
                    String groupId = rc.getContainerInstance().getIdTable().getId(buttonGroup);
                    createSetGroup(serverMessage, id, groupId);
                }
            }
        }
        
        if (!button.hasActionListeners()) {
            DomPropertyStore.createDomPropertyStore(serverMessage, id, "serverNotify", "false"); 
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        String parentId = ContainerInstance.getElementId(update.getParent());
        DomUpdate.createDomRemove(rc.getServerMessage(), parentId);
        renderAdd(rc, update, targetId, update.getParent());
        return false;
    }
}
