package it.erm.graphics.swing.items.ctrl;

import it.erm.graphics.swing.*;
import it.erm.graphics.swing.items.action.ESActionOpenAna;
import it.erm.graphics.swing.items.action.ESActionSearch;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.crud.dbo.*;
import it.erm.lib.utils.Utils;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.*;

public class ESCtrlFK extends ESCtrl {

	JPanel firstRow;
	JPanel secondRow;
	
	private ESAction actOpenAna = null;
	private ESActionSearch actSelection = null;
	protected DBTable foreignKeyTable;
	private ArrayList<DBField> fields = null;
	
	public ESCtrlFK(DBField f) {
		this.field = f;
		firstRow = new JPanel();
		firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
		secondRow = new JPanel();
		secondRow.setLayout(new GridLayout(1, 0, 5, 0));
		
		setLayout(new GridBagLayout());
		GridBagConstraints fr = new GridBagConstraints();
		fr.gridy = 0;
		fr.weightx = 1;
		fr.insets = new Insets(0, 5, 0, 5);
		fr.fill = GridBagConstraints.HORIZONTAL;
		add(firstRow, fr);
		GridBagConstraints sr = new GridBagConstraints();
		sr.gridy = 1;
		sr.weightx = 1;
		sr.insets = new Insets(0, 5, 0, 5);
		sr.fill = GridBagConstraints.HORIZONTAL;
		add(secondRow, sr);
		
        GridBagConstraints c = new GridBagConstraints();
        
        lbl = new JLabel(field.getDisplayName());
//        c.gridx = 0;
//        c.gridy = 0;
//        c.anchor = GridBagConstraints.WEST;
//        c.fill = GridBagConstraints.NONE;
//        c.insets = new  Insets(3, 5, 2, 5);
//        firstRow.add(lbl , c);
        firstRow.add(lbl);
        firstRow.add(Box.createHorizontalGlue());
        
        configureFieldContent(field);
        configureActions();
        configureRequiredField();
        addCtrlFocusListener(field);
		
		if( !field.isForeignKey() ) {
			System.out.println("Errore: il campo " + field.getDisplayName() + " non è una chiave esterna");
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void configureFieldContent(DBField F) {
	    if( foreignKeyTable == null ) {
	        foreignKeyTable = ((DBFieldFK<DBTable>)F).getForeignKeyTable();
	    }
		ArrayList<DBField> displayFields = foreignKeyTable.getDisplayFields();
		content = new ArrayList<JComponent>(displayFields.size());
		fields = new ArrayList<DBField>(displayFields.size());
		int gridx = 0;
		for( final DBField f: displayFields ) {
			GridBagConstraints c = new GridBagConstraints();
			if( gridx == 0 || gridx == displayFields.size() -1 ) {
				if( gridx == 0 ) {
					c.insets = new  Insets(3, 5, 2, 1);
				} else {
					c.insets = new  Insets(3, 1, 2, 5);
				}
				c.weightx = .5;
			} else {
				c.insets = new  Insets(3, 1, 2, 1);
				c.weightx = displayFields.size();
			}
//			c.weightx = displayFields.length;

			c.gridx = gridx++;
//			c.gridwidth = displayFields.length +1; // +1 perchè c'è la toolbar
			c.gridy = 1;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			fields.add(f);
			switch (f.getJDBCType()) {
				case BOOLEAN: {
					final JCheckBox cb = new JCheckBox(/*f.getDisplayValue()*/);
					content.add(cb);
					c.weightx = 0;
				} break;
				default: {
					final JFormattedTextField textField = new JFormattedTextField(/*f.getDisplayValue()*/);
					textField.setEditable(false);
//					textField.setColumns(100);
					content.add(textField);
				} break;
			}

			
//			secondRow.add(content.get(content.size()-1) , c);
			secondRow.add(content.get(content.size()-1));
		}
	}
	
	@Override
	protected void configureActions() {
		super.configureActions();
		actOpenAna = new ESActionOpenAna(foreignKeyTable, ESCtrlFK.this);
		actSelection = new ESActionSearch(foreignKeyTable, this, false);
		getActionBar().addAction(actOpenAna);
		getActionBar().addAction(actSelection);
		actClear.addActionListener(new ESActionListener() {
            @Override
            public Validation beforeExecute(ESAction act) {
                return null;
            }
            
            @Override
            public void afterExecute(ESAction act, Validation v) {
                foreignKeyTable.reset();
            }
        });
	}
	
	public ESActionBar getActionBar() {
		if( actionBar == null ) {
//			((GridBagLayout)getLayout()).getConstraints(content).gridwidth = 2;
			
//			GridBagConstraints c = new GridBagConstraints();
//	        c.gridx = content.size()-1;
//	        if( c.gridx == 0 )
//	        	c.gridx++;
//	        c.gridy = 0;
//	        c.anchor = GridBagConstraints.EAST;
////	        c.fill = GridBagConstraints.HORIZONTAL;
//	        c.insets = new  Insets(0, 0, 0, 5);
//	        actionBar = new ESActionBar(SwingConstants.HORIZONTAL, false);
//	        actionBar.setSpacing(0, 0);
//	        firstRow.add(actionBar, c);
            actionBar = new ESActionBar(SwingConstants.HORIZONTAL, false);
            actionBar.setSpacing(0, 0);
		    firstRow.add(actionBar);
		}
		return actionBar;
	}
	
	@Override
	public void loadValue() {
	    int id = getField().getInteger();
		foreignKeyTable.openKey(id);
		ArrayList<DBField> displayFields = foreignKeyTable.getDisplayFields();
		for( int i=0; i<content.size();i++ ) {
			DBField f = displayFields.get(i);
			switch (f.getJDBCType()) {
			case BOOLEAN: {
				((JCheckBox)content.get(i)).setSelected(((DBFieldBoolean)displayFields.get(i)).getBoolean());
			} break;
			default: {
			    JFormattedTextField t = (JFormattedTextField)content.get(i);
//		        t.setEditable(id <= 0);
				t.setText(displayFields.get(i).getDisplayValue());
			} break;
			}
			
		}
	}

	@Override
	protected void addCtrlFocusListener(DBField f) {
		for( int i=0; i<content.size(); i++ ) {
			final JComponent c = content.get(i);
			final DBField dbField = fields.get(i);
			c.addFocusListener(new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {
					Object value = onFocusLost(dbField, c);
					dbField.setValue(value, !Utils.isNull(value), true);
					if( !content.contains(e.getOppositeComponent()) ) {
						switch (dbField.getJDBCType()) {
						case BOOLEAN: {
							ESCtrlFK.this.transferFocus();
						} break;
						default: {
							ESCtrlFK.this.transferFocus();
						} break;
						}
					}
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					onFocusGained(dbField, c);
				}
			});
		}
	}
	
	
	@Override
	public Object onFocusLost(DBField f, JComponent comp) {
		setLabelText(getField().getDisplayName());
		switch (f.getJDBCType()) {
		case BOOLEAN:
			return ((JCheckBox)comp).isSelected();
		default:
		    return f.getValue();
		}
	}

	/**
	 * selezionato un controllo, indico a quale campo fa riferimento 
	 * @param f
	 * @param textField
	 */
	@Override
	public void onFocusGained(DBField f, JComponent textField) {
		StringBuilder sb = new StringBuilder(getField().getDisplayName());
		sb.append(" (").append(f.getDisplayName()).append(')');
		setLabelText(sb.toString());
	}

}
