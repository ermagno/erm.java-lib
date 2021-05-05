package it.erm.lib.filesystem;

import it.erm.lib.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class FileSearchUtils {

    /**
     * 
     * @param result inizialmente nullo, serve per la ricorsione
     * @param dirStart
     * @param name
     * @param endsWith
     * @return
     */
    public static ArrayList<File> getFiles(ArrayList<File> result, File dirStart, String name, boolean endsWith) {
    	if( result == null ) {
    		result = new ArrayList<File>();
    	}
    	try {
    	    if( !dirStart.isDirectory() ) {
    	        dirStart = dirStart.getParentFile();
    	    }
    	    
    		File[] listFiles = dirStart.listFiles();
    		if( listFiles != null ) {
    			for( File sub: listFiles ) {
    				boolean found = false;
    				if( endsWith ) {
    					found = sub.getName().endsWith(name);
    				} else {
    					found = sub.getName().equals(name);
    				}
    				if( found ) {
    					result.add(sub);
    				} else if( sub.isDirectory() ) {
    					getFiles(result, sub, name, endsWith);
    				}
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return result;
    }

    public static ArrayList<File> getDirsContainingFiles(ArrayList<File> result, File dirStart, int deepLevelStart, int deepLevelMax, String dirName, String[] includeNames, String[] excludeNames) {
        if( result == null ) {
            result = new ArrayList<File>();
        }
        if( deepLevelStart < deepLevelMax ) {
            if( dirStart != null && dirStart.isDirectory() && includeNames != null && includeNames.length > 0 ) {
                boolean fileInFolder = isFileInFolder(dirStart, includeNames, excludeNames);
                boolean dirOk = dirStart.getPath().toLowerCase().contains(dirName.toLowerCase());
                if( fileInFolder && dirOk ) {
                    result.add(dirStart);
                } else {
                    deepLevelStart++;
                    File[] listFiles = dirStart.listFiles();
                    if( listFiles != null ) {
                        for( File sub: listFiles ) {
                            if( sub.isDirectory() ) {
                                getDirsContainingFiles(result, sub, deepLevelStart, deepLevelMax, dirName, includeNames, excludeNames);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static boolean isFileInFolder(File folder, String[] includeNames, String[] excludeNames) {
        if( Utils.isNull(includeNames) && Utils.isNull(excludeNames) ) {
            return true;
        }
        if( Utils.isNull(folder) || !folder.isDirectory() ) {
            return false;
        }
        
        int indexes = (int) Math.pow(2, includeNames.length)-1;
        
        int count=0; 
        
        File[] listFiles = folder.listFiles();
        if( listFiles != null ) {
            for( File sub: listFiles ) {
                if( !Utils.isNull(includeNames) ) {
                    for( int i=0; i<includeNames.length; i++ ) {
                        if( sub.getName().equalsIgnoreCase(includeNames[i]) ) {
                            indexes -= 1<<i;
                            count++;
                        }
                    }
                }
                if( !Utils.isNull(excludeNames) ) {
                    for( int i=0; i<excludeNames.length; i++ ) {
                        if( sub.getName().equalsIgnoreCase(includeNames[i]) ) {
                            indexes = -1;
                        }
                    }
                }
            }
        }
        return indexes == 0;
    }
    
    
}
