package it.erm.graphics.swing;

import it.erm.graphics.EView;
import it.erm.lib.crud.dbo.DBField;

import javax.swing.JComponent;
import javax.swing.JPanel;

public abstract class ESView extends JPanel implements EView {

	/**
	 * Alla perdita del focus viene letto il valore del controllo e tradotto nel tipo del campo
	 * @param f il campo a cui il controllo è associato
	 * @param textField il controllo di visualizzazione del valore del campo
	 * @return ritorna il valore da passare alla {@link #setValue(Object)}
	 */
	public abstract Object onFocusLost(DBField f, JComponent textField);

	public abstract void onFocusGained(DBField f, JComponent textField);
	
}
