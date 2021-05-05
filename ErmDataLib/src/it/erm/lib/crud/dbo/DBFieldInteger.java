package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;

public class DBFieldInteger extends DBField {

	public DBFieldInteger(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldInteger(DBTable tableOwner, String name, boolean canBeNull) {
		super(tableOwner, name, JDBCType.INTEGER, canBeNull);
	}

}
