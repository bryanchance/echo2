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

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Button;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.layout.GridLayoutData;
import nextapp.echo2.testapp.interactive.ButtonColumn;
import nextapp.echo2.testapp.interactive.StyleUtil;
import nextapp.echo2.testapp.interactive.Styles;

/**
 * Interactive test for <code>Grid</code> components.
 */
public class GridTest extends SplitPane {

    private int nextCellNumber = 0;
    private Button selectedButton;
    
    private ActionListener cellButtonActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Button button = (Button) e.getSource();
            selectCellButton(button);
        }
    };
    
    public GridTest() {
        super(SplitPane.ORIENTATION_HORIZONTAL, new Extent(250, Extent.PX));
        setStyleName("DefaultResizable");
        
        Column groupContainerColumn = new Column();
        groupContainerColumn.setCellSpacing(new Extent(5));
        groupContainerColumn.setStyleName("TestControlsColumn");
        add(groupContainerColumn);
        
        Column testColumn = new Column();
        add(testColumn);

        ButtonColumn controlsColumn;
        
        controlsColumn = new ButtonColumn();
        controlsColumn.add(new Label("Insert/Delete Cells"));
        groupContainerColumn.add(controlsColumn);
        
        final Grid grid = new Grid(4);
        grid.setBorder(new Border(new Extent(1), Color.BLUE, Border.STYLE_SOLID));
        while (nextCellNumber < 17) {
            grid.add(createGridCellButton());
        }
        testColumn.add(grid);

        controlsColumn.addButton("Clear Selection", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectCellButton(null);
            }
        });

        controlsColumn.addButton("Insert Cell Before Selected", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    grid.add(createGridCellButton(), grid.indexOf(selectedButton));
                }
            }
        });

        controlsColumn.addButton("Append New Cell", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Button button = createGridCellButton(); 
                grid.add(button);
                selectCellButton(button);
            }
        });

        controlsColumn.addButton("Delete Selected Cell", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    int index = grid.indexOf(selectedButton);
                    grid.remove(selectedButton);
                    if (grid.getComponentCount() != 0) {
                        if (index < grid.getComponentCount()) {
                            selectCellButton((Button) grid.getComponent(index));
                        } else {
                            selectCellButton((Button) grid.getComponent(grid.getComponentCount() - 1));
                        }
                    } else {
                        selectCellButton(null);
                    }
                }
            }
        });
        
        controlsColumn = new ButtonColumn();
        controlsColumn.add(new Label("Configure Grid"));
        groupContainerColumn.add(controlsColumn);
        
        controlsColumn.addButton("Swap Orientation", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setOrientation(grid.getOrientation() == Grid.ORIENTATION_VERTICAL 
                        ? Grid.ORIENTATION_HORIZONTAL : Grid.ORIENTATION_VERTICAL);
            }
        });
        
        controlsColumn.addButton("[+] Size", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setSize(grid.getSize() + 1);
            }
        });

        controlsColumn.addButton("[-] Size", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (grid.getSize() > 1) {
                    grid.setSize(grid.getSize() - 1);
                }
            }
        });
        controlsColumn.addButton("Change Foreground", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setForeground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Change Background", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setBackground(StyleUtil.randomColor());
            }
        });
        controlsColumn.addButton("Change Border (All Attributes)", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setBorder(StyleUtil.randomBorder());
            }
        });
        controlsColumn.addButton("Change Border Color", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Border border = grid.getBorder();
                grid.setBorder(new Border(border.getSize(), StyleUtil.randomColor(), border.getStyle()));
            }
        });
        controlsColumn.addButton("Change Border Size", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setBorder(StyleUtil.nextBorderSize(grid.getBorder()));
            }
        });
        controlsColumn.addButton("Change Border Style", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setBorder(StyleUtil.nextBorderStyle(grid.getBorder()));
            }
        });
        
        controlsColumn.addButton("Set Insets 0px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setInsets(new Insets(0));
            }
        });
        controlsColumn.addButton("Set Insets 2px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setInsets(new Insets(2));
            }
        });
        controlsColumn.addButton("Set Insets 10/5px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setInsets(new Insets(10, 5));
            }
        });
        controlsColumn.addButton("Set Insets 10/20/30/40px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setInsets(new Insets(10, 20, 30, 40));
            }
        });
        controlsColumn.addButton("Set Width = null", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setWidth(null);
            }
        });
        controlsColumn.addButton("Set Width = 500px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setWidth(new Extent(500));
            }
        });
        controlsColumn.addButton("Set Width = 100%", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                grid.setWidth(new Extent(100, Extent.PERCENT));
            }
        });
        
        controlsColumn = new ButtonColumn();
        controlsColumn.add(new Label("Configure Cell"));
        groupContainerColumn.add(controlsColumn);
        
        controlsColumn.addButton("[+] Column Span", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    if (layoutData.getColumnSpan() < 1) {
                        layoutData.setColumnSpan(1);
                    } else {
                        layoutData.setColumnSpan(layoutData.getColumnSpan() + 1);
                    }
                    selectedButton.setLayoutData(layoutData);
                    retitle(selectedButton);
                }
            }
        });

        controlsColumn.addButton("[-] Column Span", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    if (layoutData.getColumnSpan() > 1) {
                        layoutData.setColumnSpan(layoutData.getColumnSpan() - 1);
                    } else {
                        layoutData.setColumnSpan(1);
                    }
                    selectedButton.setLayoutData(layoutData);
                    retitle(selectedButton);
                }
            }
        });
        
        controlsColumn.addButton("[+] Row Span", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    if (layoutData.getRowSpan() < 1) {
                        layoutData.setRowSpan(1);
                    } else {
                        layoutData.setRowSpan(layoutData.getRowSpan() + 1);
                    }
                    selectedButton.setLayoutData(layoutData);
                    retitle(selectedButton);
                }
            }
        });

        controlsColumn.addButton("[-] Row Span", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    if (layoutData.getRowSpan() > 1) {
                        layoutData.setRowSpan(layoutData.getRowSpan() - 1);
                    } else {
                        layoutData.setRowSpan(1);
                    }
                    selectedButton.setLayoutData(layoutData);
                    retitle(selectedButton);
                }
            }
        });

        controlsColumn.addButton("Column Span: FILL", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setColumnSpan(GridLayoutData.SPAN_FILL);
                    selectedButton.setLayoutData(layoutData);
                    retitle(selectedButton);
                }
            }
        });

        controlsColumn.addButton("Row Span: FILL", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setRowSpan(GridLayoutData.SPAN_FILL);
                    selectedButton.setLayoutData(layoutData);
                    retitle(selectedButton);
                }
            }
        });

        controlsColumn.addButton("Set Insets 0px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setInsets(new Insets(0));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Insets 2px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setInsets(new Insets(2));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Insets 10/5px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setInsets(new Insets(10, 5));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Insets 10/20/30/40px", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setInsets(new Insets(10, 20, 30, 40));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Alignment = Default", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setAlignment(null);
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Alignment = Leading/Top", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setAlignment(new Alignment(Alignment.LEADING, Alignment.TOP));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Alignment = Trailing/Bottom", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setAlignment(new Alignment(Alignment.TRAILING, Alignment.BOTTOM));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Alignment = Left/Top", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setAlignment(new Alignment(Alignment.LEFT, Alignment.TOP));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Alignment = Right/Bottom", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setAlignment(new Alignment(Alignment.RIGHT, Alignment.BOTTOM));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set Alignment = Center/Center", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setAlignment(new Alignment(Alignment.CENTER, Alignment.CENTER));
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Set BackgroundImage", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setBackgroundImage(Styles.BG_SHADOW_DARK_BLUE);
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });
        controlsColumn.addButton("Clear BackgroundImage", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedButton != null) {
                    GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
                    layoutData.setBackgroundImage(null);
                    selectedButton.setLayoutData(layoutData);
                }
            }
        });

        controlsColumn = new ButtonColumn();
        controlsColumn.add(new Label("Configure Rows/Columns"));
        groupContainerColumn.add(controlsColumn);
        
        controlsColumn.addButton("Clear Widths of First 16 Columns", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 16; ++i) {
                    grid.setColumnWidth(i, null);
                }
            }
        });
        
        controlsColumn.addButton("Set First 16 Columns to 100px Width", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Extent width = new Extent(100);
                for (int i = 0; i < 16; ++i) {
                    grid.setColumnWidth(i, width);
                }
            }
        });
        
        controlsColumn.addButton("Set First 16 Columns to Random Width", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 16; ++i) {
                    grid.setColumnWidth(i, new Extent( ((int) (Math.random() * 100)) + 50));
                }
            }
        });

        controlsColumn.addButton("Clear Heights of First 16 Rows", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 16; ++i) {
                    grid.setRowHeight(i, null);
                }
            }
        });
        
        controlsColumn.addButton("Set First 16 Rows to 100px Height", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Extent height = new Extent(100);
                for (int i = 0; i < 16; ++i) {
                    grid.setRowHeight(i, height);
                }
            }
        });

        controlsColumn.addButton("Set First 16 Rows to Random Height", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 16; ++i) {
                    grid.setRowHeight(i, new Extent( ((int) (Math.random() * 100)) + 50));
                }
            }
        });
    }

    public Button createGridCellButton() {
        Button button = new Button("Grid Cell #" + nextCellNumber++);
        GridLayoutData layoutData = new GridLayoutData();
        button.setLayoutData(layoutData);
        button.addActionListener(cellButtonActionListener);
        return button;
    }
    
    private void retitle(Button button) {
        StringBuffer out = new StringBuffer();
        GridLayoutData layoutData = (GridLayoutData) selectedButton.getLayoutData();
        if (layoutData.getColumnSpan() != 1 || layoutData.getRowSpan() != 1) {
            out.append("[" + (
                    layoutData.getColumnSpan() == GridLayoutData.SPAN_FILL 
                    ? "F" : Integer.toString(layoutData.getColumnSpan())) + "x" + 
                    (layoutData.getRowSpan() == GridLayoutData.SPAN_FILL 
                    ? "F" : Integer.toString(layoutData.getRowSpan())) + "]"); 
        }
        String text = button.getText();
        if (text.indexOf(":") == -1) {
            if (out.length() == 0) {
                return;
            }
            text = text + " : " + out;
        } else {
            if (out.length() == 0) {
                text = text.substring(0, text.indexOf(":"));
            } else {
                text = text.substring(0, text.indexOf(":") + 2) + out;
            }
        }
        button.setText(text);
    }
    
    private void selectCellButton(Button button) {
        GridLayoutData layoutData;
        if (selectedButton != null) {
            layoutData = (GridLayoutData) selectedButton.getLayoutData();
            layoutData.setBackground(null);
            selectedButton.setLayoutData(layoutData);
        }
        if (button != null) {
            layoutData = (GridLayoutData) button.getLayoutData();
            layoutData.setBackground(new Color(0xefefaf));
            button.setLayoutData(layoutData);
        }
        selectedButton = button;
    }
}
