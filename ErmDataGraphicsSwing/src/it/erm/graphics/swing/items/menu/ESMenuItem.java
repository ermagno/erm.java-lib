package it.erm.graphics.swing.items.menu;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import it.erm.graphics.swing.ESAction;

public class ESMenuItem {

	public enum TipoMenuItem {
		ACTION,
		RADIO,
		CHECK;
	}
	
	private JMenuItem menuItem = null;
	private ButtonGroup radioGroup;
	
	public ESMenuItem(ButtonGroup group, ESAction act, TipoMenuItem TMI) {
		radioGroup = group;
		try {
			switch (TMI) {
			case ACTION:
				menuItem = new JMenuItem(act);
				break;
			case CHECK:
				menuItem = new JCheckBoxMenuItem(act);
				break;
			case RADIO:
				menuItem = new JRadioButtonMenuItem(act);
				radioGroup.add(menuItem);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			menuItem.setText(act.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			//TODO: gestire gli eventi al cambio di stato dell'azione
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JMenuItem getMenuItem() {
		return menuItem;
	}
	
	
	
}
