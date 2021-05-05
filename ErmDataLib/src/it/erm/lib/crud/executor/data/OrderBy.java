package it.erm.lib.crud.executor.data;

import it.erm.lib.crud.dbo.DBField;

public class OrderBy {

	private DBField field       = null;
	private boolean ascendent   = true;
	
	public OrderBy(DBField f, boolean asc) {
		field = f;
		ascendent = asc;
	}
	
	public DBField getField() {
		return field;
	}
	
	public boolean isAscendent() {
		return ascendent;
	}
}
