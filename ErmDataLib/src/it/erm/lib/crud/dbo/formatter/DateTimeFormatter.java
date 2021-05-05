package it.erm.lib.crud.dbo.formatter;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldDateTime;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeFormatter implements DisplayFormatter {

    @Override
    public String getDisplayPreviousValue(DBField f) {
        DBFieldDateTime fdt = (DBFieldDateTime) f;
        boolean date = fdt.isDate();
        boolean time = fdt.isTime();
        long milliSecond = fdt.getMilliSecondPrevious();
        if( milliSecond == 0 ) {
            return "";
        } else if( date && time ) {
            return getDateTimeFormatted(milliSecond);
        } else if( date ) {
            return getDateFormatted(milliSecond);
        } else if( time ) {
            return getTimeFormatted(milliSecond);
        } else {
            return "";
        }
    }

    
	@Override
	public String getDisplayValue(DBField f) {
		DBFieldDateTime fdt = (DBFieldDateTime) f;
		boolean date = fdt.isDate();
		boolean time = fdt.isTime();
		long milliSecond = fdt.getMilliSecond();
		if( milliSecond == 0 ) {
			return "";
		} else if( date && time ) {
			return getDateTimeFormatted(milliSecond);
		} else if( date ) {
			return getDateFormatted(milliSecond);
		} else if( time ) {
			return getTimeFormatted(milliSecond);
		} else {
			return "";
		}
	}

	public String getMask(DBFieldDateTime fdt) {
		if( fdt.isDate() && fdt.isTime() ) {
			return "##/##/#### ##:##";
		} else if( fdt.isDate() ) {
			return "##/##/####";
		} else if( fdt.isTime() ) {
			return "##:##";
		} else {
			return "";
		}
	}
	
	
	@Override
	public String toString() {
		return "Date Formatter";
	}
	
	public static final int DO_NOT_OVERWRITE = -1;
	
	public static long fillDateTime(long timeToFill, int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(timeToFill);
		if( second > DO_NOT_OVERWRITE ) {
			c.set(Calendar.SECOND, second);
		}
		if( minute > DO_NOT_OVERWRITE ) {
			c.set(Calendar.MINUTE, minute);
		}
		if( hourOfDay > DO_NOT_OVERWRITE ) {
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		}
		if( dayOfMonth > DO_NOT_OVERWRITE ) {
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		}
		if( month > DO_NOT_OVERWRITE ) {
			c.set(Calendar.MONTH, month);
		}
		if( year > DO_NOT_OVERWRITE ) {
			c.set(Calendar.YEAR, year);
		}
		return c.getTimeInMillis();
	}
	
	public static long fillDateTime(long timeToFill, long timeFiller, boolean fillDate, boolean fillTime) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(timeToFill);
		
		Calendar cFiller = Calendar.getInstance(Locale.ROOT);
		cFiller.setTimeInMillis(timeFiller);

		if( fillDate ) {
			c.set(Calendar.DAY_OF_MONTH, cFiller.get(Calendar.DAY_OF_MONTH));
			c.set(Calendar.MONTH, cFiller.get(Calendar.MONTH));
			c.set(Calendar.YEAR, cFiller.get(Calendar.YEAR));
		}
		
		if( fillTime ) {
			c.set(Calendar.SECOND, cFiller.get(Calendar.SECOND));
			c.set(Calendar.MINUTE, cFiller.get(Calendar.MINUTE));
			c.set(Calendar.HOUR_OF_DAY, cFiller.get(Calendar.HOUR_OF_DAY));
		}
		
		return c.getTimeInMillis();
	}
	
	
	public static long getDateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		return fillDateTime(0, year, month, dayOfMonth, hourOfDay, minute, second);
	}

	public static long getDate(int year, int month, int dayOfMonth) {
		return fillDateTime(0, year, month, dayOfMonth, DO_NOT_OVERWRITE, DO_NOT_OVERWRITE, DO_NOT_OVERWRITE);
	}

	public static long getTime(int hourOfDay, int minute, int second) {
		return fillDateTime(0, DO_NOT_OVERWRITE, DO_NOT_OVERWRITE, DO_NOT_OVERWRITE, hourOfDay, minute, second);
	}
	
	public static int getYear(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return c.get(Calendar.YEAR);
	}
	
	public static int getMonth(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return c.get(Calendar.MONTH);
	}
	
	public static int getDayOfMonth(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHourOfDay(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return c.get(Calendar.MINUTE);
	}

	public static int getSecond(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return c.get(Calendar.SECOND);
	}

	/**24 ore x 60 minuti x 60 secondi x 1000/*millisecondi*/
	public static final long		ONE_DAY_MS = 24/*ore*/ * 60/*minuti*/ * 60/*secondi*/ * 1000/*millisecondi*/;

	public static String getTimerFormatted(long time) {
		long seconds = (time / 1000);
		
		long ss =  seconds % 60;
		long mm = (seconds / 60) % 60;
		long HH =  seconds / (60*60);
		long gg = HH / 24;
		HH = HH % 24;
		if( gg > 0 )
			return String.format("%02dgg %02d:%02d:%02d", gg, HH, mm, ss);
		return String.format("%02d:%02d:%02d", HH, mm, ss);
	}

	public static String getTimeFormatted(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	public static String getDateFormatted(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return String.format("%02d/%02d/%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
	}

	public static String getDateTimeFormatted(long time) {
		Calendar c = Calendar.getInstance(Locale.ROOT);
		c.setTimeInMillis(time);
		return String.format("%02d/%02d/%02d %02d:%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	public static boolean isToday(long time) {
		return (System.currentTimeMillis() / ONE_DAY_MS) == (time / ONE_DAY_MS);  
	}
	
	public static long daysAgo(long time) {
		return ((System.currentTimeMillis() - time) % ONE_DAY_MS);
	}

	public static long getFromObject(Object v) {
		if( v == null )
			return 0;
		long time = 0;
		if( v instanceof Long ) {
			time = (Long) v;
		} else if( v instanceof Date ) {
			time = (((Date) v).getTime());
		} else if( v instanceof java.util.Date ) {
			time = ((java.util.Date) v).getTime();
		}
		return time;

	}

	public static long getTimeDistanceIn24H(long dest, long start) {
		dest = fillDateTime(0, dest, false, true);
		start = fillDateTime(0, start, false, true);
		long distance = dest - start;
		if( distance <= 0 )
			distance += ONE_DAY_MS;
		return distance;
	}

	
}
