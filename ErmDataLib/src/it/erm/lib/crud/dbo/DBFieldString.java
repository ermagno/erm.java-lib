package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;

public class DBFieldString extends DBField {

	public DBFieldString(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldString(DBTable tableOwner, String name, boolean canBeNull) {
		super(tableOwner, name, JDBCType.VARCHAR, canBeNull);
	}

}
