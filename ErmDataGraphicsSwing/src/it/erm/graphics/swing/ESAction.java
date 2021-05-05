package it.erm.graphics.swing;

import it.erm.graphics.EAction;
import it.erm.graphics.swing.component.ESTask;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.utils.Utils;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.*;


public abstract class ESAction extends AbstractAction implements EAction {

	private JButton btn;
	private ESActionBar barContainer;
	private int state = 0;
	ArrayList<String> texts = new ArrayList<String>(1);
	ArrayList<Icon>   icons = new ArrayList<Icon>(1);
	ESTask<Void> task;
	ArrayList<ESActionListener> listener = new ArrayList<ESActionListener>();
	public ESAction(Icon ico, String text, String tooltip) {
		super(text);
		if( ico != null ) {
			putValue(Action.SMALL_ICON, ico);
		}
		texts.add(text);
		icons.add(ico);
		setButton(new JButton(), tooltip);
	}
	
	public void addActionListener(ESActionListener ESAL) {
	    listener.add(ESAL);
	}
	
	private Validation notifyBeforeExecute(ESAction act) {
	    Validation v = new Validation();
	    for( ESActionListener l: listener ) {
	        Validation beforeExecute = l.beforeExecute(act);
	        if( !Utils.isNull(beforeExecute) ) {
	            v.add(beforeExecute);
	        }
	    }
	    return v;
	}

	private void notifyAfterExecute(ESAction act, Validation v) {
	    for( ESActionListener l: listener ) {
	        l.afterExecute(act, v);
	    }
	}

	public void setToolTipText(String toolTip) {
	    btn.setToolTipText(toolTip);
	}
	
	public ESTask<Void> getTask() {
		return task;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Component parent = btn;
		while( parent.getParent() != null ) {
			parent = parent.getParent();
			if( parent instanceof Window ) {
			    break;
			}
		}
		
		task = new ESTask<Void>(parent) {
			@Override
			protected Validation doInBackground() throws Exception {
			    btn.setEnabled(false);
			    if( barContainer != null ) {
			        barContainer.setEnabled(false);
			    }
			    setProgress(0);
			    Validation notifyBeforeExecute = notifyBeforeExecute(ESAction.this);
			    if( !Utils.isNull(notifyBeforeExecute) && !notifyBeforeExecute.isValid() ) {
			        return notifyBeforeExecute;
			    }
				Validation executeAction = null;
				try {
					executeAction = executeAction();
					notifyAfterExecute(ESAction.this, executeAction);
				} catch (Exception ex) {
					ex.printStackTrace();
					executeAction = new Validation();
					executeAction.addMsg(false, Utils.getFullStackTrace(ex));
				}
				setProgress(100);
				return executeAction;
			}
			@Override
			protected void done() {
				super.done();
                if( barContainer != null ) {
                    barContainer.setEnabled(true);
                }
				btn.setEnabled(true);
			}
			
		};
		task.configureProgressMonitor(getText(), " ");
		task.execute();
		state++;
		if( texts.size() <= state ) {
			state = 0;
		}
		setState(state);
	}

	public void setButton(JButton btn, String tooltip) {
		this.btn = btn;
		this.btn.setText(getText());
		this.btn.setAction(this);
		setToolTipText(tooltip);
	}
	
	public void setBarContainer(ESActionBar barContainer) {
        this.barContainer = barContainer;
    }
	
	public JButton getButton() {
		return btn;
	};

	public void addState(String text, Icon ico) {
		texts.add(text);
		icons.add(ico);
	}
	
	public void setState(int state) {
		if( this.state != state ) {
			//TODO: gestire eventi al cambio di stato
		}
		this.state = state;
		putValue(Action.NAME, texts.get(state));
		putValue(Action.SMALL_ICON, icons.get(state));
	}
	
	public int getState() {
		return state;
	}
	
	/**
	 * cambia il testo allo stato iniziale dell'azione
	 * @param text
	 */
	public void setText(String text) {
		String old = this.texts.remove(0);
		this.texts.add(0, text);
		putValue(Action.NAME, text);
	}
	
	public String getText() {
		return texts.get(state);
	}
}
