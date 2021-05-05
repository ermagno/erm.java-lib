package it.erm.lib.crud;

public enum JDBCType {
	/**Identifies the generic SQL type ARRAY.*/
	ARRAY,
	/**Identifies the generic SQL type BIGINT.*/
	BIGINT,
	/**Identifies the generic SQL type BINARY.*/
	BINARY,
	/**Identifies the generic SQL type BIT.*/
	BIT,
	/**Identifies the generic SQL type BLOB.*/
	BLOB,
	/**Identifies the generic SQL type BOOLEAN.*/
	BOOLEAN,
	/**Identifies the generic SQL type CHAR.*/
	CHAR,
	/**Identifies the generic SQL type CLOB.*/
	CLOB,
	/**Identifies the generic SQL type DATALINK.*/
	DATALINK,
	/**Identifies the generic SQL type DATE.*/
	DATE,
	/**Identifies the generic SQL type DECIMAL.*/
	DECIMAL,
	/**Identifies the generic SQL type DISTINCT.*/
	DISTINCT,
	/**Identifies the generic SQL type DOUBLE.*/
	DOUBLE,
	/**Identifies the generic SQL type FLOAT.*/
	FLOAT,
	/**Identifies the generic SQL type INTEGER.*/
	INTEGER,
	/**Indicates that the SQL type is database-specific and gets mapped to a Java object that can be accessed via the methods getObject and setObject.*/
	JAVA_OBJECT,
	/**Identifies the generic SQL type LONGNVARCHAR.*/
	LONGNVARCHAR,
	/**Identifies the generic SQL type LONGVARBINARY.*/
	LONGVARBINARY,
	/**Identifies the generic SQL type LONGVARCHAR.*/
	LONGVARCHAR,
	/**Identifies the generic SQL type NCHAR.*/
	NCHAR,
	/**Identifies the generic SQL type NCLOB.*/
	NCLOB,
	/**Identifies the generic SQL value NULL.*/
	NULL,
	/**Identifies the generic SQL type NUMERIC.*/
	NUMERIC,
	/**Identifies the generic SQL type NVARCHAR.*/
	NVARCHAR,
	/**Indicates that the SQL type is database-specific and gets mapped to a Java object that can be accessed via the methods getObject and setObject.*/
	OTHER,
	
	PASSWORD,
	/**Identifies the generic SQL type REAL.*/
	REAL,
	/**Identifies the generic SQL type REF.*/
	REF,
	/**Identifies the generic SQL type REF_CURSOR.*/
	REF_CURSOR,
	/**Identifies the SQL type ROWID.*/
	ROWID,
	/**Identifies the generic SQL type SMALLINT.*/
	SMALLINT,
	/**Identifies the generic SQL type SQLXML.*/
	SQLXML,
	/**Identifies the generic SQL type STRUCT.*/
	STRUCT,
	/**Identifies the generic SQL type TIME.*/
	TIME,
	/**Identifies the generic SQL type TIME_WITH_TIMEZONE.*/
	TIME_WITH_TIMEZONE,
	/**Identifies the generic SQL type TIMESTAMP.*/
	TIMESTAMP,
	/**Identifies the generic SQL type TIMESTAMP_WITH_TIMEZONE.*/
	TIMESTAMP_WITH_TIMEZONE,
	/**Identifies the generic SQL type TINYINT.*/
	TINYINT,
	/**Identifies the generic SQL type VARBINARY.*/
	VARBINARY,
	/**Identifies the generic SQL type VARCHAR.*/
	VARCHAR;

}