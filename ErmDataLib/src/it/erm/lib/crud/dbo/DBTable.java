package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbm.*;
import it.erm.lib.crud.executor.*;
import it.erm.lib.crud.executor.data.*;
import it.erm.lib.crud.executor.data.Filter.Operator;
import it.erm.lib.utils.Utils;

import java.util.ArrayList;




/**
 *
 */
public class DBTable implements Cloneable {

	private CrudData CRUD = null;
	
    private ConnectionType type = ConnectionType.PRIMARY;
    
	private DBFieldPK id;
	private String name;
	
	/**
	 * tutti i campi 'fisici' senza la chiave primaria
	 */
	private ArrayList<DBField> physicalfields = new ArrayList<DBField>();;
	
	
	/**
	 * tutti i campi, compresi la chiave primaria e i campi calcolati
	 */
	private ArrayList<DBField> virtualFields = new ArrayList<DBField>();
	
//	DBField[] lookupResourceFields = null;
	
	private ArrayList<DBField> displayFields = null;
	
	public String LastSql = null;

	protected ArrayList<DBTableListener> listener = new ArrayList<DBTableListener>();
	
	protected boolean useCache = false;
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("table: ").append(name).append("\n").append("id: ").append(id.getInteger());
		return s.toString();
	}
	
	/**
	 * i campi vengono aggiunti alla tabella con la {@link #setFields(DBField...)}
	 * @param name
	 * @param autoPrimaryKey TODO
	 */
	public DBTable(String name, boolean autoPrimaryKey) {
		this.name = name;
		this.CRUD = getDatabase().createCrudData(this);
		String idName = getDatabase().formatIdName(this);
		if( autoPrimaryKey ) {
			id = new DBFieldPK(this, idName, JDBCType.INTEGER, false);
		}
	}
	
	/**
	 * il metodo è pubblico per creare tabelle dinamicamente
	 * @param fields
	 */
	public void setFields(DBField... fields) {
//	    //verifico se la tabella non ne estende un'altra
//	    if( this.fields != null && this.fields.length > 0 ) {
//	        DBField[] fieldsMore = new DBField[fields.length+this.fields.length];
//	        for( int i=0; i<this.fields.length; i++ ) {
//	            fieldsMore[i] = this.fields[i];
//	        }
//	        
//	        for( int i=0; i<fields.length; i++ ) {
//	            fieldsMore[this.fields.length+i] = fields[i];
//	        }
//	        this.fields = fieldsMore;
//	    } else {
//	        this.fields = fields;
//	    }
//	    
//		allFields = new DBField[this.fields.length+1];
//		allFields[0] = id;
//		for( int i=0; i<this.fields.length; i++ )
//			allFields[i+1] = this.fields[i];
//		
//		HashSet<String> names = new HashSet<String>();
//		int i = 0;
//		for( DBField f: fields ) {
//		    if( !names.add(f.getName()) ) {
//		        String oldName = f.getName();
//		        f.setName(f.getName() + i);
//		        String newName = f.getName();
//		        System.err.println("campo " + oldName + " già presente nella tabella " + name);
//		        System.err.println("campo " + oldName + " rinominato in " + newName);
//		    }
//		    i++;
//		}
	}
	
	public ArrayList<DBField> getAllFields() {
		ArrayList<DBField> allFields = new ArrayList<DBField>(physicalfields.size() + virtualFields.size());
		allFields.addAll(physicalfields);
		allFields.addAll(virtualFields);
		return allFields;
	}
	
	public SelectList getSelectList() {
		return getCRUD().getSelectFields();
	}
	
	public ArrayList<DBField> getPhysicalfields() {
		return physicalfields;
	}
	
	public ArrayList<DBField> getVirtualFields() {
		return virtualFields;
	}
	
//	protected void setIndexFields(String indexName, DBField... fs) {
//		for( DBField f: fs ) {
//			f.setIndexName(indexName);
//		}
//	}
//	
//	protected void setUniqueIndexFields(String indexName, DBField... fs) {
//		for( DBField f: fs ) {
//			f.setUniqueIndexName(indexName);
//		}
//	}
	
	public DBField getIdField() {
		return id;
	}
	
//	public DBField getFieldFromIndex(int index) {
//	    return fields[index];
//	}
	
	/**
	 * @return tutti i campi 'fisici' senza la chiave primaria
	 */
	public ArrayList<DBField> getFields() {
        return physicalfields;
    }
	
	/**
	 * reucpera il valore della chiave primaria
	 * @return 
	 */
	public int getId() {
		return getIdField().getInteger();
	}
	
	public boolean isLoaded() {
		return getId() > 0;
	}
	
	public String getName() {
		return name;
	}

	public CrudExecutor getCRUDExecutor() {
	    DatabaseItem db  = getDatabase();
	    ConnectionItem connection = db.getConnection(type);
	    CrudExecutor crudExecutor = connection.getCRUDExecutor();
	    return crudExecutor;
	}
	
	public int insert(Validation v) {
	    doBeforeInsert();
	    if( Utils.isNull(v) ) {
	        v = validateInsert();
	        if( !v.isValid() ) {
	            System.out.println(v.getMsgAsString(", "));
	        }
	    }
	    if( v.isValid() ) {
	        initDefaultValues();
	        int create = getCRUDExecutor().create(this);
	        doAfterInsert();
	        return create;
	    }
	    return -1;
	}
	
	public void initDefaultValues() {
		for( DBField f: physicalfields ) {
			if( f.isNull() && f.canBeNull() && f.isModified()  )
				continue;
			if( !f.isNull() && !f.isModified()  )
				continue;
			if( f.getValue() == null && f.getDefaultValue() != null ) {
			    f.setValue(f.getDefaultValue());
			} else if( f.getJDBCType() == JDBCType.BOOLEAN && f.isNull() ) {
			    f.setBoolean(false);
			}
		}
	}
	
	protected void doBeforeInsert() {}
	
	public Validation validateInsert() {
		Validation v = new Validation();
		ArrayList<DBField> dbFields = getFields();
		for( int i=0; i<dbFields.size(); i++ ) {
		    DBField f = dbFields.get(i);
			if( f == id ) {
				continue;
			}
			if( !f.canBeNull() ) {
				boolean isNull = f.isNull();
				if( !isNull && f.getJDBCType() == JDBCType.VARCHAR ) {
					isNull = f.getString().isEmpty();
				}
				if( isNull ) {
				    Object defaultValue = f.getDefaultValue();
				    if( Utils.isNull(defaultValue) ) {
				        v.setValid(false);
				        v.addMsg("Il campo ", f.getName(), " non può essere null");
				    }
				}
			}
		}
		
		return v;
	}
	    
	protected void doAfterInsert() {}
	    
	/**
	 * @return numero l'id eliminato
	 */
	public int delete(Validation v) {
	    doBeforeDelete();
	    if( Utils.isNull(v) ) {
	        v = validateDelete();
	    }
	    if( v.isValid() ) {
	        if( isUseCache() ) {
	            TableManager.clearTableCache(getClass(), getId());
	        }
	        int delete = getCRUDExecutor().delete(this);
	        reset();
	        doAfterDelete(getId());
	        return delete;
	    }
	    return 0;
	}
	
	
	protected void doBeforeDelete() {}
	
	public Validation validateDelete() {
	    return new Validation();
	}
	
	protected void doAfterDelete(int count) {}
	
	
	/**
	 * @param v TODO
	 * @return numero di righe aggiornate
	 */
	public int update(Validation v) {
	    doBeforeUpdate();
	    if( Utils.isNull(v) ) {
	        v = validateUpdate();
	        if( !v.isValid() ) {
	            System.out.println(v.getMsgAsString(", "));
	        }
	    }
        if( v.isValid() ) {
            if( isUseCache() ) {
                TableManager.clearTableCache(getClass(), getId());
            }
            int update = getCRUDExecutor().update(this);
            doAfterUpdate();
            return update;
        }
        return -1;
	}
	
	protected void doBeforeUpdate() {}

	public Validation validateUpdate() {
		Validation v = new Validation();
		ArrayList<DBField> dbFields = getFields();
		for( int i=0; i<dbFields.size(); i++ ) {
			if( !dbFields.get(i).canBeNull() ) {
				boolean isNull = dbFields.get(i).isNull();
				if( !isNull && dbFields.get(i).getJDBCType() == JDBCType.VARCHAR ) {
					isNull = dbFields.get(i).getString().isEmpty();
				}
				if( isNull ) {
					v.setValid(false);
					v.addMsg("Il campo ", dbFields.get(i).getName(), " non può essere null");
				}
			}
		}
		return v;
	}

	protected void doAfterUpdate() {}
	
	public synchronized int getCount() {
	    int count = getCRUDExecutor().getCount(this);
	    getCRUDExecutor().close(this);
	    return count;
	}
	
	public boolean openfind() {
		return getCRUDExecutor().executeQuery(this);
	}
	
	public boolean first() {
		return getCRUDExecutor().first(this);
	}
	
	public boolean hasNext() {
		return getCRUDExecutor().hasNext(this);
	}
	
	
	
	public boolean next() {
	    return getCRUDExecutor().next(this);
	}

	public boolean openKey(int idKey) {
		reset();
		boolean openKey = false;
		if( idKey > 0 ) {
			openKey = getCRUDExecutor().openKey(this, idKey);
			for( DBTableListener tl: listener ) {
				tl.onOpenKey(idKey);
			}
		}
	    return openKey;
	}
	
	@Override
	public Object clone() {
		DBTable result = TableManager.get(this.getClass()).newTable();
		for( int i=0; i<getAllFields().size(); i++ ) {
			result.getAllFields().get(i).setValue(getAllFields().get(i).getValue());
		}
		return result;
	}
	
	public void close() {
	    getCRUDExecutor().close(this);
	}
	
//	/**
//	 * aggiungo ogni campo tante volte quante sono agganciate
//	 * @param force
//	 * @return
//	 */
//	public DBField[] getResourceLookupFields(boolean force) {
//		if( force || lookupResourceFields == null ) {
//			ArrayList<DBField> lf = new ArrayList<DBField>();
//			for( DBField f: allFields ) {
//				if( f.getLookupResource().size() > 0 ) {
//					lf.add(f);
//				}
//			}
//			lookupResourceFields = lf.toArray(new DBField[lf.size()]);
//		}
//		return lookupResourceFields;
//	}
	
	public void reset() {
		for( DBField f: getAllFields() ) {
			f.reset(true);
		}
		getCRUD().reset();
		for( DBTable t: getCRUD().getJoinedTables() ) {
			t.reset();
		}
		for( DBTableListener tl: listener ) {
			tl.onReset();
		}
	}
	
	public void clearFilters() {
		getCRUD().clearFilters();
	}
	
	public DBField getEquivalentField(String fieldName) {
		for( DBField f: getAllFields() ) {
			if( f.getName().equals(fieldName) )
				return f;
		}
		return null;
	}
	
	/**
	 * @param from
	 * @param clearAnCopy 
	 */
	public void copyFiltersFromTable(DBTable from, boolean clearAnCopy) {
		if( !name.equals(from.getName()) )
			return;
		
		getCRUD().copyFilters(from.getCRUD(), clearAnCopy);
	}


	public void setDisplayFields(DBField... displayFields) {
		if( displayFields == null || displayFields.length == 0 )
			return;
		ArrayList<DBField> df = new ArrayList<DBField>(displayFields.length);
		int i = 0;
		for( DBField f: displayFields ) {
			df.add(getEquivalentField(f.getName()));
			i++;
		}
		this.displayFields = df;
	}
	
	public ArrayList<DBField> getDisplayFields() {
		if( displayFields == null ) {
			return getAllFields();
		}
		return displayFields;
	}
	
	public String getDisplayFieldsRecord(boolean ignoreKeyFields) {
	    ArrayList<DBField> dfo = null;
		if( displayFields == null || displayFields.size() == 0 ) {
		    if( physicalfields != null && 	physicalfields.size() > 0 ) {
                dfo = physicalfields;
		    } else {
		        return "";
		    }
		} else {
		    dfo = getDisplayFields();
		}
	    if( ignoreKeyFields ) {
	        ArrayList<DBField> df = new ArrayList<DBField>();
	        for( DBField f: dfo ) {
	            if( !f.isForeignKey() ) {
	                df.add(f);
	            }
	        }
	        dfo = df;
	    }
		return formatFields(dfo);
	}
	
	private String formatFields(ArrayList<DBField> fields) {
        StringBuilder df = new StringBuilder();
        String sep = " / ";
        for( DBField f: fields ) {
            if( df.length() > 0 ) {
                df.append(sep);
            }
            df.append(f.getDisplayValue());
        }
        return df.toString();

	}
	
	public String getDescription(int id) {
	    return TableManager.get(this.getClass()).getDescription(id);
	}
	
	/**
	 * @return
	 */
	public DatabaseItem getDatabase() {
		return DatabaseManager.get(getClass());
	}
	
	public void addTableListener(DBTableListener tl) {
		listener.add(tl);
	}
	
	public void addCondition(Operator Op, boolean and, DBField f, Object... filters) {
		getCRUD().addFilter(new Filter(Op, and, f, filters));
	}
	
	public CrudData getCRUD() {
		return CRUD;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public boolean isUseCache() {
		return useCache;
	}
	
	public boolean isModified(Validation v) {
	    if( v == null ) {
	        v = new Validation();
	    }
	    for( DBField f: getFields() ) {
	        if( f.isModified() ) {
	            String before = f.getDisplayPreviousValue();
	            String after  = f.getDisplayValue();
	            if( Utils.isNull(before) ) {
	                before = "prima vuoto";
	            } else {
	                before = Utils.fromArray("", "prima: ", before);
	            }
	            if( Utils.isNull(after) ) {
	                after = "poi vuoto";
	            } else {
	                after = Utils.fromArray("", "poi: ", after);
	            }
	            
	            v.addMsg(false, "Campo ", f.getDisplayName(), " modificato (", before, ", ", after, ")");
	        }
	    }
	    return !v.isValid();
	}
}
