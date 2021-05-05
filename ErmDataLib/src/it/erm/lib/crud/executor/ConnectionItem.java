package it.erm.lib.crud.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import it.erm.lib.crud.dbo.DBTable;

/**
 * Rappresenta una connessione al database<br>
 * 
 * 
 * @author erm
 *
 */
public abstract class ConnectionItem {

	private ConcurrentHashMap<DBTable, Statement> tableStatementMap = new ConcurrentHashMap<DBTable, Statement>();
	private DatabaseItem databaseItem = null;
	private Connection connection = null;
	
	public ConnectionItem(DatabaseItem dbItem, Connection c) {
		databaseItem = dbItem;
		connection = c;
	}
	
	public DatabaseItem getDatabaseItem() {
		return databaseItem;
	}
	
	public abstract CrudExecutor getCRUDExecutor();

	public Statement getStatement(DBTable table) {
		Statement statement = tableStatementMap.get(table);
		if( statement == null ) {
			try {
				statement = connection.createStatement();
				statement.setQueryTimeout(2);
				tableStatementMap.put(table, statement);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return statement;
	}
	
	public void removeStatement(DBTable table) {
		Statement s = tableStatementMap.remove(table);
		if( s != null ) {
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void close() {
		for( DBTable t: tableStatementMap.keySet() ) {
			Statement s = tableStatementMap.get(t);
			if( s != null ) {
				try {
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
