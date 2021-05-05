package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;

public class DBFieldDouble extends DBField {

	public DBFieldDouble(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldDouble(DBTable tableOwner, String name, boolean canBeNull) {
		super(tableOwner, name, JDBCType.DOUBLE, canBeNull);
	}

}
