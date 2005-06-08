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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import echo2example.email.MessageListTable.MessageSelectionEvent;
import echo2example.email.PageNavigator.PageIndexChangeEvent;

import nextapp.echo2.app.Button;
import nextapp.echo2.app.Column;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.ContentPane;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Label;
import nextapp.echo2.app.SelectField;
import nextapp.echo2.app.SplitPane;
import nextapp.echo2.app.event.ActionEvent;
import nextapp.echo2.app.event.ActionListener;
import nextapp.echo2.app.list.AbstractListModel;

/**
 * 
 */
public class MailScreen extends ContentPane {
    
    private Folder[] folders;
    
    private MessageListTable messageListTable;
    private PageNavigator pageNavigator;
    private MessagePane messagePane;
    private SelectField folderSelect;
    private Message selectedMessage;
    
    public MailScreen() {
        super();
        
        SplitPane mainSplitPane = new SplitPane(SplitPane.ORIENTATION_HORIZONTAL, new Extent(175));
        mainSplitPane.setSeparatorWidth(new Extent(1, Extent.PX));
        add(mainSplitPane);
        
        SplitPane titleMenuSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new Extent(70));
        titleMenuSplitPane.setSeparatorHeight(new Extent(1, Extent.PX));
        mainSplitPane.add(titleMenuSplitPane);
        
        Column titleColumn = new Column();
        titleColumn.setStyleName("MailScreen.TitleColumn");
        titleMenuSplitPane.add(titleColumn);
        Label label;
        
        label = new Label(Messages.getString("Title.Main"));
        label.setStyleName("Title.Main");
        titleColumn.add(label);
        
        label = new Label(Messages.getString("Title.Sub"));
        label.setStyleName("Title.Sub");
        titleColumn.add(label);
        
        titleMenuSplitPane.add(createMenu());
        
        SplitPane mailSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new Extent(320));
        mailSplitPane.setResizable(true);
        mainSplitPane.add(mailSplitPane);
        
        SplitPane messageListSplitPane = new SplitPane(SplitPane.ORIENTATION_VERTICAL, new Extent(32));
        messageListSplitPane.setSeparatorHeight(new Extent(1, Extent.PX));
        mailSplitPane.add(messageListSplitPane);
        
        pageNavigator = new PageNavigator();
        pageNavigator.setStyleName("PageNavigator");
        pageNavigator.addPageIndexChangeListener(new PageNavigator.PageIndexChangeListener() {
            public void pageIndexChanged(PageIndexChangeEvent e) {
                try {
                    messageListTable.setPageIndex(e.getNewPageIndex());
                    messagePane.setMessage(null);
                } catch (MessagingException ex) {
                    EmailApp.getApp().processFatalException(ex);
                }
            }
        });
        messageListSplitPane.add(pageNavigator);
        
        messageListTable = new MessageListTable();
        messageListTable.addMessageSelectionListener(new MessageListTable.MessageSelectionListener() {
            public void messageSelected(MessageSelectionEvent e) {
                try {
                    selectedMessage = e.getMessage();
                    messagePane.setMessage(selectedMessage);
                } catch (MessagingException ex) {
                    EmailApp.getApp().processFatalException(ex);
                }
            }
        });
        messageListSplitPane.add(messageListTable);
        
        messagePane = new MessagePane();
        mailSplitPane.add(messagePane);
    }
    
    private Component createMenu() {
        Button button;
        Label label;

        Column menuColumn = new Column();
        menuColumn.setStyleName("MailScreen.MenuColumn");
        
        Column folderSelectColumn = new Column();
        menuColumn.add(folderSelectColumn);
        
        label = new Label(Messages.getString("MailScreen.PromptFolderSelect"));
        folderSelectColumn.add(label);
        
        folderSelect = new SelectField();
        folderSelectColumn.add(folderSelect);
        
        Column optionsColumn = new Column();
        menuColumn.add(optionsColumn);
        
        button = new Button(Messages.getString("MailScreen.ButtonNewMessage"), Styles.ICON_24_MAIL_COMPOSE);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ComposeWindow composeWindow = new ComposeWindow(null);
                getApplicationInstance().getWindows()[0].getContent().add(composeWindow);
            }
        });
        optionsColumn.add(button);
        
        button = new Button(Messages.getString("MailScreen.ButtonReplyTo"), Styles.ICON_24_MAIL_REPLY);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ComposeWindow composeWindow = new ComposeWindow(selectedMessage);
                getApplicationInstance().getWindows()[0].getContent().add(composeWindow);
            }
        });
        optionsColumn.add(button);
        
        button = new Button(Messages.getString("MailScreen.ButtonLogOut"), Styles.ICON_24_EXIT);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((EmailApp) getApplicationInstance()).disconnect();
            }
        });
        menuColumn.add(button);
        
        return menuColumn;
    }

    /**
     * Sets the active folder.
     * 
     * @param folder the <code>Folder</code>
     */
    private void setFolder(Folder folder) {
        try {
            messageListTable.setFolder(null);
            int messageCount = folder.getMessageCount();
            int totalPages = folder.getMessageCount() / EmailApp.MESSAGES_PER_PAGE;
            if (messageCount % EmailApp.MESSAGES_PER_PAGE > 0) {
                ++totalPages;
            }
            pageNavigator.setTotalPages(totalPages);
            pageNavigator.setPageIndex(totalPages - 1);
            messageListTable.setFolder(folder);
            messagePane.setMessage(null);
        } catch (MessagingException ex) {
            EmailApp.getApp().processFatalException(ex);
        }
    }

    /**
     * Sets the mail <code>Store</code>.
     * 
     * @param store the <code>Store</code>
     */
    public void setStore(Store store) 
    throws MessagingException {
        folders = store.getDefaultFolder().list("*");
        folderSelect.setModel(new AbstractListModel() {
        
            public Object get(int index) {
                return folders[index].getName();
            }
    
            public int size() {
                return folders.length;
            }
        });
        for (int i = 0; i < folders.length; ++i) {
            if ("INBOX".equals(folders[i].getName())) {
                folderSelect.setSelectedIndex(i);
                setFolder(folders[i]);
                break;
            }
        }
    }
}
