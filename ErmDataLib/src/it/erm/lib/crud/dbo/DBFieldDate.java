package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;

public class DBFieldDate extends DBField {

	public DBFieldDate(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldDate(DBTable tableOwner, String name, boolean canBeNull) {
		this(tableOwner, name, JDBCType.DATE, canBeNull);
	}

}
