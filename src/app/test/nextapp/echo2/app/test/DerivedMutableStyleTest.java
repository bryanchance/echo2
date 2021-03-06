/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2009 NextApp, Inc.
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

import nextapp.echo2.app.DerivedMutableStyle;
import nextapp.echo2.app.MutableStyle;
import junit.framework.TestCase;

/**
 * Unit tests for <code>DerivedMutableStyle</code>s. 
 */
public class DerivedMutableStyleTest extends TestCase {
    
    public void testIndexed() {
        MutableStyle baseStyle = new MutableStyle();
        baseStyle.setIndexedProperty("alpha", 1, "a");
        baseStyle.setIndexedProperty("bravo", 2,  "b");
        baseStyle.setIndexedProperty("bravo", 1,  "b3");
        
        DerivedMutableStyle derivedStyle = new DerivedMutableStyle(baseStyle);
        derivedStyle.setIndexedProperty("bravo", 2, "b2");
        derivedStyle.setIndexedProperty("charlie", 3, "c");

        assertEquals("b", baseStyle.getIndexedProperty("bravo", 2));
        
        assertEquals("b2", derivedStyle.getIndexedProperty("bravo", 2));
        assertEquals("c", derivedStyle.getIndexedProperty("charlie", 3));
        
        assertEquals("a", derivedStyle.getIndexedProperty("alpha", 1));
        assertEquals("b3", derivedStyle.getIndexedProperty("bravo", 1));
        
        assertTrue(derivedStyle.isIndexedPropertySet("alpha", 1));
        assertFalse(derivedStyle.isIndexedPropertySet("alpha", 2));
        assertFalse(derivedStyle.isIndexedPropertySet("bravo", 0));
        assertTrue(derivedStyle.isIndexedPropertySet("bravo", 1));
        assertTrue(derivedStyle.isIndexedPropertySet("bravo", 2));
        assertFalse(derivedStyle.isIndexedPropertySet("bravo", 3));
    }

    public void testSimple() {
        MutableStyle baseStyle = new MutableStyle();
        baseStyle.setProperty("alpha", "a");
        baseStyle.setProperty("bravo", "b");
        
        DerivedMutableStyle derivedStyle = new DerivedMutableStyle(baseStyle);
        baseStyle.setProperty("bravo", "b2");
        baseStyle.setProperty("charlie", "c");
        
        assertEquals("a", derivedStyle.getProperty("alpha"));
        assertEquals("b2", derivedStyle.getProperty("bravo"));
        assertEquals("c", derivedStyle.getProperty("charlie"));
    }
}
