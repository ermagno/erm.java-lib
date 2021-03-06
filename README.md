# erm.java-lib

main classes example:
<pre>

public class ExamplePropertyUtil extends PropertyUtils {
    public ExamplePropertyUtil() {
        //this is the name of .properties file
        DefaultName = "ExamplePropertyUtil";
    }
    
    @Override
    protected void addProperties() {
        super.addProperties();
        addProperty(PropertyUtils.DATABASE_USER, "ExamplePropertyUtil");
    }
}
  
public class DBExample extends DBTable {

    public DBFieldString				desc 		    = new DBFieldString(this, "descrizione", true);
    public DBFieldString				nome			= new DBFieldString(this, "nome", true);
    // Foreign Key 
    // point to another table, i.e. DBDBExample2
    public DBFieldFK<DBExample2>	id_example2	        = new DBFieldFK<DBExample2>(this, DBExample2.class, "",    true);
    // Enum type stored to db as integer, Tipo implements EnumInteger 
    public DBFieldEnum<Tipo>	tipo 			        = new DBFieldEnum<Tipo>(this, Tipo.class, "tipo", true, false);
    public DBFieldInteger             num 				= new DBFieldInteger(this, "num", true);
    
    public DBExample() {
       super("Example", true /** primary key field auto management */ );
       setDisplayFields(desc, id_example2, tipo, num);
       useCache = true;
    }
}

public class ExampleDatabaseManager extends DatabaseManager {
    public ExampleDatabaseManager() {
       setDatabaseProperties();
       initTable(DBExample.class);
       initTable(DBExample2.class);
    }
}

public static void main(String[] args) {
    //first initialize properties file
    PropertyUtils.initialize(ExamplePropertyUtil.class, null);
    //second initialize database
    new ExampleDatabaseManager();

    //finally, use them
    JFrame frame = new JFrame("Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ESFormStandardEdit frm = new ESFormStandardEdit(frame, false, new DBExample(), 1, true);
    frame.getContentPane().add(frm, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
   
}
</pre>
