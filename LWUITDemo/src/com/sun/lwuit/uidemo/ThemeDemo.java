/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Button;
import com.sun.lwuit.ButtonGroup;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.RadioButton;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.FlowLayout;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.IOException;

/**
 * Simple demo showing off how a theme can be manipulated in LWUIT. Once the demo
 * is executed the theme changes completely for the rest of the application...
 * 
 * @author Shai Almog
 */
public class ThemeDemo extends Demo {

    /**
     * The full path to the storage location of the themes
     */
    //private static final String THEME_PATH = "/com/sun/lwuit/uidemo/themes/";
    /**
     * Names of the themes in the storage and display
     */
    private static final String[] THEMES = {"LWUITtheme", "businessTheme", "oceanfishTheme"};
    private static final String[] THEME_LABELS = {"Default", "Business", "Ocean Fish"};

    public String getName() {
        return "Themes";
    }

    protected void execute(Form f) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        final ButtonGroup group1 = new ButtonGroup();
        Label title = new Label("Please choose a theme:");
        title.getStyle().setMargin(0, 0, 0, 0);
        title.getStyle().setBgTransparency(70);
        f.addComponent(title);
        for (int iter = 0; iter < THEME_LABELS.length; iter++) {
            RadioButton rb = new RadioButton(THEME_LABELS[iter]);
            group1.add(rb);
            f.addComponent(rb);
        }
        group1.setSelected(getSelectedThemeIndex());

        Button updateButton = new Button("Update Theme");
        updateButton.setAlignment(Button.CENTER);
        updateButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                int newSelectedIndex = group1.getSelectedIndex();

                try {
                    Resources r = UIDemoMIDlet.getResource(THEMES[newSelectedIndex]);
                    UIManager.getInstance().setThemeProps(r.getTheme(r.getThemeResourceNames()[0]));
                    Display.getInstance().getCurrent().refreshTheme();
                    UIDemoMIDlet.getMainForm().refreshTheme();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        Container buttonPanel = new Container(new FlowLayout(Component.CENTER));
        buttonPanel.addComponent(updateButton);
        f.addComponent(buttonPanel);
    }

    private int getSelectedThemeIndex() {
        int selectedThemeIndex = 0;
        String themeName = UIManager.getInstance().getThemeName();
        if (themeName == null) {
            return 0;
        }
        themeName = themeName.toLowerCase();
        for (int i = 0; i < THEMES.length; i++) {
            if (THEMES[i].equals(themeName)) {
                selectedThemeIndex = i;
            }
        }
        return selectedThemeIndex;
    }

    /**
     * Returns the text that should appear in the help command
     */
    protected String getHelp() {
        return "Demonstrates theme switching, select a theme to see the changes occur in the application" +
                " A Theme can modify colors, backgrounds, fonts, transparency, margin and padding.";
    }
}
