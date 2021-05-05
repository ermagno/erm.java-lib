package it.erm.graphics.swing.component;

import it.erm.lib.crud.dbm.Validation;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

public abstract class ESTask<V> extends SwingWorker<Validation, V> {

	Component parent;
	ProgressMonitor pm;
	JProgressBar    pb;
	
	public ESTask(Component p) {
		parent = p;
	}
	
	public ProgressMonitor getProgressMonitor() {
		return pm;
	}
	
	public JProgressBar getProgressBar() {
		return pb;
	}
	
	public void configureProgressMonitor(ProgressMonitor pm) {
		this.pm = pm;
		pm.setProgress(0);
		addPropertyChange();
	}
	
	/**
	 * crea internamente un ProgressMonitor
	 * @param parent
	 * @param title
	 * @param note
	 * @param min
	 * @param max
	 */
	public void configureProgressMonitor(String title, String note) {
		pm = new ProgressMonitor(parent, title, note, 0, 100);
		pm.setProgress(0);
		pm.setMillisToPopup(50);
		pm.setMillisToDecideToPopup(20);
		addPropertyChange();
	}
	
	private void addPropertyChange() {
		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Object newValue = evt.getNewValue();
				if( newValue != StateValue.STARTED && newValue != StateValue.PENDING && newValue != StateValue.DONE ) {
					pm.setProgress((Integer) newValue);
				}
			}
		});
	}
	
	/**
	 * @param pb 
	 * @param min
	 * @param max
	 */
	public void configureProgrgessBar(JProgressBar pB, int min, int max) {
		this.pb = pB;
		pb.setMinimum(min);
		pb.setMaximum(max);
		pb.setStringPainted(true);
		addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Object newValue = evt.getNewValue();
				if( newValue != StateValue.STARTED && newValue != StateValue.PENDING && newValue != StateValue.DONE ) {
					pb.setValue((Integer) newValue);
				}
			}
		});
	}
	
	
	public void fillProgress(int progress) {
		if( pm != null ) {
			pm.setProgress(progress);
		}
		if( pb != null ) {
			pb.setValue(progress);
		}
		setProgress(progress);
	}
	
	@Override
	protected void done() {
		if( pm != null ) {
			pm.setProgress(100);
		}
		super.done();
		try {
			Validation v = get();
			if( v != null && !v.isValid() ) {
				JOptionPane.showMessageDialog(parent, v.getMsgAsString("\n"), " Attenzione", JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	protected T doInBackground() throws Exception {
//		publish("Inizio ricerca...");
//	}
//	
//	@Override
//	protected void process(List<String> chunks) {
//		for( String note: chunks ) {
//			pm.setNote(note);
//		}
//	}
}
