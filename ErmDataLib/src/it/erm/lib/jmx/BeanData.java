package it.erm.lib.jmx;

import it.erm.lib.utils.Utils;

import java.io.IOException;
import java.util.HashMap;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.*;

public class BeanData {

    private JMXConnector connection;
    
    private boolean connected = false;
    
    public BeanData(String host, int port) {
        try {
            close();
            JMXServiceURL serviceURL = new JMXServiceURL("rmi", "", 0, "/jndi/rmi://" + host + ":" + port + "/jmxrmi");
            HashMap<String, Object> environment = new HashMap<String, Object>();
            int jmxconnect_timeout = 10000;
            environment.put("jmx.remote.x.request.waiting.timeout", Long.toString(jmxconnect_timeout));
            
            connection = JMXConnectorFactory.newJMXConnector(serviceURL, environment);
            connection.addConnectionNotificationListener(new NotificationListener() {
                
                @Override
                public void handleNotification(Notification notification, Object handback) {
                    if( notification != null ) {
                        System.out.println(notification);
                    }
                    if( handback != null ) {
                        System.out.println(handback);
                    }
                    String type = notification.getType();
                    System.out.println(type);
                    
                    connected = !type.endsWith("closed");
                }
            }, new NotificationFilter() {
                
                @Override
                public boolean isNotificationEnabled(Notification notification) {
                    if( notification != null ) {
                        System.out.println(notification);
                    }
                    String type = notification.getType();
                    
                    System.out.println(type);
                    return type.startsWith("jmx.remote.connection.");
                }
            }, null);
            connection.connect();
            connected = true;
        } catch( Exception e ) {
            close();
            connection = null;
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void close() {
        try {
            if( !Utils.isNull(connection) ) {
                connection.close();
            }
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    public Object get(String name, String attribute, String compositeDataKey) {
        if( !Utils.isNull(connection) ) {
            try {
                Object o = connection.getMBeanServerConnection().getAttribute(new ObjectName(name), attribute);
                if( !Utils.isNull(compositeDataKey) ) {
                    CompositeData cd = (CompositeData) o;
                    o = cd.get(compositeDataKey);
                }
                return o;
            } catch( Exception e ) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Object get(String name, String attribute) {
        Object o = get(name, attribute, null);
        return o;
    }

    public static long getLong(Object object) {
        if( !Utils.isNull(object) && object instanceof Long ) {
            return (Long) object;
        }
        return 0;
    }

    public static int getInteger(Object object) {
        if( !Utils.isNull(object) && object instanceof Integer ) {
            return (Integer) object;
        }
        return 0;
    }

    public long getStartTime() {
        Object object = get("java.lang:type=Runtime", "StartTime");
//        Object startTime = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Runtime"), "StartTime");
//        System.out.println("TimeAlive: " + Utils.dateDiff((Long) startTime, new Date().getTime()));
////        return Utils.dateDiff((Long) startTime, new Date().getTime());
        return getLong(object);
    }
    
    public int getThreadCount() {
        Object object = get("java.lang:type=Threading", "ThreadCount");
//        Object threadCount = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Threading"), "ThreadCount");
//        System.out.println("ThreadCount: " + threadCount);
        return getInteger(object);
    }
    
    public int getDaemonThreadCount() {
        Object object = get("java.lang:type=Threading", "DaemonThreadCount");
//        Object daemonThreadCount = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Threading"), "DaemonThreadCount");
//        System.out.println("DaemonThreadCount: " + daemonThreadCount);
        return getInteger(object);
    }

    public long getHeapMemoryUsage() {
        Object object = get("java.lang:type=Memory", "HeapMemoryUsage", "used");
//        Object heapMemoryUsage = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage");
//        CompositeData cd = (CompositeData) heapMemoryUsage;
//        Long used = (Long) cd.get("used");
//        Long committed = (Long) cd.get("committed");
//        System.out.println("HeapMemoryUsage used: "+used + ", committed: " + committed);
        return getLong(object);
    }

    public long getNonHeapMemoryUsage() {
        Object object = get("java.lang:type=Memory", "NonHeapMemoryUsage", "used");
//        Object nonHeapMemoryUsage = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:type=Memory"), "NonHeapMemoryUsage");
//        CompositeData cd = (CompositeData) nonHeapMemoryUsage;
//        Long used = (Long) cd.get("used");
//        committed = (Long) cd.get("committed");
//        System.out.println("NonHeapMemoryUsage used: "+used + ", committed: " + committed);
        return getLong(object);
    }
    
    public long getPSEdenSpace() {
        Object object = get("java.lang:name=PS Eden Space,type=MemoryPool", "Usage", "used");
//        Object PSEdenSpace = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:name=PS Eden Space,type=MemoryPool"), "Usage");
//        CompositeData cd = (CompositeData) PSEdenSpace;
//        Long used = (Long) cd.get("used");
//        committed = (Long) cd.get("committed");
//        System.out.println("PS Eden Space Usage used: "+used + ", committed: " + committed);
        return getLong(object);
    }

    
    public long getPSSurvivorSpace() {
        Object object = get("java.lang:name=PS Survivor Space,type=MemoryPool", "Usage", "used");
//        Object PSSurvivorSpace = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:name=PS Survivor Space,type=MemoryPool"), "Usage");
//        CompositeData cd = (CompositeData) PSSurvivorSpace;
//        Long used = (Long) cd.get("used");
//        committed = (Long) cd.get("committed");
//        System.out.println("PS Survivor Space Usage used: "+used + ", committed: " + committed);
        return getLong(object);
    }

    public long getMetaspace() {
        Object object = get("java.lang:name=Metaspace,type=MemoryPool", "Usage", "used");
//        Object metaspace = connection.getMBeanServerConnection().getAttribute(new ObjectName("java.lang:name=Metaspace,type=MemoryPool"), "Usage");
//        CompositeData cd = (CompositeData) metaspace;
//        Long used = (Long) cd.get("used");
//        committed = (Long) cd.get("committed");
//        System.out.println("Metaspace used: "+used + ", committed: " + committed);
        return getLong(object);
    }
    
}
