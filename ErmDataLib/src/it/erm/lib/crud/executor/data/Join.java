package it.erm.lib.crud.executor.data;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

public class Join {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(joinType != null ? joinType.name():"No joinType");
		sb.append(", ");
		sb.append(masterField != null ? masterField.getFullName():"No masterField");
		sb.append(", ");
		sb.append(joinedField != null ? joinedField.getFullName():"No joinedField");
		sb.append(", ");
		sb.append(joinTableName != null ? joinTableName:"No joinTableName");
		sb.append(")");
		return sb.toString();
	}
	
	public enum JoinType {
		LEFT, LEFT_OUTER
	}
	
	private JoinType joinType      = JoinType.LEFT;
	private DBField  masterField   = null;
	private DBField  joinedField   = null;
	private String   joinTableName = null;
	
	public Join(JoinType JT, DBField master, DBField joined) {
		joinType = JT;
		masterField = master;
		joinedField = joined;
	}
	
	public JoinType getJoinType() {
		return joinType;
	}
	
	public DBField getMasterField() {
		return masterField;
	}
	
	public DBTable getMasterTable() {
		return masterField.getTableOwner();
	}
	
	public DBField getJoinedField() {
		return joinedField;
	}
	
	public DBTable getJoinedTable() {
		return joinedField.getTableOwner();
	}
	
	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}
	
	public String getJoinTableName() {
		if( joinTableName == null ) {
			return getJoinedTable().getName();
		}
		return joinTableName;
	}
}
