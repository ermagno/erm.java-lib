package it.erm.lib.crud.dbm;

import it.erm.lib.crud.DBFieldKey;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;
import it.erm.lib.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;

public class TableManager<T extends DBTable> {

//	private static CacheDataManager cacheDataManager;
	private static HashMap<Class<? extends DBTable>, HashMap<Integer, DBTable>> map = new HashMap<Class<? extends DBTable>, HashMap<Integer, DBTable>>();
	
    public static final HashMap<Class<? extends DBTable>, HashMap<Integer, String>>         mapClassToIdToDisplayeable  = new HashMap<Class<? extends DBTable>, HashMap<Integer,String>>();
    
    private static final HashMap<Class<? extends DBTable>, TableManager<? extends DBTable>> mapClassTableCreatorManager = new HashMap<Class<? extends DBTable>, TableManager<? extends DBTable>>();
    
    private Class<T> TableClass = null; 
    
    public static <C extends DBTable> TableManager<C> get(Class<C> tableClass) {
        TableManager<C> createManager = null;
        if( mapClassTableCreatorManager.containsKey(tableClass) ) {
            createManager = (TableManager<C>) mapClassTableCreatorManager.get(tableClass);
        } else try {
            createManager = new TableManager<C>();
            createManager.TableClass = tableClass;
            mapClassTableCreatorManager.put(tableClass, createManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createManager;
    }
    
    public static <C extends DBTable> C getNoCache(Class<C> tableClass, int id) {
		C table = get(tableClass).newTable();
		table.openKey(id);
		return table;
    }
    
    public static <C extends DBTable> C get(Class<C> tableClass, DBFieldKey id) {
        return get(tableClass, id.getInteger());
    }
    
    public static <C extends DBTable> C get(Class<C> tableClass, int id) {
    	C table = null;
    	HashMap<Integer,DBTable> hashMap = map.get(tableClass);
    	if( hashMap == null ) {
    		table = get(tableClass).newTable();
    		if( table.isUseCache() ) {
    			hashMap = new HashMap<Integer, DBTable>();
    			map.put(tableClass, hashMap);
    			hashMap.put(id, table);
    		}
    		table.openKey(id);
    	} else {
    		table = (C) hashMap.get(id);
    		if( table == null ) {
    			table = get(tableClass).newTable();
    			hashMap.put(id, table);
        		table.openKey(id);
    		}
    	}
    	return table;
    }

    public T newTable() {
        try {
            return TableClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearTableCache(Class<? extends DBTable> tableClass, int id) {
        Class<?> tableClassSuper = tableClass.getSuperclass();
        while( tableClassSuper != DBTable.class ) {
            tableClass = (Class<? extends DBTable>) tableClassSuper;
            tableClassSuper = tableClass.getSuperclass();
        }
        
        HashMap<Integer,DBTable> hashMap = map.get(tableClass);
        if( hashMap != null ) {
            if( id > 0 ) {
                hashMap.remove(id);
            } else {
                hashMap.clear();
            }
        }
    }

    public String getDescription(int id) {
        String descrizione = null;
        HashMap<Integer, String> IdToDisplayeable = null;
        if( mapClassToIdToDisplayeable.containsKey(TableClass) ) {
            IdToDisplayeable = mapClassToIdToDisplayeable.get(TableClass);
        } else {
            IdToDisplayeable = new HashMap<Integer, String>();
            mapClassToIdToDisplayeable.put(TableClass, IdToDisplayeable);
        }
        
        if( IdToDisplayeable.get(id) != null ) {
            descrizione = IdToDisplayeable.get(id);
        } else {
            T table = get(TableClass).newTable();
            table.openKey(id);
            descrizione = table.getDisplayFieldsRecord(true);
            IdToDisplayeable.put(id, descrizione);
        }
        return descrizione;
    }
    
    /**
     * se non precisato, copia anche la chiave primaria<br> 
     * <b>se non è esclusa la chiave primaria, una insert sulla tabella <pre>to</pre> darà errore</b> 
     * @param from
     * @param to
     * @param exclude
     */
    public static void copyValues(DBTable from, DBTable to, DBField... exclude) {
        HashSet<String> excludeSet = new HashSet<String>();
        if( !Utils.isNull(exclude) ) {
            for( DBField f: exclude ) {
                excludeSet.add(f.getName());
            }
        }
        
    	for( DBField fFrom: from.getFields() ) {
    	    String name = fFrom.getName();
    	    if( excludeSet.contains(name) ) {
    	        continue;
    	    }
    	    to.getEquivalentField(name).setValue(fFrom.getValue());
    	}
    }
    
}
