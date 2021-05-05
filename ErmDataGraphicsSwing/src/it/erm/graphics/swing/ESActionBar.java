package it.erm.graphics.swing;

import it.erm.graphics.EAction;
import it.erm.graphics.EActionBar;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class ESActionBar extends JToolBar implements EActionBar {

	private ArrayList<EAction> actions = null;
	int before = 2;
	int after = 2;
	
	public ESActionBar(int Orientation, boolean addBorder) {
		super(Orientation);
		actions = new ArrayList<EAction>();
//		if( addBorder ) {
//			setBorder(new EtchedBorder());
//		}
//		setBorder(BorderFactory.createEmptyBorder());
		setFloatable(false);
//		setRollover(true);
//		setOrientation(Orientation);
		setOpaque(false);
		setBorderPainted(false);
	}

	public void setSpacing(int before, int after) {
		this.before = before;
		this.after = after;
	}
	
	public ESActionBar() {
		this(HORIZONTAL, false);
	}
	
	public void addAction(EAction act) {
		actions.add(act);
		if( act instanceof ESAction ) {
		    ((ESAction)act).setBarContainer(this);
		}
		JButton actBtn = ((ESAction)act).getButton();
		Insets i = null;
		if( getOrientation() == HORIZONTAL ) {
			i = new Insets(2, before, 2, after);
		} else {
			i = new Insets(before, 5, after, 5);
		}
		
//		actBtn.setUI(new BasicButtonUI());
		actBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(true);
	            }
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(false);
	            }
			}
		});
		actBtn.setBorderPainted(false);
		
		actBtn.setMargin(i);
		actBtn.setAlignmentX(CENTER_ALIGNMENT);
		actBtn.setAlignmentY(CENTER_ALIGNMENT);
		actBtn.setRolloverEnabled(true);
		actBtn.setOpaque(false);
		add(actBtn);
	}
	
	public void removeAction(ESAction act) {
	    remove(act.getButton());
	}

	
	@Override
	public List<EAction> getActions() {
		return actions;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(150, 600);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
	    for( EAction act: getActions() ) {
	        ((ESAction)act).setEnabled(enabled);
	    }
	}
	
}
