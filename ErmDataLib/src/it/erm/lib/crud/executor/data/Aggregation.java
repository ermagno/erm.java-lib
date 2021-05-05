package it.erm.lib.crud.executor.data;

import it.erm.lib.crud.dbo.DBField;

public class Aggregation {
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(aggFunction != null ? aggFunction.name():"No aggFunction");
		sb.append(", ");
		sb.append(field != null ? field.getFullName():"No Field");
		sb.append(")");
		return sb.toString();
	}
	
    static public enum AggFunction {
        NONE,
        MAX,
        MIN,
        SUM,
        COUNT,
        COUNT_ALL,
        COUNT_DISTINCT,
        DISTINCT,
        GROUPBY
    };
    
    private AggFunction aggFunction = AggFunction.NONE;
    private DBField field = null;
    
    public Aggregation(AggFunction AFunction, DBField f) {
    	field = f;
    	aggFunction = AFunction;
	}
    
    public AggFunction getAggFunction() {
		return aggFunction;
	}
    
    public DBField getField() {
		return field;
	}
}
