package it.erm.lib.crud.dbo.formatter;

import it.erm.lib.crud.dbo.DBField;
import it.erm.lib.crud.dbo.DBFieldFK;

public class ForeignKeyFormatter implements DisplayFormatter {

	@Override
	public String getDisplayValue(DBField f) {
		String result = "";
		if( f.getInteger() > 0 ) {
			DBFieldFK<?> fk = (DBFieldFK<?>) f;
			result = fk.getForeignKeyTable().getDescription((Integer) f.getValue());
		}
		return result;
	}

    @Override
    public String getDisplayPreviousValue(DBField f) {
        String result = "";
        if( f.getPreviousInteger() > 0 ) {
            DBFieldFK<?> fk = (DBFieldFK<?>) f;
            result = fk.getForeignKeyTable().getDescription((Integer) f.getPreviousValue());
        }
        return result;
    }

}
