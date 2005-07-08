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

package nextapp.echo2.testapp.interactive.testscreen;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.SplitPaneLayoutData;
import nextapp.echo2.testapp.interactive.CoolDelayMessage;
import nextapp.echo2.webcontainer.ContainerContext;
import nextapp.echo2.webcontainer.DefaultServerDelayMessage;
/**
 * A test for handling of long-running server-interactions.
 */
public class DelayTest extends Column {
    
    private int clickCount = 0;
    
    public DelayTest() {
        super();
        
        SplitPaneLayoutData splitPaneLayoutData = new SplitPaneLayoutData();
        splitPaneLayoutData.setInsets(new Insets(10));
        setLayoutData(splitPaneLayoutData);
        
        Button delayButton = new Button("Test 3 second delay");
        delayButton.setStyleName("Default");
        delayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }
            }
        });
        add(delayButton);
        
        final Button blockedButton = new Button("This button has been clicked 0 times");
        blockedButton.setStyleName("Default");
        blockedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blockedButton.setText("This button has been clicked " + ++clickCount + " times");
            }
        });
        add(blockedButton);
        
        Button setNullButton = new Button("Set ServerDelayMessage to None");
        setNullButton.setStyleName("Default");
        setNullButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContainerContext containerContext 
                        = (ContainerContext) getApplicationInstance().getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
                containerContext.setServerDelayMessage(null);
            }
        });
        add(setNullButton);
        
        Button setDefaultButton = new Button("Set ServerDelayMessage to Default");
        setDefaultButton.setStyleName("Default");
        setDefaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContainerContext containerContext 
                        = (ContainerContext) getApplicationInstance().getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
                containerContext.setServerDelayMessage(DefaultServerDelayMessage.INSTANCE);
            }
        });
        add(setDefaultButton);
        
        Button setCustomDefaultButton = new Button("Set ServerDelayMessage to Custom DefaultServerDelayMessage");
        setCustomDefaultButton.setStyleName("Default");
        setCustomDefaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContainerContext containerContext 
                        = (ContainerContext) getApplicationInstance().getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
                containerContext.setServerDelayMessage(new DefaultServerDelayMessage("Well, this seems to be taking a while.  "
                        + "Now might be a good time to grab a snack or a frosty beverage from the kitchen."));
            }
        });
        add(setCustomDefaultButton);
        
        Button coolButton = new Button("Set ServerDelayMessage to CoolDelayMessage");
        coolButton.setStyleName("Default");
        coolButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContainerContext containerContext 
                        = (ContainerContext) getApplicationInstance().getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
                containerContext.setServerDelayMessage(new CoolDelayMessage(containerContext, "PLEASE WAIT"));
            }
        });
        add(coolButton);
    }
}
