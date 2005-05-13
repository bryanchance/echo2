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

package nextapp.echo2.webcontainer.util;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Provides the capability to transparently assign generated unique string 
 * identifiers to arbitrary objects.  The identifier to object mapping is 
 * stored using <code>WeakReference</code>s such that the identified objects
 * may be garbage collected without regard for this object's references to 
 * them.  The identifiers themselves are also destroyed when the underlying 
 * objects are garbage collected. 
 */
public class IdTable 
implements Serializable {
    
    //BUGBUG. This object needs a fully custom serialization/deserialization strategy such that weak refs will be held.

    private transient Map objectToIdMap = new WeakHashMap();
    private transient HashMap idToReferenceMap = new HashMap();
    private transient ReferenceQueue referenceQueue = new ReferenceQueue();
    
    /**
     * Creates or retrieves a unique identifier for the specified object.
     * Future invocations of this method with the same object will result
     * in the same identifier being returned.
     * 
     * @param object the object to identify
     * @return a unique identifier
     */
    public String getId(Object object) {
        purge();
        String id = (String) objectToIdMap.get(object);
        if (id == null) {
            id = Uid.generateUidString();
            objectToIdMap.put(object, id);
            WeakReference weakReference = new WeakReference(object, referenceQueue);
            idToReferenceMap.put(id, weakReference);
        }
        return id;
    }
    
    /**
     * Retrieves the object associated with the specified identifier.
     * 
     * @param id the identifier
     * @return the object (or null, if the object is not in the queue, perhaps
     *         due to having been dereferenced and garbage collected)
     */
    public Object getObject(String id) {
        purge();
        WeakReference weakReference = (WeakReference) idToReferenceMap.get(id);
        if (weakReference == null) {
            return null;
        }
        Object object = weakReference.get();
        return object;
    }
    
    /**
     * Purges dereferenced/garbage collected entries from the 
     * <code>IdTable</code>.
     */
    private void purge() {
        Reference reference = referenceQueue.poll();
        if (reference == null) {
            return;
        }
        Set referenceSet = new HashSet();
        while (reference != null) {
            referenceSet.add(reference);
            reference = referenceQueue.poll();
        }
        Iterator idIt = idToReferenceMap.keySet().iterator();
        while (idIt.hasNext()) {
            String id = (String) idIt.next();
            if (referenceSet.contains(idToReferenceMap.get(id))) {
                idIt.remove();
            }
        }
    }
}
