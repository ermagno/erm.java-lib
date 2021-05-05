package it.erm.lib.crud.executor;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;
import it.erm.lib.crud.executor.CrudData.CRUDOperation;
import it.erm.lib.crud.executor.data.Select;
import it.erm.lib.crud.executor.data.SelectList;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Rappresenta uno statement<br> 
 * 
 * @author erm
 *
 */
public abstract class CrudExecutor {
	
	private ConnectionItem connectionItem = null;
	private ConcurrentHashMap<DBTable, ResultSet> tableResultSetMap = new ConcurrentHashMap<DBTable, ResultSet>();
	
	public ConnectionItem getConnectionItem() {
		return connectionItem;
	}
	
	public CrudExecutor(ConnectionItem connItem) {
		connectionItem = connItem;
	}
	
	protected ResultSet getResultSet(DBTable table) {
		return tableResultSetMap.get(table);
	}
	
	protected void setResultSet(DBTable table, ResultSet rs) {
		tableResultSetMap.put(table, rs);
	}
	
	protected void removeResultSet(DBTable table) {
		tableResultSetMap.remove(table);
	}
	
	public abstract boolean existTable(Class<? extends DBTable> tableClass);
	public abstract void createTable(Class<? extends DBTable> tableClass);
	public abstract void alterTable(Class<? extends DBTable> tableClass);
	public abstract void dropTable(Class<? extends DBTable> tableClass);
	
	/**
	 * inserisce un record in base ai valori inseriti nell'oggetto table
	 * @param table
	 * @param ci
	 * @return -1 oppure l'id dell'oggetto creato
	 */
	public abstract int 	create(DBTable table);
	
	public boolean executeQuery(DBTable table) {
		ResultSet rs = getResultSet(table);
		if( rs != null ) {
			close(table);
		}
		try {
			ConnectionItem sqlci = (ConnectionItem) getConnectionItem();
			String sql = table.getCRUD().getSql(CRUDOperation.READ);
			table.LastSql = sql;
			rs = sqlci.getStatement(table).executeQuery(sql);
			setResultSet(table, rs);
			boolean beforeFirst = rs.isBeforeFirst();
			return beforeFirst;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(table.LastSql);
            return false;
        }
	}
	
	public boolean first(DBTable table) {
		ResultSet rs = getResultSet(table);
		try {
			if( rs == null ) {
				return false;
			} else {
				if( rs.isClosed() ) {
					return false;
				}
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
	
	public boolean hasNext(DBTable table) {
		ResultSet rs = getResultSet(table);
		if( rs == null ) {
            return false;
		}
		try {
			boolean hasNext = rs.isBeforeFirst() || !rs.isAfterLast();
			return hasNext;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * esegue la query con i filtri impostati nell'oggetto table<br>
	 * se la query non restituisce record ritorna null<br>
	 * ritorna un oggetto table con i dati caricati<br>
	 * ad ogni chiamata ritorna il record successivo<br>
	 * l'esecuzione del metodo dopo l'ultimo record ritorna null 
	 * @param table
	 * @param ci
	 * @return null oppure il record trovato
	 */
	public boolean next(DBTable table) {
		SelectList selectFields = table.getSelectList();
		for( Select s: selectFields ){
			s.getField().reset(false);
		}
		ResultSet rs = getResultSet(table);
		if( rs == null ) {
			return false;
		}
		
		try {
	        return rs.next();
//	            return loadRecord(rs, table);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}
	
	public void loadFieldValue(DBField f) {
		DBTable table = f.getTableOwner().getCRUD().getJoinMasterTable();
		if( table == null ) {
			table = f.getTableOwner();
		}
		ResultSet resultSet = getResultSet(table);
		if( resultSet == null ) {
			return;
		}
		CrudData crud = table.getCRUD();
		if( crud == null ) {
			return;
		}
		boolean canBeRead = false;
		try {
			canBeRead = resultSet.isBeforeFirst() || !resultSet.isAfterLast();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if( canBeRead ) {
			SelectList selectFields = crud.getSelectFields();
			try {
				ResultSetMetaData metaData = resultSet.getMetaData();
				Select fromField = selectFields.getFromField(f);
				if( fromField == null ) {
					String selectTableName = crud.getSelectTableName(f);
					System.err.println(String.format("Campo %s.%s non estratto dalla query, non verrà più letto", selectTableName, f.getName()));
					f.setDbLoaded(true);
				} else {
					int columnIndex = selectFields.getColumnIndex(fromField);
					JDBCType jdbcType = f.getJDBCType();
					switch( jdbcType ) {
					case INTEGER:
					    int i = resultSet.getInt(columnIndex);
					    if( i <= 0 && f.isKey() ) {
					        f.setValue(null, false, false);
					    } else {
					        f.setValue(i, false, false);
					    }
						break;
					case BOOLEAN:
						f.setValue(resultSet.getBoolean(columnIndex), false, false);
						break;
					case DOUBLE:
						f.setValue(resultSet.getDouble(columnIndex), false, false);
						break;
					case VARCHAR:
					case PASSWORD:
						f.setValue(resultSet.getString(columnIndex), false, false);
						break;
					case TIME:
						f.setValue(resultSet.getTime(columnIndex), false, false);
						break;
					case TIMESTAMP:
						f.setValue(resultSet.getTimestamp(columnIndex), false, false);
						break;
					case DATE:
						f.setValue(resultSet.getDate(columnIndex), false, false);
						break;
					default:
						f.setValue(resultSet.getObject(columnIndex), false, false);
						break;
					}
					
					f.setDbLoaded(true);
					f.resetModfied();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			f.reset(false);
		}
	}
	
	
//	private boolean loadRecord(ResultSet rs, DBTable table) {
//		try {
//        	ResultSetMetaData metaData = rs.getMetaData();
//        	int columnCount = metaData.getColumnCount();
//        	metaData.getTableName(1);
//        	rs.isAfterLast();
//        	rs.isBeforeFirst();
//        	rs.isFirst();
//            List<Select> selectFields = table.getCRUD().getSelectFields();
//            
//            for( Select s: selectFields ) {
//                int columnIndex = rs.findColumn(s.getColumnName());
//                DBField f = s.getField();
//                if( rs.getObject(columnIndex) == null ) {
////                    f.setValue(null);
////                    f.setPreviousValue(null);
//                	f.loadDBValue(null);
//                	continue;
//                }
//                switch (f.getJDBCType()) {
//                case BOOLEAN:
////                    f.setValue(rs.getInt(columnIndex) > 0);
////                    f.setPreviousValue(rs.getInt(columnIndex) > 0);
//                    f.loadDBValue(rs.getInt(columnIndex) > 0);
//                    break;
//                case TIMESTAMP:
////                    f.setValue(new java.sql.Date(rs.getLong(columnIndex)));
////                    f.setPreviousValue(new java.sql.Date(rs.getLong(columnIndex)));
//                	f.loadDBValue(new java.sql.Date(rs.getLong(columnIndex)));
//                    break;
//                case DOUBLE:
////                    f.setValue(rs.getDouble(columnIndex));
////                    f.setPreviousValue(rs.getDouble(columnIndex));
//                	f.loadDBValue(rs.getDouble(columnIndex));
//                    break;
//                case INTEGER:
////                    f.setValue(rs.getInt(columnIndex));
////                    f.setPreviousValue(rs.getInt(columnIndex));
//                	f.loadDBValue(rs.getInt(columnIndex));
//                    break;
//                case VARCHAR:
////                    f.setValue(rs.getString(columnIndex));
////                    f.setPreviousValue(rs.getString(columnIndex));
//                	f.loadDBValue(rs.getString(columnIndex));
//                    break;
//                default:
//                    break;
//                }
//            }
//            return true;
//        } catch(Exception e) {
//        	close(table);
//        	e.printStackTrace();
//        	return false;
//        }
//	}
	
	public abstract boolean openKey(DBTable table, int id);
	
	/**
	 * aggiorna un record in base ai filtri impostati nell'oggetto table e ai valori impostati nei campi<br>
	 * se la chiave primaria � valorizzata usa solo questa  come filtro
	 * @param table
	 * @param ci
	 * @return -1 oppure il numero di record aggiornati
	 */
	public abstract int 	update(DBTable table);
	
	/**
	 * elimina un record in base ai filtri impostati nell'oggetto table
	 * @param table
	 * @param ci
	 * @return -1 oppure il numero di record eliminati
	 */
	public abstract int 	delete(DBTable table);

	public abstract int getCount(DBTable dbTable);

	public void close(DBTable table) {
		ResultSet rs = getResultSet(table);
		if( rs != null ) try {
			rs.close();
			removeResultSet(table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ConnectionItem sqlci = (ConnectionItem) getConnectionItem();
		sqlci.removeStatement(table);
	}
	
}
