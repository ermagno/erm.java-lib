package it.erm.lib.crud.dbo.formatter;

import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldEnum;
import it.erm.lib.utils.Utils;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;

public class EnumFormatter<T extends EnumInteger> implements DisplayFormatter {
	
	private static ConcurrentHashMap<Class<? extends EnumInteger>, ConcurrentHashMap<EnumInteger, String>> MapConverter = new ConcurrentHashMap<Class<? extends EnumInteger>, ConcurrentHashMap<EnumInteger,String>>(); 

    @Override
    public String getDisplayPreviousValue(DBField f) {
        if( Utils.isNull(f.getPreviousValue()) )
            return "";
        return ((EnumInteger)((DBFieldEnum<T>)f).getPreviousEnum()).toString();
    }
	
	@Override
	public String getDisplayValue(DBField f) {
		if( f.isNull() )
			return "";
		return ((EnumInteger)((DBFieldEnum<T>)f).getEnum()).toString();
	}

	public static <E extends EnumInteger> E getFromInt(int v, Class<E> ec) {
		E[] enumConstants = ec.getEnumConstants();
		for( E e: enumConstants ) {
			if( e.getInteger() == v )
				return e;
		}
		return null;
	}

	public static <E extends EnumInteger> E getFromString(String str, Class<E> ec) {
		E[] enumConstants = ec.getEnumConstants();
		for( E e: enumConstants ) {
			if( !(e instanceof EnumInteger) )
				continue;
			String conversion = getConversion(ec, (EnumInteger)e);
			if( conversion != null && str != null && conversion.equals(str) )
				return e;
		}
		return null;
	}

	public static void unregisterConversion(Class<? extends EnumInteger> enumClass) {
		MapConverter.remove(enumClass);
	}
	
	public static void registerConversion(Class<? extends EnumInteger> enumClass, EnumInteger enumValue, String displayValueEnum) {
		if( MapConverter.get(enumClass) == null )
			MapConverter.put(enumClass, new ConcurrentHashMap<EnumInteger, String>());
		MapConverter.get(enumClass).put(enumValue, displayValueEnum);
	}
	
	public static boolean hasConversion(Class<? extends EnumInteger> enumClass, EnumInteger enumValue) {
		if( MapConverter.get(enumClass) == null )
			return false;
		if( MapConverter.get(enumClass).get(enumValue) == null )
			return false;
		return true;
	}

    public static String getConversion(EnumInteger enumValue) {
        return getConversion(enumValue.getClass(), enumValue);
    }

	public static String getConversion(Class<? extends EnumInteger> enumClass, EnumInteger enumValue) {
		if( !hasConversion(enumClass, enumValue) )
			return registerDefaultConversion(enumClass, enumValue);
		return MapConverter.get(enumClass).get(enumValue);
	}
	
	private static String registerDefaultConversion(Class<? extends EnumInteger> enumClass, EnumInteger enumValue) {
	    String name = ((Enum<?>)enumValue).name();
	    try {
	        StringBuilder sb = new StringBuilder();
	        for( int i=0; i<name.length(); i++ ) {
	            char charAt = name.charAt(i);
	            if( i>0 && (Character.isUpperCase(charAt) || charAt == '_')) {
	                sb.append(' ');
	            }
	            if( i == 0 && Character.isLowerCase(charAt) ) {
	                charAt = Character.toUpperCase(charAt);
	            }
	            sb.append(charAt);
	        }
	        String result = sb.toString();
	        registerConversion(enumClass, enumValue, result);
	        name = result;
        } catch( Exception e ) {
            e.printStackTrace();
        }
	    return name;
	}
	
    /** Converte una stringa di BIT 0 e 1 in una enumset con enum di tipo VEnumInteger .
     *  L'indice del bit � il valore della 'getInteger' dell'enum , NON l'ordinale dell'enum
     */
    public static <E extends Enum<E>> EnumSet<E> strBitToEnumSet(String s, Class<E> EnumType)  {
    	E[] enumConstants = EnumType.getEnumConstants();
        EnumSet<E>  set = EnumSet.noneOf(EnumType);
        char c;
        for( int i=0; i<s.length(); i++ )  {
            c = s.charAt(i);
            if( c!='0' && c!='1' )
                c = '0';
            if( c=='1' ) {
            	for( E e: enumConstants ) {
            		EnumInteger ei = (EnumInteger) e;
            		if( ei.getInteger() == i )
            			set.add(e);
            	}
            }
        }
        return set;
    }
    
    
    /** Converte un enumset con enum di tipo VEnumInteger in una stringa di BIT 0 e 1.
     *  L'indice del bit è il valore della 'getInteger' dell'enum , NON l'ordinale dell'enum
     */
    public static <E extends Enum<E>> String enumSetToStrBit(EnumSet<E> set, Class<E> EnumType)  {
        StringBuilder sb = new StringBuilder();
        int max = 0;
        for( Enum<E> e: EnumType.getEnumConstants() ) {
            int i = ((EnumInteger) e).getInteger();
            if( i > max ) max = i;
        }

        if( EnumType.getEnumConstants().length > max )
            max = EnumType.getEnumConstants().length;
        else {
            //incremento il massimo perch� a me serve il numero degli elementi presenti nell'enum 
            //e non l'ultimo intero utilizzato.
            max++;
        }
        for( int i = 0; i < max; i++ )
            sb.append("0");
        for( Enum<E> e: set )  {
            int i = ((EnumInteger)e).getInteger();
            sb.setCharAt( i, '1' );
        }
        return sb.toString();
    }

}
