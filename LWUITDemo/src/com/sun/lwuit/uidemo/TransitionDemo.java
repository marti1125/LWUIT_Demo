/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Button;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.M3G;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.animations.Transition;
import com.sun.lwuit.animations.Transition3D;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.FlowLayout;
import com.sun.lwuit.spinner.Spinner;


/**
 * Transitons between screens
 *
 * @author Shai Almog
 */
public class TransitionDemo extends Demo {
    /**
     * The selected radio button index 
     */
    private static int selectedIndex = 0;

    public String getName() {
        return "Transitions";
    }

    protected String getHelp() {
        return "Transitions appear when switching from one form to the next, a transition can be bound " +
            "to the operation of exiting or entering the screen. There are default transitions in the toolkit " +
            "and custom transitions are easy to write.";
    }

    private RadioButton createRB(String label, ButtonGroup g, Form f) {
        RadioButton b = new RadioButton(label);
//        Style s = b.getStyle();
//        s.setMargin(0, 0, 0, 0);
//        s.setBgTransparency(70);
        g.add(b);
        f.addComponent(b);
        return b;
    }
    
    protected void execute(final Form f) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        Label title = new Label("Please select a transition type:");
        title.getStyle().setMargin(0, 0, 0, 0);
        title.getStyle().setBgTransparency(70);
        f.addComponent(title);

        final ButtonGroup radioButtonGroup = new ButtonGroup();
        createRB("Slide Horizontal", radioButtonGroup, f);
        createRB("Slide Vertical", radioButtonGroup, f);
        createRB("Fade", radioButtonGroup, f);
        if(M3G.isM3GSupported()) {
            createRB("Rotate", radioButtonGroup, f);
            createRB("Fly In", radioButtonGroup, f);
            createRB("Cube", radioButtonGroup, f);
            createRB("Static Rotation", radioButtonGroup, f);
            createRB("Swing Top", radioButtonGroup, f);
            createRB("Swing Bottom", radioButtonGroup, f);
        }

        radioButtonGroup.setSelected(selectedIndex);

        final Spinner speed = Spinner.create(0, 50000, 500, 100);
        f.addComponent(createPair("Speed", speed));

        final Form destination = new Form("Destination");
        destination.addComponent(new Label("This is the transition destination..."));
        destination.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent evt) {
                f.show();
            }
        });
        
        final CheckBox highQuality = new CheckBox("High Quality");
        highQuality.setSelected(!Display.getInstance().isLightMode());
        f.addComponent(highQuality);
        highQuality.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(Display.getInstance().isLightMode()) {
                    Dialog.show("Warning", "The device seems a bit weak for high quality rendering, " +
                        "using this mode might crash your device.", "OK", null);
                }
            }
        });

        final Button updateButton = new Button("Preview Transition");
        final Button applyButton = new Button("Apply Transition");
        final Button applyMenu = new Button("Apply To Menu");
        final Button applyComponents = new Button("Apply To Components");
        updateButton.setAlignment(Button.CENTER);
        //updateButton.getStyle().setPadding(5, 5, 7, 7);
        applyButton.setAlignment(Button.CENTER);
        //applyButton.getStyle().setPadding(5, 5, 7, 7);
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                int runSpeed = ((Integer)speed.getValue()).intValue();
                Transition in, out;
                switch (radioButtonGroup.getSelectedIndex()) {
                    case 0: {
                        out = CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, runSpeed);
                        in = CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, true, runSpeed);
                        break;
                    }
                    case 1: {
                        out = CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, false, runSpeed);
                        in = CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, true, runSpeed);
                        break;
                    }
                    case 2: {
                        out = CommonTransitions.createFade(runSpeed);
                        in = CommonTransitions.createFade(runSpeed);
                        break;
                    }
                    case 3:  {
                        out = Transition3D.createRotation(runSpeed, true);
                        in = Transition3D.createRotation(runSpeed, false);
                        break;
                    }
                    case 4:  {
                        out = Transition3D.createFlyIn(runSpeed);
                        in = Transition3D.createFlyIn(runSpeed);
                        break;
                    }
                    case 5:  {
                        out = Transition3D.createCube(runSpeed, true);
                        in = Transition3D.createCube(runSpeed, false);
                        break;
                    }
                    case 6:  {
                        out = Transition3D.createStaticRotation(runSpeed, true);
                        in = Transition3D.createStaticRotation(runSpeed, false);
                        break;
                    }
                    case 7:  {
                        out = Transition3D.createSwingIn(runSpeed);
                        in = out;
                        break;
                    }
                    default:  {
                        out = Transition3D.createSwingIn(runSpeed, false);
                        in = out;
                        break;
                    }
                }
                selectedIndex = radioButtonGroup.getSelectedIndex();
                if(out instanceof Transition3D) {
                    ((Transition3D)out).setHighQualityMode(highQuality.isSelected());
                    ((Transition3D)in).setHighQualityMode(highQuality.isSelected());
                }
                if(updateButton == ev.getSource()) {
                    destination.setTransitionOutAnimator(out);
                    destination.setTransitionInAnimator(in);
                    destination.show();
                } else {
                    if(applyButton == ev.getSource()) {
                        UIDemoMIDlet.setTransition(in, out);
                    } else {
                        if(applyMenu == ev.getSource()) {
                            UIDemoMIDlet.setMenuTransition(in, out);
                        } else {
                            if(applyComponents == ev.getSource()) {
                                UIDemoMIDlet.setComponentTransition(in);
                            }
                        }
                    }
                }
            }
        };
        updateButton.addActionListener(listener);
        applyButton.addActionListener(listener);
        applyMenu.addActionListener(listener);
        applyComponents.addActionListener(listener);

        Container buttonPanel = new Container(new FlowLayout(Component.CENTER));
        buttonPanel.addComponent(updateButton);
        f.addComponent(buttonPanel);
        buttonPanel = new Container(new FlowLayout(Component.CENTER));
        buttonPanel.addComponent(applyButton);
        f.addComponent(buttonPanel);
        buttonPanel = new Container(new FlowLayout(Component.CENTER));
        buttonPanel.addComponent(applyMenu);
        f.addComponent(buttonPanel);
        buttonPanel = new Container(new FlowLayout(Component.CENTER));
        buttonPanel.addComponent(applyComponents);
        f.addComponent(buttonPanel);
    }    
}