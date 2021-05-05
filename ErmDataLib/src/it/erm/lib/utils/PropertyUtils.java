package it.erm.lib.utils;

import it.erm.lib.jmx.JMXManager;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class PropertyUtils {

    public static final String  DATABASE_USER             = "Database.User";
    public static final String  DATABASE_PATH             = "Database.Path";
    public static final String  DATABASE_CLASS            = "Database.DatabaseClass";
    public static final String  APP_LOG_LEVEL             = "App.Log.Level";

    protected void addProperties() {
        addProperty(DATABASE_USER,   "");
        addProperty(DATABASE_PATH,   "");
        addProperty(DATABASE_CLASS,  "it.erm.sqlite.crud.dbm.SQLiteDatabaseItem");
        addProperty(APP_LOG_LEVEL,   "ALL");
    }
    
    private static final String EXTENSION                = ".properties";
    private static Class<? extends PropertyUtils> clazz  = PropertyUtils.class;
    
    protected String DefaultName                         = "startup";
    private String Name;
    private String fileName;
    private static PropertyUtils PROPERTY = null;
    private HashMap<String, String> properties;
    private boolean propertyToAdd = false;
    private Logger  log = null;
    
    public static void initialize(Class<? extends PropertyUtils> c, String propName) {
        LogUtils.logClose();
        JMXManager.close();
        clazz = c;
        PropertyUtils propertyUtils = get();
        propertyUtils.Name = propName;
        if( Utils.isNull(propertyUtils.Name) ) {
            propertyUtils.Name = propertyUtils.DefaultName;
        }
        propertyUtils.fileName = propertyUtils.Name+EXTENSION;
        propertyUtils.properties = new HashMap<String, String>();
        propertyUtils.loadPropertyFile(propertyUtils.fileName);
        LogUtils.setupLogger(propertyUtils.Name);
    }
    
    public static PropertyUtils get() {
        if( PROPERTY == null ) {
            try {
                PROPERTY = clazz.newInstance();
            } catch( Exception e ) {
                // TODO: handle exception
            }
        }
        return PROPERTY;
    }
    
    final public String getFileName() {
        return fileName;
    }
    
    final public void addProperty(String key, String value) {
        String valuePrec = properties.get(key);
        if( Utils.isNull(valuePrec) ) {
            setProperty(key, value);
        }
    }

    final public void setProperty(String key, String value) {
        properties.put(key, value);
        store();
    }

    final public boolean addPropertyListItemValue(String key, String value) {
        boolean result = false;
        if( !Utils.isNull(value) ) {
            if( properties.containsKey(key) ) {
                String valueItems = properties.get(key);
                String[] split = valueItems.split(",");
                if( split.length > 0 ) {
                    HashSet<String> set = new HashSet<String>();
                    StringBuilder list = new StringBuilder();
                    for( String s: split ) {
                        String item = s == null ? "" : s.trim();
                        if( !Utils.isNull(item) ) {
                            if( list.length() > 0 ) {
                                list.append(",");
                            }
                            if( !set.contains(item) ) {
                                list.append(item);
                                set.add(item.toLowerCase());
                            }
                        }
                    }
                    if( list.length() > 0 ) {
                        list.append(",");
                    }
                    if( !set.contains(value.toLowerCase()) ) {
                        list.append(value.trim());
                        value = list.toString();
                        result = true;
                    }
                }
                properties.put(key, value);
                store();
            } else {
                properties.put(key, value);
                store();
            }
        }
        return result;
    }

    final public boolean getBooleanProperty(String param) {
        String prop = getProperty(param);
        boolean parse = Boolean.parseBoolean(prop);
        return parse;
    }

    final public int getIntProperty(String param) {
        String prop = getProperty(param);
        try {
            int parse = Integer.parseInt(prop);
            return parse;
        } catch( Exception e ) {
            return 0;
        }
    }

    
    final public String getProperty(String param) {
        String value = properties.get(param);
        if( Utils.isNull(value) ) {
            return "";
        }
        return value.trim();
    }
    
    final public ArrayList<String> getPropertyList(String param) {
        ArrayList<String> list = new ArrayList<String>();
        String value = properties.get(param);
        if( !Utils.isNull(value) ) {
            String[] split = value.split(",");
            if( split.length > 0 ) {
                for( String s: split ) {
                    list.add(s.trim());
                }
            }
        }
        return list;
    }

    final public boolean isDefault() {
        return fileName.equals(DefaultName+EXTENSION);
    }
    
    private void loadPropertyFile(String fileName) {
        boolean exists = new File(fileName).exists();
        if( exists ) {
            InputStream input   = null;
            try {
                Properties prop = new Properties();
                input = new FileInputStream(fileName);
                prop.load(input);
                for( Object o: prop.keySet() ) {
                    String key = (String) o;
                    String value = prop.getProperty(key);
                    properties.put(key, value);
                }
                addProperties();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close(input);
            }
        } else {
            addProperties();
        }
    }
    
    private void store() {
        OutputStream output = null;
        try {
            output = new FileOutputStream(fileName);
            getFromMap().store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            close(output);
        }
    }
    
    private Properties getFromMap() {
        Properties p = new Properties();
        p.putAll(properties);
        return p;
    }
    
    private static void close(Closeable stream) {
        try {
            stream.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
