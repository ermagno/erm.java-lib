package it.erm.lib.crud.executor.data;

import it.erm.lib.crud.dbo.DBField;

public class Select {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(tableName != null ? tableName:"No Tablename");
		sb.append(", ");
		sb.append(field != null ? field.getName():"No Field");
		sb.append(", ");
		sb.append(function != null ? function:"No Function");
		sb.append(")");
		return sb.toString();
	}
	
	private String     tableName;
	private DBField    field;
	private String     function;
//	private String     originalName;
//	private String     columnName;
//	private String     queryName = null;
	
//	public Select(DBField f) {
//		field = f;
//		originalName = f.getFullName();
//		columnName = f.getFullName().replace('.', '_');
//		queryName = f.getFullName();
//	}

	public Select(String tableName, DBField f, String function) {
		this.tableName = tableName;
		this.field = f;
		this.function = function;
		
//		columnName = new StringBuilder(function).append("_").append(columnName).toString();
//		this.queryName = new StringBuilder(function).append("(").append(originalName).append(") AS ").append(columnName).append("").toString();
	}

	public DBField getField() {
		return field;
	}

	public String getSelect() {
		String result = null;
		String s = new StringBuilder(tableName).append('.').append(field.getName()).toString();
		if( function != null ) {
			result = String.format(function, s);
		} else {
			result = s;
		}
		return result;
	}
	
//	public void setColumnName(String columnName) {
//		this.columnName = columnName;
//	}
//	
//	public String getColumnName() {
//		return columnName;
//	}
//	
//	public void setQueryName(String queryName) {
//		this.queryName = queryName;
//	}
//	
//	public String getQueryName() {
//		if( queryName == null ) {
//			return new StringBuilder(originalName).append(" AS ").append(columnName).append("").toString();
//		}
//		return queryName.toString();
//	}
	
}
