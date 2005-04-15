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

package nextapp.echo2.app.test;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.DuplicateIdException;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.Window;
import junit.framework.TestCase;

/**
 * Unit test(s) for the <code>nextapp.echo2.app.ApplicationInstance</code> object.
 */
public class ApplicationInstanceTest extends TestCase {

    private class IdLabel extends Label {
        
        public IdLabel(String idText) {
            super(idText);
            setId(idText);
        }
    }
    
    private class ValidatingLabel extends Label {
        
        boolean valid = false;
        
        public void invalidate() {
            valid = false;
        }
        
        public void validate() {
            super.validate();
            valid = true;
        }
    }
    
    /**
     * Test setting active (ThreadLocal) <code>ApplicationInstance</code>.
     */
    public void testActivation() {
        assertNull(ApplicationInstance.getActive());
        HelloWorldApp app = new HelloWorldApp();
        assertNull(ApplicationInstance.getActive());
        ApplicationInstance.setActive(app);
        assertTrue(app == ApplicationInstance.getActive());
        ApplicationInstance.setActive(null);
        assertNull(ApplicationInstance.getActive());
    }
    
    /**
     * Test setting and retrieving contextual information.
     */
    public void testContext() {
        HelloWorldApp app = new HelloWorldApp();
        assertNull(app.getContextProperty("alpha"));
        app.setContextProperty("alpha", "bravo");
        assertEquals("bravo", app.getContextProperty("alpha"));
        app.setContextProperty("alpha", null);
        assertNull(app.getContextProperty("alpha"));
    }

    /**
     * Test behavior when adding components to hierarchy with same ids
     * and existing components.
     */
    public void testDuplicateIds() {
        RowApp app = new RowApp();
        app.doInit();
        Row row = app.getRow();
        try {
            row.add(new IdLabel("alpha"));
            row.add(new IdLabel("alpha"));
            fail("Should have thrown DuplicateIdException");
        } catch (DuplicateIdException expected) { }
    }
    
    /**
     * Test registration flag of components in hierarchies belong to the
     * <code>ApplicationInstance</code>.
     */
    public void testRegistration() {
        RowApp rowApp = new RowApp();
        Window window = rowApp.doInit();
        assertTrue(window.isRegistered());
        assertTrue(rowApp.getRow().isRegistered());
        Label label = new Label();
        assertFalse(label.isRegistered());
        rowApp.getRow().add(label);
        assertTrue(label.isRegistered());
        rowApp.getRow().remove(label);
        assertFalse(label.isRegistered());
        rowApp.getRow().add(label);
        assertTrue(label.isRegistered());
        rowApp.getContentPane().remove(rowApp.getRow());
        assertFalse(label.isRegistered());
    }
    
    /**
     * Test <code>Component.validate()</code> being invoked at
     * application initialization and after client update processing.
     */
    public void testValidation() {
        final ValidatingLabel validatingLabel = new ValidatingLabel();
        RowApp app = new RowApp() {
            public Window init() {
                Window window = super.init();
                getRow().add(validatingLabel);
                return window;
            }
        };
        
        assertFalse(validatingLabel.valid);
        app.doInit();
        
        // Test for initial validation.
        assertTrue(validatingLabel.valid);

        validatingLabel.invalidate();
        assertFalse(validatingLabel.valid);
        
        // test validation after client update processing.
        app.getUpdateManager().processClientUpdates();
        assertTrue(validatingLabel.valid);
    }
}
