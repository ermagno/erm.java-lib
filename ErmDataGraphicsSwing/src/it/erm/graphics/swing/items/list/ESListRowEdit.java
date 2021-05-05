package it.erm.graphics.swing.items.list;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.erm.graphics.swing.ESActionBar;
import it.erm.graphics.swing.ESView;
import it.erm.graphics.swing.items.action.ESActionOpenAna;
import it.erm.lib.crud.dbm.TableManager;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

public class ESListRowEdit extends ESView {

	private DBTable masterTable;
	private DBField rowLink;
	private JPanel header = null;
	private ESList rows;
	
	public ESListRowEdit(DBTable masterTable, DBField rowLink, boolean searchAllFields, boolean singleChoice) {
		setLayout(new BorderLayout());
		setOpaque(false);
		
		this.masterTable = masterTable;
		this.rowLink = rowLink;
		
		header = new JPanel(new GridBagLayout());
		header.setOpaque(false);
		JLabel title = new JLabel(rowLink.getTableOwner().getName());
		title.setOpaque(true);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.ipadx = 0;
        gbc.ipady = 0;;
		header.add(title, gbc);

		ESActionBar actBar = new ESActionBar();
		actBar.setOpaque(true);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.ipadx = 0;
        gbc.ipady = 0;;
		header.add(actBar, gbc);
		
		add(header, BorderLayout.PAGE_START);
		rows = new ESList(rowLink.getTableOwner(), true, true);
        add(rows, BorderLayout.CENTER);

        ESActionOpenAna actOpenAna = new ESActionOpenAna(TableManager.get(rowLink.getTableOwner().getClass()).newTable(), this) {
        	@Override
        	protected void onAfterOpen() {
        		this.table.getEquivalentField(ESListRowEdit.this.rowLink.getName()).setValue(ESListRowEdit.this.masterTable.getId());
        	}
        };
        actBar.addAction(actOpenAna);
        
	}

	@Override
	public void setValue(Object value) {
		
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public void loadValue() {
		rows.update();
	}

	@Override
	public Object onFocusLost(DBField f, JComponent textField) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFocusGained(DBField f, JComponent textField) {
		// TODO Auto-generated method stub
		
	}
	
}
