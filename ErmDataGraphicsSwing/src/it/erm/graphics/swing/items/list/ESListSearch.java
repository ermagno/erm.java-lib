package it.erm.graphics.swing.items.list;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.*;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.crud.dbo.DBTable;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

public class ESListSearch extends JPanel {

	private JPanel title = null;
	private JTextField txtSearch = null;
	
	private ESList list;
	
	public ESAction actSelection = null;
	public ESAction actMostraTutto = null;
	
	public ESListSearch(final Container parent, final DBTable table, final ESView selectionTerget, boolean searchAllFields, boolean singleChoice) {
		setLayout(new BorderLayout());
		
		title = new JPanel(new BorderLayout());
		title.add(new JLabel(table.getName(), SwingConstants.CENTER), BorderLayout.NORTH);
		if( searchAllFields ) {
			title.add(txtSearch = new JTextField(), BorderLayout.SOUTH);
			txtSearch.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String text = txtSearch.getText();
					searchAll(text);
				}
			});
		}
		
		add(title, BorderLayout.PAGE_START);
		
        list = new ESList(table, searchAllFields, singleChoice);
        add(list, BorderLayout.CENTER);
		
		
		ESActionBar actBar = new ESActionBar();
		actSelection = new ESAction(ResourceUtils.getImageIcon("table_go.png", "seleziona..."), "Seleziona", "Seleziona") {
			@Override
			public Validation executeAction() {
				List<DBTable> selecteItems = list.getSelecteItems();
				if( selecteItems.size() > 0 ) {
					Object id = list.getSelecteItems().get(0).getId();
					selectionTerget.setValue(id);
					selectionTerget.loadValue();
				}
				if( parent instanceof JDialog ) {
					((JDialog)parent).dispose();
				}
				return null;
			}
		};
		
		actMostraTutto = new ESAction(ResourceUtils.getImageIcon("application_view_detail.png", "mostra tutto"), "Tutto", "Mostra tutto") {
			@Override
			public Validation executeAction() {
				list.getTableModel().getFilterTable().reset();
				if( getState()==1 ) {
					list.getTableModel().getFilterTable().copyFiltersFromTable(list.getTableModel().getOriginalFilterTable(), true);
				}
				list.update();
				return null;
			}
		};
		actMostraTutto.addState("Precedente", ResourceUtils.getImageIcon("application_view_detail.png", "Mostra selezione precedente"));
		
		actBar.addAction(actSelection);
		actBar.addAction(actMostraTutto);
		
		add(actBar, BorderLayout.SOUTH);
		
		list.getJTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if( !list.isSingleSelection() )
					return;
		        if (me.getClickCount() == 2) {
		            actSelection.executeAction(); 
		        }
			}
		});
		
		list.getJTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if( !list.isSingleSelection() )
					return;
				if( e.getKeyCode() == KeyEvent.VK_SPACE) {
		            actSelection.executeAction(); 
		        }
			}
		});
		
		parent.add(this);
	}

	public List<DBTable> getSelectedItems() {
		return list.getSelecteItems();
	}
	
	public void searchAll(String text) {
		if( text != null && text.length() > 0 ) {
			list.getTableModel().setFullTextSearch(text);
		} else {
			list.getTableModel().setFullTextSearch(null);
		}
		list.update();
	}
	
}
