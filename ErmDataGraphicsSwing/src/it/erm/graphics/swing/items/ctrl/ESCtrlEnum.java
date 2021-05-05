package it.erm.graphics.swing.items.ctrl;

import it.erm.graphics.EView;
import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.items.list.ESListSearchEnum;
import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldEnum;
import it.erm.lib.crud.dbo.formatter.EnumFormatter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

public class ESCtrlEnum extends ESCtrl {
	private ESAction actSelection = null;
	public ESCtrlEnum(DBFieldEnum<? extends EnumInteger> field) {
		super(field);
		
		final DBFieldEnum<?> f = (DBFieldEnum<?>) field;
		
		actSelection  = new ESAction(ResourceUtils.getImageIcon("table_multiple.png", "selezione..."), null, "Elenca") {
			
			@Override
			public Validation executeAction() {
				final JDialog frame = new JDialog();
				frame.setIconImage(ResourceUtils.getImageIcon("table_multiple.png", "selezione...").getImage());
				ESListSearchEnum search = new ESListSearchEnum(frame, f.getEnumClass(), (EView) ESCtrlEnum.this);
				frame.setTitle("Ricerca");
				frame.setModal(true);
				{
				    int rowHeight = search.getJTable().getRowHeight();
				    int rowCount = search.getJTable().getRowCount();
				    Rectangle bounds = ESCtrlEnum.this.getBounds();
				    int sizeTitles = 120;
				    frame.setSize(new Dimension(bounds.width, (rowHeight*rowCount)+sizeTitles));
				    Point locationOnScreen = ESCtrlEnum.this.getLocationOnScreen();
				    frame.setLocation(locationOnScreen.x, locationOnScreen.y + bounds.height);
				}
				//
				frame.getRootPane().registerKeyboardAction(new ActionListener() {
			        @Override
			        public void actionPerformed(ActionEvent e) {
			            frame.dispose();
			        }
			    },  KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

				//Display the window.
//				frame.repaint();
				frame.setVisible(true);
				return null;
			}
		};
		getActionBar().addAction(actSelection);
	}

	protected void configureFieldContent(final DBField f) {
	    content = new ArrayList<JComponent>(1);
	    DefaultFormatter format = new DefaultFormatter();
	    format.setOverwriteMode(false);
	    JFormattedTextField t = new JFormattedTextField(format);
	    t.setEditable(false);
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
	   
	@Override
	public void loadValue() {
		((JFormattedTextField)content.get(0)).setText(field.getDisplayValue());			
	}
	@Override
	public Object onFocusLost(DBField f, JComponent textField) {
		DBFieldEnum<?> field = (DBFieldEnum<?>) f;
		EnumInteger enumInteger = EnumFormatter.getFromString(((JFormattedTextField)textField).getText(), field.getEnumClass());
		if( enumInteger == null )
			return null;
		return decodeStringCtrl(f.getJDBCType(), String.valueOf(enumInteger.getInteger()), null);
	}
	@Override
	public void onFocusGained(DBField f, JComponent textField) {
        DBFieldEnum<?> field = (DBFieldEnum<?>) f;
        EnumInteger enumInteger = EnumFormatter.getFromString(((JFormattedTextField)textField).getText(), field.getEnumClass());
        if( enumInteger == null ) {
            f.setValue(null, false, true);		
        } else {
            Object decodeStringCtrl = decodeStringCtrl(f.getJDBCType(), String.valueOf(enumInteger.getInteger()), null);
            f.setValue(decodeStringCtrl, false, true);      
        }

	}

}
