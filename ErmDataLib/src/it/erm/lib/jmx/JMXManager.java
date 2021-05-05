package it.erm.lib.jmx;

import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.remote.JMXServiceURL;

public class JMXManager {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                JMXManager.close();
            }
        });
    }
    
    private static ConcurrentHashMap<String, BeanData> beanMap = new ConcurrentHashMap<String, BeanData>();
    
    
    private static JMXServiceURL createConnectionURL(String host, int port) throws MalformedURLException {
        JMXServiceURL jmxServiceURL = new JMXServiceURL("rmi", "", 0, "/jndi/rmi://" + host + ":" + port + "/jmxrmi");
        return jmxServiceURL;
    }
    
    private static String buildKey(String host, int port) {
        StringBuilder key = new StringBuilder();
        key.append(host);
        key.append(".");
        key.append(port);
        return key.toString();
    }
    
    public static BeanData getBeanData(String host, int port) {
        String key = buildKey(host, port);
        BeanData beanData = beanMap.get(key);
        if( beanData != null ) {
            if( !beanData.isConnected() ) {
                BeanData remove = beanMap.remove(key);
                remove.close();
                beanData = null;
            }
        }
        if( beanData == null ) {
            beanData = new BeanData(host, port);
            beanMap.put(key, beanData);
        }
        return beanData;
    }
    
    public static void close() {
        for( String key: beanMap.keySet() ) {
            try {
                beanMap.get(key).close();
            } catch( Exception e ) {
            }
        }
    }
}
