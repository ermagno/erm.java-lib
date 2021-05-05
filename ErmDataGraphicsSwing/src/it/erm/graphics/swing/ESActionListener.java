package it.erm.graphics.swing;

import it.erm.lib.crud.dbm.Validation;

public interface ESActionListener {

    public Validation beforeExecute(ESAction act);
    public void afterExecute(ESAction act, Validation v);
    
}
