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

import java.util.Locale;

import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.layout.GridCellLayoutData;
import junit.framework.TestCase;

/**
 * Unit test(s) for the <code>nextapp.echo2.app.Component</code> object. 
 */
public class ComponentTest extends TestCase {
    
    /**
     * Test <code>background</code> property.
     */
    public void testBackground() {
        NullComponent c = new NullComponent();
        PropertyChangeEvaluator pce = new PropertyChangeEvaluator();
        c.addPropertyChangeListener(pce);
        c.setBackground(new Color(0x12, 0x34, 0x56));
        assertEquals(new Color(0x12, 0x34, 0x56), c.getBackground());
        assertEquals(Component.PROPERTY_BACKGROUND, pce.lastEvent.getPropertyName());
    }

    /**
     * Test <code>font</code> property. 
     */
    public void testFont() {
        NullComponent c = new NullComponent();
        PropertyChangeEvaluator pce = new PropertyChangeEvaluator();
        c.addPropertyChangeListener(pce);
        c.setFont(new Font(Font.COURIER, Font.BOLD, new Extent(12, Extent.PT)));
        assertEquals(new Font(Font.COURIER, Font.BOLD, new Extent(12, Extent.PT)), c.getFont());
        assertEquals(Component.PROPERTY_FONT, pce.lastEvent.getPropertyName());
    }

    /**
     * Test <code>foreground</code> property.
     */
    public void testForeground() {
        NullComponent c = new NullComponent();
        PropertyChangeEvaluator pce = new PropertyChangeEvaluator();
        c.addPropertyChangeListener(pce);
        c.setForeground(new Color(0x12, 0x34, 0x56));
        assertEquals(new Color(0x12, 0x34, 0x56), c.getForeground());
        assertEquals(Component.PROPERTY_FOREGROUND, pce.lastEvent.getPropertyName());
    }

    /**
     * Test adding multiple child components and retrieving one at a specific
     * index via <code>getComponent()</code>.
     */
    public void testGetComponent() {
        NullComponent c = new NullComponent();
        for (int i = 0; i < 5; ++ i) {
            c.add(new NullComponent());
        }
        NullComponent sixthComponent = new NullComponent();
        c.add(sixthComponent);
        for (int i = 0; i < 5; ++ i) {
            c.add(new NullComponent());
        }
        assertTrue(sixthComponent == c.getComponent(5));
    }

    /**
     * Test <code>getComponentCount()</code>.
     */
    public void testGetComponentCount() {
        NullComponent c = new NullComponent();
        for (int i = 0; i < 5; ++ i) {
            c.add(new NullComponent());
        }
        assertEquals(5, c.getComponentCount());
    }

    /**
     * Test <code>getComponents()</code>.
     */
    public void testGetComponents() {
        NullComponent parent = new NullComponent();
        NullComponent child1 = new NullComponent();
        NullComponent child2 = new NullComponent();
        parent.add(child1);
        parent.add(child2);
        Component[] children = parent.getComponents();
        assertSame(child1, children[0]);
        assertSame(child2, children[1]);
    }

    /**
     * Test <code>layoutData</code> property.
     */
    public void testLayoutData() {
        NullComponent c = new NullComponent();
        assertNull(c.getLayoutData());
        PropertyChangeEvaluator pce = new PropertyChangeEvaluator();
        c.addPropertyChangeListener(pce);
        GridCellLayoutData data = new GridCellLayoutData();
        data.setColumnSpan(2);
        c.setLayoutData(data);
        assertEquals(2, ((GridCellLayoutData) c.getLayoutData()).getColumnSpan());
        assertEquals(Component.PROPERTY_LAYOUT_DATA, pce.lastEvent.getPropertyName());
    }

    /**
     * Test querying <code>locale</code> property when no application is 
     * active. 
     */
    public void testLocaleWithoutApplication() {
        NullComponent c = new NullComponent();
        assertNull(c.getLocale());
        c.setLocale(Locale.TRADITIONAL_CHINESE);
        assertEquals(Locale.TRADITIONAL_CHINESE, c.getLocale());
    }
    
    /**
     * Test basic <code>PropertyChangeListener</code> functionality.
     */
    public void testPropertyChangeListeners() {
        PropertyChangeEvaluator pce = new PropertyChangeEvaluator();
        NullComponent c = new NullComponent();
        c.addPropertyChangeListener(pce);
        c.setBackground(new Color(0xabcdef));
        assertEquals(null, pce.lastEvent.getOldValue());
        assertEquals(new Color(0xabcdef), pce.lastEvent.getNewValue());
        assertEquals(c, pce.lastEvent.getSource());
        assertEquals(Component.PROPERTY_BACKGROUND, pce.lastEvent.getPropertyName());
        c.setBackground(new Color(0xfedcba));
        assertEquals(new Color(0xabcdef), pce.lastEvent.getOldValue());
        assertEquals(new Color(0xfedcba), pce.lastEvent.getNewValue());
        c.setBackground(null);
        assertEquals(new Color(0xfedcba), pce.lastEvent.getOldValue());
        assertEquals(null, pce.lastEvent.getNewValue());
    }
    
    /**
     * Test <code>indexOf()</code> method.
     */
    public void testIndexOf() {
        NullComponent parent = new NullComponent();
        NullComponent a = new NullComponent();
        NullComponent b = new NullComponent();
        NullComponent c = new NullComponent();
        NullComponent d = new NullComponent();
        parent.add(a);
        parent.add(b);
        parent.add(c);
        assertEquals(0, parent.indexOf(a));
        assertEquals(1, parent.indexOf(b));
        assertEquals(2, parent.indexOf(c));
        assertEquals(-1, parent.indexOf(d));
    }
    
    /**
     * Test <code>removeAll()</code> method.
     */
    public void testRemoveAll() {
        NullComponent c = new NullComponent();
        c.add(new NullComponent());
        c.add(new NullComponent());
        c.add(new NullComponent());
        assertEquals(3, c.getComponentCount());
        c.removeAll();
        assertEquals(0, c.getComponentCount());
    }
    
    /**
     * Test <code>remove(index)</code> method.
     */
    public void testRemoveByIndex() {
        NullComponent parent = new NullComponent();
        NullComponent a = new NullComponent();
        NullComponent b = new NullComponent();
        NullComponent c = new NullComponent();
        NullComponent d = new NullComponent();
        parent.add(a);
        parent.add(b);
        parent.add(c);
        parent.add(d);
        parent.remove(2);
        assertEquals(0, parent.indexOf(a));
        assertEquals(1, parent.indexOf(b));
        assertEquals(2, parent.indexOf(d));
        assertEquals(-1, parent.indexOf(c));
    }
}
