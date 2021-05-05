package it.erm.graphics.swing.component;

import it.erm.lib.crud.dbm.TableManager;
import it.erm.lib.crud.dbo.DBTable;

import java.util.ArrayList;

import javax.swing.JComboBox;

public class ESComboBoxTable<T extends DBTable> extends JComboBox {
	Class<T> tableClass;
	
	public ESComboBoxTable(final Class<T> tableClass) {
		this.tableClass = tableClass;
//	      setEditor(new BasicComboBoxEditor() {
//	            @Override
//	            public Component getEditorComponent() {
//	                Component editorComponent = super.getEditorComponent();
//	                Rectangle b = editorComponent.getBounds();
//	                editorComponent.setBounds(b.x, b.y, b.width, b.height+10);
//	                return editorComponent;
//	            }
//	        });
	}
	
	public static class ComboTableValue<T extends DBTable>  {
		int id;
		Class<T> tableClass;
		String emptyValue;
		
		public ComboTableValue(Class<T> tClass, int id) {
			this(tClass, id, null);
		}
		public ComboTableValue(Class<T> tClass, int id, String emptyValue) {
			this.tableClass = tClass;
			this.id = id;
			this.emptyValue = emptyValue;
		}

		@Override
		public boolean equals(Object obj) {
			if( obj instanceof ComboTableValue<?> ) {
				return ((ComboTableValue<?>)obj).id == id;
			}
			return super.equals(obj);
		}
		
	    @Override
	    public String toString() {
	    	if( id == 0 ) {
	    		return emptyValue == null ? "" : emptyValue;
	    	} else {
	    		T table = TableManager.get(tableClass, id);
	    		return table.getDisplayFieldsRecord(false);
	    	}
	    }

	    public int getId() {
	        return id;
	    }
	    
	}
	
	@SuppressWarnings("unchecked")
	public ComboTableValue<T> getItemSelected() {
		Object selectedItem = getSelectedItem();
		return (ComboTableValue<T>) selectedItem;
	}
	
	@Override
	public void removeItem(Object anObject) {
		if( anObject instanceof Integer ) {
			anObject = new ComboTableValue<T>(tableClass, (Integer)anObject);
		}
		super.removeItem(anObject);
	}
	
	@Override
	public void addItem(Object anObject) {
        if( anObject instanceof Integer ) {
            anObject = new ComboTableValue<T>(tableClass, (Integer)anObject);
        }
	    super.addItem(anObject);
	}
	
	@Override
	public void setSelectedItem(Object anObject) {
        if( anObject instanceof Integer ) {
            anObject = new ComboTableValue<T>(tableClass, (Integer)anObject);
        }
	    super.setSelectedItem(anObject);
	}
	
	public void loadValues(T tableWithFilters, String emptyValue) {
		removeAllItems();
				
		ArrayList<ComboTableValue<T>> list = new ArrayList<ComboTableValue<T>>();
		if( emptyValue != null ) {
			addItem(new ComboTableValue<T>(tableClass, 0, emptyValue));
		}
		if( tableWithFilters == null ) {
			tableWithFilters = TableManager.get(tableClass).newTable();
			tableWithFilters.reset();
		}
		boolean openfind = tableWithFilters.openfind();
		if( openfind ) {
			while( tableWithFilters.next() ) {
				addItem(new ComboTableValue<T>(tableClass, tableWithFilters.getId()));
			}
			tableWithFilters.close();
		}
	}
	
	public void refreshItem(int id) {
		ComboTableValue<T> cv = new ComboTableValue<T>(tableClass, id);
		removeItem(cv);
		addItem(cv);
		setSelectedItem(cv);
	}
	
	public void refreshItemSelected() {
		ComboTableValue<T> itemSelected = getItemSelected();
		refreshItem(itemSelected.getId());
	}
	
}
