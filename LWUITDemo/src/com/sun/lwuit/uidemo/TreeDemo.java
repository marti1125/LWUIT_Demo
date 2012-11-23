/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Dialog;
import com.sun.lwuit.Form;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.tree.Tree;
import com.sun.lwuit.tree.TreeModel;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

/**
 * Simple demo showing off how to use the tree component in LWUIT
 * 
 * @author Shai Almog
 */
public class TreeDemo extends Demo {
    
    private static boolean dummyMode = false;
    
    public String getName() {
        return "Tree";
    }

    protected void execute(Form f) {
        try {
            f.setLayout(new BorderLayout());
            Resources imageRes = UIDemoMIDlet.getResource("images");
            Tree.setFolderIcon(imageRes.getImage("sady.png"));
            Tree.setFolderOpenIcon(imageRes.getImage("smily.png"));
            Tree tree = new Tree(new FileTreeModel()){
                protected String childToDisplayLabel(Object child) {                    
                    if (!dummyMode) {
                        if (((String) child).endsWith("/")) {
                            return ((String) child).substring(((String) child).lastIndexOf('/', ((String) child).length() - 2));
                        }
                        return ((String) child).substring(((String) child).lastIndexOf('/'));
                    }else{
                        return super.childToDisplayLabel(child);
                    }
                }
            };
            f.addComponent(BorderLayout.CENTER, tree);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class FileTreeModel implements TreeModel {

        

        public Vector getChildren(Object parent) {
            Vector response = new Vector();
            if(dummyMode) {
                if(parent == null) {
                    response.addElement("One");
                    response.addElement("Two");
                    response.addElement("Three");
                } else {
                    if(!isLeaf(parent)) {
                        response.addElement("Child One");
                        response.addElement("Child Two");
                        response.addElement("Child Three");
                    }
                }
                return response;
            }
            try {
                if(parent == null) {
                    Enumeration e = FileSystemRegistry.listRoots();
                    while(e.hasMoreElements()) {
                        response.addElement("file:///" + e.nextElement());
                    }
                } else {
                    String name = (String)parent;
                    FileConnection c = (FileConnection)Connector.open(name, Connector.READ);
                    Enumeration e = c.list();
                    while(e.hasMoreElements()) {
                        response.addElement(name + e.nextElement());
                    }
                    c.close();
                }
            } catch(Throwable err) {
                err.printStackTrace();
                dummyMode = true;
                return getChildren(parent);
            }
            return response;
        }

        public boolean isLeaf(Object node) {
            if(dummyMode) {
                return ((String)node).indexOf("Child") > -1;
            }
            boolean d = true;
            try {
                FileConnection c = (FileConnection)Connector.open((String)node, Connector.READ);
                d = c.isDirectory();
                c.close();
            } catch(Throwable err) {
                err.printStackTrace();
                dummyMode = true;
                return isLeaf(node);
            }
            return !d;
        }
        
        
    }

    /**
     * Returns the text that should appear in the help command
     */
    protected String getHelp() {
        return "The tree component represents a hierarchy view, here we show the filesystem of the phone as an example";
    }
}
