package it.erm.lib.crud;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBTable;

public class DBFieldKey extends DBField {

    
    public DBFieldKey(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
        super(tableOwner, name, tipo, canBeNull);
    }

    @Override
    public void setInteger(int id) {
        if( id <= 0 ) {
            setValue(null);
        } else {
            super.setInteger(id);
        }
    }
    
    @Override
    public int getInteger() {
        if( isNull() ) {
            return -1;
        }
        return super.getInteger();
    }

}
