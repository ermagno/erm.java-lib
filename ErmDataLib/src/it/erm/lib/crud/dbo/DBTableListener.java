package it.erm.lib.crud.dbo;

public interface DBTableListener {

	public void onOpenKey(int id);
	public void onReset();
}
