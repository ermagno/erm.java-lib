package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.utils.CryptString;

public class DBFieldPassword extends DBField {

	public DBFieldPassword(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldPassword(DBTable tableOwner, String name, boolean canBeNull) {
		super(tableOwner, name, JDBCType.PASSWORD, canBeNull);
	}

	public String decodePSW() {
	    return CryptString.decrypt(getString());
	}
	
}
