import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeField {
   private final int numFigure;
   private int tableID;
   private int tableBound;
   private int fieldBound;
   private int dataType;
   private int varcharValue;
   private final String name;
   private String defaultValue;
   private boolean disallowNull, isPrimaryKey;
   private static final String[] strDataType = {"Varchar", "Boolean", "Integer", "Double"};
   public static final int VARCHAR_DEFAULT_LENGTH = 1;
   public static Logger logger = LogManager.getLogger(EdgeField.class.getName());
   
   public EdgeField(String inputString) {
      logger.debug("EdgeField constructor called with the input of %s", inputString);
      StringTokenizer st = new StringTokenizer(inputString, EdgeConvertFileParser.DELIM);
      numFigure = Integer.parseInt(st.nextToken());
      name = st.nextToken();
      tableID = 0;
      tableBound = 0;
      fieldBound = 0;
      disallowNull = false;
      isPrimaryKey = false;
      defaultValue = "";
      varcharValue = VARCHAR_DEFAULT_LENGTH;
      dataType = 0;
   }
   
   public int getNumFigure() {
      logger.debug("getNumFigure has been called.");
      return numFigure;
   }
   
   public String getName() {
      logger.debug("getName has been called.");
      return name;
   }
   
   public int getTableID() {
      logger.debug("getTableID has been called.");
      return tableID;
   }
   
   public void setTableID(int value) {
      logger.debug("setTableID has been called with a value of %s", value);
      tableID = value;
   }
   
   public int getTableBound() {
      logger.debug("getTableBound has been called.");
      return tableBound;
   }
   
   public void setTableBound(int value) {
      logger.debug("setTableBound has been called with a value of %s", value);
      tableBound = value;
   }

   public int getFieldBound() {
      logger.debug("getFieldBound has been called.");
      return fieldBound;
   }
   
   public void setFieldBound(int value) {
      logger.debug("setFieldBound has been called with a value of %s", value);
      fieldBound = value;
   }

   public boolean getDisallowNull() {
      logger.debug("getDisallowNull has been called.");
      return disallowNull;
   }
   
   public void setDisallowNull(boolean value) {
      logger.debug("setDisallowNull has been called with a value of %s", value);
      disallowNull = value;
   }
   
   public boolean getIsPrimaryKey() {
      logger.debug("getIsPrimaryKey has been called.");
      return isPrimaryKey;
   }
   
   public void setIsPrimaryKey(boolean value) {
      logger.debug("setIsPrimaryKey has been called with a value of %s", value);
      isPrimaryKey = value;
   }
   
   public String getDefaultValue() {
      logger.debug("getDefaultValue has been called.");
      return defaultValue;
   }
   
   public void setDefaultValue(String value) {
      logger.debug("setDefaultValue has been called with a value of %s", value);
      defaultValue = value;
   }
   
   public int getVarcharValue() {
      logger.debug("getVarcharValue has been called.");
      return varcharValue;
   }
   
   public void setVarcharValue(int value) {
      logger.debug("setVarcharValue has been called with a value of %s", value);
      if (value > 0) {
         varcharValue = value;
      }
      else
      {
         logger.warn("setVarcharValue called with a value less than or equal to 0.");
      }
   }
   public int getDataType() {
      logger.debug("getDataType has been called.");
      return dataType;
   }
   
   public void setDataType(int value) {
      logger.debug("setDataType has been called with a value of %s", value);
      if (value >= 0 && value < strDataType.length) {
         dataType = value;
      }
      else
      {
         logger.warn("SetDataType called with a value less than 0, or greater than the length of the string.");
      }
   }
   
   public static String[] getStrDataType() {
      logger.debug("getStrDataType has been called.");
      return strDataType;
   }
   
   public String toString() {
      logger.info("toString has been called.");
      return numFigure + EdgeConvertFileParser.DELIM +
      name + EdgeConvertFileParser.DELIM +
      tableID + EdgeConvertFileParser.DELIM +
      tableBound + EdgeConvertFileParser.DELIM +
      fieldBound + EdgeConvertFileParser.DELIM +
      dataType + EdgeConvertFileParser.DELIM +
      varcharValue + EdgeConvertFileParser.DELIM +
      isPrimaryKey + EdgeConvertFileParser.DELIM +
      disallowNull + EdgeConvertFileParser.DELIM +
      defaultValue;
   }
}
