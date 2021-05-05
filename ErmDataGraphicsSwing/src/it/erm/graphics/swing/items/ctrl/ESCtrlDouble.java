package it.erm.graphics.swing.items.ctrl;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import it.erm.lib.crud.dbo.DBField;

public class ESCtrlDouble extends ESCtrl {

	public ESCtrlDouble(DBField field) {
		super(field);
	}

	@Override
	public void loadValue() {
		
	}

	@Override
	public Object onFocusLost(DBField f, JComponent textField) {
		return decodeStringCtrl(f.getJDBCType(), ((JFormattedTextField)textField).getText(), null);
	}

	@Override
	public void onFocusGained(DBField f, JComponent textField) {
		
	}

}
