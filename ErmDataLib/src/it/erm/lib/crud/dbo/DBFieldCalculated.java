package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;

public abstract class DBFieldCalculated extends DBField implements FieldCalcListener {

	public DBFieldCalculated(DBTable tableOwner, String name, JDBCType tipo) {
		super(tableOwner, name, tipo, true);
//		isCalculated = true;
	}

	@Override
	public Object getValue() {
		return onGetValue();
	}
	
}
