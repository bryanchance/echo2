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

package nextapp.echo2.webcontainer.image;

import java.io.IOException;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.SynchronizePeer;
import nextapp.echo2.webcontainer.SynchronizePeerFactory;
import nextapp.echo2.webrender.server.Connection;
import nextapp.echo2.webrender.server.Service;

/**
 * Abstract base service for rendering images sourced from the application
 * container.
 */
public abstract class AbstractImageService 
implements Service {

    private static final String PARAMETER_IMAGE_ID = "imageid"; 
    private static final String PARAMETER_COMPONENT_ID = "componentid"; 

    private static final String[] URL_PARAMETERS = new String[]{PARAMETER_COMPONENT_ID, PARAMETER_IMAGE_ID}; 
    
    /**
     * Creates a URI to retrieve a specific image for a specific component 
     * from the server.
     * 
     * @param containerInstance the relevant application container instance.
     * @param componentId the id of the component
     * @param imageId the id of the image
     */
    public String createUri(ContainerInstance containerInstance, String componentId, String imageId) {
        return containerInstance.getServiceUri(this, URL_PARAMETERS, new String[]{componentId, imageId});
    }

    /**
     * Renders the specified image to the given connection.
     * Implementations should set the response content type, and write image
     * data to the response <code>OutputStream</code>.
     * 
     * @param conn the <code>Connection</code> on which to render the image
     * @param imageReference the image to be rendered
     * @throws IOException if the image cannot be rendered
     */
    public abstract void renderImage(Connection conn, ImageReference imageReference) 
    throws IOException;
    
    /**
     * @see nextapp.echo2.webrender.server.Service#service(nextapp.echo2.webrender.server.Connection)
     */
    public void service(Connection conn)
    throws IOException {
        ContainerInstance containerInstance = (ContainerInstance) conn.getUserInstance();
        if (containerInstance == null) {
            throw new IOException("No container available.");
        }
        String componentId = conn.getRequest().getParameter(PARAMETER_COMPONENT_ID);
        if (componentId == null) {
            throw new IOException("Component id not specified.");
        }
        String imageId = conn.getRequest().getParameter(PARAMETER_IMAGE_ID);
        if (imageId == null) {
            throw new IOException("Image id not specified.");
        }
        Component component = containerInstance.getApplicationInstance().getComponent(componentId);
        if (component == null) {
            throw new IOException("Invalid component id.");
        }
        SynchronizePeer synchronizePeer = SynchronizePeerFactory.getPeerForComponent(component.getClass());
        if (!(synchronizePeer instanceof ImageRenderSupport)) {
            throw new IOException("Component synchronization peer does not support image rendering.");
        }
        ImageReference imageReference = (((ImageRenderSupport) synchronizePeer).getImage(component, imageId));
        if (imageReference == null) {
            throw new IOException("Image id is not valid.");
        }
        renderImage(conn, imageReference);
    }
}
