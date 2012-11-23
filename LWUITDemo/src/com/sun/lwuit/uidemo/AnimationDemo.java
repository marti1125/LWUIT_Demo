/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.util.Resources;
import java.io.IOException;

/**
 * Demonstrates simple animation both static and manual
 *
 * @author Shai Almog
 */
public class AnimationDemo extends Demo {

    public String getName() {
        return "Animations";
    }

    protected void execute(final Form f) {
        try {
            Resources images = UIDemoMIDlet.getResource("images");
            Image a = images.getImage("progress0.png");
            Image b = images.getImage("progress1.png");
            Image c = images.getImage("progress2.png");
            final Image[] secondAnimation = new Image[] {
                a, 
                b, 
                c, 
                a.rotate(90), 
                b.rotate(90), 
                c.rotate(90), 
                a.rotate(180), 
                b.rotate(180), 
                c.rotate(180), 
                a.rotate(270), 
                b.rotate(270), 
                c.rotate(270),
            };


            Label animation = new Label(UIDemoMIDlet.getResource("duke").getAnimation("duke3_1.gif"));
            animation.setFocusable(true);
            f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            f.addComponent(animation);

            Label animation2 = new Label() {

                private int currentImage = 0;
                private long lastInvoke;

                public void initComponent() {
                    f.registerAnimated(this);
                }

                public boolean animate() {
                    long current = System.currentTimeMillis();
                    if (current - lastInvoke > 50) {
                        lastInvoke = current;
                        currentImage++;
                        if (currentImage == secondAnimation.length) {
                            currentImage = 0;
                        }
                        setIcon(secondAnimation[currentImage]);
                        return true;
                    }
                    return false;
                }
            };
            animation2.setIcon(secondAnimation[0]);
            animation2.setFocusable(true);
            f.addComponent(animation2);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected String getHelp() {
        return "Animations can be invoked by registering the Animation to the " +
                "Form. When registered the event loop calls to the Animation " +
                "animate() method and then calls to repaint() " +
                "In This examples we have 2 simple Animations that replace " +
                "images in a loop";
    }
    
    
}