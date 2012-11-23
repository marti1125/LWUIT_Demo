/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Calendar;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.TabbedPane;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * Demot of the TabbedPane available in the UI
 * 
 * @author Tamir Shabat
 */
public class TabbedPaneDemo extends Demo {

    TabbedPane tp = null;

     public void cleanup() {
         tp = null;
     }

    public String getName() {
        return "Tabs";
    }

    protected String getHelp() {
        return "This Demo shows TabbedPane functionalities";
    }

    protected void execute(Form f) {
        f.setLayout(new BorderLayout());
        f.setScrollable(false);
        tp = new TabbedPane();

        tp.addTab("Tab 1", new Label("Welcome to TabbedPane demo!"));

        Container radioButtonsPanel = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        RadioButton topRB = new RadioButton("Top");
        RadioButton LeftRB = new RadioButton("Left");
        RadioButton BottomRB = new RadioButton("Bottom");
        RadioButton RightRB = new RadioButton("Right");

        RadioListener myListener = new RadioListener();
        topRB.addActionListener(myListener);
        LeftRB.addActionListener(myListener);
        BottomRB.addActionListener(myListener);
        RightRB.addActionListener(myListener);

        ButtonGroup group1 = new ButtonGroup();
        group1.add(topRB);
        group1.add(LeftRB);
        group1.add(BottomRB);
        group1.add(RightRB);

        radioButtonsPanel.addComponent(new Label("Please choose a tab placement direction:"));
        radioButtonsPanel.addComponent(topRB);
        radioButtonsPanel.addComponent(LeftRB);
        radioButtonsPanel.addComponent(BottomRB);
        radioButtonsPanel.addComponent(RightRB);

        tp.addTab("Tab 2", radioButtonsPanel);
        tp.addTab("Tab 3", new Calendar());
        
        f.addComponent("Center", tp);
    }

    /** Listens to the radio buttons. */
    class RadioListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String title = ((RadioButton) e.getSource()).getText();
            if ("Top".equals(title)) {
                tp.setTabPlacement(TabbedPane.TOP);
            } else if ("Left".equals(title)) {
                tp.setTabPlacement(TabbedPane.LEFT);
            } else if ("Bottom".equals(title)) {
                tp.setTabPlacement(TabbedPane.BOTTOM);
            } else {//right
                tp.setTabPlacement(TabbedPane.RIGHT);
            }
        }
    }
}
