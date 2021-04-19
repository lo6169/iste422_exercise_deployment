import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertFileParser {
   protected final File file;
   protected BufferedReader br;
   protected final ArrayList<EdgeTable> alTables;
   protected final ArrayList<EdgeField> alFields;
   protected final ArrayList<EdgeConnector> alConnectors;
   private EdgeTable[] tables;
   private EdgeField[] fields;
   private EdgeConnector[] connectors;
   protected boolean isEntity;
   protected boolean isAttribute;
   protected boolean isUnderlined = false;
   protected int numFigure;
   protected int numConnector;
   // private int numNativeRelatedFields;
   // private int numLine;
   public static final String EDGE_ID = "EDGE Diagram File"; //first line of .edg files should be this
   public static final String SAVE_ID = "EdgeConvert Save File"; //first line of save files should be this
   public static final String DELIM = "|";

   static final Logger logger = LogManager.getLogger(EdgeConvertFileParser.class.getName());
   
   protected EdgeConvertFileParser(File constructorFile) {
      numFigure = 0;
      numConnector = 0;
      alTables = new ArrayList<>();
      alFields = new ArrayList<>();
      alConnectors = new ArrayList<>();
      isEntity = false;
      isAttribute = false;
      file = constructorFile;
      // numLine = 0;
      this.openFile(file);
   }
   
   protected void resolveConnectors() { //Identify nature of Connector endpoints
      int endPoint1;
      int endPoint2;
      int fieldIndex;
      int table1Index = 0;
      int table2Index = 0;
      for (EdgeConnector connector : connectors) {
         endPoint1 = connector.getEndPoint1();
         endPoint2 = connector.getEndPoint2();
         fieldIndex = -1;
         for (int fIndex = 0; fIndex < fields.length; fIndex++) { //search fields array for endpoints
            if (endPoint1 == fields[fIndex].getNumFigure()) { //found endPoint1 in fields array
               connector.setIsEP1Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint1 was found in
            }
            if (endPoint2 == fields[fIndex].getNumFigure()) { //found endPoint2 in fields array
               connector.setIsEP2Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint2 was found in
            }
         }
         for (int tIndex = 0; tIndex < tables.length; tIndex++) { //search tables array for endpoints
            if (endPoint1 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               connector.setIsEP1Table(true); //set appropriate flag
               table1Index = tIndex; //identify which element of the tables array that endPoint1 was found in
            }
            if (endPoint2 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               connector.setIsEP2Table(true); //set appropriate flag
               table2Index = tIndex; //identify which element of the tables array that endPoint2 was found in
            }
         }

         if (connector.getIsEP1Field() && connector.getIsEP2Field()) { //both endpoints are fields, implies lack of normalization
            JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + file + "\ncontains composite attributes. Please resolve them and try again.");
            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
            break; //stop processing list of Connectors
         }

         if (connector.getIsEP1Table() && connector.getIsEP2Table()) { //both endpoints are tables
            if ((connector.getEndStyle1().contains("many")) &&
              (connector.getEndStyle2().contains("many"))) { //the connector represents a many-many relationship, implies lack of normalization
               JOptionPane.showMessageDialog(null, "There is a many-many relationship between tables\n\"" + tables[table1Index].getName() + "\" and \"" + tables[table2Index].getName() + "\"" + "\nPlease resolve this and try again.");
               EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
               break; //stop processing list of Connectors
            } else { //add Figure number to each table's list of related tables
               tables[table1Index].addRelatedTable(tables[table2Index].getNumFigure());
               tables[table2Index].addRelatedTable(tables[table1Index].getNumFigure());
               continue; //next Connector
            }
         }

         if (fieldIndex >= 0 && fields[fieldIndex].getTableID() == 0) { //field has not been assigned to a table yet
            if (connector.getIsEP1Table()) { //endpoint1 is the table
               tables[table1Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table1Index].getNumFigure()); //tell the field what table it belongs to
            } else { //endpoint2 is the table
               tables[table2Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table2Index].getNumFigure()); //tell the field what table it belongs to
            }
         } else if (fieldIndex >= 0) { //field has already been assigned to a table
            JOptionPane.showMessageDialog(null, "The attribute " + fields[fieldIndex].getName() + " is connected to multiple tables.\nPlease resolve this and try again.");
            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
            break; //stop processing list of Connectors
         }
      } // connectors for() loop
   } // resolveConnectors()

   protected void makeArrays() { //convert ArrayList objects into arrays of the appropriate Class type
      if (alTables != null) {
         tables = alTables.toArray(new EdgeTable[alTables.size()]);
         // tables = (EdgeTable[])alTables.toArray(new EdgeTable[alTables.size()]);
      }
      if (alFields != null) {
         fields = alFields.toArray(new EdgeField[alFields.size()]);
         // fields = (EdgeField[])alFields.toArray(new EdgeField[alFields.size()]);
      }
      if (alConnectors != null) {
         connectors = alConnectors.toArray(new EdgeConnector[alConnectors.size()]);
         // connectors = (EdgeConnector[])alConnectors.toArray(new EdgeConnector[alConnectors.size()]);
      }
   }
   
   protected boolean isTableDup(String testTableName) {
      for (EdgeTable tempTable : alTables) {
         // EdgeTable tempTable = (EdgeTable)alTables.get(i);
         if (tempTable.getName().equals(testTableName)) {
            return true;
         }
      }
      return false;
   }
   
   public EdgeTable[] getEdgeTables() {
      return tables;
   }
   
   public EdgeField[] getEdgeFields() {
      return fields;
   }

   protected abstract void openFile(File inputFile);
   protected abstract void parseFile() throws IOException;

} // EdgeConvertFileHandler
