package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbo.formatter.EnumFormatter;
import it.erm.lib.utils.Utils;


public class DBFieldEnum<T extends EnumInteger> extends DBField {

	Class<T> enumClass;
	
	public DBFieldEnum(DBTable tableOwner, Class<T> enumClass, String name, Boolean canBeNull, Boolean indexUnique) {
		super(tableOwner, name, JDBCType.INTEGER, canBeNull);
		if( enumClass.isEnum() ) {
			this.enumClass = enumClass;
		} else {
			System.out.println("La classe " + enumClass.getName() + " deve essere un enum");
		}
		setDisplayFormatter(new EnumFormatter<T>());
	}

	
	@Override
	public void setDefaultValue(Object value) {
		super.setDefaultValue((T)value);
	}
	
	@Override
	public void setValue(Object value) {
		if( value == null ) {
			super.setValue(null);
		} else if( value instanceof EnumInteger ) {
	        super.setValue(((EnumInteger)value).getInteger());
	    } else {
	    	super.setValue(value);
	    }
	}
	
	public void setDefaultEnum(T e) {
		setDefaultValue(e);
	}

	public T getDefaultEnum() {
		T e = EnumFormatter.getFromInt((Integer)getDefaultValue(), enumClass);
		if( e != null )
			return e;
		return null;
	}
	
	public void setEnum(T e) {
	    if( e != null ) {
	        super.setValue(e.getInteger());
	    }
	}
	
	public T getPreviousEnum() {
	    if( Utils.isNull(getPreviousValue()) )
	        return null;
	    T e = EnumFormatter.getFromInt((Integer)getPreviousValue(), enumClass);
	    if( e != null )
	        return e;
	    return null;
	}

	
	public T getEnum() {
		if( isNull() )
			return null;
		Object v = getValue();
		T e;
		if( v instanceof Integer ) {
		    e = EnumFormatter.getFromInt((Integer)getValue(), enumClass);
		} else {
		    e = (T) v;
		}
		if( e != null )
			return e;
		return null;
	}

	public void addFilterEnum(T filterEnum) {
		super.addFilter(filterEnum.getInteger());
	}

	public Class<T> getEnumClass() {
		return enumClass;
	}

}
