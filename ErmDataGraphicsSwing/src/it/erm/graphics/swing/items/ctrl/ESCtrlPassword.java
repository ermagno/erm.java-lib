package it.erm.graphics.swing.items.ctrl;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldPassword;
import it.erm.lib.utils.CryptString;
import it.erm.lib.utils.Utils;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPasswordField;

public class ESCtrlPassword extends ESCtrl {

	public ESCtrlPassword(DBFieldPassword field) {
		super(field);
	}

	public Object onFocusLost(DBField f, JComponent textField) {
        String psw = "";
        try {
            char[] password = ((JPasswordField)textField).getPassword();
            psw = new String(password);
        } catch (Exception e) {}
        String encrypt = Utils.isNull(psw) ? null : CryptString.encrypt(psw);
		return encrypt;
	}

	public void onFocusGained(DBField f, JComponent textField) {
		
	}
	
	public void loadValue() {
	    String decrypt = CryptString.decrypt(field.getString());
		((JPasswordField)content.get(0)).setText(decrypt);
	}

	@Override
	protected void configureFieldContent(final DBField f) {
	    content = new ArrayList<JComponent>(1);
	    content.add(new JPasswordField(field.getDisplayValue()));
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
	
}
