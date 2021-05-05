package it.erm.sqlite.crud.dbm;

import it.erm.lib.crud.dbm.TableManager;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;
import it.erm.lib.crud.executor.*;
import it.erm.lib.crud.executor.CrudData.CRUDOperation;
import it.erm.lib.crud.executor.data.Aggregation;
import it.erm.lib.crud.executor.data.Aggregation.AggFunction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteCrudExecutor extends CrudExecutor {

	private static final String SEPARATOR = ", ";
	
	public SQLiteCrudExecutor(ConnectionItem connItem) {
		super(connItem);
	}

	
	@Override
	public int create(DBTable table) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
        int generatedKey = -1;
        
        String sql = table.getCRUD().getSql(CRUDOperation.CREATE);
        table.LastSql = sql;
//        System.out.println(sql);
		try {
			int count = sqlci.getStatement(table).executeUpdate(sql);
			if( count >= 1 ) {
				generatedKey = getGeneratedKey(sqlci, table);
				openKey(table, generatedKey);
				return generatedKey;
			} else {
				System.out.println("Errore in inserimento: " + sql + count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			close(table);
		}
        
        return -1;
	}

    /**
     * As SQLite's last_insert_rowid() function is DB-specific not statement
     * specific, this function introduces a race condition if the same
     * connection is used by two threads and both insert.
     */
	private synchronized int getGeneratedKey(SQLiteConnectioItem sqlci, DBTable table) {
		try {
			ResultSet resultSet = sqlci.getStatement(table).getGeneratedKeys();
			setResultSet(table, resultSet);
			if( resultSet.next() ) {
				try {
					int last_insert_rowid = resultSet.getInt("last_insert_rowid()");
					return last_insert_rowid;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(table);
		}
		return -1;
	}
	
	

	
	@Override
	public int update(DBTable table) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
		int count = -1;
		String sql = table.getCRUD().getSql(CRUDOperation.UPDATE);
		table.LastSql = sql;
        try {
			count = sqlci.getStatement(table).executeUpdate(sql);
			int id = table.getId();
			if( count == 1 ) {
				try {
					openKey(table, id);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			close(table);
		} catch (SQLException e) {
			close(table);
			e.printStackTrace();
		}
        return count;
	}

	@Override
	public int delete(DBTable table) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();

        String sql = table.getCRUD().getSql(CRUDOperation.DELETE);
        int count = -1;
		try {
			count = sqlci.getStatement(table).executeUpdate(sql);
			if( count < 0 ) {
				try {
					count = sqlci.getStatement(table).getUpdateCount();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			close(table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return count;
	}

	
	
	@Override
	public int getCount(DBTable table) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
		
		ResultSet resultSet = getResultSet(table);
		
		if( resultSet == null ) {
			table.getCRUD().addAggregation(new Aggregation(AggFunction.COUNT_ALL, null));
			
			String sql = table.getCRUD().getSql(CRUDOperation.READ);
			
			try {
				resultSet = sqlci.getStatement(table).executeQuery(sql.toString());
				setResultSet(table, resultSet);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			int count = 0;
			try {
				while( resultSet.next() ) {
					count++;
				}
				return count;
			} catch (SQLException e) {
				e.printStackTrace();
				return -1;
			}
		} else {
			/*
			synchronized (resultSet) {
				try {
					int row = resultSet.getRow();
					resultSet.beforeFirst();
					int count = 0;
					while( resultSet.next() ) {
						count++;
					}
					resultSet.absolute(row);
					return count;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			*/
		}
		return -1;
	}

	public boolean first(DBTable table) {
		ResultSet rs = getResultSet(table);
		try {
			if( rs == null ) {
				return false;
			} else {
				if( rs.getType() == ResultSet.TYPE_FORWARD_ONLY ) {
					return false;
				}
				return rs.first();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean openKey(DBTable table, int id) {
		boolean openKey = false; 
		close(table);
		table.reset();
		table.getIdField().addFilter(id);
		if( id > 0 && table.openfind() ) {
			openKey = next(table);
			table.getCRUD().clearFilters();
			for( DBField f: table.getFields() ) {
				loadFieldValue(f);
			}
			table.getIdField().addFilter(id);
			close(table);
		}
		return openKey;
	}
	
	@Override
	public void createTable(Class<? extends DBTable> tableClass) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
		TableManager<? extends DBTable> tableManager = TableManager.get(tableClass);
		DBTable table = tableManager.newTable();

		String sep = ", ";
		String start = " (";
		String end = ") ";
		
		String sql = "CREATE TABLE " + table.getName() + " (" + sqlci.getDatabaseItem().formatIdName(table) + " INTEGER PRIMARY KEY,";
		for( DBField f: table.getFields() ) {
			if( f == table.getIdField() ) {
				continue;
			}
			sql += f.getName();
			switch (f.getJDBCType()) {
			case BOOLEAN:
			case INTEGER:
				sql += " INTEGER";
				break;
			case TIMESTAMP:
				sql += " DATETIME";
				break;
			case DOUBLE:
				sql += " NUMERIC";
				break;
			case VARCHAR:
			case PASSWORD:
				sql += " TEXT";
				break;
			default:
				break;
			}
			if( !f.canBeNull() ) {
				sql += " NOT NULL";
			}
			
			sql += sep;
		}
		
		sql = sql.substring(0, sql.lastIndexOf(sep));
		sql += end;

		try {
			sqlci.getStatement(table).executeUpdate(sql);
			close(table);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
	}

	@Override
	public void alterTable(Class<? extends DBTable> tableClass) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
		TableManager<? extends DBTable> tableManager = TableManager.get(tableClass);
		DBTable table = tableManager.newTable();

        for( DBField f: table.getFields() ) {
        	try {
        		if( sqlci.getStatement(table).execute("SELECT " + f.getName() + " FROM " + table.getName() + " WHERE 1=0") ) {
        			continue;
        		}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        	StringBuilder sql = new StringBuilder("ALTER TABLE ");
        	sql.append(table.getName());
        	sql.append(" ADD COLUMN "); 
        	sql.append(f.getName());
        	switch (f.getJDBCType()) {
        	case BOOLEAN:
        	case INTEGER:
        		sql.append(" INTEGER");
        		break;
        	case TIMESTAMP:
        		sql.append(" DATETIME");
        		break;
        	case DOUBLE:
        		sql.append(" NUMERIC");
        		break;
        	case VARCHAR:
        	case PASSWORD:
        		sql.append(" TEXT");
        		break;
        	default:
        		break;
        	}
//        	if( !f.canBeNull() ) {
//        		sql.append(" NOT NULL DEFAULT ");
//        		Object defaultValue = f.getDefaultValue();
//        		if( defaultValue == null ) {
//        			if( f.isForeignKey() ) {
//        				defaultValue = 0;
//        				System.out.println("valore foreign key impostato a zero di default");
//        			}
//        		}
//        		sql.append(DBFilter.getValueFormatted(defaultValue));
//        	}
        	try {
        		sqlci.getStatement(table).executeUpdate(sql.toString());
        	} catch (Exception e) {
        		close(table);
        		e.printStackTrace();
        		return;
        	}
        }
        close(table);	    
		
	}

	@Override
	public void dropTable(Class<? extends DBTable> tableClass) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
		TableManager<? extends DBTable> tableManager = TableManager.get(tableClass);
		DBTable table = tableManager.newTable();

		try {
			sqlci.getStatement(table).executeUpdate("DROP TABLE " + table.getName());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean existTable(Class<? extends DBTable> tableClass) {
		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
		TableManager<? extends DBTable> tableManager = TableManager.get(tableClass);
		DBTable table = tableManager.newTable();

		try {
			sqlci.getStatement(table).executeUpdate("SELECT * FROM " + table.getName() + " WHERE 1=0");
		} catch (Exception e) {
			close(table);
			return false;
		}
		close(table);
        return true;
	}

//	public void createIndex(Class<? extends DBTable> tableClass) {
//		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
//		TableManager<? extends DBTable> tableManager = TableManager.get(tableClass);
//		DBTable table = tableManager.newTable();
//
//		String sep = ", ";
//		String start = " (";
//		String end = ") ";
//		HashMap<String, ArrayList<DBField>> mapIndex = new HashMap<String, ArrayList<DBField>>();
//		for (DBField f : table.getFields()) {
//			if( f.getIndexName() != null && !f.getIndexName().isEmpty() ) {
//				if( mapIndex.get(f.getIndexName()) == null )
//					mapIndex.put(f.getIndexName(), new ArrayList<DBField>());
//				mapIndex.get(f.getIndexName()).add(f);
//			}
//		}
//		
//		for (String indexName : mapIndex.keySet()) {
//			String sql = " CREATE INDEX " + indexName +" ON " + table.getName() + start;
//			for( DBField f: mapIndex.get(indexName) ) {
//				sql += f.getName() + sep;
//			}
//			sql = sql.substring(0, sql.lastIndexOf(sep));
//			sql += end;
//			try {
//				sqlci.getStatement(table).executeUpdate(sql);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
//	public void createUniqueIndex(Class<? extends DBTable> tableClass) {
//		SQLiteConnectioItem sqlci = (SQLiteConnectioItem) getConnectionItem();
//		TableManager<? extends DBTable> tableManager = TableManager.get(tableClass);
//		DBTable table = tableManager.newTable();
//
//		String sep = ", ";
//		String start = " (";
//		String end = ") ";
//		HashMap<String, ArrayList<DBField>> mapIndex = new HashMap<String, ArrayList<DBField>>();
//		for (DBField f : table.getFields()) {
//			if( f.getUniqueIndexName() != null && !f.getUniqueIndexName().isEmpty() ) {
//				if( mapIndex.get(f.getUniqueIndexName()) == null )
//					mapIndex.put(f.getUniqueIndexName(), new ArrayList<DBField>());
//				mapIndex.get(f.getUniqueIndexName()).add(f);
//			}
//		}
//		
//		for (String indexUniqueName : mapIndex.keySet()) {
//			String sql = " CREATE UNIQUE INDEX " + indexUniqueName +" ON " + table.getName() + start;
//			for( DBField f: mapIndex.get(indexUniqueName) ) {
//				sql += f.getName() + sep;
//			}
//			sql = sql.substring(0, sql.lastIndexOf(sep));
//			sql += end;
//			try {
//				sqlci.getStatement(table).executeUpdate(sql);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	@Override
	public boolean next(DBTable table) {
		ResultSet resultSet = getResultSet(table);
		if( resultSet == null ) {
			table.openfind();
		}
		return super.next(table);
	}
	
	@Override
	public boolean hasNext(DBTable table) {
		throw new RuntimeException("metodo non implementato per SQLite");
	}
}
