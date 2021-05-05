package it.erm.sqlite.crud.dbm;

import it.erm.lib.crud.dbo.DBTable;
import it.erm.lib.crud.executor.*;
import it.erm.lib.utils.PropertyUtils;
import it.erm.lib.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;


public class SQLiteDatabaseItem extends DatabaseItem {

	private ConnectionItem primary = null;
	private ConnectionItem secondary = null;
	private ConnectionItem other = null;

	@Override
	public ConnectionItem initConnection(ConnectionType type) {
		switch (type) {
		case PRIMARY:
			if( primary == null ) {
				primary = new SQLiteConnectioItem(this, initialize());
			}
			return primary;
		case SECONDARY:
			if( secondary == null ) {
				secondary = new SQLiteConnectioItem(this, initialize());
			}
			return secondary;
		case OTHER:
			if( other == null ) {
				other = new SQLiteConnectioItem(this, initialize());
			}
			return other;
		default:
			
			return null;
		}
	}

	private Connection initialize() {
		try {
			Class.forName("org.sqlite.JDBC");
			String name = PropertyUtils.get().getProperty(PropertyUtils.DATABASE_USER);
			name = (Utils.isNull(name) ? "startup" : name) + ".db";
			String path = PropertyUtils.get().getProperty(PropertyUtils.DATABASE_PATH);
			StringBuilder sb = new StringBuilder("jdbc:sqlite:");
			String connectionString = sb.append(path).append(name).toString();
			Connection connection = DriverManager.getConnection(connectionString);
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CrudData createCrudData(DBTable table) {
		return new SQLIteCrudData(table);
	}
	

}
