package com.sun.me.web.sample.local;

import com.sun.lwuit.Button;
import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.me.web.request.Response;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import com.sun.lwuit.Display;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Graphics;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.animations.CommonTransitions;
import com.sun.lwuit.animations.Transition3D;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.impl.midp.VKBImplementationFactory;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.DefaultListCellRenderer;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.list.ListModel;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;
import com.sun.me.web.path.Result;
import com.sun.me.web.path.ResultException;
import com.sun.me.web.request.Arg;
import com.sun.me.web.request.Request;
import com.sun.me.web.request.RequestListener;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Implements the local business search UI of the makeover demo
 *
 * @author  Shai Almog
 */
public class LocalMIDlet extends MIDlet {
    /* Demo mode is activiated if network connection fails */
    private boolean demoMode = false;

    private static final int DEFAULT_MAP_ZOOM = 6;
    private int zoom = DEFAULT_MAP_ZOOM;

    /* TODO: Get own APP ID for Yahoo Local and Maps APIs */
    private static final String APPID = "VS2gtQrV34ElS4obpTabGJ0lxYxDjwPzrjgaj_xTo.VbdnpA24586Jul4oDCXpO3UVN7";

    private static final String LOCAL_BASE = "http://local.yahooapis.com/LocalSearchService/V2/localSearch";
    
    private static final String MAP_BASE = "http://api.local.yahoo.com/MapsService/V1/mapImage";

    static final Object LOADING_MARKER = new Object();
    
    private boolean started;
    private Command exitCommand = new Command("Exit") {
        public void actionPerformed(ActionEvent ev) {
            notifyDestroyed();
        }
    };
    
    private Command defaultThemeCommand = new Command("Default Theme") {
        public void actionPerformed(ActionEvent ev) {
            setTheme("/theme.res");
        }
    };
    
    private Command javaThemeCommand = new Command("LWUIT Theme") {
        public void actionPerformed(ActionEvent ev) {
            setTheme("/LWUITtheme.res");
        }
    };

    private void setTheme(String name) {
        try {
            com.sun.lwuit.util.Resources res = com.sun.lwuit.util.Resources.open(name);
            com.sun.lwuit.plaf.UIManager.getInstance().setThemeProps(res.getTheme(res.getThemeResourceNames()[0]));
            Display.getInstance().getCurrent().refreshTheme();
        } catch(java.io.IOException err) {
             err.printStackTrace();
        }
    }
    
    private static Image getImage(String name) {
        try {
            return Image.createImage(name);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    protected void startApp() {
        VKBImplementationFactory.init();
        Display.init(this);

        // distinguish between start and resume from pause
        if(!started) {
            started = true;
                        
            // show your LWUIT form here e.g.: new MyForm().show();
            // this is a good place to set your default theme using
            // the UIManager class e.g.:
            try {
                com.sun.lwuit.util.Resources res = com.sun.lwuit.util.Resources.open("/theme.res");
                com.sun.lwuit.plaf.UIManager.getInstance().setThemeProps(res.getTheme(res.getThemeResourceNames()[0]));
            } catch(java.io.IOException err) {
                 err.printStackTrace();
            }
            showMainForm();
        }
    }
    
    private void showMainForm() {
        Form mainForm = createForm("Local Search");
        mainForm.setTransitionInAnimator(Transition3D.createCube(500, false));
        mainForm.setTransitionOutAnimator(Transition3D.createCube(500, true));
        mainForm.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        mainForm.addComponent(new Label("search for:"));
        final TextField searchFor = new TextField("coffee", 50);
        mainForm.addComponent(searchFor);
        mainForm.addComponent(new Label("location:"));
        final TextField location = new TextField("95054", 50);
        mainForm.addComponent(location);
        mainForm.addComponent(new Label("street:"));
        final TextField street = new TextField(50);
        mainForm.addComponent(street);
        mainForm.addComponent(new Label("sort results by:"));
        final ComboBox sortResults = new ComboBox(new String[] {"Distance", "Title", "Rating", "Relevance"});
        mainForm.addComponent(sortResults);
        mainForm.addCommand(exitCommand);
        mainForm.addCommand(defaultThemeCommand);
        mainForm.addCommand(javaThemeCommand);
        mainForm.addCommand(new Command("Search") {
            public void actionPerformed(ActionEvent ev) {
                showSearchResultForm(searchFor.getText(), location.getText(), street.getText(), (String) sortResults.getSelectedItem());
            }
        });
        mainForm.show();
    }
    
    private void exception(Exception ex) {
        ex.printStackTrace();
        Dialog.show("Error", "Error connecting to search service - Turning on DEMO MODE", "OK", null);
        demoMode = true;
        showMainForm();
    }
    
    private void showSearchResultForm(String searchFor, String location, String street, String sortOrder) {
        final Form resultForm = createForm("result list");
        resultForm.setScrollable(false);
        resultForm.setLayout(new BorderLayout());
        InfiniteProgressIndicator tempIndicator = null;
        try {
            tempIndicator = new InfiniteProgressIndicator(Image.createImage("/wait-circle.png"));
        } catch (IOException ex) {
            tempIndicator = null;
            ex.printStackTrace();
        }
        final InfiniteProgressIndicator indicator = tempIndicator;
        final List resultList = new List(new LocalResultModel(searchFor, location, sortOrder, street)) {
            public boolean animate() {
                boolean val = super.animate();
                
                // return true of animate only if there is data loading, this saves battery and CPU
                if(indicator.animate()) {
                    int index = getSelectedIndex();
                    index = Math.max(0, index - 4);
                    ListModel model = getModel();
                    int dest = Math.min(index + 4, model.getSize());
                    for(int iter = index ; iter < dest ; iter++) {
                        if(model.getItemAt(index) == LOADING_MARKER) {
                            return true;
                        }
                    }
                }
                return val;
            }
        };
        Links pro = new Links();
        pro.title = "prototype";
        pro.tel = "9999999999";
        pro.distance = "9999999";
        pro.address = "Long address string";
        pro.rating = "5";
        resultList.setRenderingPrototype(pro);
        resultList.setFixedSelection(List.FIXED_NONE_CYCLIC);
        resultList.getStyle().setBorder(null);

        resultList.setListCellRenderer(new DefaultListCellRenderer(false) {
            private Label focus;
            private Container selected;
            private Label firstLine;
            private Label secondLine;
            private boolean loading;
            
            {
                selected = new Container(new BoxLayout(BoxLayout.Y_AXIS));
                firstLine = new Label("First Line");
                secondLine = new Label("Second Line");
                int iconWidth = 20;
                firstLine.getStyle().setMargin(LEFT, iconWidth);
                secondLine.getStyle().setMargin(LEFT, iconWidth);
                selected.addComponent(firstLine);
                selected.addComponent(secondLine);
            }
            
            public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
                if(value == null || value == LOADING_MARKER) {
                    loading = true;
                    if(isSelected) {
                        firstLine.setText("Loading...");
                        secondLine.setText("Loading...");
                        return selected;
                    }
                    return indicator;
                }
                loading = false;
                if(isSelected) {
                    int listSelectionColor = list.getStyle().getFgColor();
                    firstLine.getStyle().setFgColor(listSelectionColor);
                    secondLine.getStyle().setFgColor(listSelectionColor);
                    firstLine.getStyle().setBgTransparency(0);
                    secondLine.getStyle().setBgTransparency(0);
                    Links l = (Links)value;
                    firstLine.setText(l.address + " " + l.tel);
                    secondLine.setText(l.distance + " miles " + ("".equals(l.rating) ? "" : ", " + l.rating + "*"));
                    return selected;
                }
                super.getListCellRendererComponent(list, ((Links)value).title, index, isSelected);
                return this;
            }
            
            public void paint(Graphics g) {
                if(loading) {
                    indicator.setX(getX());
                    indicator.setY(getY());
                    indicator.setWidth(getWidth());
                    indicator.setHeight(getHeight());
                    indicator.paint(g);
                } else {
                    super.paint(g);
                }
            }
            
            public Component getListFocusComponent(List list) {
                if(focus == null) {
                    try {
                        focus = new Label(Image.createImage("/svgSelectionMarker.png"));
                        focus.getStyle().setBgTransparency(0);
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }
                return focus;
            }
        });
        resultForm.addComponent(BorderLayout.CENTER, resultList);
        resultForm.addCommand(new Command("Map") {
            public void actionPerformed(ActionEvent ev) {
                showMap(resultForm, resultList.getSelectedItem());
            }
        });
        resultForm.addCommand(new Command("Details") {
            public void actionPerformed(ActionEvent ev) {
                showDetails(resultForm, resultList.getSelectedItem());
            }
        });
        resultForm.addCommand(new Command("Back") {
            public void actionPerformed(ActionEvent ev) {
                showMainForm();
            }
        });
        resultForm.addCommand(exitCommand);
        resultForm.show();
    }
    
    private Label createSubLabel(String text, int fgColor, Font f) {
        Label l = new Label(text);
        Style s = l.getStyle();
        s.setFgColor(fgColor);
        s.setFont(f);
        return l;
    }
    
    private void showDetails(final Form resultForm, final Object selectedItem) {
        if(selectedItem != null && selectedItem instanceof Links) {
            Links l = (Links)selectedItem;
            int fgColor = resultForm.getStyle().getFgColor();
            Font standardFont = resultForm.getStyle().getFont();
            Form details = createForm(l.title);
            details.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            details.addComponent(createSubLabel("address", fgColor, standardFont));
            details.addComponent(new Label(l.address));
            details.addComponent(createSubLabel("distance", fgColor, standardFont));
            details.addComponent(new Label(l.distance + " mi."));
            details.addComponent(createSubLabel("average rating", fgColor, standardFont));
            details.addComponent(new Label(l.rating + "*"));
            details.addComponent(createSubLabel("telephone", fgColor, standardFont));
            details.addComponent(new Label(l.tel));
            details.addCommand(exitCommand);
            details.addCommand(new Command("Back") {
                public void actionPerformed(ActionEvent ev) {
                    resultForm.show();
                }
            });
            details.setTransitionInAnimator(Transition3D.createCube(500, false));
            details.setTransitionOutAnimator(Transition3D.createCube(500, true));
            details.show();
        }
    }
    
    private void showMap(final Form resultForm, final Object selectedItem) {
        try {
            // might still be downloading the entry...
            if(selectedItem instanceof Links) {
                final Links link = (Links)selectedItem;
                if (link == null) {
                    return;
                }

                final Arg[] args = {
                    new Arg("output", "json"),
                    new Arg("appid", APPID),
                    new Arg("latitude", link.latitude),
                    new Arg("longitude", link.longitude),
                    new Arg("image_height", Integer.toString((int)(Display.getInstance().getDisplayHeight() * 1.5))),
                    new Arg("image_width", Integer.toString((int)(Display.getInstance().getDisplayWidth() * 1.5))),
                    new Arg("zoom", Integer.toString(zoom))
                };

                final Dialog progress = new Dialog();
                progress.getDialogStyle().setBorder(Border.createRoundBorder(6, 6, 0xe3ef5a));
                progress.setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, true, 400));
                progress.setTransitionOutAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_VERTICAL, false, 400));
                progress.addComponent(new Label("Please Wait"));
                progress.addComponent(new InfiniteProgressIndicator(Image.createImage("/wait-circle.png")));
                int height = Display.getInstance().getDisplayHeight() - (progress.getContentPane().getPreferredH() + progress.getTitleComponent().getPreferredH());
                height /= 2;
                progress.show(height, height, 20, 20, true, false);
                new Thread() {
                    public void run() {
                        try {
                            Response response;
                            if (!demoMode) {
                                response = Request.get(MAP_BASE, args, null, null);
                            }
                            else {
                                response = Request.get(Request.DEMO_URL, args, null, null);
                            }
                            Result result = response.getResult();
                            String location = result.getAsString("ResultSet.Result");
                            HttpConnection imgConn;
                            imgConn = (HttpConnection) Connector.open(location);
                            imgConn.setRequestProperty("Accept", "image/png");

                            //int totalToReceive = imgConn.getHeaderFieldInt(Arg.CONTENT_LENGTH, 0);
                            InputStream is = imgConn.openInputStream();        
                            Image mapImage = Image.createImage(is);
                            final Form map = createForm("Map");
                            map.setScrollable(false);
                            map.setTransitionInAnimator(Transition3D.createCube(500, false));
                            map.setTransitionOutAnimator(Transition3D.createCube(500, true));
                            map.setLayout(new BorderLayout());
                            MotionComponent mapLabel = new MotionComponent(mapImage);
                            map.addComponent(BorderLayout.CENTER, mapLabel);
                            if(zoom < 9) {
                                map.addCommand(new Command("Zoom Out") {
                                    public void actionPerformed(ActionEvent ev) {
                                        zoom++;
                                        map.setTransitionOutAnimator(null);
                                        showMap(resultForm, selectedItem);
                                    }
                                });
                            }
                            if(zoom > 1) {
                                map.addCommand(new Command("Zoom In") {
                                    public void actionPerformed(ActionEvent ev) {
                                        zoom--;
                                        map.setTransitionOutAnimator(null);
                                        showMap(resultForm, selectedItem);
                                    }
                                });
                            }
                            map.addCommand(new Command("Back") {
                                public void actionPerformed(ActionEvent ev) {
                                    resultForm.show();
                                }
                            });
                            map.addCommand(exitCommand);
                            progress.dispose();
                            map.show();
                        } catch(IOException ioErr) {
                            exception(ioErr);
                        } 
                    }
                }.start();
            } else {
                Dialog.show("Please Wait", "Please wait for download to complete", "OK", null);
            }
        } catch(IOException ioErr) {
            exception(ioErr);
        }
    }
    
    protected void pauseApp() {
    }

    protected void destroyApp(boolean arg0) {
    }

    private static class Links {
        String title;
        String business;
        String listing;
        String map;
        String tel;
        String latitude;
        String longitude;
        String rating;
        String address;
        String distance;
    };

    private Form createForm(String title) {
        Form f = new Form(title);
        f.getTitleComponent().setAlignment(Component.LEFT);
        f.setMenuCellRenderer(new DefaultListCellRenderer(false));
        return f;
    }
    
    /**
     * A list model that lazily fetches a result over the web if its unavailable
     */
    class LocalResultModel implements ListModel {
        private Vector cache;
        private Arg[] args;
        private boolean fetching;
        private Vector fetchQueue = new Vector();
        private Vector dataListeners = new Vector();

        private Vector selectionListeners = new Vector();

        private int selectedIndex = 0;
        private boolean firstTime = true;
        
        public LocalResultModel(String searchFor, String location, String sortOrder, String street) {
            cache = new Vector();
            cache.setSize(1);
            args = new Arg[]{
                new Arg("output", "json"),  
                new Arg("appid", APPID),
                new Arg("query", searchFor),
                new Arg("location", location),
                new Arg("sort", sortOrder.toLowerCase()),
                null,
                null
            };
            final String str = street;
            if (!"".equals(str)) {
                args[6] = new Arg("street", str);
            }
        }
        
        private void fetch(final int startOffset) {
            int count = Math.min(cache.size(), startOffset + 9);
            for(int iter = startOffset - 1 ; iter < count ; iter++) {
                if(cache.elementAt(iter) == null) {
                    cache.setElementAt(LOADING_MARKER, iter);
                }
            }
            if(!fetching) {
                fetching = true;
                new Thread() {
                    public void run() {
                        if(firstTime) {
                            firstTime = false;
                            try {
                                // yield a bit CPU the first time around since the model
                                // call might occur before the display is refreshed
                                Thread.sleep(400);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        fetchThread(startOffset);
                        while(fetchQueue.size() > 0) {
                            int i = ((Integer)fetchQueue.elementAt(0)).intValue();
                            fetchQueue.removeElementAt(0);
                            fetchThread(i);
                        }
                        fetching = false;
                    }
                }.start();
            } else {
                fetchQueue.addElement(new Integer(startOffset));
            }
        }
        
        private void fetchThread(int startOffset) {
            try {
                Response response;
                args[5] = new Arg("start", Integer.toString(startOffset));
                if (!demoMode) {
                    response = Request.get(LOCAL_BASE, args, null, null);
                } else {
                    response = Request.get(Request.DEMO_URL, args, null, null);
                }
                final Exception ex = response.getException();
                if (ex != null || response.getCode() != HttpConnection.HTTP_OK) {
                    Dialog.show("Error", "Error connecting to search service - Turning on DEMO MODE", "OK", null);
                    demoMode = true;
                    showMainForm();
                    return;
                }

                Result result = response.getResult();
                //String mapAllLink = result.getAsString("ResultSet.ResultSetMapUrl");
                int totalResultsAvailable = result.getAsInteger("ResultSet.totalResultsAvailable");
                final int resultCount = result.getSizeOfArray("ResultSet.Result");

                // this is the first time... set the size of the vector to match the results!
                if(startOffset == 1) {
                    cache.setSize(totalResultsAvailable);
                }

                for(int i = 0 ; i < resultCount ; i++) {
                    String title = result.getAsString("ResultSet.Result["+i+"].Title");

                    Links link = new Links();
                    link.title = title;
                    link.address = result.getAsString("ResultSet.Result["+i+"].Address");
                    link.map = result.getAsString("ResultSet.Result["+i+"].MapUrl");
                    link.listing = result.getAsString("ResultSet.Result["+i+"].ClickUrl");
                    link.business = result.getAsString("ResultSet.Result["+i+"].BusinessClickUrl");
                    link.tel = result.getAsString("ResultSet.Result["+i+"].Phone");
                    link.latitude = result.getAsString("ResultSet.Result["+i+"].Latitude");
                    link.longitude = result.getAsString("ResultSet.Result["+i+"].Longitude");
                    link.rating = result.getAsString("ResultSet.Result["+i+"].Rating.AverageRating");
                    link.distance = result.getAsString("ResultSet.Result["+i+"].Distance");
                    cache.setElementAt(link, startOffset + i - 1);
                    fireDataChangedEvent(DataChangedListener.CHANGED, startOffset + i - 1);
                }
            } catch (Exception ex) {
                exception(ex);
            }
        }
        
        public Object getItemAt(int index) {
            Object val = cache.elementAt(index);
            if(val == null) {
                fetch(index + 1);
                return LOADING_MARKER;
            }
            return val;
        }

        public int getSize() {
            return cache.size();
        }

        public void setSelectedIndex(int index) {
            int oldIndex = selectedIndex;
            this.selectedIndex = index;
            fireSelectionEvent(oldIndex, selectedIndex);
        }

        public void addDataChangedListener(DataChangedListener l) {
            dataListeners.addElement(l);
        }

        public void removeDataChangedListener(DataChangedListener l) {
            dataListeners.removeElement(l);
        }

        private void fireDataChangedEvent(final int status, final int index){
            if(!Display.getInstance().isEdt()) {
                Display.getInstance().callSeriallyAndWait(new Runnable() {
                    public void run() {
                        fireDataChangedEvent(status, index);
                    }
                });
                return;
            }
            // we query size with every iteration and avoid an Enumeration since a data 
            // changed event can remove a listener instance thus break the enum...
            for(int iter = 0 ; iter < dataListeners.size() ; iter++) {
                DataChangedListener l = (DataChangedListener)dataListeners.elementAt(iter);
                l.dataChanged(status, index);
            }
        }

        public void addSelectionListener(SelectionListener l) {
            selectionListeners.addElement(l);
        }

        public void removeSelectionListener(SelectionListener l) {
            selectionListeners.removeElement(l);
        }

        private void fireSelectionEvent(int oldIndex, int newIndex){
            Enumeration listenersEnum = selectionListeners.elements();
            while(listenersEnum.hasMoreElements()){
                SelectionListener l = (SelectionListener)listenersEnum.nextElement();
                l.selectionChanged(oldIndex, newIndex);
            }
        }

        public void addItem(Object item) {
        }

        public void removeItem(int index) {
        }
        
        public int getSelectedIndex() {
            return selectedIndex;
        }
    }
}
