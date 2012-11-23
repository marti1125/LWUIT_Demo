/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Painter;
import com.sun.lwuit.animations.Animation;
import com.sun.lwuit.animations.Motion;
import com.sun.lwuit.animations.Transition;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.geom.Rectangle;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import java.util.Vector;

/**
 * LWUIT Demo main Form, this special Form allows drag&drop on top of it. 
 * 
 * 
 * @author Chen Fishbein
 */
public class MainScreenForm extends Form {
    private Component dragged;
    private int oldx;
    private int oldy;
    private int draggedx;
    private int draggedy;
    private Image draggedImage;
    private Vector cmps;
    private Transition cmpTransition;
    private UIDemoMIDlet parent;
    private boolean dragMode;

    public MainScreenForm(UIDemoMIDlet parent, String title) {
        setTitle(title);
        this.parent = parent;
    }
    
    public void setDragMode(boolean dragMode) {
        this.dragMode = dragMode;
        setSmoothScrolling(!dragMode);
    }
    
    protected void sizeChanged(int w, int h) {
        super.sizeChanged(w, h);
        try {
            parent.setMainForm(Resources.open("/resources.res"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void pointerDragged(int x, int y) {
        if(!dragMode) {
            super.pointerDragged(x, y);
            return;
        }
        if (draggedImage == null) {
            dragged = getComponentAt(x, y);
            if (dragged == null || !getContentPane().contains(dragged)) {
                super.pointerDragged(x, y);
                return;
            }
            draggedImage = Image.createImage(dragged.getWidth(), dragged.getHeight());
            Graphics g = draggedImage.getGraphics();
            g.setClip(0, 0, dragged.getWidth(), dragged.getHeight());
            //choose a rare color
            g.setColor(0xff7777);
            g.fillRect(0, 0, dragged.getWidth(), dragged.getHeight());
            g.translate(-dragged.getX(), -dragged.getY());
            dragged.paint(g);
            g.translate(dragged.getX(), dragged.getY());

            //remove all occurences of the rare color
            draggedImage = draggedImage.modifyAlpha((byte)0x55, 0xff7777);

            oldx = x;
            oldy = y;
            draggedx = dragged.getAbsoluteX();
            draggedy = dragged.getAbsoluteY();
            dragged.setVisible(false);
            Painter glassPane = new Painter() {
                public void paint(Graphics g, Rectangle rect) {
                    if (draggedImage != null) {
                        g.drawImage(draggedImage, draggedx, draggedy);
                    }
                }
            };
            setGlassPane(glassPane);
            cmpTransition = UIDemoMIDlet.getComponentTransition();
            UIDemoMIDlet.setComponentTransition(null);
            return;
        }

        repaint(draggedx, draggedy, dragged.getWidth(), dragged.getHeight());

        draggedx = draggedx + (x - oldx);
        draggedy = draggedy + (y - oldy);
        oldx = x;
        oldy = y;
        repaint(draggedx, draggedy, dragged.getWidth(), dragged.getHeight());
        super.pointerDragged(x, y);
    }

    public void pointerReleased(int x, int y) {
        if(!dragMode) {
            super.pointerReleased(x, y);
            return;
        }
        if (draggedImage == null) {
            super.pointerReleased(x, y);
            return;
        }
        setVisible(false);
        oldx = 0;
        oldy = 0;

        Component cmp = getFocused();

        final int index = getContentPane().getComponentIndex(cmp);
        cmps = new Vector();
        if (index >= 0) {
            int draggedIndex = getContentPane().getComponentIndex(dragged);
            int startIndex = Math.min(index, draggedIndex);
            for (int i = startIndex; i < getContentPane().getComponentCount(); i++) {
                Component toMove = getContentPane().getComponentAt(i);
                LayoutAnimation la = new LayoutAnimation(toMove);
                la.setFrom(new Dimension(toMove.getX(), toMove.getY()));
                cmps.addElement(la);
            }

            removeComponent(dragged);
            addComponent(index, dragged);

            layoutContainer();

            LayoutAnimation la = new LayoutAnimation(dragged);
            int dx= Math.max(0, draggedx - (dragged.getAbsoluteX() - dragged.getX()));
            int dy= Math.max(0, draggedy - (dragged.getAbsoluteY() - dragged.getY()));
            dx= Math.min(dx, getContentPane().getPreferredW() - dragged.getWidth());
            dy= Math.min(dy, getContentPane().getPreferredH() - dragged.getHeight());
            
            la.setFrom(new Dimension(dx, dy));
            la.setTo(new Dimension(dragged.getX(), dragged.getY()));
            la.init();

            for (int i = 0; i < cmps.size(); i++) {
                LayoutAnimation l = (LayoutAnimation) cmps.elementAt(i);
                l.setTo(new Dimension(l.toAnimate.getX(), l.toAnimate.getY()));
                l.init();
            }
            cmps.addElement(la);

            removeComponent(dragged);
            addComponent(draggedIndex, dragged);
            layoutContainer();
            la.init();
        } else {
            finishDrag();
            return;
        }

        registerAnimated(new Animation() {
            public boolean animate() {
                boolean retVal = false;
                for (int i = 0; i < cmps.size(); i++) {
                    LayoutAnimation la = (LayoutAnimation) cmps.elementAt(i);
                    if (la.animate()) {
                        retVal = true;
                    }
                }
                //if finished
                if (!retVal) {
                    deregisterAnimated(this);
                    if(getContentPane().contains(dragged)){
                        removeComponent(dragged);
                        addComponent(index, dragged);
                    }
                    finishDrag();
                }

                return retVal;
            }

            public void paint(Graphics g) {
                for (int i = 0; i < cmps.size(); i++) {
                    LayoutAnimation la = (LayoutAnimation) cmps.elementAt(i);
                    la.paint(g);
                    repaint(la.toAnimate.getAbsoluteX(), la.toAnimate.getAbsoluteY(),
                            la.toAnimate.getWidth(), la.toAnimate.getHeight());
                }
                if (!dragged.isVisible()) {
                    dragged.setVisible(true);
                }
            }
        });


        setVisible(true);
        repaint();
        setGlassPane(null);
    }

    private void finishDrag() {
        setGlassPane(null);
        if (dragged != null) {
            dragged.requestFocus();
            if (!dragged.isVisible()) {
                dragged.setVisible(true);
                dragged.requestFocus();
            }
            dragged = null;
        }
        draggedImage = null;
        UIDemoMIDlet.setComponentTransition(cmpTransition);
        repaint();
    }

    class LayoutAnimation implements Animation {
        private Component toAnimate;
        private Dimension from;
        private Dimension to;
        private Motion xMotion;
        private Motion yMotion;

        LayoutAnimation(Component toAnimate) {
            this.toAnimate = toAnimate;
        }

        public void setFrom(Dimension from) {
            this.from = from;
        }

        public void setTo(Dimension to) {
            this.to = to;
        }

        public void init() {
            toAnimate.setX(from.getWidth());
            toAnimate.setY(from.getHeight());
            xMotion = Motion.createSplineMotion(from.getWidth(), to.getWidth(), 500);
            yMotion = Motion.createSplineMotion(from.getHeight(), to.getHeight(), 500);
            xMotion.start();
            yMotion.start();
        }

        public boolean animate() {
            toAnimate.setX(xMotion.getValue());
            toAnimate.setY(yMotion.getValue());
            return !xMotion.isFinished() && !yMotion.isFinished();
        }

        public void paint(Graphics g) {
            toAnimate.paintComponent(g);
        }
    }
}
