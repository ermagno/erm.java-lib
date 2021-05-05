package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;


public class DBFieldBoolean extends DBField {

	public DBFieldBoolean(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		super(tableOwner, name, tipo, canBeNull);
	}

	public DBFieldBoolean(DBTable tableOwner, String name, boolean canBeNull) {
		super(tableOwner, name, JDBCType.BOOLEAN, canBeNull);
	}
	
//	@Override
//	public void setValue(Object value, boolean addFilter) {
//		if( value instanceof Boolean ) {
//			value = ((Boolean)value) ? 1 : 0; 
//		}
//		super.setValue(value, addFilter);
//	}
	
}
