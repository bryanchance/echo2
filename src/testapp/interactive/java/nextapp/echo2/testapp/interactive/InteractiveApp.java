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

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.TaskQueueHandle;
import nextapp.echo2.app.Window;
import nextapp.echo2.webcontainer.ContainerContext;

/**
 * Interactive Test Application Instance.
 * <p>
 * <b>WARNING TO DEVELOPERS:</b>
 * <p>
 * Beware that the Interactive Test Application is not a good example of an
 * Echo application.  The intent of this application is to serve as a running
 * testbed for the Echo framework.  As such, the requirements of this 
 * application are dramatically different from a production internet 
 * application.  There is stuff in this code of this application that is 
 * downright absurd.  Please do not look to this application to see good design
 * practices for building your own Echo applications--you will not find them 
 * here.
 */
public class InteractiveApp extends ApplicationInstance {

    /**
     * A boolean flag indicating whether the application is running on a live
     * demo server.  This flag is used to disable certain tests based on 
     * whether the <code>nextapp.echo2.demoserver</code> system property is
     * set.
     */
    public static final boolean LIVE_DEMO_SERVER;
    static {
        boolean liveDemoServer;
        try {
            if ("true".equals(System.getProperties().getProperty("nextapp.echo2.demoserver"))) {
                liveDemoServer = true;
            } else {
                liveDemoServer = false;
            }
        } catch (SecurityException ex) {
            liveDemoServer = false;
        }
        LIVE_DEMO_SERVER = liveDemoServer;
    }
    
    /**
     * Convenience method to return the <code>ThreadLocal</code> instance 
     * precast to the appropriate type.
     * 
     * @return the active <code>InteractiveApp</code>
     * @see #getActive()
     */
    public static InteractiveApp getApp() {
        return (InteractiveApp) ApplicationInstance.getActive();
    }

    private TaskQueueHandle ghostTaskQueue;
    private Window mainWindow;
    private ConsoleWindowPane console;
    
    /**
     * Writes a message to a pop-up debugging console.
     * The console is used for displaying information such as
     * fired events.
     * 
     * @param message the message to write to the console
     */
    public void consoleWrite(String message) {
        if (console == null) {
            console = new ConsoleWindowPane();
            getDefaultWindow().getContent().add(console);
        } else if (console.getParent() == null) {
            getDefaultWindow().getContent().add(console);
        }
        console.writeMessage(message);
    }
    
    /**
     * Displays a <code>TestPane</code> from which the user may select an
     * interactive test to run.
     */
    public void displayTestPane() {
        mainWindow.setContent(new TestPane());
    }
    
    /**
     * Displays the <code>WelcomePane</code> which greets new users visiting
     * the application.
     */
    public void displayWelcomePane() {
        mainWindow.setContent(new WelcomePane());
    }

    /**
     * @see nextapp.echo2.app.ApplicationInstance#init()
     */
    public Window init() {
        setStyleSheet(Styles.DEFAULT_STYLE_SHEET);
        mainWindow = new Window();
        mainWindow.setTitle("NextApp Echo2 Test Application");
        mainWindow.setContent(new WelcomePane());
        
        ContainerContext cc = (ContainerContext) getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
        if (!LIVE_DEMO_SERVER && cc.getInitialParameterMap().containsKey("ghost")) {
            startGhostTask(0, 0);
        }
        
        return mainWindow;
    }
    
    /**
     * Sets the window title to display the current iteration of the ghost 
     * test.
     * 
     * @param iteration the iteration number (negative values will restore
     *        the window title to its normal state)
     */
    public void setGhostIterationWindowTitle(int iteration) {
        if (iteration < 0) {
            mainWindow.setTitle("NextApp Echo2 Test Application");
        } else {
            mainWindow.setTitle("Iteration #" + iteration);
        }
    }
    
    /**
     * Starts the ghost test with the specified callback interval and run-time.
     * 
     * @param interval the callback interval between ghost actions, in
     *        milliseconds
     * @param runTime the total run-time of the ghost test, in milliseconds;
     *        specifying 0 will run the ghost test indefinitely
     */
    public void startGhostTask(int interval, long runTime) {
        if (ghostTaskQueue != null) {
            return;
        }
        ghostTaskQueue = createTaskQueue();
        ContainerContext containerContext = 
                (ContainerContext) getContextProperty(ContainerContext.CONTEXT_PROPERTY_NAME);
        containerContext.setTaskQueueCallbackInterval(ghostTaskQueue, interval);
        GhostTask.start(this, ghostTaskQueue, runTime);
    }
    
    /**
     * Stops the currently running ghost test.
     */
    public void stopGhostTest() {
        removeTaskQueue(ghostTaskQueue);
        ghostTaskQueue = null;
    }
}
