package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbo.formatter.DisplayFormatter;
import it.erm.lib.crud.executor.data.*;
import it.erm.lib.crud.executor.data.Aggregation.AggFunction;
import it.erm.lib.crud.executor.data.Filter.Operator;
import it.erm.lib.crud.executor.data.Join.JoinType;
import it.erm.lib.utils.Utils;


public class DBField {

//	public enum TipoCampo implements EnumInteger {
//		INT(0),
//		DOUBLE(1),
//		/**
//		 * internamente e' un long
//		 */
//		DATETIME(2),
//		BOOLEAN(3),
//		STRING(4);
//		
//		private int i = 0;
//		
//		private TipoCampo(int i) {
//            this.i = i;
//        }
//		
//        @Override
//        public int getInteger() {
//            return i;
//        }
//
//        @Override
//        public String getDisplayValue() {
//            return EnumFormatter.getConversion(TipoCampo.class, this);
//        }
//		
//        @Override
//        public void setInteger(int i) {
//            this.i = i;
//        }
//	}
	
	
	private DBTable tableOwner = null;
	
	private String name = null;
//	private String selectName = null;

	private JDBCType jdbcType = null;
	private boolean canBeNull = true;
//	private String indexName = null;
//	private String uniqueIndexName = null;
	private boolean dbLoaded = false;
	private Object previousValue = null;
	private Object value = null;
	private Object defaultValue = null;
//	private List<DBFieldFilter> filters = null;
	
//	private boolean ascending = true;
//	private LinkedHashMap<Integer, Integer> resource = new LinkedHashMap<Integer, Integer>();
	
	private DisplayFormatter displayFormatter = null;
	
//	private AggFunction aggFunction = AggFunction.NONE;
	
//	protected boolean isCalculated = false;
	
//	private ArrayList<DBFieldListener> listners = new ArrayList<DBFieldListener>();
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if( name != null ) s.append("\ncampo: ").append(getFullName()).append("\n"); else s.append("");
		if( jdbcType != null ) s.append("tipo: ").append(jdbcType).append("\n"); else s.append("");
		
		if( previousValue != null ) s.append("Previous value: ").append(previousValue).append("\n"); else s.append("");
		if( value != null ) s.append("VALUE: ").append(value).append("\n"); else s.append("");
		if( defaultValue != null ) s.append("DefValue: ").append(defaultValue).append("\n"); else s.append("");
//		filters != null ) s.append("FiltersValue: ").append(Utils.fromArray(filters.toArray())).append("\n"); else s.append("");
		
		if( canBeNull ) s.append(""); else s.append("cannot be null\n");
//		if( indexName != null ) s.append("Index: ").append(indexName).append("\n"); else s.append("" );
//		if( uniqueIndexName != null ) s.append("Unique index: ").append(uniqueIndexName).append("\n"); else s.append("" );
		
		return s.toString();
	}
	
	/**
	 * 
	 * @param tableOwner
	 * @param name
	 * @param tipo
	 * @param canBeNull
	 */
	public DBField(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
		this.tableOwner = tableOwner;
		this.name = name;
//		this.selectName = name;
		this.jdbcType = tipo;
		this.canBeNull = canBeNull;
		if( isCalculated() ) {
			tableOwner.getVirtualFields().add(this);
		} else {
			tableOwner.getPhysicalfields().add(this);
		}
//		if( indexUnique != null ) {
//			String tableOwnerName = tableOwner.getName().toUpperCase();
//			if( indexUnique ) {
//				this.uniqueIndexName = "INDEX_UNIQUE_" + tableOwnerName;
//			}
//			this.indexName = "INDEX_" + tableOwnerName + "_" + name.toUpperCase();
//		}
	}

	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		StringBuilder displayname = new StringBuilder();
		boolean first = true;
		char[] charArray = name.toCharArray();
		int i = 0;
		if( charArray.length > 3 ) {
			if( charArray[0] == 'i' && charArray[1] == 'd' && charArray[2] == '_' )
				i = 3;
		}
		for( ;i<charArray.length; i++) {
			if( Character.isSpaceChar(charArray[i]) || charArray[i] == '_' ) {
				first  = true;
				displayname.append(' ');
				continue;
			}
			if(first) {
				displayname.append(Character.toUpperCase(charArray[i]));
				first = false;
			} else {
				displayname.append(Character.toLowerCase(charArray[i]));
			}
		}
		return displayname.toString();
	}
	
	/**
	 * Solo nel caso di tabelle create dinamicamente in cui due campi hanno lo stesso nome e uno deve essere rinominato
	 * @param name
	 */
	public void setName(String name) {
        this.name = name;
    }
	
	public String getFullName() {
		return tableOwner.getName() + "." + name;
	}
	
//	public void setSelectName(String selectName) {
//		this.selectName = selectName;
//	}
//	
//	public String getSelectName() {
//		return selectName;
//	}
	
	public JDBCType getJDBCType() {
		return jdbcType;
	}
	
	public DBTable getTableOwner() {
		return tableOwner;
	}
	
	public boolean canBeNull() {
		return canBeNull;
	}
	
//	void setIndexName(String indexName) {
//		this.indexName = indexName;
//	}
//	
//	public String getIndexName() {
//		return indexName;
//	}
//	
//	void setUniqueIndexName(String uniqueIndexName) {
//		this.uniqueIndexName = uniqueIndexName;
//	}
//
//	public String getUniqueIndexName() {
//		return uniqueIndexName;
//	}

	public void addFilter(Object... filterValues) {
		tableOwner.addCondition(Operator.EQUAL, true, this, filterValues);
	}
	


//	public void addFieldListners(DBFieldListener fl) {
//		listners.add(fl);
//	}
	
//	public void setDBValue(Object value) {
//		setDbLoaded(true);
//		this.previousValue = value;
//		this.value = value;
//	}
	
	public void setValue(Object value) {
		this.setValue(value, true, true);
	}
	
	public void setValue(Object value, boolean addFilter, boolean loadDBValue) {
		if( !isDbLoaded() && loadDBValue ) {
			tableOwner.getCRUDExecutor().loadFieldValue(this);
		}
//		setPreviousValue(this.value);
		this.value = value;
		if( addFilter ) {
			clearFilters();
			addFilter(value);
		}
	}
	
	public void setNotNull() {
        clearFilters();
        tableOwner.addCondition(Operator.NOT_EQUAL, true, this, null);
	}
	
	public void clearFilters() {
		tableOwner.getCRUD().clearFilters(this);
	}

	private void setPreviousValue(Object previousValue) {
		this.previousValue = previousValue;
	}
	
	public Object getPreviousValue() {
        return previousValue;
    }
	
	public void reset(boolean clearFilters) {
		this.value = null;
		this.previousValue = null;
		this.dbLoaded = false;
		if( clearFilters ) {
			clearFilters();
		}
	}
	
	public void resetModfied() {
		setPreviousValue(getValue());
	}
	
	public Object getValue() {
		if( !isDbLoaded() ) {
			tableOwner.getCRUDExecutor().loadFieldValue(this);
		}
		return value;
	}
	
	public boolean isDbLoaded() {
		return dbLoaded;
	}
	
	public void setDbLoaded(boolean isDbLoaded) {
		this.dbLoaded = isDbLoaded;
	}
	
	public void setString(String value) {
	    setValue(value);
	    Filter filter = tableOwner.getCRUD().getLastFilter(this);
	    if( filter != null ) {
	    	filter.setOp(Operator.LIKE);
	    }
    }
	
	public String getString() {
	    if( isNull() )
	        return "";
	    return value.toString();
	}
	
	public void setInteger(int id) {
        setValue(id);
    }
    
    /**
     * 
     * @return -1 
     */
    public int getPreviousInteger() {
        if( Utils.isNull(getPreviousValue()) )
            return -1;
        if( this instanceof DBFieldEnum ) {
            return ((DBFieldEnum<?>)this).getPreviousEnum().getInteger();
        }
        if( previousValue instanceof Boolean )
            return (((Boolean)previousValue) ? 1:0);
        return (Integer) getPreviousValue();
    }
	
    /**
     * 
     * @return -1 se null 
     */
    public int getInteger() {
        if( isNull() )
            return 0;
        if( this instanceof DBFieldEnum ) {
            return ((DBFieldEnum<?>)this).getEnum().getInteger();
        }
        if( value instanceof Boolean )
            return (((Boolean)value) ? 1:0);
        return (Integer) getValue();
    }

    public void setBoolean(boolean b) {
        setValue(b);
    }
    
    public boolean getBoolean() {
        if( isNull() )
            return false;
        if( this instanceof DBFieldEnum ) {
            return ((DBFieldEnum<?>)this).getEnum().getInteger() > 0;
        }
        if( value instanceof Integer ) {
            return ((Integer)value).intValue() > 0;
        }
        return (Boolean)value;
    }
	
    
    
	public void setDisplayFormatter(DisplayFormatter displayFormatter) {
		this.displayFormatter = displayFormatter;
	}
	
	public DisplayFormatter getDisplayFormatter() {
		return displayFormatter;
	}

	public String getDisplayPreviousValue() {
	    if( Utils.isNull(displayFormatter) ) {
	        if( !Utils.isNull(getPreviousValue()) ) {
	            return getPreviousValue().toString();
	        } else {
	            return "";
	        }
	    } else {
	        return displayFormatter.getDisplayPreviousValue(this);
	    }
	}

	
	public String getDisplayValue() {
		if( Utils.isNull(displayFormatter) ) {
			if( !Utils.isNull(getValue()) ) {
				return getValue().toString();
			} else {
				return "";
			}
		} else {
			return displayFormatter.getDisplayValue(this);
		}
	}
	
	public void setDefaultValue(Object defValue) {
		defaultValue = defValue;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public boolean isNull() {
		if( !isDbLoaded() ) {
			tableOwner.getCRUDExecutor().loadFieldValue(this);
		}
		boolean isNull = Utils.isNull(value);
		if( !isNull && isKey() ) {
		    if( value instanceof Integer ) {
		        isNull = ((Integer)value) <= 0;
		    }
		}
		return isNull;
	}
	
//	/**
//	 * serve per indicare su quale controllo visualizzare il contenuto.<br>
//	 * @param lookup
//	 */
//	public void setLookupResource(int resourceParent, int resource) {
//		this.resource.put(resourceParent, resource);
//
//	}
//	
//	public LinkedHashMap<Integer, Integer> getLookupResource() {
//		return this.resource;
//	}
//
//	public Integer getLookupResource(int resourceParent) {
//		return this.resource.get(resourceParent);
//	}

	public boolean isCalculated() {
		return this instanceof DBFieldCalculated;
	};
	
    public boolean isPrimaryKey() {
        return false;
    }

	public boolean isForeignKey() {
	    return false;
    }

	public boolean isKey() {
	    return isPrimaryKey() || isForeignKey();
	}
	
	public boolean isModified() {
	    if( !isDbLoaded() ) {
	        tableOwner.getCRUDExecutor().loadFieldValue(this);
	    }
	    Object vp = previousValue;
	    Object v  = value;
	    if( isKey() ) {
	        if( !Utils.isNull(vp) && (Integer)vp <= 0 ) {
	            vp = null;
	        }
	        if( !Utils.isNull(v) && (Integer)v <= 0 ) {
	            v = null;
	        }
	    }
	    
	    boolean modified = vp != v;
		if( modified ) {
		    boolean toCompare = !Utils.isNull(vp) && !Utils.isNull(v);
		    if( toCompare )
		        modified = !vp.equals(v);
		    else
		        modified = !(Utils.isNull(vp) && Utils.isNull(v));
		}
		return modified;
	}
	
	public void joinLeft(DBField f) {
		tableOwner.getCRUD().addJoin(new Join(JoinType.LEFT, this, f));
	}

	public void joinLeftOuter(DBField f) {
		tableOwner.getCRUD().addJoin(new Join(JoinType.LEFT_OUTER, this, f));
	}

	public void orderBy(boolean asc) {
		tableOwner.getCRUD().addOrderBy(new OrderBy(this, asc));
	}
	
	public void aggregation(AggFunction AggFun) {
		tableOwner.getCRUD().addAggregation(new Aggregation(AggFun, this));
	}
}
