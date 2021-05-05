package it.erm.lib.crud.dbm;

import it.erm.lib.utils.Utils;

import java.util.ArrayList;

public class Validation {

	private boolean valid = true;
	private ArrayList<String> msg = new ArrayList<String>(3);
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void addMsg(boolean setValid, String... msg) {
		setValid(setValid);
		addMsg(msg);
	}
	
	public void addMsg(String... msg) {
		if( msg != null && msg[0] != null ) {
			StringBuilder sb = new StringBuilder();
			for( String m: msg ) {
			    if( !Utils.isNull(m) ) {
			        sb.append(m);
			    }
			}
			if( !Utils.isNull(sb.toString()) ) {
			    this.msg.add(sb.toString());
			}
		}
	}

	public ArrayList<String> getMsg() {
		return msg;
	}

	public String getMsgAsString(String sep) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( int i=0; i<msg.size(); i++ ) {
			if( !first ) {
			    if( !Utils.isNull(sep) ) {
			        sb.append(sep);
			    }
			}
			sb.append(msg.get(i));
			first = false;
		}
		return sb.toString();
	}

    public void add(Validation v) {
        if( v != null && !v.msg.isEmpty() ) {
            for( String s: v.msg ) {
                addMsg(s);
            }
        }
        if( isValid() && !v.isValid() ) {
            setValid(false);
        }
    }
	
	
}
