package it.erm.graphics;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

public interface EListChoice {

	public EView getRicercaEText();
	
	public DBTable getTable();
	
	public DBField getRicercaField();
	
	
}
