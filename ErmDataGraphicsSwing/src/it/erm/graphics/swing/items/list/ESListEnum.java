package it.erm.graphics.swing.items.list;

import it.erm.graphics.EList;
import it.erm.lib.crud.dbm.EnumInteger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

public class ESListEnum extends JScrollPane implements EList {

	private JTable  jtable; 
	
	public ESListEnum(Class<? extends EnumInteger> table) {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jtable = new JTable(new ESEnumModel(table));
		jtable.createDefaultColumnsFromModel();
        setViewportView(jtable);
        setLayout(new ScrollPaneLayout());
        FontMetrics fm = new FontMetrics(jtable.getFont()) {};
        jtable.setRowHeight(fm.getHeight()+15);
//        jtable.setRowMargin(5);
//        // set gap between columns, api in TableColumnModel
//        jtable.getColumnModel().setColumnMargin(5);
        // convenience for setting both row and column gaps
        jtable.setIntercellSpacing(new Dimension(15, 0)); 
//        jtable.setPreferredScrollableViewportSize(jtable.getPreferredSize());
//        jtable.setFillsViewportHeight(true);
        TableColumnModel columnModel = jtable.getColumnModel();
        for( int i=0; i<columnModel.getColumnCount(); i++ ) {
            TableColumn column = columnModel.getColumn(i);
            column.setHeaderRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    l.setHorizontalAlignment(JLabel.HORIZONTAL);
                    l.setBackground(Color.LIGHT_GRAY);
                    Font f = getFont();
                    l.setFont(new Font(f.getName(), Font.BOLD, f.getSize()+2));
                    return l;
                }
            });
        }
	}
	
	public JTable getJTable() {
		return jtable;
	}
	
	public ESEnumModel getTableModel() {
		return (ESEnumModel) jtable.getModel();
	}
	

	@Override
	public List<Object> getRecords() {
		return getTableModel().getData();
	}

	public List<EnumInteger> getSelecteItems() {
		int selectedRowCount = getJTable().getSelectedRowCount();
		ArrayList<EnumInteger> selectedItems = new ArrayList<EnumInteger>(selectedRowCount);
		int[] selectedRows = getJTable().getSelectedRows();
		for( int index: selectedRows ) {
			selectedItems.add((EnumInteger) getRecords().get(index));
		}
		return selectedItems;
	}

	public void update() {
		getTableModel().fireTableDataChanged();
		
	}
	
}
