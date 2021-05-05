package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;

public class DBFieldTime extends DBField {

	public DBFieldTime(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldTime(DBTable tableOwner, String name, boolean canBeNull) {
		super(tableOwner, name, JDBCType.TIME, canBeNull);
	}
	
	
}
