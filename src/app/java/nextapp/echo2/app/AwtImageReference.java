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

package nextapp.echo2.app;

import java.awt.Image;

/**
 * An ImageReference describing an image which may be rendered from
 * a <code>java.awt.Image</code>.
 * Note that the JVM running the Echo Application Container will require 
 * access to a graphics context for the Java AWT to function.
 */
public class AwtImageReference 
implements ImageReference {

    private Image image;
    
    /**
     * Default constructor for use only in serialization and cases
     * where a class is derived from AwtImageReference and the 
     * <code>getImage()</code> method is overridden.
     */
    public AwtImageReference() {
        super();
    }
    
    /**
     * Creates an <code>AwtImageReference</code> to the specified 
     * <code>java.awt.Image</code>.
     * Note that changes to the underlying image will not necessarily be 
     * reflected on the client unless the image-containing property of the 
     * target component is update.
     *
     * @param image the <code>java.awt.Image </code>to be displayed.
     */
    public AwtImageReference(Image image) {
        super();
        this.image = image;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof AwtImageReference)) {
            return false;
        }
        AwtImageReference that = (AwtImageReference) o;
        if (!(this.image == that.image || (this.image != null && this.image.equals(that.image)))) {
            return false;
        }
        return true;
    }

    /**
     * @see nextapp.echo2.app.ImageReference#getHeight()
     */
    public Extent getHeight() {
        if (image == null) {
            return null;
        }
        int height = image.getWidth(null);
        if (height > 0) {
            return new Extent(height, Extent.PX);
        } else {
            return null;
        }
    }
    
    /**
     * Retrieves the image.  Calls to this method will be minimized such that
     * applications may extend this class and override this method such that
     * images are created only when they are needed, thereby reducing memory
     * usage at the cost of increased processor workload.
     * You should also override the <code>getWidth()</code> and 
     * <code>getHeight()</code> methods.
     */
    public Image getImage() {
        return image;
    }    
    
    /**
     * @see nextapp.echo2.app.ImageReference#getWidth()
     */
    public Extent getWidth() {
        if (image == null) {
            return null;
        }
        int width = image.getWidth(null);
        if (width > 0) {
            return new Extent(width, Extent.PX);
        } else {
            return null;
        }
    }
}
