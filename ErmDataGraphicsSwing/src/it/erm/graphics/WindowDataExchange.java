package it.erm.graphics;

import java.awt.Container;
import java.awt.Window;
import java.util.HashMap;

import javax.swing.JPanel;

public class WindowDataExchange {

    Window win;
    Container parent;
    String title;
    String iconPath;
    JPanel frm;
    HashMap<Object, Object> mapData = new HashMap<Object, Object>();
    
    public WindowDataExchange(Container parent, String title, String iconPath, JPanel frm) {
        this.parent = parent;
        this.title = title;
        this.iconPath = iconPath;
        this.frm = frm;
    }
    
    public void setWindow(Window win) {
        this.win = win;
    }
    
    public Window getWindow() {
        return win;
    }
    
    public JPanel getFrm() {
        return frm;
    }
    
    public Container getParent() {
        return parent;
    }
    
    public String getTitle() {
        return title;
    }

    public String getIconPath() {
        return iconPath;
    }

    public HashMap<Object, Object> getMapData() {
        return mapData;
    }
    
    public void setMapData(HashMap<Object, Object> mapData) {
        this.mapData = mapData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WindowDataExchange [win=");
        if( win != null ) {
            builder.append(win.getClass());
            builder.append(" / ");
            builder.append(win.getName());
        }
        builder.append(", parent=");
        if( parent != null ) {
            builder.append(parent.getClass());
            builder.append(" / ");
            builder.append(parent.getName());
        }
        builder.append(", title=");
        builder.append(title);
        builder.append(", iconPath=");
        builder.append(iconPath);
        builder.append(", frm=");
        if( frm != null ) {
            builder.append(frm.getClass());
            builder.append(" / ");
            builder.append(frm.getName());
        }
        builder.append(", mapData=");
        builder.append(mapData);
        builder.append("]");
        return builder.toString();
    }
    
    
}
