package it.erm.lib.crud.dbo.formatter;

import it.erm.lib.crud.dbo.DBField;


public interface DisplayFormatter {

    public String getDisplayPreviousValue(DBField f);
    
	public String getDisplayValue(DBField f);
	
}
