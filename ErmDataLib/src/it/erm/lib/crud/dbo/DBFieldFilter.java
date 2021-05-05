package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;



public class DBFieldFilter {

	public static final DBFieldFilter NOT_NULL_FILTER = new DBFieldFilter(true, new Object());
	public static final DBFieldFilter NULL_FILTER = new DBFieldFilter(true, Void.TYPE);
	
	private boolean include = true;
	protected Object[] filterValues;
	
	public DBFieldFilter(boolean include, Object... filterValues) {
		this.include = include;
		this.filterValues = filterValues;
	}
	
	public boolean include() {
		return include;
	}
	
	public Object[] getFilterValues() {
		return filterValues;
	}
	
    private String[] getFilterValuesFormatted() {
        String args[] = new String[filterValues.length];
        int argIndex = 0;
        for (Object value : filterValues) {
            if (value instanceof java.sql.Date) {
                args[argIndex] = String.valueOf(((java.sql.Date) value).getTime());
            } else if (value instanceof java.util.Date) {
                args[argIndex] = String.valueOf(((java.util.Date) value).getTime());
            } else if (value instanceof Boolean) {
                args[argIndex] = ((Boolean) value) ? "1" : "0";
            } else if (value instanceof Integer) {
                args[argIndex] = Integer.toString((Integer) value);
            } else if (value instanceof Double) {
                args[argIndex] = Double.toString((Double) value);
            } else if (value instanceof String) {
                args[argIndex] = "\"" + value.toString() + "\"";
            } else if ( value instanceof EnumInteger ){
                args[argIndex] = Integer.toString(((EnumInteger) value).getInteger());
            } else {
                args[argIndex] = "null";
            }
            argIndex++;
        }
        return args;
    }
	
    public static String getValueFormatted(Object value) {
        if (value instanceof java.sql.Date) {
            return String.valueOf(((java.sql.Date) value).getTime());
        } else if (value instanceof java.util.Date) {
            return String.valueOf(((java.util.Date) value).getTime());
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "1" : "0";
        } else if (value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if (value instanceof Double) {
            return Double.toString((Double) value);
        } else if (value instanceof String) {
            return "\"" + value.toString() + "\"";
        } else if ( value instanceof EnumInteger ){
            return Integer.toString(((EnumInteger) value).getInteger());
        } else {
            return "null";
        }
    }
    
	public boolean hasFilters(boolean excludeNull) {
		if( (filterValues == null || filterValues.length == 0) )
			return false;
		if( excludeNull ) {
			if( this == NULL_FILTER )
				return false;
			if( this == NOT_NULL_FILTER )
				return false;
			return true;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		return (include?"IN ":"NOT IN ") + (filterValues == null ? "{}" : "{" + Utils.fromArray(", ", filterValues))+ "}";
	}
	
	/**
	 * build a string that must be placed after the field name<br>
	 * <ul>cases:
	 * <li>nome_campo IS NULL
	 * <li>nome_campo IS NOT NULL
	 * <li>nome_campo = ?
	 * <li>nome_campo <> ?
	 * <li>nome_campo IN(?,?)
	 * <li>nome_campo NOT IN(?,?)
	 * </ul>
	 * @param tipoCampo 
	 * @return
	 */
	String getWhereClause(JDBCType tipoCampo) {
		if( !hasFilters(false) )
			return null;
		
		if( this == NULL_FILTER )
			return " IS NULL";
		
		if( this == NOT_NULL_FILTER )
			return " IS NOT NULL";
		
		if( getFilterValues().length == 1 ) {
			StringBuilder whereClause = new StringBuilder();
			String filtervalue = getFilterValuesFormatted()[0];
			if( include() ) {
				if( tipoCampo == JDBCType.VARCHAR && filtervalue.indexOf("%") > -1 ) {
					whereClause.append(" like "); 
				} else {
					whereClause.append(" = ");
				}
			} else {
				whereClause.append(" <> ");
			}
			return whereClause.append(filtervalue).toString();
		}
		
		String sql = "";
		
		if( include() ) {
			sql = " IN (";
		} else {
			sql = " NOT IN (";
		}

		String sep = ", ";
		for( Object o: getFilterValues() ) {
//			sql += "?" + sep;
			sql += o + sep;
		}
		return sql.substring(0, sql.lastIndexOf(sep)) + ") ";
	}
	
	/**
	 * @return a List of values on the filter
	 */
	List<String> getWhereArgs() {
		if( !hasFilters(true) )
			return null;
		
		ArrayList<String> args = new ArrayList<String>(filterValues.length);
		for( Object value: filterValues ) {
			if( value instanceof java.sql.Date ) {
				args.add(String.valueOf(((java.sql.Date)value).getTime()));
			} else if( value instanceof java.util.Date ) {
				args.add(String.valueOf(((java.util.Date)value).getTime()));
			} else if( value instanceof Boolean ) {
				args.add(((Boolean)value) ? "1" : "0");
			} else if( value instanceof Integer ) {
				args.add(Integer.toString((Integer)value, 10));
			} else if( value instanceof Double ) {
				args.add(Double.toString((Double)value));
			} else if( value instanceof String ) {
				args.add(value.toString());
			} else {
				args.add("null");
			}
		}
		
		return args;
	}
}
