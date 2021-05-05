package it.erm.graphics.swing.items.action;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.ESView;
import it.erm.graphics.swing.items.list.ESListSearch;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.crud.dbo.DBTable;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ESActionSearch extends ESAction {

	private DBTable searchTable;
	/**
	 * la view chiamante
	 */
	private ESView view;
	/**
	 * indica se aprire la finestra delle dimensioni del chiamante
	 */
	boolean fillAllOnOpen = false;
	
	public ESActionSearch(DBTable searchTable, ESView v, boolean fillOnOpen) {
		super(ResourceUtils.getImageIcon("table_multiple.png", "selezione..."), fillOnOpen?"Cerca":null, "Elenca");
		this.searchTable = searchTable;
		this.view = v;
		this.fillAllOnOpen = fillOnOpen;
	}

	@Override
	public Validation executeAction() {
		final JDialog frame = new JDialog();
		
		final ESListSearch search = new ESListSearch(frame, searchTable, view, true, true);
		frame.setTitle("Ricerca");
		frame.setMinimumSize(new Dimension(250, 400));
		frame.setModal(true);
		frame.setIconImage(ResourceUtils.createImageIcon("table_multiple.png").getImage());
		if( fillAllOnOpen ) {
			Rectangle bounds = view.getParent().getBounds();
			frame.setMinimumSize(new Dimension(bounds.width, bounds.height - 25));
			Point locationOnScreen = view.getParent().getLocationOnScreen();
			frame.setLocation(locationOnScreen.x, locationOnScreen.y);
		} else {
			Rectangle bounds = view.getBounds();
			frame.setMinimumSize(new Dimension(bounds.width, 200));
			Point locationOnScreen = view.getLocationOnScreen();
			frame.setLocation(locationOnScreen.x, locationOnScreen.y + bounds.height);
		}
		//chiusura della finestra alla pressione del tasto ESC
		frame.getRootPane().registerKeyboardAction(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	search.actSelection.executeAction();
	        }
	    },  KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_IN_FOCUSED_WINDOW);

		frame.setVisible(true);
		return null;
	}

}
