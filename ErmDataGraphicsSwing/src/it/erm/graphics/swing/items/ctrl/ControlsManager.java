package it.erm.graphics.swing.items.ctrl;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.lib.utils.Utils;

import java.awt.Component;
import java.text.NumberFormat;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.text.NumberFormatter;

public class ControlsManager {
	
	HashMap<Object, Controls> map = new HashMap<Object, Controls>();	
	
	public void unregister(Object container, Enum<?>... e) {
	    Controls controls = map.get(container);
	    if( !Utils.isNull(controls) ) {
	        for( Enum<?> i: e ) {
	            controls.remove(i);
	        }
	    }
	}
	
    public Object unregister(Object container) {
        Controls controls = map.get(container);
        if( controls != null ) {
            List<Enum<?>> enumList = controls.getEnumList();
            ArrayList<Object> removeList = new ArrayList<Object>(enumList.size());
            for( Enum<?> i: enumList ) {
                Object remove = controls.remove(i);
                removeList.add(remove);
            }
            return removeList;
        }
        return null;
    }
	
	public void register(Object container, Enum<?> e, int style, int sizeAdd, Object o) {
	    register(container, e, o);
	    if( o instanceof JComponent ) {
	        ResourceUtils.FontChange((JComponent) o, style, sizeAdd);
	    }
//		register(o, e, c, v);
//		changeFont(o, e, c, style, increment);
	}

	
	public void register(Object container, Enum<?> e, Object o) {
	    Controls controls = map.get(container);
	    if( controls == null ) {
	        controls = new Controls();
	        map.put(container, controls);
	    }
	    controls.map.put(e, o);
//		Controls tipi = map.get(o);
//		if( tipi == null ) {
//			tipi = new Controls();
//			map.put(o, tipi);
//		}
//		tipi.put(e, c, v);
	}

	public <T> T get(Object o, Enum<?> e, Class<T> c) {
        Controls controls = map.get(o);
        Object object = controls.map.get(e);
        return (T) object;
	}
	
	public ArrayList<Component> get(Object o) {
	    ArrayList<Component> result = new ArrayList<Component>();
	    Controls controls = map.get(o);
	    for( Enum<?> e: controls.map.keySet() ) {
	        Object object = controls.map.get(e);
	        if( object instanceof Component ) {
	            result.add((Component) object);
	        } else if( object instanceof ESAction ) {
	            result.add(((ESAction) object).getButton());
	        }
	    }
	    return result;
	}

	public static class Controls {
		/**
		 * Enum<?>: serve per avere il riferimento univoco al controllo
		 * JComponent: il controllo stesso
		 */
		HashMap<Enum<?>, Object> map = new HashMap<Enum<?>,Object>();
		
		private void put(Enum<?> e, Object v) {
		    map.put(e, v);
		}
		
		private List<Enum<?>> getEnumList() {
		    Set<Enum<?>> keySet = map.keySet();
		    return new ArrayList<Enum<?>>(keySet);
		}
		
		private Object remove(Enum<?> e) {
		    Object remove = map.remove(e);
		    if( remove instanceof JComponent ) {
		        ((JComponent)remove).setVisible(false);
		    }
		    return remove;
		}
		

	}
    
	public static NumberFormatter getIntFormatter(int min, int max) {
	       NumberFormat format = NumberFormat.getInstance();
	        NumberFormatter formatter = new NumberFormatter(format);
	        formatter.setValueClass(Integer.class);
	        formatter.setMinimum(min);
	        format.setGroupingUsed(false);
	        formatter.setMaximum(max);
	        formatter.setAllowsInvalid(false);
	        formatter.setOverwriteMode(false);
	        // If you want the value to be committed on each keystroke instead of focus lost
	        formatter.setCommitsOnValidEdit(false);
            return formatter;
	}
	
}