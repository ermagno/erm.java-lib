package it.erm.graphics.swing.items.menu;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;

import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.items.menu.ESMenuItem.TipoMenuItem;

public class ESMenuBar extends JMenuBar {

	public ESMenuBar() {}
	
	public ESMenu addMenu(String desc) {
		ESMenu menu = new ESMenu(desc);
		add(menu);
		return menu;
	}
	
	public ESMenu addSubMenu(ESMenu menu, String desc) {
		ESMenu subMenu = new ESMenu(desc);
		menu.add(subMenu);
		return subMenu;
	}
	
	public ESMenuItem addMenuItem(ESMenu menu, ESAction act) {
		return addMenuItem(menu, act, TipoMenuItem.ACTION);
	}

	public ButtonGroup addMenuItemRadio(ESMenu menu, ESAction... acts) {
		menu.addSeparator();
		ButtonGroup buttonGroup = menu.addMenuItemRadio(acts);
		menu.addSeparator();
		return buttonGroup;
	}
	
	public ESMenuItem addMenuItem(ESMenu menu, ESAction act, TipoMenuItem TMI) {
		ESMenuItem menuItem = menu.addMenuItem(act, TMI);
		return menuItem;
	}

}
