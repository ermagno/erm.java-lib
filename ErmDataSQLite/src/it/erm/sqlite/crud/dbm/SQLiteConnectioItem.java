package it.erm.sqlite.crud.dbm;

import it.erm.lib.crud.executor.ConnectionItem;
import it.erm.lib.crud.executor.CrudExecutor;
import it.erm.lib.crud.executor.DatabaseItem;

import java.sql.Connection;

public class SQLiteConnectioItem extends ConnectionItem {

	private SQLiteCrudExecutor crudExecutor = null;
	
	public SQLiteConnectioItem(DatabaseItem dbItem, Connection c) {
		super(dbItem, c);
		crudExecutor = new SQLiteCrudExecutor(this);
	}
	
	@Override
	public CrudExecutor getCRUDExecutor() {
		return crudExecutor;
	}

}
