package com.sun.me.web.sample.local;

import com.sun.lwuit.Image;
import com.sun.lwuit.Component;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.plaf.Style;
import com.sun.lwuit.Display;
import com.sun.lwuit.animations.Motion;

/**
 * A component that allows us to drag an image file with a physical drag motion
 * effect.
 *
 * @author Shai Almog
 */
public class MotionComponent extends Component {
    private Image img;
    private int positionX;
    private int positionY;
    private Motion motionX;
    private Motion motionY;
    private int destX;
    private int destY;
    private static final int TIME = 800;
    private static final int DISTANCE_X = Display.getInstance().getDisplayWidth() / 3;
    private static final int DISTANCE_Y = Display.getInstance().getDisplayHeight() / 3;
    private int dragBeginX = -1;
    private int dragBeginY = -1;
    private int dragCount = 0;
    
    public MotionComponent(Image img) {
        this.img = img;
    }
    
    protected Dimension calcPreferredSize() {
        Style s = getStyle();
        return new Dimension(img.getWidth() + s.getPadding(LEFT) + s.getPadding(RIGHT), 
            img.getHeight() + s.getPadding(TOP) + s.getPadding(BOTTOM));
    }
    
    public void initComponent() {
        getComponentForm().registerAnimated(this);
    }
    
    public void paint(Graphics g) {
        Style s = getStyle();
        g.drawImage(img, getX() - positionX + s.getPadding(LEFT), getY() - positionY + s.getPadding(TOP));
    }
    
    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        switch(Display.getInstance().getGameAction(keyCode)) {
            case Display.GAME_DOWN:
                destY = Math.min(destY + DISTANCE_Y, img.getHeight() - Display.getInstance().getDisplayHeight());
                motionY = Motion.createSplineMotion(positionY, destY, TIME);
                motionY.start();
                break;
            case Display.GAME_UP:
                destY = Math.max(destY - DISTANCE_Y, 0);
                motionY = Motion.createSplineMotion(positionY, destY, TIME);
                motionY.start();
                break;
            case Display.GAME_LEFT:
                destX = Math.max(destX - DISTANCE_X, 0);
                motionX = Motion.createSplineMotion(positionX, destX, TIME);
                motionX.start();
                break;
            case Display.GAME_RIGHT:
                destX = Math.min(destX + DISTANCE_X, img.getWidth() - Display.getInstance().getDisplayWidth());
                motionX = Motion.createSplineMotion(positionX, destX, TIME);
                motionX.start();
                break;
            default:
                return;
        }
    }
    
    public void pointerDragged(int x, int y) {
        if(dragBeginX == -1) {
            dragBeginX = x;
            dragBeginY = y;
        }
        positionX = Math.max(0, Math.min(positionX + x - dragBeginX, img.getWidth() - Display.getInstance().getDisplayWidth()));
        positionY = Math.max(0, Math.min(positionY + y - dragBeginY, img.getHeight() - Display.getInstance().getDisplayHeight()));
        dragCount++;
    }

    public void pointerReleased(int x, int y) {
        // this is a result of a more significant drag operation, some VM's always
        // send a pointerDragged so we should ignore too few drag events
        if(dragCount > 4) {
            float velocity = -0.2f;
            if(dragBeginX < x) {
                velocity = 0.2f;
            }
            motionX = Motion.createFrictionMotion(positionX, velocity, 0.0004f);
            motionX.start();
            
            if(dragBeginY < y) {
                velocity = 0.2f;
            } else {
                velocity = -0.2f;
            }
            motionY = Motion.createFrictionMotion(positionY, velocity, 0.0004f);
            motionY.start();
        }
        dragCount = 0;
        dragBeginX = -1;
        dragBeginY = -1;
    }
    
    public boolean animate() {
        boolean val = false;
        if(motionX != null) {
            positionX = motionX.getValue();
            if(motionX.isFinished()) {
                motionX = null;
            }
            // velocity might exceed image bounds
            positionX = Math.max(0, Math.min(positionX, img.getWidth() - Display.getInstance().getDisplayWidth()));
            val = true;
        }
        if(motionY != null) {
            positionY = motionY.getValue();
            if(motionY.isFinished()) {
                motionY = null;
            }
            positionY = Math.max(0, Math.min(positionY, img.getHeight() - Display.getInstance().getDisplayHeight()));
            val = true;
        }
        return val;
    }
}
