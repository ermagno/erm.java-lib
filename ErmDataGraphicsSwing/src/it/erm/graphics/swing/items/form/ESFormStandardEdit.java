package it.erm.graphics.swing.items.form;

import it.erm.graphics.ResourceUtils;
import it.erm.graphics.swing.*;
import it.erm.graphics.swing.items.action.ESActionSearch;
import it.erm.graphics.swing.items.ctrl.*;
import it.erm.lib.crud.dbm.EnumInteger;
import it.erm.lib.crud.dbm.Validation;
import it.erm.lib.crud.dbo.*;
import it.erm.lib.utils.Utils;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class ESFormStandardEdit extends ESView {
	private   Container         parent;
	protected DBTable           table;
	private   ESActionBar       actionBar     = null;
	
	private   Positioner        positioner;
	
	private   ArrayList<ESCtrl> ctrlList      = new ArrayList<ESCtrl>();
	private   ESAction			actNuovo; 
	private   ESAction			actSalva; 
	private   ESActionSearch	actCerca; 
	private   ESAction			actElimina; 

	public ESFormStandardEdit() {}
	
	public ESFormStandardEdit(Container parent, boolean insertInParent, DBTable table, int column, boolean autoPos) {
	    init(parent, insertInParent, table, column, autoPos);
	}
	
	public boolean isModified(Validation v) {
	    boolean modified = table.isModified(v);
	    return modified;
	}
	
	public void init(Container parent, boolean insertInParent, DBTable table, int column, boolean autoPos) {
		setLayout(new BorderLayout());
		this.parent = parent;
		this.table = table;
		table.addTableListener(new DBTableAdapter() {
			@Override
			public void onOpenKey(int id) {
				loadValue();
			}
			
			@Override
			public void onReset() {
				loadValue();
			}
		});
		
		setOpaque(false);
		
		JPanel controls = new JPanel();
		controls.setOpaque(false);
		
		if( autoPos ) {
			positioner = new Positioner(controls, column, table.getFields().size(), true);
			for( DBField f: table.getFields() ) {
				if( table.getIdField() != f ) {
					Component view = register(f);
					ctrlList.add((ESCtrl) view);
					if( positioner != null ) {
					    positioner.add(view);
					}
				}
			}
		}
		//é fondamentale indicare al contenitore di mettere i controlli in alto e la barra in basso
		add(controls, BorderLayout.NORTH); 

		actionBar = createActionBar();
		actionBar.setOpaque(true);
		add(actionBar, BorderLayout.SOUTH);
		if( this.parent != null && insertInParent ) {
			this.parent.setLayout(new GridBagLayout());
			this.parent.add(this, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		if( table.getId() > 0 ) {
		    loadValue();
		}
	}
	
	public Component register(DBField f) {
		Component view = null;
		switch(f.getJDBCType()) {
		case BOOLEAN: {
			view = new ESCtrlBoolean(f);
			break;
		}
		case TIMESTAMP: {
			view = new ESCtrlDateTime(f);
			break;
		}
		case DOUBLE:
		case INTEGER: {
			if( f.isForeignKey() ) {
				view = new ESCtrlFK(f);
				break;
			} else if( f instanceof DBFieldEnum ) {
				view = new ESCtrlEnum((DBFieldEnum<EnumInteger>) f);
				break;
			} else {
                view = new ESCtrlInt((DBFieldInteger) f);
                break;
			}
		}
		case VARCHAR:
		    try {
		        view = new ESCtrlString((DBFieldString) f);
            } catch( Exception e ) {
                e.printStackTrace();
            }
		    break;
		case PASSWORD:
		    try {
		        view = new ESCtrlPassword((DBFieldPassword) f);
		    } catch( Exception e ) {
		        e.printStackTrace();
		    }
            break;
		default:
			break;
		}
		return view;
	}
	
	public DBTable getTable() {
		return table;
	}
	
	private ESActionBar createActionBar() {
		ESActionBar actionBar = new ESActionBar();
        actionBar.setSpacing(10, 10);
        actNuovo = new ESAction(ResourceUtils.getImageIcon("table_add.png", "nuovo..."), "Nuovo", "Nuovo") {
			@Override
			public Validation executeAction() {
			    boolean reset = true;
			    Validation v = new Validation();
                boolean modified = isModified(v);
                if( modified ) {
                    String ObjButtons[] = {" Si "," No "};
                    int PromptResult  = JOptionPane.showOptionDialog(parent, v.getMsgAsString(Utils.NewLine()) + Utils.NewLine() + "Resettare senza salvare?","Modifica non salvata",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[0]);
                    reset = PromptResult == JOptionPane.YES_OPTION;
                }
                if( reset ) {
                    table.reset();
                }
				return null;
			}
        };
        
        
        
        actSalva = new ESAction(ResourceUtils.getImageIcon("table_save.png", "salva..."), "Salva", "Salva") {
			@Override
			public Validation executeAction() {
				onSalva();
				return null;
			}
        };

		actElimina = new ESAction(ResourceUtils.getImageIcon("table_delete.png", "elimina..."), "Elimina", "Elimina") {
			@Override
			public Validation executeAction() {
				onElimina();
				return null;
			}
		};
        
		actCerca = new ESActionSearch(table, this, true);
		
        actionBar.addAction(actSalva);
        actionBar.addAction(actCerca);
        actionBar.addAction(actNuovo);
        actionBar.addAction(actElimina);

        return actionBar;
	}

	
	private class Positioner {
		Container container;
		int column;
		int rows;
		GridBagConstraints constrains;
		boolean upBottom = false;
		
		public Positioner(Container container, int column, int totFields, boolean upBottom) {
			this.container = container;
			this.column = column;
			this.upBottom = upBottom;
			this.container.setLayout(new GridBagLayout());
			
//			GridBagConstraints cl = new GridBagConstraints();
//			cl.gridx = 0;
//			cl.gridy = 0;
//			cl.gridwidth = column;
//			cl.weightx = 1;
//			cl.weighty = 0;
////			cl.fill = GridBagConstraints.HORIZONTAL;
////			cl.anchor = GridBagConstraints.CENTER;
//			cl.insets = new  Insets(15, 0, 10, 0);
//			JLabel lbl = new JLabel(table.getName());
//			lbl.setBorder(BorderFactory.createCompoundBorder());
//			container.add(lbl, cl);
			
			constrains = new GridBagConstraints();
			constrains.gridx = 0;
			constrains.gridy = 1;
			constrains.weightx = 1;
			constrains.weighty = 0;
			constrains.fill = GridBagConstraints.HORIZONTAL;
			constrains.insets = new  Insets(15, 0, 0, 0);

			
			
			rows = (totFields/column) +1;
			boolean addRows = totFields%column > 0;
			if( addRows )
				rows++;
		}

		public void add(Component view) {
			container.add(view, constrains);
			if( upBottom ) {
				if( constrains.gridy == rows-1 ) {
					constrains.gridy = 1;
					constrains.gridx++;
				} else {
					constrains.gridy++;
				}
			} else {
				if( constrains.gridx == column-1 ) {
					constrains.gridx = 0;
					constrains.gridy++;
				} else {
					constrains.gridx++;
				}
			}
		}
	}

	@Override
	public void setValue(Object value) {
		table.openKey((Integer) value);
	}

	@Override
	public Object getValue() {
		return table.getId();
	}

	@Override
	public void loadValue() {
		for( ESCtrl ctrl: ctrlList ) {
			ctrl.loadValue();
		}		
	}

	public ArrayList<ESCtrl> getCtrlList() {
        return ctrlList;
    }
	
	private void onSalva() { 
		if( table.isLoaded() ) {
			Validation validateUpdate = table.validateUpdate();
			if( !validateUpdate.isValid() ) {
				JOptionPane.showMessageDialog(
						this, 
						"Impossibile aggiornare " + table.getDescription(table.getId()) + ":\n" + validateUpdate.getMsgAsString("\n"),
						" Errore",
						JOptionPane.ERROR_MESSAGE);
			} else {
				table.update(null);
			}
		} else {
			Validation validateInsert = table.validateInsert();
			if( !validateInsert.isValid() ) {
				JOptionPane.showMessageDialog(
						this, 
						"Impossibile inserire il dato:\n" + validateInsert.getMsgAsString("\n"),
						" Errore",
						JOptionPane.ERROR_MESSAGE);
			} else {
				table.insert(null);
			}
		}
		loadValue();
	}
	
	private void onElimina() {
		if( table.isLoaded() ) {
		    Validation validateDelete = table.validateDelete();
		    if( !validateDelete.isValid() ) {
		        JOptionPane.showMessageDialog(
		                this, 
		                "Impossibile eliminare il dato:\n" + validateDelete.getMsgAsString("\n"),
		                " Errore",
		                JOptionPane.ERROR_MESSAGE);
		        return;
		    }
		    
			int response = JOptionPane.showConfirmDialog(
					this, 
					"Eliminare " + table.getDescription(table.getId()) + "?", 
					"Elimina", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				table.delete(null);
				JOptionPane.showMessageDialog(this, "Eliminazione completata");
			} else {
				JOptionPane.showMessageDialog(this, table.getDescription(table.getId()) + "\nNON eliminato");
			}
		} else {
			actNuovo.executeAction();
		}
	}

	@Override
	public Object onFocusLost(DBField f, JComponent textField) {
		return table.getId();
	}

	@Override
	public void onFocusGained(DBField f, JComponent textField) {
		// TODO Auto-generated method stub
		
	}

	public ESActionBar getActionBar() {
        return actionBar;
    }
	
	public ESAction getActNuovo() {
        return actNuovo;
    }
	
	public ESActionSearch getActCerca() {
        return actCerca;
    }
	
	public ESAction getActSalva() {
        return actSalva;
    }
	
	public ESAction getActElimina() {
        return actElimina;
    }
}
