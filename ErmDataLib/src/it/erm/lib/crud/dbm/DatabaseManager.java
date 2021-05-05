package it.erm.lib.crud.dbm;

import it.erm.lib.crud.dbo.DBTable;
import it.erm.lib.crud.executor.*;
import it.erm.lib.utils.PropertyUtils;
import it.erm.lib.utils.Utils;

import java.util.HashMap;

/**
 * 
 * Serve per caricare i file .properties dove sono indicate le proprietà di connessione al database.<br>
 * Per ogni file .properties viene creato un oggetto {@link DatabaseItem} 
 * 
 * @author erm
 *
 */
public abstract class DatabaseManager {
	
	private final static HashMap<Class<? extends DBTable>, DatabaseItem> mapTableToDatabase = new HashMap<Class<? extends DBTable>, DatabaseItem>();
	public static DatabaseItem get(Class<? extends DBTable> tableClass) {
		DatabaseItem databaseItem = mapTableToDatabase.get(tableClass);
		while( databaseItem == null ) try {
			tableClass = (Class<? extends DBTable>) tableClass.getSuperclass();
			if( tableClass == DBTable.class ) {
				break;
			}
			databaseItem = mapTableToDatabase.get(tableClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return databaseItem;
	}
	
	protected void close() {
	    for( Class<? extends DBTable> key: mapTableToDatabase.keySet() ) {
	        for( ConnectionType ct: ConnectionType.values() ) {
	            ConnectionItem connection = mapTableToDatabase.get(key).getConnection(ct);
	            if( !Utils.isNull(connection) ) {
	                connection.close();
	            }
	        }
	    }
	    mapTableToDatabase.clear();
	}
	
	private DatabaseItem DefaultDBItem = null;
	public void setDatabaseProperties() {
	    close();
		DefaultDBItem = initializeDB();
	}

//	public void setDatabaseProperties(String propertyFileName) {
//		Properties loadPropertyFile;
//		try {
//			loadPropertyFile = Utils.loadPropertyFile(propertyFileName);
//			setDatabaseProperties(loadPropertyFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	
	public void initTable(Class<? extends DBTable> tableClass) {
		mapTableToDatabase.put(tableClass, DefaultDBItem);
		CrudExecutor crudExecutor = get(tableClass).getConnection(ConnectionType.PRIMARY).getCRUDExecutor();

		if( !crudExecutor.existTable(tableClass) ) {
			crudExecutor.createTable(tableClass);
		} else {
			crudExecutor.alterTable(tableClass);
		}
	}
	
	public void dropTable(Class<? extends DBTable> tableClass) {
		CrudExecutor crudExecutor = get(tableClass).getConnection(ConnectionType.PRIMARY).getCRUDExecutor();
		if( crudExecutor.existTable(tableClass) ) {
			crudExecutor.dropTable(tableClass);
		}
	}
	
	private DatabaseItem initializeDB() {
		DatabaseItem di = null;
		String databaseClassProp = PropertyUtils.get().getProperty(PropertyUtils.DATABASE_CLASS);
		try {
			Class<? extends DatabaseItem> databaseClass = (Class<? extends DatabaseItem>) Class.forName(databaseClassProp);
			di = (DatabaseItem) databaseClass.newInstance();
			di.init(this);
			return di;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public abstract Object[] getAdditionalObjectParams();
	
	public abstract void onUpgrade(Object... args); 
	public abstract void onCreate(Object... args);

}
