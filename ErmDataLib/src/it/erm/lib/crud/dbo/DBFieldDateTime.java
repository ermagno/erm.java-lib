package it.erm.lib.crud.dbo;

import it.erm.lib.crud.JDBCType;
import it.erm.lib.crud.dbo.formatter.DateTimeFormatter;



public class DBFieldDateTime extends DBField {

	private boolean date = true;
	private boolean time = true;
	
	public static final int DO_NOT_OVERWRITE = -1;
	
	public DBFieldDateTime(DBTable tableOwner, String name, boolean date, boolean time, boolean canBeNull, Boolean indexUnique) {
		super(tableOwner, name, JDBCType.TIMESTAMP, canBeNull);
		this.date = date;
		this.time = time;
		setDisplayFormatter(new DateTimeFormatter());
	}

	public boolean isDate() {
		return date;
	}
	
	public boolean isTime() {
		return time;
	}
	
	public void setMilliSecond(long time) {
		setValue(time);
	}

	public long getMilliSecondPrevious() {
	    return DateTimeFormatter.getFromObject(getPreviousValue());
	}

	public long getMilliSecond() {
		return DateTimeFormatter.getFromObject(getValue());
	}

	public void setDate(int year, int month, int dayOfMonth) {
		setMilliSecond(DateTimeFormatter.getDate(year, month, dayOfMonth));
	}

	public void setDate(long time) {
		setMilliSecond(DateTimeFormatter.fillDateTime(0, time, true, false));
	}

	public void setTime(int hourOfDay, int minute, int second) {
		setMilliSecond(DateTimeFormatter.getTime(hourOfDay, minute, second));
	}
	
	public void setTime(long time) {
		setMilliSecond(DateTimeFormatter.fillDateTime(0, time, false, true));
	}
	
	public void setDateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		setValue(DateTimeFormatter.getDateTime(year, month, dayOfMonth, hourOfDay, minute, second));
	}

	
	public int getYear() {
		return DateTimeFormatter.getYear(getMilliSecond());
	}
	
	public int getMonth() {
		return DateTimeFormatter.getMonth(getMilliSecond());
	}
	
	public int getDayOfMonth() {
		return DateTimeFormatter.getDayOfMonth(getMilliSecond());
	}

	public int getHourOfDay() {
		return DateTimeFormatter.getHourOfDay(getMilliSecond());
	}

	public int getMinute() {
		return DateTimeFormatter.getMinute(getMilliSecond());
	}

	public int getSecond() {
		return DateTimeFormatter.getSecond(getMilliSecond());
	}
	
	
}
