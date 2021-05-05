package it.erm.graphics.swing.items.ctrl;

import it.erm.lib.crud.dbo.DBField;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

public class ESCtrlInt extends ESCtrl {

	public ESCtrlInt(DBField field) {
		super(field);
	}
	
    protected void configureFieldContent(final DBField f) {
        content = new ArrayList<JComponent>(1);
        JFormattedTextField t = new JFormattedTextField(ControlsManager.getIntFormatter(0, Integer.MAX_VALUE));
        t.setText(field.getDisplayValue());
        content.add(t);
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new  Insets(3, 5, 2, 5);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = .5; 
        add(content.get(0) , c);
//        content.get(0).addFocusListener(new FocusListener() {
//          
//          @Override
//          public void focusLost(FocusEvent e) {
//              onFocusLost(f, content.get(0));
//          }
//          
//          @Override
//          public void focusGained(FocusEvent e) {
//              onFocusGained(f, content.get(0));
//          }
//      });
    }
    
	@Override
	public void loadValue() {
	    JFormattedTextField t = ((JFormattedTextField)content.get(0));
	    if( field.isNull() ) {
	        t.setValue(null);
	    } else {
	        t.setValue(field.getInteger());
	    }
	}

	@Override
	public Object onFocusLost(DBField f, JComponent textField) {
	    int parseInt = 0;
	    String text = ((JFormattedTextField)textField).getText();
	    try {
	        parseInt = Integer.parseInt(text);
        } catch( Exception e ) {
            text = null;
            ((JFormattedTextField)content.get(0)).setText(null);
        }
		return parseInt;
	}

	@Override
	public void onFocusGained(DBField f, JComponent textField) {
//	    ((JFormattedTextField)content.get(0)).setText(field.getDisplayValue());
	}

}
