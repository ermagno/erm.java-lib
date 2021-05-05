package it.erm.graphics.swing.items.action;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.ESView;
import it.erm.lib.crud.dbm.Validation;

public class ESActionClear extends ESAction {

	private ESView view;

	public ESActionClear(ESView v) {
		super(ResourceUtils.getImageIcon("clear.gif", "pulisice il controllo"), null, "Pulisci");
		this.view = v;
	}
	
	@Override
	public Validation executeAction() {
		view.setValue(null);
		view.loadValue();
		return null;
	}

}
