package it.erm.graphics.swing.items.action;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.ESView;
import it.erm.graphics.swing.items.form.ESFormStandardEdit;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.crud.dbo.DBTable;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.JDialog;

public class ESActionOpenAna extends ESAction {

    public static Class<? extends ESFormStandardEdit> clazzDft = ESFormStandardEdit.class;
    public static HashMap<Class<? extends DBTable>, Class<? extends ESFormStandardEdit>> clazzMap;
    
	protected DBTable table;
	private   ESView view;

	public ESActionOpenAna(DBTable table, ESView v) {
		this(table, v, false);
	}
	
	public ESActionOpenAna(DBTable table, ESView v, boolean showText) {
		super(ResourceUtils.getImageIcon("table_go.png", "anagrafica..."), null, "Apri anagrafica");
		this.table = table;
		this.view = v;
		if( showText ) {
			setText(table.getName());
		}
	}

	@Override
	public Validation executeAction() {
		final JDialog frame = new JDialog();
		frame.setIconImage(ResourceUtils.createImageIcon("table.png").getImage());
		if( clazzDft != null ) {
		    Class<? extends ESFormStandardEdit> clazz = clazzDft;
		    if( clazzMap != null ) {
		        Class<? extends ESFormStandardEdit> tmp = clazzMap.get(table.getClass());
		        if( tmp != null ) {
		            clazz = tmp;
		        }
		    }
		    
		    try {
                ESFormStandardEdit newInstance = clazz.newInstance();
                newInstance.init(frame, true, table, 1, true);
            } catch( Exception e ) {
                e.printStackTrace();
                /*frmRicerca search = */new ESFormStandardEdit(frame, true, table, 1, true);
            }
		} else {
		    /*frmRicerca search = */new ESFormStandardEdit(frame, true, table, 1, true);
		}
		frame.setTitle(table.getName());
		frame.setModal(true);

		Rectangle bounds = view.getParent().getBounds();
		int w = 800 > bounds.width  ? 800 : bounds.width; 
		int h = 600 > bounds.height ? 600 : bounds.height;
		
		frame.setMinimumSize(new Dimension(w, h));
		Point locationOnScreen = view.getParent().getLocationOnScreen();
		frame.setLocation(locationOnScreen.x, locationOnScreen.y);
		
		frame.addWindowListener(new WindowListener() {
            
		    @Override
		    public void windowDeactivated(WindowEvent e) {
		        if( !table.isModified(null) ) {
		            view.setValue(table.getId());
		            view.loadValue();
		        }
		    }
            @Override
            public void windowClosed(WindowEvent e) {
                view.setValue(table.getId());
                view.loadValue();
            }

            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
        });
		
		onAfterOpen();
		frame.setVisible(true);
		return null;
	}

	protected void onAfterOpen() {
		
	}
	
}
