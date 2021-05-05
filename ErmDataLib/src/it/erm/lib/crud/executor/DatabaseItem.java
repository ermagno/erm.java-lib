package it.erm.lib.crud.executor;

import it.erm.lib.crud.dbm.DatabaseManager;
import it.erm.lib.crud.dbo.DBTable;


/**
 * Rappresenta un database<br>
 * Serve per caricare il driver
 * @author erm
 *
 */
public abstract class DatabaseItem {
	
	protected	DatabaseManager databaseManager = null;

	
	protected 	ConnectionItem 	PrimaryConnection = null;
	protected 	ConnectionItem 	SecondaryConnection = null;
	protected 	ConnectionItem 	OtherConnection = null;
	
	public DatabaseItem() {}
	
	public void init(DatabaseManager dm) {
		databaseManager = dm;
		initConnection(ConnectionType.PRIMARY);
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	
	/**
	 * carica dal file property i dati per la connessione<br>
	 * inizializza la connessione in input in base al tipo se non � gi� stata inizializzata
	 * @param propertyFile
	 * @param type
	 * @return 
	 */
	public abstract ConnectionItem initConnection(ConnectionType type);
	
	public ConnectionItem getConnection(ConnectionType type) {
		return initConnection(type);
	}
	
	public abstract CrudData createCrudData(DBTable dbTable);
	
	public String formatIdName(DBTable table) {
		return (new StringBuilder("id_").append(table.getName())).toString().toLowerCase();
	}
	
	
}
