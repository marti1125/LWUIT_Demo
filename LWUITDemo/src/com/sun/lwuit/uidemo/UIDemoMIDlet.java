/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Button;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.animations.Transition;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.impl.midp.VKBImplementationFactory;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.plaf.LookAndFeel;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Bootstraps the UI toolkit demos 
 *
 * @author Shai Almog
 */
public class UIDemoMIDlet extends javax.microedition.midlet.MIDlet implements ActionListener {

    private static final int EXIT_COMMAND = 1;
    private static final int RUN_COMMAND = 2;
    private static final int BACK_COMMAND = 3;
    private static final int ABOUT_COMMAND = 4;
    private static final int DRAG_MODE_COMMAND = 5;
    private static final int SCROLL_MODE_COMMAND = 6;
    private static final int RTL_COMMAND = 7;
    
    private static final Command runCommand = new Command("Run", RUN_COMMAND);
    private static final Command exitCommand = new Command("Exit", EXIT_COMMAND);
    private static final Command backCommand = new Command("Back", BACK_COMMAND);
    private static final Command aboutCommand = new Command("About", ABOUT_COMMAND);
    private static final Command dragModeCommand = new Command("Drag Mode", DRAG_MODE_COMMAND);
    private static final Command scrollModeCommand = new Command("Scroll Mode", SCROLL_MODE_COMMAND);
    private static final Command rtlCommand = new Command("RTL", RTL_COMMAND);
    private static final Demo[] DEMOS = new Demo[]{
        new ThemeDemo(), new RenderingDemo(), new AnimationDemo(), new ButtonsDemo(),
        new TransitionDemo(), new FontDemo(), new TabbedPaneDemo(), new DialogDemo(), 
        new LayoutDemo(), new ScrollDemo(), new TableDemo(), new TreeDemo()
    };
    private Demo currentDemo;
    private static Transition componentTransitions;
    private Hashtable demosHash = new Hashtable();
    private static MainScreenForm mainMenu;
    private int cols = 0;
    
    private int elementWidth;
    private Resources res;
    
    protected void startApp() {
        try {
            //By using the VKBImplementationFactory.init() we automatically 
            //bundle the LWUIT Virtual Keyboard.
            //If your application is not aimed to touch screen devices, 
            //this line of code should be removed.
            VKBImplementationFactory.init();
            Display.init(this);
            //set the theme
            Resources theme = Resources.open("/LWUITtheme.res");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
            //open the resources file that contains all the icons
            res = Resources.open("/resources.res");
            //although calling directly to setMainForm(r2) will work on
            //most devices, a good coding practice will be to allow the midp 
            //thread to return and to do all the UI on the EDT.
            Display.getInstance().callSerially(new Runnable() {
                public void run() {
                    setMainForm(res);
                }
            });
            
        } catch (Throwable ex) {
            ex.printStackTrace();
            Dialog.show("Exception", ex.getMessage(), "OK", null);
        }
    }

    /**
     * Used instead of using the Resources API to allow us to fetch locally downloaded
     * resources
     * 
     * @param name the name of the resource
     * @return a resources object
     */
    public static Resources getResource(String name) throws IOException {
            return Resources.open("/" + name + ".res");
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean arg0) {
    }

    public static void setTransition(Transition in, Transition out) {
        mainMenu.setTransitionInAnimator(in);
        mainMenu.setTransitionOutAnimator(out);
    }

    public static void setMenuTransition(Transition in, Transition out) {
        mainMenu.setMenuTransitions(in, out);
        UIManager.getInstance().getLookAndFeel().setDefaultMenuTransitionIn(in);
        UIManager.getInstance().getLookAndFeel().setDefaultMenuTransitionOut(out);
    }

    public static Form getMainForm() {
        return mainMenu;
    }

    public static void setComponentTransition(Transition t) {
        if (t != null) {
            mainMenu.setSmoothScrolling(false);
        }
        componentTransitions = t;
    }

    public static Transition getComponentTransition() {
        return componentTransitions;
    }

    void setMainForm(Resources r) {
        UIManager.getInstance().setResourceBundle(r.getL10N("localize", "en"));

        MainScreenForm main = new MainScreenForm(this, "LWUIT Demo");
        if(mainMenu != null){
            main.setTransitionInAnimator(mainMenu.getTransitionInAnimator());
            main.setTransitionOutAnimator(mainMenu.getTransitionOutAnimator());
        }else{
            main.setTransitionOutAnimator(CommonTransitions.createFade(400));
        }
        mainMenu = main;
        
        // application logic determins the number of columns based on the screen size
        // this is why we need to be aware of screen size changes which is currently
        // only received using this approach
        int width = Display.getInstance().getDisplayWidth(); //get the display width 

        elementWidth = 0;

        
        Image[] selectedImages = new Image[DEMOS.length];
        Image[] unselectedImages = new Image[DEMOS.length];

        final ButtonActionListener bAListner = new ButtonActionListener();
        for (int i = 0; i < DEMOS.length; i++) {
            Image temp = r.getImage(DEMOS[i].getName() + "_sel.png");
            selectedImages[i] = temp;
            unselectedImages[i] = r.getImage(DEMOS[i].getName() + "_unsel.png");
            final Button b = new Button(DEMOS[i].getName(), unselectedImages[i]);
            b.setUIID("DemoButton");
            b.setRolloverIcon(selectedImages[i]);
            b.setAlignment(Label.CENTER);
            b.setTextPosition(Label.BOTTOM);
            mainMenu.addComponent(b);
            b.addActionListener(bAListner);
            b.addFocusListener(new FocusListener() {

                public void focusGained(Component cmp) {
                    if (componentTransitions != null) {
                        mainMenu.replace(b, b, componentTransitions);
                    }
                }

                public void focusLost(Component cmp) {
                }
            });

            demosHash.put(b, DEMOS[i]);
            elementWidth = Math.max(b.getPreferredW(), elementWidth);
        }

        //Calculate the number of columns for the GridLayout according to the 
        //screen width
        if(cols == 0){
            cols = width / elementWidth;
        }
        int rows = DEMOS.length / cols;
        mainMenu.setLayout(new GridLayout(rows, cols));

        mainMenu.addCommand(exitCommand);
        mainMenu.addCommand(aboutCommand);
        mainMenu.addCommand(rtlCommand);
        mainMenu.addCommand(dragModeCommand);
        mainMenu.addCommand(runCommand);

        mainMenu.setCommandListener(this);
        mainMenu.show();
    }

    /**
     * Invoked when a command is clicked. We could derive from Command but that would 
     * require 3 separate classes.
     */
    public void actionPerformed(ActionEvent evt) {
        Command cmd = evt.getCommand();
        switch (cmd.getId()) {
            case RUN_COMMAND:
                currentDemo = ((Demo) (demosHash.get(mainMenu.getFocused())));
                currentDemo.run(backCommand, this);
                break;
            case EXIT_COMMAND:
                notifyDestroyed();
                break;
            case BACK_COMMAND:
                currentDemo.cleanup();
                mainMenu.show();
                break;
            case ABOUT_COMMAND:
                Form aboutForm = new Form("About");
                aboutForm.setScrollable(false);
                aboutForm.setLayout(new BorderLayout());
                TextArea aboutText = new TextArea(getAboutText(), 5, 10);
                aboutText.setEditable(false);
                aboutForm.addComponent(BorderLayout.CENTER, aboutText);
                aboutForm.addCommand(new Command("Back") {

                    public void actionPerformed(ActionEvent evt) {
                        mainMenu.show();
                    }
                });
                aboutForm.show();
                break;
            case DRAG_MODE_COMMAND:
                mainMenu.setDragMode(true);
                mainMenu.removeCommand(dragModeCommand);
                mainMenu.addCommand(scrollModeCommand);
                break;
            case SCROLL_MODE_COMMAND:
                mainMenu.setDragMode(false);
                mainMenu.removeCommand(scrollModeCommand);
                mainMenu.addCommand(dragModeCommand);
                break;
            case RTL_COMMAND:
                LookAndFeel laf = UIManager.getInstance().getLookAndFeel();
                laf.setRTL(!laf.isRTL());
                setMainForm(res);
                break;
        }
    }

    private String getAboutText() {
        return "LWUIT Demo shows the main features for developing a User " +
                "Interface (UI) in Java ME. " +
                "This demo contains inside additional different sub-demos " +
                "to demonstrate key features, where each one can be reached " +
                "from the main screen. For more details about each sub-demo, " +
                "please visit the demo help screen. For more details about LWUIT" +
                " feel free to contact us at lwuit@sun.com.";
    }

    private class ButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            currentDemo = ((Demo) (demosHash.get(evt.getSource())));
            currentDemo.run(backCommand, UIDemoMIDlet.this);
        }
    }

    
}
