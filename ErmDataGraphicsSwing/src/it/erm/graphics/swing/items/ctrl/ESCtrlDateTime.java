package it.erm.graphics.swing.items.ctrl;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldDateTime;
import it.erm.lib.crud.dbo.formatter.DateTimeFormatter;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class ESCtrlDateTime extends ESCtrl {
	
	MaskFormatter Formatter;
	
	public ESCtrlDateTime(DBField field) {
		super(field);
	}
	
	protected void configureFieldContent(final DBField field) {
		content = new ArrayList<JComponent>(1);
		try {
			DateTimeFormatter displayFormatter = (DateTimeFormatter) field.getDisplayFormatter();
			Formatter = new MaskFormatter(displayFormatter.getMask((DBFieldDateTime) field));
			Formatter.setPlaceholderCharacter('_');
			content.add(new JFormattedTextField(Formatter));
		} catch (Exception e) {
			e.printStackTrace();
		}
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new  Insets(3, 5, 2, 5);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = .5; 
        add(content.get(0) , c);
        content.get(0).addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				onFocusLost(field, content.get(0));
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				onFocusGained(field, content.get(0));
			}
		});
		
	}
	
	@Override
	public void loadValue() {
		((JFormattedTextField)content.get(0)).setText(field.getDisplayValue());	
		
	}

	@Override
	public Object onFocusLost(DBField f, JComponent comp) {
		JFormattedTextField textField =(JFormattedTextField)comp;
		return decodeStringCtrl(f.getJDBCType(), textField.getText(), Formatter);
	}

	@Override
	public void onFocusGained(DBField f, JComponent textField) {
		// TODO Auto-generated method stub
		
	}
}
