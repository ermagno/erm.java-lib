package it.erm.graphics.swing.items.ctrl;

import it.erm.graphics.swing.ESActionBar;
import it.erm.graphics.swing.ESView;
import it.erm.graphics.swing.items.action.ESActionClear;
import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldDateTime;
import it.erm.lib.utils.Utils;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.*;
import javax.swing.text.*;

public abstract class ESCtrl extends ESView {

	protected DBField field;
	
	protected JLabel            lbl;
	protected ESActionBar       actionBar;
	public    ESActionClear     actClear;
	
	protected ArrayList<JComponent> content;
	
	protected ESCtrl(){}
	
	public ESCtrl(DBField f) {
		this.field = f;
		setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        lbl = new JLabel(field.getDisplayName());
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new  Insets(3, 5, 2, 5);
        add(lbl , c);
        
        configureFieldContent(field);
        configureActions();
        configureRequiredField();
        addCtrlFocusListener(field);
	}
	
	protected void configureFieldContent(final DBField f) {
		content = new ArrayList<JComponent>(1);
		DefaultFormatter format = new DefaultFormatter();
	    format.setOverwriteMode(false);
	    format.setCommitsOnValidEdit(true);
	    JFormattedTextField t = new JFormattedTextField(format);
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
	}
	
	protected void configureActions() {
        actClear = new ESActionClear(this);
        getActionBar().addAction(actClear);
	}
	
	protected void configureRequiredField() {
		if( !field.canBeNull() ) {
			for( JComponent ft: content ) {
				ft.setBackground(new Color(212, 224, 224));
			}
		}
	}
	
	public void setEditable(boolean editable) {
        for( JComponent c: content ) {
            if( c instanceof JTextComponent ) {
                ((JTextComponent)c).setEditable(editable);
            } else if( c instanceof JButton ) {
                c.setEnabled(editable);
            }
            if( actionBar != null ) {
                actionBar.setEnabled(editable);
            }
        }
    }
	
	public JLabel getLbl() {
        return lbl;
    }
	
	public ArrayList<JComponent> getContent() {
        return content;
    }
	
	protected void addCtrlFocusListener(final DBField f) {
		for( final JComponent c: content ) {
			c.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					Object value = onFocusLost(f, content.get(0));
					setValue(value);
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					onFocusGained(f, content.get(0));
				}
			});
			c.addKeyListener(new KeyListener() {
                
                @Override
                public void keyTyped(KeyEvent e) {

                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                }
                
                @Override
                public void keyPressed(KeyEvent e) {
                    if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                        dispatchEvent(new FocusEvent(c, FocusEvent.FOCUS_LOST));
                    }
                }
            });
		}
	}
	
	protected void setLabelText(String text) {
		lbl.setText(text);
	}
	
	public DBField getField() {
		return field;
	}
	
	public ESActionBar getActionBar() {
		if( actionBar == null ) {
//			((GridBagLayout)getLayout()).getConstraints(content).gridwidth = 2;
			
			GridBagConstraints c = new GridBagConstraints();
	        c.gridx = content.size()-1;
	        if( c.gridx == 0 )
	        	c.gridx++;
	        c.gridy = 0;
	        c.anchor = GridBagConstraints.EAST;
//	        c.fill = GridBagConstraints.HORIZONTAL;
	        c.insets = new  Insets(0, 0, 0, 5);
	        actionBar = new ESActionBar(SwingConstants.HORIZONTAL, false);
	        actionBar.setSpacing(0, 0);
	        add(actionBar, c);
			
		}
		return actionBar;
	}

	@Override
	public void setValue(Object value) {
		field.setValue(value, !Utils.isNull(value), true);
	}
	
	public Object getValue() {
		return field.getValue();
	}

	String BOOLEAN_STR_TRUE[] = {"true", "si", "x"};
	String BOOLEAN_STR_FALSE[] = {"false", "no", " "};
	
	protected Object decodeStringCtrl(JDBCType tc, String str, MaskFormatter mf) {
		switch(tc) {
		case BOOLEAN:
			if( str.isEmpty() )
				return null;
			for( String t: BOOLEAN_STR_TRUE ) {
				if( t.equalsIgnoreCase(str) ) {
					return true;
				}
			}
			for( String t: BOOLEAN_STR_FALSE ) {
				if( t.equalsIgnoreCase(str) ) {
					return false;
				}
			}
			return null;
		case TIMESTAMP:
			Date parse = null;
			try {
				if( mf != null && !mf.getAllowsInvalid() ) {
					DBFieldDateTime f = (DBFieldDateTime) field;
					if( f.isDate() && f.isTime() ) {
						parse = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(str);
					} else if( f.isDate() ) {
						parse = new SimpleDateFormat("dd/MM/yyyy").parse(str);
					} else if( f.isTime() ) {
						parse = new SimpleDateFormat("HH:mm").parse(str);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return parse;
		case DOUBLE:
			if( str.trim().isEmpty() )
				return null;
			try {
				return Double.valueOf(str);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		case INTEGER:
			if( str.trim().isEmpty() )
				return null;
			try {
				return Integer.valueOf(str);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		case VARCHAR:
		default:
		    if( str.trim().isEmpty() )
		        return null;
			return str;
		}
	}
	
}
