package it.erm.lib.crud.executor.data;

import java.util.ArrayList;
import java.util.Iterator;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

public class JoinList extends ArrayList<Join> {

	private static final long serialVersionUID = -5057065620760363363L;
	private DBTable joinMasterTable = null;
	
	public void setJoinMasterTable(DBTable joinMasterTable) {
		this.joinMasterTable = joinMasterTable;
	}
	
	public DBTable getJoinMasterTable() {
		return joinMasterTable;
	}
	
	@Override
	public void add(int index, Join element) {
		super.add(index, element);
		System.err.println("L'ordine delle join non va modificato!");
	}
	
	@Override
	public boolean add(Join e) {
		if( !isEmpty() ) {
			Iterator<Join> iterator = iterator();
			int count = 0;
			StringBuilder joinTableName = new StringBuilder(e.getJoinTableName());
			while( iterator.hasNext() ) {
				Join next = iterator.next();
				if( next.getJoinedTable().getName().equalsIgnoreCase(e.getJoinTableName()) ) {
					count++;
				}
			}
			if( count > 0 ) {
				e.setJoinTableName(joinTableName.append('_').append(count).toString());
			}
		}
		return super.add(e);
	}
	
	public String getSelectTableName(DBField f) {
		DBTable tableOwner = f.getTableOwner();
		String tableName = tableOwner.getName();
		if( !isEmpty() ) {
			Iterator<Join> iterator = iterator();
			while( iterator.hasNext() ) {
				Join next = iterator.next();
				if( next.getJoinedTable() == tableOwner ) {
					tableName = next.getJoinTableName();
					break;
				}
			}
			
		}
		return tableName;
	}
	
}
