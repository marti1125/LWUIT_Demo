/*
 * Copyright © 2008 Sun Microsystems, Inc. All rights reserved.
 * Use is subject to license terms.
 *
 */
package com.sun.lwuit.uidemo;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.util.Resources;
import java.io.IOException;

/**
 *
 * @author cf130546
 */
public class ScrollDemo extends Demo {
    private int orientation;
    
    private String[][] CONTACTS_INFO = {
    {"Nir V.","Nir.Vazana@Sun.COM"},
    {"Tidhar G.","Tidhar.Gilor@Sun.COM"},
    {"Iddo A.","Iddo.Arie@Sun.COM"},
    {"Ari S.","Ari.Shapiro@Sun.COM"},
    {"Chen F.","Chen.Fishbein@Sun.COM"},
    {"Yoav B.","Yoav.Barel@Sun.COM"},
    {"Moshe S.","Moshe.Sambol@Sun.COM"},
    {"Keren S.","Keren.Strul@Sun.COM"},
    {"Amit H.","Amit.Harel@Sun.COM"},
    {"Arkady N.","Arcadi.Novosiolok@Sun.COM"},
    {"Shai A.","Shai.Almog@Sun.COM"},
    {"Elina K.","Elina.Kleyman@Sun.COM"},
    {"Yaniv V.","Yaniv.Vakrat@Sun.COM"},
    {"Nadav B.","Nadav.Benedek@Sun.COM"},
    {"Martin L.","Martin.Lichtbrun@Sun.COM"},
    {"Tamir S.","Tamir.Shabat@Sun.COM"},
    {"Nir S.","Nir.Shabi@Sun.COM"},
    {"Eran K.","Eran.Katz@Sun.COM"}
    };
    
    public String getName() {
        return "Scrolling";
    }

    protected String getHelp() {
        return "Scrolling is supported in Container, List and Textarea." +
                "In This Demo we display a List with many enteries to show " +
                "a simple use-case for scrolling." +
                "LWUIT supports touch screen devices as well, and the scrolling" +
                " feature has an accelerated motion in touch mode.";
    }

    protected void execute(final Form f) { 
        f.setLayout(new BorderLayout());
        //disable the scroll on the Form.
        f.setScrollable(false);
        Image contacts = null;
        Image  persons[] = null;
       
        //some constants to help us parse the people image
        int contactWidth= 36;
        int contactHeight= 48;
        int cols = 4;
 
        try {
            Resources images = UIDemoMIDlet.getResource("images");
            contacts = images.getImage("people.jpg");
            persons = new Image[CONTACTS_INFO.length];
            for(int i = 0; i < persons.length ; i++){
                    persons[i] = contacts.subImage((i%cols)*contactWidth, (i/cols)*contactHeight, contactWidth, contactHeight, true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        final Contact[] contactArray = new Contact[5 * persons.length];
        for (int i = 0; i < contactArray.length; i++) {
            int pos = i % CONTACTS_INFO.length;
            contactArray[i] = new Contact(CONTACTS_INFO[pos][0], CONTACTS_INFO[pos][1], persons[pos]);
        }
        f.addComponent(BorderLayout.CENTER, createList(contactArray, List.VERTICAL, new ContactsRenderer()));
        f.addCommand(new Command("Horizontal") {
            public void actionPerformed(ActionEvent ev) {
                if(orientation == List.VERTICAL) {
                    f.removeAll();
                    f.addComponent(BorderLayout.NORTH, createList(contactArray, List.HORIZONTAL, new ImageRenderer()));        
                    f.revalidate();
                }
            }
        });
        f.addCommand(new Command("Vertical") {
            public void actionPerformed(ActionEvent ev) {
                if(orientation == List.HORIZONTAL) {
                    f.removeAll();
                    f.addComponent(BorderLayout.CENTER, createList(contactArray, List.VERTICAL, new ContactsRenderer()));        
                    f.revalidate();
                }
            }
        });
    }

    private List createList(Contact[] contacts, int orientation, ListCellRenderer renderer) {
        List list = new List(contacts);
        this.orientation = orientation;
        list.getStyle().setBgTransparency(0);
        list.setListCellRenderer(renderer);
        list.setOrientation(orientation);
        list.setPaintFocusBehindList(true);
        return list;
    }
    
    class ImageRenderer extends DefaultListCellRenderer {
        private Label focus = new Label("");
        public ImageRenderer() {
            super(false);
            //focus.getStyle().setBgTransparency(100);
        }
        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            super.getListCellRendererComponent(list, "", index, isSelected);
            setIcon(((Contact) value).getPic());
            return this;
        }
        public Component getListFocusComponent(List list) {
            return focus;
        }
    }

    class ContactsRenderer extends Container implements ListCellRenderer {

        private Label name = new Label("");
        private Label email = new Label("");
        private Label pic = new Label("");

        private Label focus = new Label("");
        
        public ContactsRenderer() {
            setLayout(new BorderLayout());
            addComponent(BorderLayout.WEST, pic);
            Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            name.getStyle().setBgTransparency(0);
            email.getStyle().setBgTransparency(0);
            pic.getStyle().setBgTransparency(0);
            cnt.addComponent(name);
            cnt.addComponent(email);
            addComponent(BorderLayout.CENTER, cnt);
            focus.setFocus(true);
            
        }

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {

            Contact person = (Contact) value;
            name.setText(person.getName());
            email.setText(person.getEmail());
            pic.setIcon(person.getPic());
            return this;
        }

        public Component getListFocusComponent(List list) {
            return focus;
        }
    }

    class Contact {

        private String name;
        private String email;
        private Image pic;

        public Contact(String name, String email, Image pic) {
            this.name = name;
            this.email = email;
            this.pic = pic;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public Image getPic() {
            return pic;
        }
    }
}
