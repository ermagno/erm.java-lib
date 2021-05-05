package it.erm.graphics.swing.items.menu;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.items.menu.ESMenuItem.TipoMenuItem;

public class ESMenu extends JMenu {

	public ESMenu(String desc) {
		super(desc);
	}
	
	ESMenuItem addMenuItem(ESAction act, TipoMenuItem TMI) {
		if( TMI == TipoMenuItem.RADIO ) {
			System.out.println("Usare il metodo addMenuItemRadio");
			return null;
		}
		ESMenuItem item = new ESMenuItem(null, act, TMI);
		add(item.getMenuItem());
		return item;
	}
	
	ButtonGroup addMenuItemRadio(ESAction... acts) {
		ButtonGroup radioGroup = new ButtonGroup();
		for( ESAction act: acts ) {
			ESMenuItem item = new ESMenuItem(radioGroup, act, TipoMenuItem.RADIO);
			add(item.getMenuItem());
		}
		return radioGroup;
	}
}
