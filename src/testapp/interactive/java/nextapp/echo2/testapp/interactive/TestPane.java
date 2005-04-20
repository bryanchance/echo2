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

package nextapp.echo2.testapp.interactive;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/**
 * 
 */
public class TestPane extends ContentPane {

    private SplitPane horizontalPane;
    
    private ActionListener commandActionListener = new ActionListener() {
        
        private Button activeButton = null;
        
        /**
         * @see nextapp.echo2.app.event.ActionListener#actionPerformed(nextapp.echo2.app.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            try {
                if (activeButton != null) {
                    activeButton.setStyleName(Styles.DEFAULT_STYLE_NAME);
                }
                Button button = (Button) e.getSource();
                button.setStyleName(Styles.SELECTED_BUTTON_STYLE_NAME);
                activeButton = button;
                String screenClassName = "nextapp.echo2.testapp.interactive.testscreen." + e.getActionCommand();
                Class screenClass = Class.forName(screenClassName);
                Component content = (Component) screenClass.newInstance();
                if (horizontalPane.getComponentCount() > 1) {
                    horizontalPane.remove(1);
                }
                horizontalPane.add(content);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.toString());
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex.toString());
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex.toString());
            }

        }
    };
    
    private Row testLaunchButtonsRow;
    
    public TestPane() {
        super();
        
        SplitPane verticalPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new Extent(40));
        verticalPane.setSeparatorSize(new Extent(1));
        add(verticalPane);
        
        Label titleLabel = new Label("NextApp Echo2 Test Application [EARLY ACCESS/EXPERIMENTAL]");
        titleLabel.setStyleName(Styles.TITLE_LABEL_STYLE_NAME);
        verticalPane.add(titleLabel);
        
        horizontalPane = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL, new Extent(215));
        horizontalPane.setResizable(true);
        verticalPane.add(horizontalPane);
        
        Row controlsRow = new Row();
        controlsRow.setStyleName(Styles.APPLICATION_CONTROLS_ROW_STYLE_NAME);
        controlsRow.setCellSpacing(new Extent(5));
        
        horizontalPane.add(controlsRow);
        
        testLaunchButtonsRow = new Row();
        controlsRow.add(testLaunchButtonsRow);

        addTest("Asynchronous Updates", "AsynchronousTest");
        addTest("Button", "ButtonTest");
        addTest("Client Properties", "ClientPropertiesTest");
        //addTest("ContentPane", "ContentPaneTest");
        addTest("Delay", "DelayTest");
        addTest("Grid", "GridTest");
        addTest("Image", "ImageReferenceTest");
        addTest("Label", "LabelTest");
        addTest("Row", "RowTest");
        addTest("SplitPane (Basic)", "SplitPaneTest");
        addTest("SplitPane (Nested)", "SplitPaneNestedTest");
        addTest("StyleSheet", "StyleSheetTest");
        addTest("TextComponent", "TextComponentTest");
        addTest("WindowPane", "WindowPaneTest");
        
        Row applicationControlsRow = new Row();
        controlsRow.add(applicationControlsRow);

        Button button = new Button("Exit");
        button.setStyleName(Styles.DEFAULT_STYLE_NAME);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InteractiveApp.getApp().displayWelcomePane();
            }
        });
        applicationControlsRow.add(button);
    }
    
    private void addTest(String name, String action) {
        Button button = new Button(name);
        button.setActionCommand(action);
        button.setStyleName(Styles.DEFAULT_STYLE_NAME);
        button.addActionListener(commandActionListener);
        testLaunchButtonsRow.add(button);
    }
}
