package it.erm.lib.crud.executor.data;

import it.erm.lib.crud.dbo.DBField;

public class Filter {

    public enum Operator {
    	/**
    	 * corrisponde a = se deve essere filtrato un valore singolo<br>
    	 * corrisponde a IN se devono essere filtrati pi� valori
    	 */
        EQUAL,
        /**
    	 * corrisponde a <> se deve essere filtrato un valore singolo<br>
    	 * corrisponde a NOT IN se devono essere filtrati pi� valori
         */
        NOT_EQUAL,
        MINOR,
        MINOR_EQUAL,
        GREATER,
        GREATER_EQUAL,
        LIKE,
        NOT_LIKE
    }
	
    private Operator               op                      = Operator.EQUAL;
    private boolean                connectAND              = true;
    private DBField                field                   = null;
    private Object[]               valueFilters            = null;
//    private ArrayList<String>      sqlValueFilters         = null;
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	if( op != null ) { sb.append(op.name()); } else { sb.append("op = null "); }
    	if( connectAND ) { sb.append(" and "); } else { sb.append(" or "); }
    	if(field != null) { sb.append(field.getName()); } else { sb.append("filed = null "); }
    	if(valueFilters != null) { sb.append(valueFilters); } else { sb.append("valueFilters = null "); }
    	return sb.toString();
    }
    
    public Filter(Operator Op, boolean and, DBField f, Object... filters) {
    	op = Op;
    	connectAND = and;
    	field = f;
    	valueFilters = filters;
	}
    
    public void setOp(Operator op) {
		this.op = op;
	}
    
    public Operator getOperator() {
		return op;
	}
    
    public boolean getConnectAND() {
    	return connectAND;
	}
    
    public DBField getField() {
		return field;
	}

    public Object[] getValueFilters() {
		return valueFilters;
	}

    public boolean isMultiValueFilter() {
    	return valueFilters != null && valueFilters.length > 1;
    }
    
    public String getSqlOperator() {
    	String sqlOp = null;
    	boolean multiValueFilters = isMultiValueFilter();
    	switch (op) {
		case EQUAL:
			if( multiValueFilters ) {
				sqlOp = " IN ";
			} else {
				if( valueFilters == null || valueFilters.length == 0 || valueFilters[0] == null ) {
					sqlOp = " is ";
				} else {
					sqlOp = " = ";
				}
			}
			break;
		case NOT_EQUAL:
			if( multiValueFilters ) {
				sqlOp = " NOT IN ";
			} else {
				if( valueFilters == null || valueFilters.length == 0 || valueFilters[0] == null ) {
					sqlOp = " is not ";
				} else {
					sqlOp = " <> ";
				}
			}
			break;
		case LIKE:
			sqlOp = " LIKE ";
			break;
		case NOT_LIKE:
			sqlOp = " NOT LIKE ";
			break;
		case GREATER:
			sqlOp = " > ";
			break;
		case GREATER_EQUAL:
			sqlOp = " >= ";				
			break;
		case MINOR:
			sqlOp = " < ";
			break;
		case MINOR_EQUAL:
			sqlOp = " <= ";
			break;
		default:
			break;
		}
		
    	return sqlOp;
    }
    
    
}
