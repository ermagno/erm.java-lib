package it.erm.graphics.swing.items.list;

import it.erm.graphics.EView;
import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.ESActionBar;
import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbm.Validation;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

public class ESListSearchEnum extends JPanel {

	private ESListEnum list;
	
	public ESAction selection = null;
	
	public ESListSearchEnum(final Container parent, final Class<? extends EnumInteger> table, final EView selectionTerget) {
		setLayout(new BorderLayout());
		
//		parent.add(new JLabel(table.getSimpleName(), SwingConstants.CENTER), BorderLayout.PAGE_START);
		
        list = new ESListEnum(table);
        add(list, BorderLayout.CENTER);
		
		
		ESActionBar actBar = new ESActionBar();
		selection = new ESAction(ResourceUtils.getImageIcon("table_go.png", "seleziona..."), "Seleziona", "Seleziona") {
			@Override
			public Validation executeAction() {
				List<EnumInteger> selecteItems = list.getSelecteItems();
				int id = -1;
				if( selecteItems.size() > 0 ) {
					id = list.getSelecteItems().get(0).getInteger();
				}
				if( parent instanceof JDialog ) {
					selectionTerget.setValue(id);
					selectionTerget.loadValue();
					((JDialog)parent).dispose();
				}
				return null;
			}
		};
		actBar.addAction(selection);
		add(actBar, BorderLayout.SOUTH);
		
		list.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
//		        JTable table =(JTable) me.getSource();
//		        Point p = me.getPoint();
//		        int row = table.rowAtPoint(p);
		        if (me.getClickCount() == 2) {
		            selection.executeAction(); 
		        }
			}
		});
		
		list.getJTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_SPACE) {
		            selection.executeAction(); 
		        }
			}
		});
		
		parent.add(this);
	}

	public JTable getJTable() {
	    return list.getJTable();
	}
}
