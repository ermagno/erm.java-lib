package it.erm.graphics.swing.items.list;

import it.erm.graphics.swing.items.ctrl.ESCtrl;
import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbo.DBField;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class ESEnumModel extends AbstractTableModel {

	private ArrayList<Object> data = null;
	
	private Class<? extends EnumInteger> enumIntegerClass = null;
	
	private boolean forceReload = false;
	private TableRowSorter<ESEnumModel> sorter;
	
	public ESEnumModel(Class<? extends EnumInteger> enumClass) {
		this.enumIntegerClass = enumClass;
	}
	
	public void configureSorter(JTable table, ESCtrl text, DBField field, boolean searchAllFields) {
		sorter = new TableRowSorter<ESEnumModel>(this);
		table.setRowSorter(sorter);
		forceReload = true;
	}
	
	
	public EnumInteger getEnumInteger(int rowIndex) {
		EnumInteger result = null;
		loadData(forceReload);
		result = (EnumInteger) data.get(rowIndex);
		return result;
	}
	
	@Override
	public int getRowCount() {
		loadData(forceReload);
		int count = data.size();
		return count;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		loadData(forceReload);
		result = data.get(rowIndex);
		return result;
	}

	private void loadData(boolean forceReload) {
		if( forceReload ) {
			if( data != null ) {
				data.clear();
				data = null;
			}
		}
		
		if( data == null ) {
			data = new ArrayList<Object>();
			for( EnumInteger e: enumIntegerClass.getEnumConstants() ) {
				data.add(e);
			}
			forceReload = false;
		}
	}

	public List<Object> getData() {
		loadData(false);
		return data;
	}
	
	@Override
	public String getColumnName(int column) {
		return enumIntegerClass.getSimpleName();
	}
	
	@Override
	public void fireTableDataChanged() {
		loadData(true);
		super.fireTableDataChanged();
	}
	
}
