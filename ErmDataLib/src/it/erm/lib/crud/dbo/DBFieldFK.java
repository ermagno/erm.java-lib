package it.erm.lib.crud.dbo;

import it.erm.lib.crud.DBFieldKey;
import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbm.TableManager;
import it.erm.lib.crud.dbo.formatter.ForeignKeyFormatter;




public class DBFieldFK<F extends DBTable> extends DBFieldKey {

	private Class<F> foreignTableClass = null;
	private F foreignTable = null;
	
	public DBFieldFK(DBTable tableOwner, Class<F> tableClass, String suffisso, boolean canBeNull) {
		super(tableOwner, formattedNameField(tableClass, suffisso), JDBCType.INTEGER, canBeNull);
		foreignTableClass = tableClass;
		setDisplayFormatter(new ForeignKeyFormatter());
	}
	
	private static String formattedNameField(Class<?> tableClass, String suffisso) {
	    String fieldName = "id_";
	    String simpleName = tableClass.getSimpleName();
	    if( simpleName.substring(0, 2).equalsIgnoreCase("db") )
	        simpleName = simpleName.substring(2, simpleName.length());
	    fieldName += simpleName;
	    if( suffisso != null && !suffisso.trim().equals("") ) {
	        fieldName += "_" + suffisso;
	    }
	    return fieldName.toLowerCase();
	}
	
	
	public F getForeignKeyTable() {
		if( foreignTable == null ) try {
			this.foreignTable = foreignTableClass.newInstance();
		} catch (Exception e) {
			System.out.println("Errore nella creazione della tabella puntata dalla chiave esterna: " + getName() + " -> " + foreignTableClass.getName());
		}
		return this.foreignTable;
	}

    public F getFKTableOpened() {
//        F foreignKeyTable = getForeignKeyTable();
//        foreignKeyTable.openKey(getInteger());
    	F foreignKeyTable = TableManager.get(foreignTableClass, getInteger());
        return foreignKeyTable;
    }

	
	@Override
	public boolean isForeignKey() {
	    return true;
	}

}
