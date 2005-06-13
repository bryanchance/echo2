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

package echo2example.email;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Grid;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.PasswordField;
import nextapp.echo2.app.Row;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.TextField;
import nextapp.echo2.app.WindowPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;

/**
 * Login screen <code>ContentPane</code>.
 */
public class LoginScreen extends ContentPane {

    private static final Extent PX_300 = new Extent(300, Extent.PX);

    private TextField emailAddressField;
    private PasswordField passwordField;
    
    /**
     * Creates a new <code>LoginScreen</cdoe>.
     */
    public LoginScreen() {
        super();
        setStyleName("LoginScreen.ContentPane");
        
        Label label;

        Column column = new Column();
        column.setStyleName("LoginScreen.Column");
        add(column);
        
        label = new Label(Styles.NEXTAPP_LOG_IMAGE);
        column.add(label);
        
        label = new Label(Styles.ECHO2_IMAGE);
        column.add(label);
        
        label = new Label(Styles.WEBMAIL_EXAMPLE_IMAGE);
        column.add(label);
        
        WindowPane loginWindow = new WindowPane();
        loginWindow.setTitle(Messages.getString("LoginScreen.LoginWindowTitle"));
        loginWindow.setStyleName("LoginScreen.LoginWindow");
        loginWindow.setDefaultCloseOperation(WindowPane.DO_NOTHING_ON_CLOSE);
        add(loginWindow);
        
        SplitPane splitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new Extent(-32));
        loginWindow.add(splitPane);
        
        Grid layoutGrid = new Grid();
        layoutGrid.setStyleName("LoginScreen.LayoutGrid");
        splitPane.add(layoutGrid);

        label = new Label(Messages.getString("LoginScreen.PromptEmailAddress"));
        label.setStyleName("LoginScreen.Prompt");
        layoutGrid.add(label);
        
        emailAddressField = new TextField();
        emailAddressField.setWidth(PX_300);
        emailAddressField.setStyleName("Default");
        layoutGrid.add(emailAddressField);
        
        label = new Label(Messages.getString("LoginScreen.PromptPassword"));
        label.setStyleName("LoginScreen.Prompt");
        layoutGrid.add(label);
        
        passwordField = new PasswordField();
        passwordField.setWidth(PX_300);
        passwordField.setStyleName("Default");
        layoutGrid.add(passwordField);
        
        Row controlRow = new Row();
        controlRow.setStyleName("ControlPane");
        splitPane.add(controlRow);
        
        Button button = new Button(Messages.getString("LoginScreen.Continue"), Styles.ICON_24_YES);
        button.setStyleName("ControlPane.Button");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processLogin();
            }
        });
        controlRow.add(button);

        if (EmailApp.FAUX_MODE) {
            emailAddressField.setText("joe.smith@test.nextapp.com");
            passwordField.setText("Joshua");
        }
    }
    
    /**
     * Processes a user log-in request.
     */
    private void processLogin() {
        if (!EmailApp.getApp().connect(emailAddressField.getText(), passwordField.getText())) {
            MessageDialog messageDialog = new MessageDialog(Messages.getString("LoginScreen.InvalidLogin.Title"),
                    Messages.getString("LoginScreen.InvalidLogin.Message"), MessageDialog.TYPE_ERROR, MessageDialog.CONTROLS_OK);
            getApplicationInstance().getDefaultWindow().getContent().add(messageDialog);
        }
    }
}