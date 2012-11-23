/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.CheckBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.util.Resources;
import java.io.IOException;

/**
 * A demo showing off the power of renderers both for combo boxes and lists
 *
 * @author Shai Almog
 */
public class RenderingDemo extends Demo implements ActionListener {

    private String[] RENDERED_CONTENT = {
        "ImageRenderer",
        "WidgetRenderer",
        "ColorRenderer",
        "DefaultListCellRenderer",
        "FishEyeRenderer"
    };
    private List list;
    private ListCellRenderer[] renderers;

    public String getName() {
        return "Rendering";
    }

    public void cleanup() {
        list = null;
        renderers = null;
    }

    protected void execute(final Form f) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        f.setScrollable(false);
        list = new List(RENDERED_CONTENT);
        list.setFixedSelection(List.FIXED_NONE_CYCLIC);
        list.setSmoothScrolling(true);
        list.getStyle().setBgTransparency(0);
        renderers = new ListCellRenderer[RENDERED_CONTENT.length];
        renderers[0] = new AlternateImageRenderer();
        renderers[1] = new WidgetRenderer();
        renderers[2] = new AlternateColorRenderer();
        renderers[3] = new DefaultListCellRenderer();
        renderers[4] = new FishEyeRenderer();
        list.setListCellRenderer(renderers[0]);
        list.addActionListener(this);

        Label l = new Label("Press \"Select\" to update:");
        l.getStyle().setBgTransparency(100);
        f.addComponent(l);
        f.addComponent(list);
        l = new Label("Try Applying Renderer To Menu");
        l.getStyle().setBgTransparency(100);
        f.addComponent(l);
        f.addCommand(new Command("Set To Menu") {

            public void actionPerformed(ActionEvent ev) {
                f.setMenuCellRenderer(list.getRenderer());
            }
        });
    }

    /**
     * Demonstrates we can derive from any component and implement a renderer any way
     * we please... Normally it would be best to derive from DefaultListCellRenderer.
     */
    private static class AlternateColorRenderer extends Label implements ListCellRenderer {

        private int[] colors = new int[]{0xFF0000, 0xFF00, 0xFF}; //Red , Green , Blue

        private static int currentIndex;

        public AlternateColorRenderer() {
            super("");
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            setText(value.toString());
            currentIndex = index % 3;

            setFocus(isSelected);
            getStyle().setBgTransparency(100);
            getStyle().setFgColor(colors[currentIndex]);

            return this;
        }

        public Component getListFocusComponent(List list) {
            setText("");
            setFocus(true);
            getStyle().setBgTransparency(100);
            return this;
        }
    }

    /**
     * Returns the text that should appear in the help command
     */
    protected String getHelp() {
        return "Demonstrates renderers allowing us to customize the look of data " +
                "structures returned from the model. By allowing the model to return " +
                "any data type we can save the need to type convert which allows for far " +
                "simpler code. We can also customize the widgets to any degree desired.";
    }

    public void actionPerformed(ActionEvent evt) {
        int newSelected = ((List) evt.getSource()).getSelectedIndex();
        list.setListCellRenderer(renderers[newSelected]);
        list.requestFocus();
        list.getParent().revalidate();
    }

    /**
     * Demonstrates implementation of a renderer derived from a label 
     * and the use of icons in the renderer
     */
    private static class AlternateImageRenderer extends Label implements ListCellRenderer {

        private Image[] images;

        /** Creates a new instance of AlternateImageRenderer */
        public AlternateImageRenderer() {
            super("");
            images = new Image[2];
            try {
                Resources imageRes = UIDemoMIDlet.getResource("images");
                images[0] = imageRes.getImage("sady.png");
                images[1] = imageRes.getImage("smily.png");
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            setText(value.toString());
            setFocus(isSelected);
            if (isSelected) {
                setIcon(images[1]);
                getStyle().setBgTransparency(100);
            } else {
                setIcon(images[0]);
                getStyle().setBgTransparency(0);
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            setIcon(images[1]);
            setText("");
            setFocus(true);
            getStyle().setBgTransparency(100);
            return this;
        }
    }

    /**
     * Demonstrates implementation of a renderer derived from a CheckBox 
     */
    private static class WidgetRenderer extends CheckBox implements ListCellRenderer {

        /** Creates a new instance of WidgetsRenderer */
        public WidgetRenderer() {
            super("");
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            setText("" + value);
            setFocus(isSelected);
            setSelected(isSelected);
            if (isSelected) {
                getStyle().setBgTransparency(100);
            } else {
                getStyle().setBgTransparency(0);
            }
            return this;
        }

        public Component getListFocusComponent(List list) {
            setText("");
            setFocus(true);
            getStyle().setBgTransparency(100);
            setSelected(true);
            return this;
        }
    }

    /**
     * Demonstrates implementation of a renderer derived from a label 
     * and the use of icons in the renderer
     */
    private static class FishEyeRenderer extends Label implements ListCellRenderer {

        private Label title;
        private Container selected = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        public FishEyeRenderer() {
            super("");
            Image smily = null;
            try {
                Resources imageRes = UIDemoMIDlet.getResource("images");
                smily = imageRes.getImage("smily.png");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            title = new Label("");
            title.getSelectedStyle().setBgTransparency(0);
            Label description = new Label(smily);
            description.setText("description...");
            description.getSelectedStyle().setBgTransparency(0);
            selected.addComponent(title);
            selected.addComponent(description);
            title.setFocus(true);
            description.setFocus(true);

        //selected.getStyle().setBgTransparency(0xFF);
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            if (isSelected) {
                title.setText(value.toString());
                return selected;
            }
            setText(value.toString());
            setFocus(false);
            getStyle().setBgTransparency(0);
            return this;
        }

        public Component getListFocusComponent(List list) {
            setText("");
            setFocus(true);
            getStyle().setBgTransparency(100);
            return this;
        }
    }
}
