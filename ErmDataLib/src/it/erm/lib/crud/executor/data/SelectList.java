package it.erm.lib.crud.executor.data;

import java.util.ArrayList;
import java.util.Iterator;

import it.erm.lib.crud.dbo.DBField;

public class SelectList extends ArrayList<Select> {

	public int getColumnIndex(Select s) {
		//l'indice delle colonne nel ResultSet parte da 1 
		return indexOf(s)+1;
	}
	
	@Override
	public void add(int index, Select element) {
		super.add(index, element);
		System.err.println("L'ordine delle select non va modificato!");
	}
	
	private static final long serialVersionUID = -1873766613058138931L;
	public Select getFromField(DBField field) {
		if( isEmpty() ) {
			return null;
		} else {
			Iterator<Select> iterator = iterator();
			while( iterator.hasNext() ) {
				Select next = iterator.next();
				if( next.getField() == field ) {
					return next;
				}
			}
		}
		return null;
	}
	
}
