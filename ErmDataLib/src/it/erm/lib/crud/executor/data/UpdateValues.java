package it.erm.lib.crud.executor.data;

import it.erm.lib.crud.dbo.DBField;

public class UpdateValues {

	private DBField field = null;
	private Object  value = null;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("field: ").append(field).append("\nvalue: ").append(value==null?"null":value);
		return sb.toString();
	}

	public UpdateValues(DBField f, Object v) {
		this.field = f;
		this.value = v;
	}
	
	public DBField getField() {
		return field;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getSqlField() {
		return field.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof UpdateValues ) {
			return ((UpdateValues)obj).field.equals(field);
		}
		return super.equals(obj);
	}
}
