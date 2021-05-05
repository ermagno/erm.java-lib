package it.erm.lib.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;



public class Utils {

    public static String formatDate(java.util.Date data, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern(pattern);
        String format = df.format(data);
        return format;
    }

    public static String formatDate(long date, String pattern) {
        return formatDate(new java.util.Date(date), pattern);
    }
    
    public static String formatToday(String pattern) {
        return formatDate(new Date(), pattern);
    }
    
    public static String dateDiffShort(long start, long end) {
        long diff = end-start;
        diff = diff/1000; //tolgo i millisecondi
        int s = (int) (diff%60); //conto i secondi
        diff = diff/60; //tolgo i secondi
        int m = (int) diff%60; //conto i minuti
        diff = diff/60; //tolgo i minuti
        int o = (int) diff%24; //conto le ore
        diff = diff/24; //tolgo le ore
        int g = (int) diff;
        
        String giorni   = Integer.toString(g);
        String ore      = (o<10?"0":"")+Integer.toString(o);
        String min      = (m<10?"0":"")+Integer.toString(m);
        String sec      = (s<10?"0":"")+Integer.toString(s);
        return giorni + "g " + ore + ":" + min + ":" + sec;
    }
    
    public static String dateDiff(long start, long end) {
        long diff = end-start;
        diff = diff/1000; //tolgo i millisecondi
        int s = (int) (diff%60); //conto i secondi
        diff = diff/60; //tolgo i secondi
        int m = (int) diff%60; //conto i minuti
        diff = diff/60; //tolgo i minuti
        int o = (int) diff%24; //conto le ore
        diff = diff/24; //tolgo le ore
        int g = (int) diff;
        
        String giorni   = (String) (g > 0 ? (Utils.fromArray(" ", Integer.toString(g), g > 1 ? "giorni"  :"giorno"))    :"");
        String ore      = (String) (o > 0 ? (Utils.fromArray(" ", Integer.toString(o), o > 1 ? "ore"     :"ora"))       :"");
        String min      = (String) (m > 0 ? (Utils.fromArray(" ", Integer.toString(m), m > 1 ? "minuti"  :"minuto"))    :"");
        String sec      = (String) (s > 0 ? (Utils.fromArray(" ", Integer.toString(s), s > 1 ? "secondi" :"secondo"))   :"");
        return Utils.fromArray(", ", giorni, ore, min, sec);
    }
    
    public static String formatByte(long bytes) {
        String result = "";
        int b = (int) (bytes%1000);
        bytes = bytes/1000;
        int k = (int) (bytes%1000);
        bytes = bytes/1000;
        int m = (int) (bytes%1000);
        bytes = bytes/1000;
        int g = (int) (bytes%1000);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingUsed(true);
        int tot = 0;
        String symb = "";
        if( g > 0 ) {
            tot = (g*1000)+m;
            symb = "Gb";
        } else if( m > 0 ) {
            tot = (m*1000)+k;
            symb = "Mb";
        } else if( k > 0 ) {
            tot = (k*1000)+b;
            symb = "Kb";
        } else {
            tot = b;
            symb = "b";
        }
        return decimalFormat.format(tot)+" "+symb;
    }
    
    public static int getRandom(int min, int max, boolean inclusi) {
        min = Math.abs(min);
        max = Math.abs(max);
        int x = -1;
        while( inclusi?(x<=min||x>=max):(x<min||x>max) ) {
            x = (Math.abs(new Random().nextInt()))%max;
        }
        return x;
    }
    
    public static String getParentClassDirectory(Class<?> c) {
        try {
            String path = new File(c.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            int lastIndexOf = path.lastIndexOf("\\");
            return path.substring(0, lastIndexOf);
        } catch( URISyntaxException e ) {
            e.printStackTrace();
            return "";
        }
    }
    
	public static final String fromArray(String sep, Object... objs) {
		StringBuilder str = new StringBuilder();
		ArrayList<Object> list = new ArrayList<Object>(objs.length);
		for( Object o: objs ) {
		    if( !Utils.isNull(o) ) {
		        list.add(o);
		    }
		}
		
		for( int i=0; i<list.size(); i++ ) {
		    if( i > 0) {
		        str.append(sep);
		    }
		    str.append(list.get(i).toString());
		}
		return str.toString();
	}
	
    public static boolean isNull(Object o) {
        boolean isNull = o == null;
        if( !isNull ) {
            if( o instanceof String ) {
                isNull = ((String)o).isEmpty();
            } else if( o instanceof java.sql.Date ) {
                isNull = ((java.sql.Date)o).getTime() == 0;
            } else if( o instanceof Object[] ) {
                isNull = ((Object[])o).length == 0;
            }
        }
        return isNull;
    }
	
    public static String getFullStackTrace(Exception e) {
        if( e == null ) {
            return "<null>";
        } else {
            StringBuilder result = new StringBuilder(e.toString());
            StackTraceElement[] stackTrace = e.getStackTrace();
            if( stackTrace != null ) {
                for( StackTraceElement ste: stackTrace ) {
                    result.append(System.getProperty("line.separator"));
                    result.append(ste.toString());
                }
            }
            return result.toString();
        }
        
    }
    
	public static final String NewLine() {
		String property = System.getProperty("line.separator");
		if( property == null || property.isEmpty() ) {
			property = "\n";
		}
		return property;
	}

	public static String getNowAsString() {
        return formatToday("yyyy-MM-dd HH.mm.ss");
	}

	public static String getYear() {
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("yyyy");
        String format = df.format(new java.util.Date());
        return format;
	}

	public static String getMounth() {
	    SimpleDateFormat df = new SimpleDateFormat();
	    df.applyPattern("MM");
	    String format = df.format(new java.util.Date());
	    return format;
	}

	public static String getDay() {
	    SimpleDateFormat df = new SimpleDateFormat();
	    df.applyPattern("dd");
	    String format = df.format(new java.util.Date());
	    return format;
	}
	
	public static boolean isToday(long millinsec) {
	    long lastModifiedDate = millinsec/(3600*24*1000);
	    long today = new java.util.Date().getTime()/(3600*24*1000);
	    long diffDay = lastModifiedDate - today;
	    return diffDay >= 0;
	}
	
	public static int parseInt(String s, int def) {
	    int result = def;
	    try {
	        result = Integer.parseInt(s);
        } catch( Exception e ) {}
	    return result;
	}
	
	public static String newFileNameAddHHMM(String fileName) {
	    String extension = "";
	    String fileNameNoExtension = fileName;
        SimpleDateFormat df = new SimpleDateFormat();
        df.applyPattern("HH.mm");
        String format = df.format(new java.util.Date());
        
	    int i = fileName.lastIndexOf('.');
	    if (i > 0) {
	        extension = fileName.substring(i);
	        fileNameNoExtension = fileName.substring(0, i);
	    }
	    return fileNameNoExtension+"_"+format+extension;
	}
	
	public static String padString(Object o, int length, boolean left) {
	    return String.format((left?"%":"%-")+length+ "s", o.toString());
	}
	
	public static String removeInvalidPathChar(String path, String replace) {
	    String replaceAll = path.replaceAll("[\\\\/:*?\"<>|]", replace);
	    return replaceAll;
	}
}
