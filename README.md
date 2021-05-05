# erm.java-lib

ErmDataLib
  main classes example:
<pre>

public class ExamplePropertyUtil extends PropertyUtils {
    public ExamplePropertyUtil() {
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
    public DBFieldString				nome				= new DBFieldString(this, "nome", true);
    /** Foreign Key pointing to DBDBExample2 (extends DBTable) */
    public DBFieldFK<DBExample2>	id_example2	= new DBFieldFK<DBExample2>(this, DBExample2.class, "",    true);
    /** Tipo implements EnumInteger */
    public DBFieldEnum<Tipo>	tipo 			        = new DBFieldEnum<Tipo>(this, Tipo.class, "tipo", true, false);
    public DBFieldInteger             num 				          = new DBFieldInteger(this, "num", true);
    
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
    PropertyUtils.initialize(ExamplePropertyUtil.class, null);
    new ExampleDatabaseManager();

    JFrame frame = new JFrame("Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ItUtilsFormStandardEdit frm = new ItUtilsFormStandardEdit(frame, false, new DBExample(), 1, true);
    frame.getContentPane().add(frm, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
   
}
</pre>
