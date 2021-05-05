package it.erm.graphics.swing.items.list;

import it.erm.graphics.swing.items.ctrl.ESCtrl;
import it.erm.lib.crud.dbm.TableManager;
import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class ESTableModel extends AbstractTableModel {

	private ArrayList<Object> data = null;

	private String fullTextSearch = null;
	
	private Class<? extends DBTable> tableClass = null;
	private DBTable filterTable = null;
	private DBTable originalFilterTable = null;
	private DBTable loadTable   = null;
	
	private boolean forceReload = false;
	private TableRowSorter<ESTableModel> sorter;
	
	public ESTableModel(DBTable filterTable, boolean searchAllFields) {
		this.tableClass = filterTable.getClass();
		this.originalFilterTable = filterTable;
		this.filterTable = TableManager.get(tableClass).newTable();
		this.filterTable.copyFiltersFromTable(originalFilterTable, true);
		this.loadTable = TableManager.get(tableClass).newTable();
	}
	
	public void configureSorter(JTable table, ESCtrl text, DBField field, boolean searchAllFields) {
		sorter = new TableRowSorter<ESTableModel>(this);
		table.setRowSorter(sorter);
		forceReload = true;
	}
	
	public DBTable getFilterTable() {
		return filterTable;
	}

	public DBTable getOriginalFilterTable() {
		return originalFilterTable;
	}
	
//	public DBTable getTable(int rowIndex) {
//		DBTable result = null;
//		loadData(forceReload);
//		result = (DBTable) data.get(rowIndex);
//		return result;
//	}
	
	@Override
	public int getRowCount() {
		loadData(forceReload);
		int count = data.size();
		return count;
	}

	@Override
	public int getColumnCount() {
		return loadTable.getDisplayFields().size();
	}

	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
//		DBTable table = (DBTable) data.get(rowIndex);
//		ArrayList<DBField> displayFields = table.getDisplayFields();
//		DBField field = displayFields.get(columnIndex);
//
//		return field.getJDBCType() == JDBCType.VARCHAR;
	    return false;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		loadData(forceReload);
		DBTable table = (DBTable) data.get(rowIndex);
		ArrayList<DBField> displayFields = table.getDisplayFields();
		DBField field = displayFields.get(columnIndex);
		result = field.getDisplayValue();
//		if( field.isForeignKey() && result != null ) {
//			if( field.getInteger() > 0 ) {
//				DBFieldFK fk = (DBFieldFK) field;
//				result = fk.getForeignKeyTable().getDescription((Integer) field.getValue());
//			}
//		}
		return result;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		DBTable table = (DBTable) data.get(rowIndex);
		ArrayList<DBField> displayFields = table.getDisplayFields();
		DBField field = displayFields.get(columnIndex);
		field.setValue(aValue);
		table.update(null);
	}
	
	private void loadData(boolean forceReload) {
		if( forceReload ) {
			if( data != null ) {
				data.clear();
				data = null;
				loadTable.close();
			}
		}
		
		if( data == null ) {
			data = new ArrayList<Object>();
			loadTable.reset();
			loadTable.copyFiltersFromTable(filterTable, true);
			ArrayList<DBField> allFields = loadTable.getAllFields();
			boolean openfind = loadTable.openfind();
			if( openfind ) {
			    while( loadTable.next() ) {
			        if( this.fullTextSearch != null ) {
			            boolean found = false;
			            for( DBField f: allFields ) {
			                String value = f.getDisplayValue();
			                if( value.toLowerCase().contains(this.fullTextSearch.toLowerCase()) ) {
			                    found = true;
			                    break;
			                }
			            }
			            if( !found ) {
			                continue;
			            }
			        }
			        data.add((DBTable) loadTable.clone());
			    }
			    loadTable.close();
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
		return filterTable.getDisplayFields().get(column).getName();
	}
	
	@Override
	public void fireTableDataChanged() {
		loadData(true);
		super.fireTableDataChanged();
	}
	
	public void setFullTextSearch(String fullTextSearch) {
		this.fullTextSearch = fullTextSearch;
	}
	
}
