package it.erm.graphics.swing.items.ctrl;

import it.erm.graphics.swing.ESActionBar;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldBoolean;

import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class ESCtrlBoolean extends ESCtrl {

	public ESCtrlBoolean(DBField f) {
	    this.field = f;
	    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	    
	    content = new ArrayList<JComponent>(1);
	    content.add(new JCheckBox());
	    add(content.get(0));
	    lbl = new JLabel(field.getDisplayName());
	    lbl.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseReleased(MouseEvent e) {}
            
            @Override
            public void mousePressed(MouseEvent e) {}
            
            @Override
            public void mouseExited(MouseEvent e) {}
            
            @Override
            public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseClicked(MouseEvent e) {
                JCheckBox c = (JCheckBox)content.get(0);
                boolean selected = c.isSelected();
                c.setSelected(!selected);
            }
        });
	    add(lbl);

	    //l'azione di clear pulisce il valore, senza farlo diventare false
//	    configureActions();
	    configureRequiredField();
	    addCtrlFocusListener(field);
	}
	
	   public ESActionBar getActionBar() {
	       if( actionBar == null ) {
	           actionBar = new ESActionBar(SwingConstants.HORIZONTAL, false);
	           actionBar.setSpacing(0, 0);
	           add(Box.createHorizontalGlue());
	           add(actionBar);
	       }
	       return actionBar;
	    }

	
	public DBField getField() {
		return field;
	}

	@Override
	public void setValue(Object value) {
		field.setValue(value);
//		check.setSelected(field.getBoolean());
	}

	public Object getValue() {
		return field.getValue();
	}

	boolean loading = false;
	public void loadValue() {
		loading = true;
		((JCheckBox)content.get(0)).setSelected(((DBFieldBoolean)field).getBoolean());
		loading = false;
	}

	@Override
	protected void addCtrlFocusListener(DBField f) {
		((JCheckBox)content.get(0)).addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( !loading ) {
					boolean selected = ((JCheckBox)content.get(0)).isSelected();
					setValue(selected);
				} else {
					//il dato sta venendo caricato dal campo
				}
			}
		});

	}

	@Override
	public Object onFocusLost(DBField f, JComponent textField) {
		return ((JCheckBox)textField).isSelected();
	}


	@Override
	public void onFocusGained(DBField f, JComponent textField) {
		
	}

}
