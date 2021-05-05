package it.erm.lib.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.*;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class LogUtils {
    private static Logger  log = null;
    
    protected static class LogFormatter extends Formatter {
        boolean isError = false;;
        private static HashSet<String> classesToIgnore = new HashSet<String>();
        static {
            classesToIgnore.add(Logger.class.getName());
            classesToIgnore.add(LogUtils.class.getName());
            classesToIgnore.add(LogFormatter.class.getName());
            classesToIgnore.add(StreamHandler.class.getName());
            classesToIgnore.add(FileHandler.class.getName());
            classesToIgnore.add(LogOutputPrintStream.class.getName());
            classesToIgnore.add(PrintStream.class.getName());
            classesToIgnore.add(Throwable.class.getName());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    LogUtils.logClose();
                }
            });
        }
        
        @Override
        public String getHead(Handler h) {
            
            return Utils.fromArray("", " -- ", Utils.formatToday("yyyy-MM-dd HH.mm.ss"), " INIZIO LOG ", PropertyUtils.get().getFileName(), " -- ", Utils.NewLine(), Utils.NewLine());
        }
        
        @Override
        public String getTail(Handler h) {
            return Utils.fromArray("", Utils.NewLine(), " -- ", Utils.formatToday("yyyy-MM-dd HH.mm.ss"), " FINE LOG ", PropertyUtils.get().getFileName(), " -- ", Utils.NewLine(), Utils.NewLine());
        }
        
        @Override
        public String format(LogRecord record) {
            try {
                inferCaller(record);
                
                String call = Utils.fromArray(".", record.getSourceClassName(), record.getSourceMethodName());
                String[] split = record.getMessage().split("\\r?\\n");
                ArrayList<String> list = new ArrayList<String>(split.length);
                for( String s: split ) {
                    if( !Utils.isNull(s) ) {
                        list.add(s);
                    }
                }
                StringBuilder msg = new StringBuilder();
                for( String s: list ) {
                    if( msg.length() > 0 ) {
                        msg.append(Utils.NewLine());
                    }
                    msg.append(" * "+s);
                }
                if( isError ) {
                    return Utils.fromArray("", msg, Utils.NewLine());
                } else {
                    String start = Utils.fromArray("", record.getLevel().getName(), " (", record.getSequenceNumber(),")", ": ");
                    String init = Utils.fromArray("", Utils.padString(start, 15, false), " DATE: ", Utils.formatToday("yyyy-MM-dd HH.mm.ss"));
                    return Utils.fromArray("", init, "      SOURCE: ",call, Utils.NewLine(), msg, Utils.NewLine());
                }
            } catch( Exception e ) {
                return "errore " + ( (e == null) ? "null" : (e.getMessage() == null ? "null message" : e.getMessage()) ) + " nel log del messaggio...";
            } finally {
                isError = false;
            }
        }
        
        private void inferCaller(LogRecord record) {
            JavaLangAccess access = SharedSecrets.getJavaLangAccess();
            Throwable throwable = new Throwable();
            int depth = access.getStackTraceDepth(throwable);

            String logClassName = "java.util.logging.Logger";
            boolean lookingForLogger = true;
            for (int ix = 0; ix < depth; ix++) {
                // Calling getStackTraceElement directly prevents the VM
                // from paying the cost of building the entire stack frame.
                StackTraceElement frame = access.getStackTraceElement(throwable, ix);
                String cname = frame.getClassName();
                if( cname.equals(Throwable.class.getName()) ) {
                    isError = true;
                }
                if( !classesToIgnore.contains(cname) ) {
                    // We've found the relevant frame.
//                    stackTrace.add(Utils.fromArray("", "at ", cname, ".", frame.getMethodName(), " (", frame.getLineNumber(), ")"));
//                    if( Utils.isNull(record.getSourceClassName()) ) {
                        record.setSourceClassName(cname);
                        record.setSourceMethodName(Utils.fromArray("", frame.getMethodName(), " (", frame.getLineNumber(), ")"));
                        return;
//                    }
                }
            }
        }
    }
    
    private static class LogOutputPrintStream extends PrintStream {
        Level level;
        public LogOutputPrintStream(OutputStream out, Level l) {
            super(out);
            level = l;
        }
        
        @Override
        public void print(String x) {
            log.log(level, x);
        }

    }
    
    final static public Logger getLog() {
        return log;
    }
    
    public static void setupLogger(String Name) {
        try {
            Level level = Level.ALL;
            try {
                String l = PropertyUtils.get().getProperty(PropertyUtils.APP_LOG_LEVEL);
                level = Level.parse(l);
            } catch( Exception e ) {}

            String name = getLogName(Name);
            FileHandler fh;  
            log = Logger.getLogger(name+".log");  
            fh = new FileHandler(name+".log", true);
            fh.setFormatter(new LogFormatter());
            fh.setLevel(level);
            log.addHandler(fh);
            log.setLevel(level);
            
            System.setOut(new LogOutputPrintStream(System.out, Level.INFO));
            System.setErr(new LogOutputPrintStream(System.out, Level.SEVERE));
            
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();
        }
    }
    
    private static String getLogName(String Name) {
        String name = Name+"."+Utils.formatToday("yyyyMM");
        return name;
    }
    
    final static public void logClose() {
        if( !Utils.isNull(log) ) {
            Handler[] handlers = log.getHandlers();
            if( !Utils.isNull(handlers) ) {
                for( Handler h: handlers ) {
                    try {
                        h.close();
                    } catch( Exception e ) {}
                }
            }
        }
    }
}
