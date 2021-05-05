package it.erm.lib.crud.dbm;

import it.erm.lib.crud.dbo.DBTable;

public class ResultDBAction {

	int                      id                   = -1;
	Class<? extends DBTable> tableClass = DBTable.class;
	StringBuilder            msg = new StringBuilder();
	Class<?>                 callerClass = ResultDBAction.class;
	
	public ResultDBAction(int idTable, Class<? extends DBTable> tableClazz, String messaggio, Class<?> caller) {
		if( idTable > -1 ) {
			id = idTable;
		}
		if( tableClazz != null ) {
			tableClass = tableClazz;
		}
		if( messaggio != null && !messaggio.isEmpty() ) {
			msg.append(messaggio);
		}
		if( caller != null ) {
			callerClass = caller;
		}
	}
	
	public ResultDBAction(DBTable table, String messaggio, Class<?> caller) {
		this(table.getId(), table.getClass(), messaggio, caller);
	}

	public ResultDBAction(DBTable table, String messaggio) {
		this(table.getId(), table.getClass(), messaggio, null);
	}

	public ResultDBAction(Class<? extends DBTable> tableClazz, String messaggio, Class<?> caller) {
		this(-1, tableClazz, messaggio, caller);
	}

	public ResultDBAction(Class<? extends DBTable> tableClazz, String messaggio) {
		this(-1, tableClazz, messaggio, null);
	}
	
	public ResultDBAction() {
		this(-1, null, null, null);
	}

	public ResultDBAction(String messaggio, Class<?> caller) {
		this(-1, null, messaggio, caller);
	}

	public ResultDBAction(Class<?> caller) {
		this(-1, null, null, caller);
	}

	public void addMsg(String messaggio) {
		msg.append(System.getProperty("line.separator")).append(messaggio);
	}
	
	public DBTable getDB() {
		DBTable t = null;
		try {
			t = tableClass.newInstance();
		} catch (Exception e) {
			msg.append(e.getMessage()); 
		}

		return t;
	}
	
	public Class<?> getCallerClass() {
		return callerClass;
	}
	
	public StringBuilder getMsg() {
		return msg;
	}
	
}
