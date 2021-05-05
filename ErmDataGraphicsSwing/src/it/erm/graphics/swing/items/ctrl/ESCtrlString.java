package it.erm.graphics.swing.items.ctrl;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldString;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

public class ESCtrlString extends ESCtrl {

	public ESCtrlString(DBFieldString field) {
		super(field);
	}

	public Object onFocusLost(DBField f, JComponent textField) {
		return ((JFormattedTextField)textField).getText();
	}

	public void onFocusGained(DBField f, JComponent textField) {
		
	}
	
	public void loadValue() {
		((JFormattedTextField)content.get(0)).setText(field.getDisplayValue());		
	}

}
