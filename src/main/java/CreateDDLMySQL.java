import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateDDLMySQL extends EdgeConvertCreateDDL {

   protected String databaseName;
   //this array is for determining how MySQL refers to datatypes
   protected String[] strDataType = {"VARCHAR", "BOOL", "INT", "DOUBLE"};
   protected StringBuffer sb;
   private static Logger logger = LogManager.getLogger(CreateDDLMySQL.class.getName());

   public CreateDDLMySQL(EdgeTable[] inputTables, EdgeField[] inputFields) {
      super(inputTables, inputFields);
      logger.debug("CreateDDLMySQL.constructor, super called with fields: " + inputTables + inputFields);
      sb = new StringBuffer();
   } //CreateDDLMySQL(EdgeTable[], EdgeField[])
   
   public CreateDDLMySQL() {
      // default constructor with empty arg list for to allow output
      // dir to be set before there are table and field objects
      logger.debug("Created no-arg CreateDDLMySQL.");
   }
   
   public void createDDL() {
      logger.debug("createDLL() method was called.");

      EdgeConvertGUI.setReadSuccess(true);

      databaseName = generateDatabaseName();

      logger.debug("Database name is " + databaseName);

      sb.append("CREATE DATABASE ").append(databaseName).append(";\r\n");
      sb.append("USE ").append(databaseName).append(";\r\n");
      for (int boundCount = 0; boundCount <= maxBound; boundCount++) {
         //process tables in order from least dependent (least number of bound tables) to most dependent
         for (int tableCount = 0; tableCount < numBoundTables.length; tableCount++) {
            //step through list of tables
            if (numBoundTables[tableCount] == boundCount) { //

               logger.debug("Outputting the current DDL table: " + tables[tableCount].getName());

               sb.append("CREATE TABLE ").append(tables[tableCount].getName()).append(" (\r\n");

               logger.debug("Appended CREATE TABLE " + tables[tableCount].getName() + " (\r\n");

               int[] nativeFields = tables[tableCount].getNativeFieldsArray();
               int[] relatedFields = tables[tableCount].getRelatedFieldsArray();
               boolean[] primaryKey = new boolean[nativeFields.length];
               int numPrimaryKey = 0;
               int numForeignKey = 0;
               for (int nativeFieldCount = 0; nativeFieldCount < nativeFields.length; nativeFieldCount++) {
                  //print out the fields
                  EdgeField currentField = getField(nativeFields[nativeFieldCount]);

                  logger.debug("Grabbed current field: %s", getField(nativeFields[nativeFieldCount]).getName());

                  sb.append("\t").append(currentField.getName()).append(" ").append(strDataType[currentField.getDataType()]);
                  if (currentField.getDataType() == 0) { //varchar
                     sb.append("(").append(currentField.getVarcharValue()).append(")");
                     logger.debug("Added Varchar value to string buffer.");
                     //append varchar length in () if data type is varchar
                  }
                  if (currentField.getDisallowNull()) {
                     logger.debug("Added NOT NULL attribute to string buffer.");
                     sb.append(" NOT NULL");
                  }
                  if (!currentField.getDefaultValue().equals("")) {
                     if (currentField.getDataType() == 1) { //boolean data type
                        sb.append(" DEFAULT ").append(convertStrBooleanToInt(currentField.getDefaultValue()));
                        logger.debug("Added Boolean default value to string buffer.");
                     } else { //any other data type
                        sb.append(" DEFAULT ").append(currentField.getDefaultValue());
                        logger.debug("Added a non-boolean default value to string buffer.");
                     }
                  }
                  if (currentField.getIsPrimaryKey()) {
                     primaryKey[nativeFieldCount] = true;
                     numPrimaryKey++;
                     logger.debug("Current field is a primary key.");
                  } else {
                     primaryKey[nativeFieldCount] = false;
                  }
                  if (currentField.getFieldBound() != 0) {
                     numForeignKey++;
                     logger.debug("Current field is a foreign key.");
                  }
                  sb.append(",\r\n"); //end of field
               }
               sb.deleteCharAt(sb.length() - 3);
               if (numPrimaryKey > 0) { //table has primary key(s)
                  sb.append("CONSTRAINT ").append(tables[tableCount].getName()).append("_PK PRIMARY KEY (");
                  for (int i = 0; i < primaryKey.length; i++) {
                     if (primaryKey[i]) {
                        sb.append(getField(nativeFields[i]).getName());
                        numPrimaryKey--;
                        if (numPrimaryKey > 0) {
                           sb.append(", ");
                        }
                     }
                  }
                  sb.append(")");
                  if (numForeignKey > 0) {
                     sb.append(",");
                  }
                  sb.append("\r\n");
               }
               
               if (numForeignKey > 0) { //table has foreign keys
                  int currentFK = 1;
                  for (int i = 0; i < relatedFields.length; i++) {
                     if (relatedFields[i] != 0) {
                        sb.append("CONSTRAINT ").append(tables[tableCount].getName())
                          .append("_FK").append(currentFK).append(" FOREIGN KEY(")
                          .append(getField(nativeFields[i]).getName()).append(") REFERENCES ")
                          .append(getTable(getField(nativeFields[i]).getTableBound()).getName())
                          .append("(").append(getField(relatedFields[i]).getName()).append(")");
                        if (currentFK < numForeignKey) {
                           sb.append(",\r\n");
                        }
                        currentFK++;
                     }
                  }
                  sb.append("\r\n");
               }
               sb.append(");\r\n\r\n"); //end of table
            }
         }
      }
   }

   protected int convertStrBooleanToInt(String input) { //MySQL uses '1' and '0' for boolean types
      if (input.equals("true")) {
         return 1;
      } else {
         return 0;
      }
   }
   
   public String generateDatabaseName() { //prompts user for database name
      String dbNameDefault = "MySQLDB";
      logger.info("generateDatabaseName() method was called. Prompting for name.");
      //String databaseName = "";

      do {
         databaseName = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the database name:",
                       "Database Name",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       dbNameDefault);
         logger.info("Inputted database name: " + String.format("Databse name: %s", databaseName));
         if (databaseName == null) {
            EdgeConvertGUI.setReadSuccess(false);
            return "";
         }
         if (databaseName.equals("")) {
            logger.warn("Database has no name. You must select a name for your database.");
            JOptionPane.showMessageDialog(null,
              "You must select a name for your database.");
         }
      } while (databaseName.equals(""));
      return databaseName;
   }
   
   public String getDatabaseName() {
      return databaseName;
   }
   
   public String getProductName() {
      return "MySQL";
   }

   public String getSQLString() {
      createDDL();
      return sb.toString();
   }
   
}//EdgeConvertCreateDDL
