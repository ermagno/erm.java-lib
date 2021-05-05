package it.erm.graphics.swing.items.form;

import it.erm.graphics.swing.items.list.ESListRowEdit;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

import java.awt.BorderLayout;
import java.awt.Container;

public class ESFormStdAndRowsEdit extends ESFormStandardEdit {

	public ESFormStdAndRowsEdit(Container parent, boolean insertInParent, DBTable table, DBField rowLink, boolean autoPos) {
		super(parent, insertInParent, table, 1, autoPos);
		
		ESListRowEdit rows = new ESListRowEdit(table, rowLink, true, true);
		
		add(rows, BorderLayout.CENTER);
	}

}
