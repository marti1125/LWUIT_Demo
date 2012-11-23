/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * Demonstrates the usage and comparison between system fonts and custom bitmap
 * fonts
 *
 * @author Shai Almog
 */
public class FontDemo extends Demo {
     public void cleanup() {
     }

    public String getName() {
        return "Fonts";
    }

    protected String getHelp() {
        return "The toolkit has support for custom fonts and system/device fonts " +
            "both are fully interchangable and seamless for the developer.";
    }

    protected void execute(Form f) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        f.addComponent(createFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM), "System Font"));
        f.addComponent(createFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD | Font.STYLE_ITALIC, Font.SIZE_LARGE), "Bold Italic Large System Font"));
        f.addComponent(createFont(Font.getBitmapFont("Dialog"), "Dialog 12 Anti-Aliased Bitmap Font"));
        f.addComponent(createFont(Font.getBitmapFont("DialogInput"), "DialogInput 12 Anti-Aliased Bitmap Font"));
        f.addComponent(createFont(Font.getBitmapFont("SansSerif"), "SansSerif 20 Anti-Aliased Bitmap Font"));
        f.addComponent(createFont(Font.getBitmapFont("Monospaced"), "Monospaced 10 Anti-Aliased Bitmap Font"));
        Label l = createFont(Font.getBitmapFont("Dialog"), "Dialog 12 Bitmap Font in Red");
        l.getStyle().setFgColor(0xff0000);
        f.addComponent(l);
    }

    private Label createFont(Font f, String label) {
        Label fontLabel = new Label(label);
        fontLabel.getUnselectedStyle().setFont(f);
        fontLabel.getSelectedStyle().setFont(f);
        fontLabel.getUnselectedStyle().setBgTransparency(0);
        fontLabel.getSelectedStyle().setBgTransparency(0);
        
        fontLabel.setFocusable(true);
        return fontLabel;
    }
}
