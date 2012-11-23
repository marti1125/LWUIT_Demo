/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.CoordinateLayout;
import com.sun.lwuit.layouts.FlowLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.layouts.GroupLayout;
import com.sun.lwuit.layouts.LayoutStyle;

/**
 * Demonstrates the various layout managers that are a part of LWUIT
 *
 * @author Chen Fishbein
 */
public class LayoutDemo extends Demo {

    private Button group;
    private Button coordinate;
    private Button border;
    
    private Button boxY;
    
    private Button boxX;
    
    private Button flow;
    
    private Button grid;
    
     public void cleanup() {
         border = null;
         boxY = null;
         flow = null;
         grid = null;
         boxX = null;
     }

    public String getName() {
        return "Layouts";
    }

    protected String getHelp() {
        return "The toolkit supports 5 different Layouts: FlowLayout, BorderLayout, BoxLayout, GridLayout and GroupLayout." +
                "In this Demo we added 5 Components to the Form and we changes the Layout and rearrange the Components" +
                "A developer can add his own Layout by extending the Layout class";
    }

    protected void execute(final Form f) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        border = new Button("BorderLayout");
        
        border.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                f.setLayout(new BorderLayout());
                f.removeAll();
                f.setScrollable(false);
                f.addComponent(BorderLayout.NORTH, border);
                f.addComponent(BorderLayout.EAST, boxY);
                f.addComponent(BorderLayout.CENTER, grid);
                f.addComponent(BorderLayout.WEST, flow);
                f.addComponent(BorderLayout.SOUTH, boxX);
                f.show();
            }
        });
        boxY = new Button("BoxLayout-Y");
        boxY.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
                f.setScrollable(false);
                addComponents(f);
                f.show();
            }
        });
        flow = new Button("FlowLayout");
        flow.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                f.setLayout(new FlowLayout());
                f.setScrollable(false);
                addComponents(f);
                f.show();
            }
        });
        
        grid = new Button("GridLayout");
        grid.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                f.setLayout(new GridLayout(3, 2));
                f.setScrollable(false);
                addComponents(f);
                f.show();
            }
        });
        
        boxX = new Button("BoxLayout-X");
        boxX.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                f.setLayout(new BoxLayout(BoxLayout.X_AXIS));
                f.setScrollable(true);
                addComponents(f);
                f.show();
            }
        });
        
        coordinate = new Button("Coordinate");
        coordinate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                f.setLayout(new CoordinateLayout(100, 100));
                f.setScrollable(false);
                addComponents(f);
                boxY.setX(0);
                boxY.setY(0);
                boxX.setX(10);
                boxX.setY(15);
                border.setX(20);
                border.setY(30);
                flow.setX(30);
                flow.setY(45);
                grid.setX(40);
                grid.setY(60);
                group.setX(50);
                group.setY(75);
                coordinate.setX(60);
                coordinate.setY(90);
                f.show();
            }
        });

        group = new Button("Group");
        group.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                GroupLayout layout = new GroupLayout(f);
                f.setLayout(layout);
                f.setScrollableX(true);
                layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(group)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(coordinate)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(grid))
                            .add(layout.createSequentialGroup()
                                .add(border)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(boxY, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                            .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(boxX, GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(flow)))
                        .addContainerGap())
                );

                layout.linkSize(new Component[] {border, coordinate, flow, group}, GroupLayout.HORIZONTAL);

                layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(group)
                            .add(coordinate)
                            .add(grid))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(border)
                            .add(boxY))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(boxX, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(flow, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                );
            }
        });



        addComponents(f);
        f.show();
    }
    
    private void addComponents(final Form f){
        f.removeAll();
        f.addComponent(boxY);
        f.addComponent(boxX);
        f.addComponent(border);
        f.addComponent(flow);
        f.addComponent(grid);
        f.addComponent(group);
        f.addComponent(coordinate);
    }
}