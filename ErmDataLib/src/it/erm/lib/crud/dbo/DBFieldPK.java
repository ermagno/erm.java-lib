package it.erm.lib.crud.dbo;

import it.erm.lib.crud.DBFieldKey;
import it.erm.lib.crud.JDBCType;

public class DBFieldPK extends DBFieldKey {

    public DBFieldPK(DBTable tableOwner, String name, JDBCType tipo, boolean canBeNull) {
        super(tableOwner, name, tipo, canBeNull);
    }

    public boolean isPrimaryKey() {
        return true;
    }

}
