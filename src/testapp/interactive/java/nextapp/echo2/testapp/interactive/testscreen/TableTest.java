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

import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.Table;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.event.ChangeEvent;
import nextapp.echo2.app.event.ChangeListener;
import nextapp.echo2.app.layout.SplitPaneLayoutData;
import nextapp.echo2.app.list.ListSelectionModel;
import nextapp.echo2.app.table.AbstractTableModel;
import nextapp.echo2.app.table.DefaultTableModel;
import nextapp.echo2.testapp.interactive.ButtonColumn;
import nextapp.echo2.testapp.interactive.InteractiveApp;
import nextapp.echo2.testapp.interactive.StyleUtil;
import nextapp.echo2.testapp.interactive.Styles;

/**
 * A test for <code>Tables</code>s.
 */
public class TableTest extends SplitPane {
    
    private Table testTable;
    
    private class MultiplicationTableModel extends AbstractTableModel {

        /**
         * @see nextapp.echo2.app.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return 12;
        }
        
        /**
         * @see nextapp.echo2.app.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return 12;
        }
        
        /**
         * @see nextapp.echo2.app.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int column, int row) {
            return new Integer((column + 1) * (row + 1));
        }
    }
    
    /**
     * Writes <code>ActionEvent</code>s to console.
     */
    private ActionListener actionListener = new ActionListener() {

        /**
         * @see nextapp.echo2.app.event.ActionListener#actionPerformed(nextapp.echo2.app.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            ((InteractiveApp) getApplicationInstance()).consoleWrite(e.toString());
        }
    };
    
    /**
     * Writes <code>ChangeEvent</code>s to console.
     */
    private ChangeListener changeListener = new ChangeListener() {

        /**
         * @see nextapp.echo2.app.event.ChangeListener#stateChanged(nextapp.echo2.app.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e) {
            ((InteractiveApp) getApplicationInstance()).consoleWrite(e.toString());
        }
    };
    
    public TableTest() {
        super(SplitPane.ORIENTATION_HORIZONTAL, new Extent(250, Extent.PX));
        setStyleName("defaultResizable");
        
        Column groupContainerColumn = new Column();
        groupContainerColumn.setCellSpacing(new Extent(5));
        groupContainerColumn.setStyleName(Styles.TEST_CONTROLS_COLUMN_STYLE_NAME);
        add(groupContainerColumn);
        
        Column testColumn = new Column();
        SplitPaneLayoutData splitPaneLayoutData = new SplitPaneLayoutData();
        splitPaneLayoutData.setInsets(new Insets(10, 5));
        testColumn.setLayoutData(splitPaneLayoutData);
        add(testColumn);

        ButtonColumn controlsColumn;
        
        controlsColumn = new ButtonColumn();
        groupContainerColumn.add(controlsColumn);

        controlsColumn.add(new Label("TableModel"));
        
        controlsColumn.addButton("Multiplication Model", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setModel(new MultiplicationTableModel());
            }
        });
        
        controlsColumn.addButton("DefaultTableModel (Empty)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setModel(new DefaultTableModel());
            }
        });
        
        controlsColumn.addButton("DefaultTableModel (Employees)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnCount(3);
                model.insertRow(0, new String[]{"Bob Johnson", "bob.johnson@test.nextapp.com", "949.555.1234"});
                model.insertRow(0, new String[]{"Laura Smith", "laura.smith@test.nextapp.com", "217.555.9343"});
                model.insertRow(0, new String[]{"Jenny Roberts", "jenny.roberts@test.nextapp.com", "630.555.1987"});
                model.insertRow(0, new String[]{"Thomas Albertson", "thomas.albertson@test.nextapp.com", "619.555.1233"});
                model.insertRow(0, new String[]{"Albert Thomas", "albert.thomas@test.nextapp.com", "408.555.3232"});
                model.insertRow(0, new String[]{"Sheila Simmons", "sheila.simmons@test.nextapp.com", "212.555.8700"});
                model.insertRow(0, new String[]{"Mark Atkinson", "mark.atkinson@test.nextapp.com", "213.555.9456"});
                model.insertRow(0, new String[]{"Linda Jefferson", "linda.jefferson@test.nextapp.com", "949.555.8925"});
                model.insertRow(0, new String[]{"Yvonne Adams", "yvonne.adams@test.nextapp.com", "714.555.8543"});
                testTable.setModel(model);
            }
        });
        
        testTable = new Table();
        testTable.setBorder(new Border(new Extent(1), Color.BLUE, Border.STYLE_SOLID));
        testColumn.add(testTable);

        controlsColumn.add(new Label("Appearance"));
        
        controlsColumn.addButton("Change Foreground", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setForeground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Change Background", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setBackground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Change Border (All Attributes)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setBorder(StyleUtil.randomBorder());
            }
        });
        controlsColumn.addButton("Change Border Color", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Border border = testTable.getBorder();
                testTable.setBorder(new Border(border.getSize(), StyleUtil.randomColor(), border.getStyle()));
            }
        });
        controlsColumn.addButton("Change Border Size", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setBorder(StyleUtil.nextBorderSize(testTable.getBorder()));
            }
        });
        controlsColumn.addButton("Change Border Style", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setBorder(StyleUtil.nextBorderStyle(testTable.getBorder()));
            }
        });
        
        controlsColumn.addButton("Set Insets 0px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setInsets(new Insets(0));
            }
        });
        controlsColumn.addButton("Set Insets 2px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setInsets(new Insets(2));
            }
        });
        controlsColumn.addButton("Set Insets 10/5px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setInsets(new Insets(10, 5));
            }
        });
        controlsColumn.addButton("Set Insets 10/20/30/40px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setInsets(new Insets(10, 20, 30, 40));
            }
        });
        controlsColumn.addButton("Set Width = null", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setWidth(null);
            }
        });
        controlsColumn.addButton("Set Width = 500px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setWidth(new Extent(500));
            }
        });
        controlsColumn.addButton("Set Width = 100%", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setWidth(new Extent(100, Extent.PERCENT));
            }
        });
        
        
        // Rollover Effect Settings

        controlsColumn = new ButtonColumn();
        groupContainerColumn.add(controlsColumn);
        
        controlsColumn.add(new Label("Rollover Effects"));

        controlsColumn.addButton("Enable Rollover Effects", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverEnabled(true);
            }
        });
        controlsColumn.addButton("Disable Rollover Effects", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverEnabled(false);
            }
        });
        controlsColumn.addButton("Set Rollover Foreground", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverForeground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Clear Rollover Foreground", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverForeground(null);
            }
        });
        controlsColumn.addButton("Set Rollover Background", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 testTable.setRolloverBackground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Clear Rollover Background", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverBackground(null);
            }
        });
        controlsColumn.addButton("Set Rollover Font", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverFont(StyleUtil.randomFont());
            }
        });
        controlsColumn.addButton("Clear Rollover Font", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverFont(null);
            }
        });
        controlsColumn.addButton("Set Rollover Background Image", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverBackgroundImage(Styles.BG_NW_SHADOW);
            }
        });
        controlsColumn.addButton("Clear Rollover Background Image", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setRolloverBackgroundImage(null);
            }
        });
        
        // Selection Settings

        controlsColumn = new ButtonColumn();
        groupContainerColumn.add(controlsColumn);
        
        controlsColumn.add(new Label("Selection"));

        controlsColumn.addButton("Enable Selection", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionEnabled(true);
            }
        });
        controlsColumn.addButton("Disable Selection", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionEnabled(false);
            }
        });
        controlsColumn.addButton("Set SelectionMode = Single", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
        });
        controlsColumn.addButton("Set SelectionMode = Multiple", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_SELECTION);
            }
        });
        controlsColumn.addButton("Set Selection Foreground", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionForeground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Clear Selection Foreground", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionForeground(null);
            }
        });
        controlsColumn.addButton("Set Selection Background", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 testTable.setSelectionBackground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Clear Selection Background", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionBackground(null);
            }
        });
        controlsColumn.addButton("Set Selection Font", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionFont(StyleUtil.randomFont());
            }
        });
        controlsColumn.addButton("Clear Selection Font", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionFont(null);
            }
        });
        controlsColumn.addButton("Set Selection Background Image", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionBackgroundImage(Styles.BUTTON_PRESSED_BACKGROUND_IMAGE);
            }
        });
        controlsColumn.addButton("Clear Selection Background Image", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.setSelectionBackgroundImage(null);
            }
        });
        
        // Listener Settings

        controlsColumn = new ButtonColumn();
        groupContainerColumn.add(controlsColumn);
        
        controlsColumn.add(new Label("Listeners"));

        controlsColumn.addButton("Add ActionListener", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.addActionListener(actionListener);
            }
        });
        controlsColumn.addButton("Remove ActionListener", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.removeActionListener(actionListener);
            }
        });
        controlsColumn.addButton("Add ChangeListener", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.getSelectionModel().addChangeListener(changeListener);
            }
        });
        controlsColumn.addButton("Remove ChangeListener", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testTable.getSelectionModel().removeChangeListener(changeListener);
            }
        });
        
    }
}
