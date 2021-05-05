package it.erm.graphics.swing.items.tab;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.ESAction;
import it.erm.graphics.swing.ESActionBar;
import it.erm.lib.crud.dbm.Validation;

import java.awt.*;

import javax.swing.*;

public class ESTabbedPane extends JTabbedPane {

	private int count = 0;
	
	public ESTabbedPane(JFrame frame) {
		super(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		JPanel container = new JPanel(new GridLayout(1, 1));
		container.add(this);
		container.setOpaque(false);
		setOpaque(false);
		frame.add(container, BorderLayout.CENTER);
	} 
	
	
	public void addItem(JPanel parent, String title) {
		
		add(title, parent);
		GridBagLayout gbl = new GridBagLayout();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.gridwidth = 3;
//		gbc.gridx = 0;
		final JPanel panel = new JPanel(gbl);
		panel.setOpaque(false);
		ESActionBar actionBar = new ESActionBar();
//		actionBar.setOpaque(true);
		actionBar.addAction(new ESAction(ResourceUtils.getImageIcon("cancel.png", "chiudi..."), null, "Chiudi") {
			
			@Override
			public Validation executeAction() {
				int i = indexOfTabComponent(panel);
				removeItem(i);
				return null;
			}
		});
		gbc.insets = new Insets(0, 0, 0, 5);
		panel.add(new JLabel() {
			@Override
			public String getText() {
                int i = indexOfTabComponent(panel);
                if (i != -1) {
                    return getTitleAt(i);
                }
                return null;
			}
		}, gbc);
//		gbc.weightx = 0.5;
//		gbc.gridy = 2;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_END;
		panel.add(actionBar, gbc);

		setTabComponentAt(count, panel);
		setSelectedIndex(count);
		count++;
	}
	
	@Override
	public void remove(int index) {
		super.remove(index);
		count--;
	}
	
	public void removeItem(int index) {
		remove(index);
	}
	
	
	
}
