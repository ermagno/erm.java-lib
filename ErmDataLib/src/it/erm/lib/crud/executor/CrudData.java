package it.erm.lib.crud.executor;

import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;
import it.erm.lib.crud.executor.data.*;
import it.erm.lib.crud.executor.data.Aggregation.AggFunction;
import it.erm.lib.utils.Utils;

import java.util.ArrayList;


public class CrudData {
	
	public static enum CRUDOperation {
		CREATE, READ, UPDATE, DELETE
	}

	private static final String SEPARATOR = ", ";
	
	private DBTable                 masterTable = null;
	
	private ArrayList<InsertValues> InsertValues  = new ArrayList<InsertValues>();
	
	private SelectList              Select        = null;
	private JoinList                Joins         = null;
	private FilterList              Filters       = null;
	private ArrayList<Aggregation>  Aggregations  = new ArrayList<Aggregation>();
	private ArrayList<OrderBy>      OrderBys       = new ArrayList<OrderBy>();
	
	private ArrayList<UpdateValues> UpdateValues  = new ArrayList<UpdateValues>();
	
	public CrudData(DBTable master) {
		masterTable = master;
		Select      = new SelectList();
		Joins       = new JoinList();
		Filters     = new FilterList();
	}
	
	public void clearFilters() {
		getFilters().clear();
	}
	
	public void clearFilters(DBField field) {
		ArrayList<Filter> toRemove = new ArrayList<Filter>();
		for( Filter f: getFilters() ) {
			if( f.getField() == field ) {
				toRemove.add(f);
			}
		}
		getFilters().removeAll(toRemove);
	}
	
	public Filter getLastFilter(DBField field) {
		
		for( int i=getFilters().size()-1; i>=0; i-- ) {
			if( getFilters().get(i).getField() == field ) {
				return getFilters().get(i);
			}
		}
		return null;
	}
	
	public void clearInsertValues() {
		InsertValues.clear();
	}
	
	public void clearSelect() {
		Select.clear();
	}

	public void clearJoins() {
		Joins.clear();
	}
	
	public void clearAggregations() {
		Aggregations.clear();
	}

	public void clearOrderBy() {
		OrderBys.clear();
	}

	public void clearUpdateValues() {
		UpdateValues.clear();
	}
	
	/**
	 * non cancella le join
	 */
	public void reset() {
		masterTable.close();
		clearFilters();
		clearInsertValues();
		clearSelect();
		if( Joins.getJoinMasterTable() != null ) {
			clearJoins();
		}
		clearAggregations();
		clearOrderBy();
		clearUpdateValues();
	}
	
	public ArrayList<DBTable> getJoinedTables() {
		ArrayList<DBTable> joinedTables = new ArrayList<DBTable>(Joins.size());
		for( Join j: Joins ) {
			DBTable tableJoined = j.getJoinedField().getTableOwner();
			if( !joinedTables.contains(tableJoined) ) {
				joinedTables.add(tableJoined);
			}
		}
		return joinedTables;
	}
	
	public DBField getEquivalentField(DBTable fromTable, DBField fromField) {
		for( DBField f: masterTable.getAllFields() ) {
			if( f.getName().equals(fromField.getName()) )
				return f;
		}
		return null;
	}

	public void copyFilters(CrudData from, boolean clearAndCopy) {
		if( clearAndCopy ) {
			getFilters().clear();
		}
		for( Filter f: from.getFilters() ) {
			DBField equivalentField = getEquivalentField(from.masterTable, f.getField());
			if( equivalentField != null ) {
				getFilters().add(new Filter(f.getOperator(), f.getConnectAND(), equivalentField, f.getValueFilters()));
			}
		}
	}
	
	public void addFilter(Filter filter) {
		getFilters().add(filter);
	}
	
	protected FilterList getFilters() {
		FilterList filters = null;
		if( getJoinMasterTable() == null ) {
			filters = Filters;
		} else {
			filters = getJoinMasterTable().getCRUD().Filters;
		}
		return filters;
	}
	
	
	public boolean hasFilters() {
		return getFilters().size()>0;
	}
	
	public void addInsertValue(DBField f, Object v) {
		InsertValues.add(new InsertValues(f, v));
	}
	
	public String getSelectTableName(DBField f) {
		return Joins.getSelectTableName(f);
	}
	public DBTable getJoinMasterTable() {
		return Joins.getJoinMasterTable();
	}
	
	private void setJoinMasterTable(DBTable t) {
		this.Joins.setJoinMasterTable(t);
	}
	
	public void addJoin(Join join) {
		if( getJoinMasterTable() == null  ) {
			join.getJoinedTable().getCRUD().setJoinMasterTable(masterTable);
			Joins.add(join);
		} else {
			DBTable jmt = join.getMasterTable().getCRUD().getJoinMasterTable();
			join.getJoinedTable().getCRUD().setJoinMasterTable(jmt);
			jmt.getCRUD().addJoin(join);
		}
	}
	
	public boolean hasJoins() {
		return Joins.size()>0;
	}
	
	public void addAggregation(Aggregation aggFun) {
		if( getJoinMasterTable() == null ) {
			Aggregations.add(aggFun);
		} else {
			getJoinMasterTable().getCRUD().addAggregation(aggFun);
		}
	}
	
	public boolean hasAggregations() {
		return Aggregations.size()>0;
	}
	
	public void addOrderBy(OrderBy orderBy) {
		if( getJoinMasterTable() == null ) {
			OrderBys.add(orderBy);
		} else {
			getJoinMasterTable().getCRUD().addOrderBy(orderBy);
		}
	}
	
	public boolean hasOrderBy() {
		return OrderBys.size()>0;
	}
	
	public String getSql(CRUDOperation op) {
		String sql = null;
		switch (op) {
		case CREATE:
			sql = getSqlCreate();
//			System.out.println(sql);
			break;
		case READ:
			sql = getSqlRead();
//			System.out.println(sql);
			break;
		case UPDATE:
			sql = getSqlUpdate();
//			System.out.println(sql);
			break;
		case DELETE:
			sql = getSqlDelete();
			break;

		default:
			break;
		}
//		System.out.println(sql);
		return sql;
	}
	
	private void prepareInsertValues() {
		InsertValues.clear();
		ArrayList<DBField> fields = masterTable.getFields();
		for( DBField f: fields ) {
		    InsertValues.add(new InsertValues(f, f.getValue()));
//			if( f.isModified() ) {
//			    InsertValues.add(new InsertValues(f, f.getValue()));
////				f.clearModfied();
//			} else {
//				if( f.isNull() && f.canBeNull() && f.isModified()  )
//					continue;
//				if( !f.isNull() && !f.isModified()  )
//					continue;
//				if( f.getValue() == null && f.getDefaultValue() != null )
//					InsertValues.add(new InsertValues(f, f.getDefaultValue()));
//			}
		}
	}
	
	protected String getSqlCreate() {
		prepareInsertValues();
		if( InsertValues.size() > 0 ) {
			StringBuilder sql = new StringBuilder("INSERT INTO ");
			sql.append(masterTable.getName()).append(" (");
			boolean first = true;
			for( InsertValues column: InsertValues ) {
				if( !first ) {
					sql.append(SEPARATOR);
				}
				sql.append(column.getSqlField());
				first = false;
			}
//			sql = new StringBuilder(sql.substring(0, sql.lastIndexOf(SEPARATOR)));
			
			sql.append(") VALUES (");
			first = true;
			for( InsertValues column: InsertValues ) {
				if( !first ) {
					sql.append(SEPARATOR);
				}
				sql.append(getSqlValue(column.getValue()));
				first = false;
			}
//			sql = new StringBuilder(sql.substring(0, sql.lastIndexOf(SEPARATOR)));
			sql.append(")");
			return sql.toString();
		} else {
			return "select 'no insert values'";
		}
	}
	
	private void prepareSelectFields() {
		clearSelect();
		if( Aggregations.isEmpty() ) {
			if( Select.isEmpty() ) {
				ArrayList<DBField> fields = masterTable.getFields();
//				Select.add(new Select(masterTable.getIdField()));
				for( DBField f: fields ) {
					Select.add(new Select(masterTable.getName(), f, null));
				}
				for (Join j: Joins) {
					DBTable tableJoined = j.getJoinedField().getTableOwner();
//					Select.add(new Select(tableJoined.getIdField()));
					for( DBField f: tableJoined.getFields() ) {
						Select.add(new Select(j.getJoinTableName(), f, null));
					}
				}
			}
		} else {
			for( Aggregation agg: Aggregations ) {
				String tableName = null;
				if( !Joins.isEmpty() ) {
					tableName = Joins.getSelectTableName(agg.getField());
				}
				if( tableName == null ){
					tableName = agg.getField().getTableOwner().getName();
				}
				
				switch (agg.getAggFunction()) {
				case COUNT: {
					Select.add(new Select(tableName, agg.getField(), "COUNT(%s)"));
				} break;
				case COUNT_ALL: {
					Select s = new Select(tableName, masterTable.getIdField(), null) {
						@Override
						public String getSelect() {
							return "count(*)";
						}
					};
					Select.clear();
					Select.add(s);
					return;
				}
				case COUNT_DISTINCT: {
					Select.add(new Select(tableName, agg.getField(), "COUNT(DISTINCT %s)"));
				} break;
				case DISTINCT: {
				} break;
				case GROUPBY: {
					Select.add(new Select(tableName, agg.getField(), null));
				} break;
				case MAX: {
					Select.add(new Select(tableName, agg.getField(), "MAX(%s)"));
				} break;
				case MIN: {
					Select.add(new Select(tableName, agg.getField(), "MIN(%s)"));
				} break;
				case NONE: {
				} break;
				case SUM: {
					Select.add(new Select(tableName, agg.getField(), "SUM(%s)"));
				} break;
				default:
					break;
				}
			}
		}
	}
	
	public SelectList getSelectFields() {
		return Select;
	}
	
	private String getSqlSelect() {
		prepareSelectFields();
		boolean first = true;
		StringBuilder select = new StringBuilder("SELECT ");
		for( Select s: Select ) {
			if( !first ) {
				select.append(SEPARATOR);
			} else {
				first = false;
			}
			select.append(s.getSelect());
		}
		return select.append(Utils.NewLine()).toString();
	}
	
	private String getSqlFrom() {
		StringBuilder from = new StringBuilder("FROM ");
		from.append(masterTable.getName());
		for( Join j: Joins ) {
			from.append(Utils.NewLine());
			switch (j.getJoinType()) {
			case LEFT:
				from.append("JOIN ");
				break;
			case LEFT_OUTER:
				from.append("LEFT OUTER JOIN ");
				break;
			default:
				break;
			}
			String joinedTableName = Joins.getSelectTableName(j.getJoinedField());
			String joinedTableNameOriginal = j.getJoinedTable().getName();
			String masterTableName = Joins.getSelectTableName(j.getMasterField());
			from.append(joinedTableNameOriginal);
			if( !joinedTableNameOriginal.equalsIgnoreCase(joinedTableName) ) {
				from.append(" AS ").append(joinedTableName);
			}
			from.append(" ON ");
			from.append(masterTableName).append('.').append(j.getMasterField().getName());
			from.append(" = ");
			from.append(joinedTableName).append('.').append(j.getJoinedField().getName());
		}
		return from.append(Utils.NewLine()).toString();
	}
	
//	public void prepareValueFilters() {
//		ArrayList<Filter> toRemove  = new ArrayList<Filter>();
//		for( Filter f: Filters ) {
//			 if( f.getSqlValueFilters().size() == 0 ) {
//				 toRemove.add(f);
//			 }
//		}
//		Filters.removeAll(toRemove);
//	}

	
	private String getSqlWhere() {
		StringBuilder where = new StringBuilder("WHERE 1=1");
//		prepareValueFilters();
		
		for( Filter f: getFilters() ) {
			if( f.getConnectAND() ) {
				where.append(" AND ");
			} else {
				where.append(" OR ");
			}
			
			StringBuilder fieldName = new StringBuilder();
			fieldName.append(Joins.getSelectTableName(f.getField())).append('.').append(f.getField().getName());
			
			where.append(fieldName);
			where.append(f.getSqlOperator());
			if( f.isMultiValueFilter() ) {
				boolean first = true;
				where.append("(");
				for(Object value: f.getValueFilters() ) {
					if( !first ) {
						where.append(SEPARATOR);
					} else {
						first = false;
					}
					where.append(getSqlValue(value));
				}
				where.append(")");
			} else {
			    if( Utils.isNull(f.getValueFilters()) ) {
			        where.append(getSqlValue(null));
			    } else {
			        where.append(getSqlValue(f.getValueFilters()[0]));
			    }
			}
		}
		return where.append(Utils.NewLine()).toString();
	}
	
	private String getSqlGroupBy() {
		StringBuilder sql = new StringBuilder();
		if( hasAggregations() ) {
			boolean first = true;
			for( Aggregation af: Aggregations ) {
				if( af.getAggFunction() == AggFunction.GROUPBY ) {
					if( !first ) {
						sql.append(SEPARATOR);
					}
					sql.append(af.getField().getFullName());
					first = false;
				}
			}
			if( sql.length() > 0 ) {
				sql.insert(0, "GROUP BY ");
				sql.append(Utils.NewLine());
			}
		}
		return sql.toString();
	}
	
	private String getSqlOrderBy() {
		StringBuilder sql = new StringBuilder();
		if( hasOrderBy() ) {
			boolean first = true;
			for( OrderBy ob: OrderBys ) {
				for( Select s: Select ) {
					if( s.getField() == ob.getField() ) {
						if( !first ) {
							sql.append(SEPARATOR);
						}
						sql.append(Select.getColumnIndex(s));
						if( !ob.isAscendent() ) {
							sql.append(" DESC");
						}
						first = false;
					}
				}
			}
			if( sql.length() > 0 ) {
				sql.insert(0, "ORDER BY ");
				sql.append(Utils.NewLine());
			}
		}
		return sql.toString();
	}
	
	protected String getSqlRead() {
		StringBuilder sql = new StringBuilder();
					
		String sqlSelect = getSqlSelect();
		String sqlFrom = getSqlFrom();
		String sqlWhere = getSqlWhere();
		String sqlGroupBy = getSqlGroupBy();
		String sqlOrderBy = getSqlOrderBy();
		
		sql.append(sqlSelect);
		sql.append(sqlFrom);
		sql.append(sqlWhere);
		sql.append(sqlGroupBy);
		sql.append(sqlOrderBy);
		
		return sql.toString();
	}

	private void prepareUpdateValues() {
		UpdateValues.clear();
		ArrayList<DBField> fields = masterTable.getFields();
		for( DBField f: fields ) {
			if( f.isModified() ) {
				UpdateValues.add(new UpdateValues(f, f.getValue()));
//				f.clearModfied();
			}
		}
		
	}
	
	protected String getSqlUpdate() {
		prepareUpdateValues();
		if( UpdateValues.size() > 0 ) {
			StringBuilder sql = new StringBuilder("UPDATE ");
			sql.append(masterTable.getName());
			sql.append("\nSET ");
			boolean first = true;
			for( UpdateValues column: UpdateValues ) {
				if( !first ) {
					sql.append(SEPARATOR);
				}
				sql.append(column.getSqlField()).append(" = ").append(getSqlValue(column.getValue()));
				first = false;
			}
			sql.append(Utils.NewLine());
			int id = masterTable.getId();
			if( id > 0 ) {
				sql.append("WHERE ").append(masterTable.getIdField().getName()).append(" = ").append(id);
			} else {
				String sqlWhere = getSqlWhere();
				sql.append(sqlWhere);
			}
			return sql.toString();
		} else {
			return "select 'no update values'";
		}
	}

	protected String getSqlDelete() {
		StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(masterTable.getName());
		sql.append(Utils.NewLine());
		int id = masterTable.getId();
		if( id > 0 ) {
			sql.append("WHERE ").append(masterTable.getIdField().getName()).append(" = ").append(id);
		} else {
			String sqlWhere = getSqlWhere();
			sql.append(sqlWhere);
		}
		
		return sql.toString();
	}

	public String getSqlValue(Object value) {
		String sqlValue = null;
        if (value instanceof java.sql.Date) {
        	sqlValue = String.valueOf(((java.sql.Date) value).getTime());
        } else if (value instanceof java.util.Date) {
        	sqlValue = String.valueOf(((java.util.Date) value).getTime());
        } else if (value instanceof Boolean) {
        	sqlValue = ((Boolean) value) ? "1" : "0";
        } else if (value instanceof Integer) {
        	sqlValue = Integer.toString((Integer) value);
        } else if (value instanceof Double) {
        	sqlValue = Double.toString((Double) value);
        } else if (value instanceof String) {
        	String newValue = ((String) value).replaceAll("'", "''");
        	sqlValue = "\'" + newValue.trim() + "\'";
        } else if ( value instanceof EnumInteger ) {
        	sqlValue = Integer.toString(((EnumInteger) value).getInteger());
        } else if ( value instanceof Long ) {
        	sqlValue = Long.toString(((Long) value));
        } else {
        	sqlValue = "null";
        }
        return sqlValue;
	}
	
}
