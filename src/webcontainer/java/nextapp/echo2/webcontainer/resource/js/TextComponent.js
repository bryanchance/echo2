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

//_________________________
// Object EchoTextComponent

/**
 * Static object/namespace for Text Component support.
 * This object/namespace should not be used externally.
 */
EchoTextComponent = function() { };

/**
 * Static object/namespace for Text Component MessageProcessor 
 * implementation.
 */
EchoTextComponent.MessageProcessor = function() { };

/**
 * MessageProcessor process() implementation 
 * (invoked by ServerMessage processor).
 *
 * @param messagePartElement the <code>message-part</code> element to process.
 */
EchoTextComponent.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EchoTextComponent.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EchoTextComponent.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            case "set-text":
                EchoTextComponent.MessageProcessor.processSetText(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

/**
 * Processes a <code>dispose</code> message to finalize the state of a
 * Text Component that is being removed.
 *
 * @param disposeMessageElement the <code>dispose</code> element to process
 */
EchoTextComponent.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        EchoEventProcessor.removeHandler(elementId, "blur");
        EchoEventProcessor.removeHandler(elementId, "focus");
        EchoEventProcessor.removeHandler(elementId, "keyup");

        var textComponent = document.getElementById(elementId);
        if (textComponent) {
            EchoDomUtil.removeEventListener(textComponent, "keypress", EchoTextComponent.processKeyPress, false);
        }

        // Remove any updates to text component that occurred during client/server transaction.
        EchoClientMessage.removePropertyElement(elementId, "text");
    }
};


/**
 * Processes a <code>set-text</code> message to update the text displayed in a
 * Text Component.
 *
 * @param setTextMessageElement the <code>set-text</code> element to process
 */
EchoTextComponent.MessageProcessor.processSetText = function(setTextMessageElement) {
    for (var item = setTextMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var text = item.getAttribute("text");
        var textComponent = document.getElementById(elementId);
        textComponent.value = text;
        
        // Remove any updates to text component that occurred during client/server transaction.
        EchoClientMessage.removePropertyElement(textComponent.id, "text");
    }
};

/**
 * Processes an <code>init</code> message to initialize the state of a 
 * Text Component that is being added.
 *
 * @param initMessageElement the <code>init</code> element to process
 */
EchoTextComponent.MessageProcessor.processInit = function(initMessageElement) {
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var textComponent = document.getElementById(elementId);
        
        if (item.getAttribute("enabled") == "false") {
            textComponent.readOnly = true;
            EchoDomPropertyStore.setPropertyValue(textComponent.id, "EchoClientEngine.inputDisabled", true);
        }
        if (item.getAttribute("text")) {
            textComponent.value = item.getAttribute("text");
        }
        if (item.getAttribute("server-notify")) {
            EchoDomPropertyStore.setPropertyValue(textComponent.id, "serverNotify", true);
        }
        if (item.getAttribute("maximum-length")) {
            EchoDomPropertyStore.setPropertyValue(textComponent.id, "maximumLength", item.getAttribute("maximum-length"));
        }
        if (item.getAttribute("horizontal-scroll")) {
            textComponent.scrollLeft = parseInt(item.getAttribute("horizontal-scroll"));
        }
        if (item.getAttribute("vertical-scroll")) {
            if (EchoClientProperties.get("quirkIERepaint")) {
                // Avoid IE quirk where browser will fail to set scroll bar position.
                var originalWidth = textComponent.style.width;
                var temporaryWidth = parseInt(textComponent.clientWidth) - 1;
                textComponent.style.width = temporaryWidth + "px";
                textComponent.style.width = originalWidth;
            }
            textComponent.scrollTop = parseInt(item.getAttribute("vertical-scroll"));
        }
        
        if (EchoClientProperties.get("quirkMozillaTextInputRepaint")) {
            // Avoid Mozilla quirk where text will be rendered outside of text field
            // (this appears to be a Mozilla bug).
            var noValue = !textComponent.value;
            if (noValue) {
                textComponent.value = "-";
            }
            var currentWidth = textComponent.style.width;
            textComponent.style.width = "20px";
            textComponent.style.width = currentWidth;
            if (noValue) {
                textComponent.value = "";
            }
        }
        
        EchoEventProcessor.addHandler(elementId, "blur", "EchoTextComponent.processBlur");
        EchoEventProcessor.addHandler(elementId, "focus", "EchoTextComponent.processFocus");
        EchoEventProcessor.addHandler(elementId, "keyup", "EchoTextComponent.processKeyUp");
        
        EchoDomUtil.addEventListener(textComponent, "keypress", EchoTextComponent.processKeyPress, false);
    }
};

/**
 * Processes a user "action request" on the text component i.e., the pressing
 * of the ENTER key when the the component is focused.
 * If any server-side <code>ActionListener</code>s are registered, an action
 * will be set in the ClientMessage and a client-server connection initiated.
 *
 * @param textComponent the text component elemnet
 */
EchoTextComponent.doAction = function(textComponent) {
    if (!EchoDomPropertyStore.getPropertyValue(textComponent.id, "serverNotify")) {
        return;
    }
    
    if (!EchoClientEngine.verifyInput(textComponent.id, false)) {
        // Don't process actions when client/server transaction in progress.
        EchoDomUtil.preventEventDefault(echoEvent);
        return;
    }
    
    EchoTextComponent.updateClientMessage(textComponent);
    EchoClientMessage.setActionValue(textComponent.id, "action");
    EchoServerTransaction.connect();
};

/**
 * Processes a focus blur event:
 * Records the current state of the text field to the ClientMessage.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EchoTextComponent.processBlur = function(echoEvent) {
    var textComponent = echoEvent.registeredTarget;
    if (!EchoClientEngine.verifyInput(textComponent.id)) {
        return;
    }
    
    EchoTextComponent.updateClientMessage(textComponent);
    EchoFocusManager.setFocusedState(textComponent.id, false);
};

/**
 * Processes a focus event:
 * Notes focus state in ClientMessage.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EchoTextComponent.processFocus = function(echoEvent) {
    var textComponent = echoEvent.registeredTarget;
    if (!EchoClientEngine.verifyInput(textComponent.id)) {
        return;
    }
    EchoFocusManager.setFocusedState(textComponent.id, true);
};

/**
 * Processes a key press event:
 * Initiates an action in the event that the key pressed was the
 * ENTER key.
 *
 * @param e the DOM Level 2 event, if avaialable
 */
EchoTextComponent.processKeyPress = function(e) {
    e = e ? e : window.event;
    var textComponent = e.target ? e.target : e.srcElement;
    if (!EchoClientEngine.verifyInput(textComponent.id, true)) {
        EchoDomUtil.preventEventDefault(e);
        return;
    }
    if (e.keyCode == 13) {
        EchoTextComponent.doAction(textComponent);
    }
};

/**
 * Processes a key up event:
 * Records the current state of the text field to the ClientMessage.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EchoTextComponent.processKeyUp = function(echoEvent) {
    var textComponent = echoEvent.registeredTarget;
    if (!EchoClientEngine.verifyInput(textComponent.id, true)) {
        EchoDomUtil.preventEventDefault(echoEvent);
        return;
    }
    if (EchoDomPropertyStore.getPropertyValue(textComponent.id, "maximumLength")) {
        var maximumLength = EchoDomPropertyStore.getPropertyValue(textComponent.id, "maximumLength");
        if (textComponent.value && textComponent.value.length > maximumLength) {
            textComponent.value = textComponent.value.substring(0, maximumLength);
        }
    }
    
    EchoTextComponent.updateClientMessage(textComponent);
};

/**
 * Updates the component state in the outgoing <code>ClientMessage</code>.
 *
 * @param componentId the id of the Text Component
 */
EchoTextComponent.updateClientMessage = function(textComponent) {
    var textPropertyElement = EchoClientMessage.createPropertyElement(textComponent.id, "text");
    if (textPropertyElement.firstChild) {
        textPropertyElement.firstChild.nodeValue = textComponent.value;
    } else {
        textPropertyElement.appendChild(EchoClientMessage.messageDocument.createTextNode(textComponent.value));
    }
    
    EchoClientMessage.setPropertyValue(textComponent.id, "horizontalScroll", textComponent.scrollLeft);
    EchoClientMessage.setPropertyValue(textComponent.id, "verticalScroll", textComponent.scrollTop);
};
