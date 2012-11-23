/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Button;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import java.io.IOException;

/**
 * Set of the button types available in the UI
 * 
 * @author Shai Almog
 */
public class ButtonsDemo extends Demo {

    public String getName() {
        return "Buttons";
    }

    protected String getHelp() {
        return "This Demo shows Buttons functionalities and demos different Buttons types";
    }

    protected void execute(final Form f) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        Label buttonLabel = new Label("Buttons:");
        f.addComponent(buttonLabel);
        final Button left = new Button("Left Alignment Button");
        left.setAlignment(Label.LEFT);
        final Button right = new Button("Right Alignment Button");
        right.setAlignment(Label.RIGHT);
        final Button center = new Button("Center Alignment Button");
        center.setAlignment(Label.CENTER);
        f.addComponent(left);
        f.addComponent(right);
        f.addComponent(center);

        Label cbLabel = new Label("CheckBox:");
        f.addComponent(cbLabel);
        CheckBox firstCB = new CheckBox("First CheckBox");
        f.addComponent(firstCB);
        CheckBox secondCB = new CheckBox("Second CheckBox");
        f.addComponent(secondCB);


        Label bordersLabel = new Label("Borders:");
        f.addComponent(bordersLabel);
                
        final RadioButton defaultBorder = new RadioButton("Default");
        f.addComponent(defaultBorder);
        
        final RadioButton etchedBorder = new RadioButton("Etched Raised Theme");
        f.addComponent(etchedBorder);

        final RadioButton etchedColors = new RadioButton("Etched Raised Colors");
        f.addComponent(etchedColors);

        final RadioButton etchedLowBorder = new RadioButton("Etched Lowered Theme");
        f.addComponent(etchedLowBorder);

        final RadioButton etchedLowColors = new RadioButton("Etched Lowered Colors");
        f.addComponent(etchedLowColors);

        final RadioButton bevelBorder = new RadioButton("Bevel Raised Theme");
        f.addComponent(bevelBorder);

        final RadioButton bevelColors = new RadioButton("Bevel Raised Colors");
        f.addComponent(bevelColors);

        final RadioButton bevelLowBorder = new RadioButton("Bevel Lowered Theme");
        f.addComponent(bevelLowBorder);

        final RadioButton bevelLowColors = new RadioButton("Bevel Lowered Colors");
        f.addComponent(bevelLowColors);

        final RadioButton roundBorder = new RadioButton("Round Theme");
        f.addComponent(roundBorder);

        final RadioButton roundColors = new RadioButton("Round Colors");
        f.addComponent(roundColors);

        final RadioButton imageBorder = new RadioButton("Image Border");
        f.addComponent(imageBorder);
        
        //final Button setAsDefault = new Button("Set As Default Border");
        //f.addComponent(setAsDefault);
        
        ActionListener listener = new ActionListener() {
            private Border lastBorder;
            private void setBorder(Border b) {
                lastBorder = b;
                left.getStyle().setBorder(b);
                right.getStyle().setBorder(b);
                center.getStyle().setBorder(b);
                f.repaint();
            }
            
            public void actionPerformed(ActionEvent evt) {
                Object source = evt.getSource();
                /*if(source == setAsDefault) {
                    Border.setDefaultBorder(lastBorder);
                    return;
                }*/
                
                if(source == defaultBorder) {
                    setBorder(Border.getDefaultBorder());
                    return;
                }
                
                if(source == etchedBorder) {
                    setBorder(Border.createEtchedRaised());
                    return;
                }

                if(source == etchedColors) {
                    setBorder(Border.createEtchedRaised(0x020202, 0xBBBBBB));
                    return;
                }

                if(source == etchedLowBorder) {
                    setBorder(Border.createEtchedLowered());
                    return;
                }

                if(source == etchedLowColors) {
                    setBorder(Border.createEtchedLowered(0x020202, 0xBBBBBB));
                    return;
                }

                if(source == bevelBorder) {
                    setBorder(Border.createBevelRaised());
                    return;
                }

                if(source == bevelColors) {
                    setBorder(Border.createBevelRaised(0xdddddd, 0xAAAAAA, 0x111111,0x020202));
                    return;
                }

                if(source == bevelLowBorder) {
                    setBorder(Border.createBevelLowered());
                    return;
                }

                if(source == bevelLowColors) {
                    setBorder(Border.createBevelLowered(0xdddddd, 0xAAAAAA, 0x111111,0x020202));
                    return;
                }

                if(source == roundBorder) {
                    setBorder(Border.createRoundBorder(8, 8));
                    return;
                }

                if(source == roundColors) {
                    setBorder(Border.createRoundBorder(8, 8, 0xcccccc));
                    return;
                }

                if(source == imageBorder) {
                    try {
                        Image top = Image.createImage("/Top.png");
                        Image topLeft = Image.createImage("/TopLeft.png");
                        Image center = Image.createImage("/Center.png");
                        Border b = Border.createImageBorder(top, topLeft, center);
                        b.setPressedInstance(Border.createImageBorder(top.modifyAlpha((byte)100), topLeft.modifyAlpha((byte)100), center.modifyAlpha((byte)100)));
                        setBorder(b);
                        return;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        ButtonGroup group = new ButtonGroup();
        group.add(defaultBorder);
        defaultBorder.addActionListener(listener);
        group.add(etchedBorder);
        etchedBorder.addActionListener(listener);
        group.add(etchedColors);
        etchedColors.addActionListener(listener);
        group.add(etchedLowBorder);
        etchedLowBorder.addActionListener(listener);
        group.add(etchedLowColors);
        etchedLowColors.addActionListener(listener);
        group.add(bevelBorder);
        bevelBorder.addActionListener(listener);
        bevelColors.addActionListener(listener);
        group.add(bevelColors);
        bevelLowBorder.addActionListener(listener);
        group.add(bevelLowBorder);
        bevelLowColors.addActionListener(listener);
        group.add(bevelLowColors);
        roundBorder.addActionListener(listener);
        group.add(roundBorder);
        roundColors.addActionListener(listener);
        group.add(roundColors);
        imageBorder.addActionListener(listener);
        group.add(imageBorder);
        //setAsDefault.addActionListener(listener);
    }
}
