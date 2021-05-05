package it.erm.graphics.swing.component;

import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbo.formatter.EnumFormatter;

import java.util.ArrayList;

import javax.swing.JComboBox;

public class ESComboBoxEnum<T extends EnumInteger> extends JComboBox {
	Class<T> enumClass;
	
	public ESComboBoxEnum(Class<T> enumClass) {
		this.enumClass = enumClass;
//		setEditor(new BasicComboBoxEditor() {
//		    @Override
//		    public Component getEditorComponent() {
//		        Component editorComponent = super.getEditorComponent();
//		        Rectangle b = editorComponent.getBounds();
//		        editorComponent.setBounds(b.x, b.y, b.width, b.height+10);
//		        return editorComponent;
//		    }
//		});
	}

	public static class ComboEnumValue<T extends EnumInteger>  {
		int id;
		Class<T> enumClass;
		String emptyValue;
		
		public ComboEnumValue(Class<T> tClass, int id) {
			this(tClass, id, null);
		}
		public ComboEnumValue(Class<T> tClass, int id, String emptyValue) {
			this.enumClass = tClass;
			this.id = id;
			this.emptyValue = emptyValue;
		}

		@Override
		public boolean equals(Object obj) {
			if( obj instanceof ComboEnumValue<?> ) {
				return ((ComboEnumValue<?>)obj).id == id;
			}
			return super.equals(obj);
		}
		
	    @Override
	    public String toString() {
	    	if( id < 0 ) {
	    		return emptyValue == null ? "" : emptyValue;
	    	} else {
	    		EnumInteger ei = null;
	    		Enum<?>[] enumConstants = (Enum[]) (enumClass).getEnumConstants();
	    		for( Enum<?> e: enumConstants ) {
	    			if( e instanceof EnumInteger ) {
						if( id == ((EnumInteger)e).getInteger() ) {
							ei = (EnumInteger) e;
						}
	    			}
	    		}
	    		if( ei == null ) {
	    			return "";
	    		} else {
	    			return EnumFormatter.getConversion(ei);
	    		}
	    	}
	    }

	    public int getId() {
	        return id;
	    }
	    
	}
	
	@SuppressWarnings("unchecked")
	public ComboEnumValue<T> getItemSelected() {
		Object selectedItem = getSelectedItem();
		return (ComboEnumValue<T>) selectedItem;
	}
	
	@Override
	public void removeItem(Object anObject) {
		if( anObject instanceof Integer ) {
			anObject = new ComboEnumValue<T>(enumClass, (Integer)anObject);
		}
		super.removeItem(anObject);
	}
	
	public void loadValues(String emptyValue) {
		removeAllItems();
				
		ArrayList<ComboEnumValue<T>> list = new ArrayList<ComboEnumValue<T>>();
		if( emptyValue != null ) {
			addItem(new ComboEnumValue<T>(enumClass, -1, emptyValue));
		}
		Enum<?>[] enumConstants = (Enum[]) (enumClass).getEnumConstants();
		for( Enum<?> e: enumConstants ) {
			if( e instanceof EnumInteger ) {
				addItem(new ComboEnumValue<T>(enumClass, ((EnumInteger) e).getInteger()));
			}
		}
	}
	
	public void refreshItem(int id) {
		ComboEnumValue<T> cv = new ComboEnumValue<T>(enumClass, id);
		removeItem(cv);
		addItem(cv);
		setSelectedItem(cv);
	}
	
	public void refreshItemSelected() {
		ComboEnumValue<T> itemSelected = getItemSelected();
		refreshItem(itemSelected.getId());
	}
	
}
