package it.erm.graphics;

import it.erm.lib.crud.dbo.DBField;


public interface EView {
	/**
	 * imposta il valore nel campo
	 * @param value
	 */
	public void setValue(Object value);
	
	/**
	 * ritorna il valore del campo associato
	 * @return
	 */
	public Object getValue();
	
	/**
	 * visualizza nel controllo il valore del campo in base al metodo {@link DBField#getDisplayFormatter()}
	 */
	public void loadValue();
}
