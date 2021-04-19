import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class SaveFileParser extends EdgeConvertFileParser {

  public SaveFileParser(File constructorFile) {
    super(constructorFile);
  }

  protected void parseFile() throws IOException { //this method is unclear and confusing in places
    int numFields;
    StringTokenizer stTables;
    StringTokenizer stNatFields;
    StringTokenizer stRelFields;
    // StringTokenizer stNatRelFields;
    StringTokenizer stField;
    EdgeTable tempTable;
    EdgeField tempField;
    String currentLine;
    br.readLine();
    currentLine = br.readLine(); //this should be "Table: "
    while (currentLine.startsWith("Table: ")) {
      numFigure = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); //get the Table number
      br.readLine(); //this should be "{"
      currentLine = br.readLine(); //this should be "TableName"
      String tableName = currentLine.substring(currentLine.indexOf(" ") + 1);
      tempTable = new EdgeTable(numFigure + DELIM + tableName);
       
      currentLine = br.readLine(); //this should be the NativeFields list
      stNatFields = new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
      numFields = stNatFields.countTokens();
      for (int i = 0; i < numFields; i++) {
        tempTable.addNativeField(Integer.parseInt(stNatFields.nextToken()));
      }
       
      currentLine = br.readLine(); //this should be the RelatedTables list
      stTables = new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
      int numTables = stTables.countTokens();
      for (int i = 0; i < numTables; i++) {
        tempTable.addRelatedTable(Integer.parseInt(stTables.nextToken()));
      }
      tempTable.makeArrays();
       
      currentLine = br.readLine(); //this should be the RelatedFields list
      stRelFields = new StringTokenizer(currentLine.substring(currentLine.indexOf(" ") + 1), DELIM);
      numFields = stRelFields.countTokens();

      for (int i = 0; i < numFields; i++) {
        tempTable.setRelatedField(i, Integer.parseInt(stRelFields.nextToken()));
      }

      alTables.add(tempTable);
      br.readLine(); //this should be "}"
      br.readLine(); //this should be "\n"
      currentLine = br.readLine(); //this should be either the next "Table: ", #Fields#
    }
    while ((currentLine = br.readLine()) != null) {
      stField = new StringTokenizer(currentLine, DELIM);
      numFigure = Integer.parseInt(stField.nextToken());
      String fieldName = stField.nextToken();
      tempField = new EdgeField(numFigure + DELIM + fieldName);
      tempField.setTableID(Integer.parseInt(stField.nextToken()));
      tempField.setTableBound(Integer.parseInt(stField.nextToken()));
      tempField.setFieldBound(Integer.parseInt(stField.nextToken()));
      tempField.setDataType(Integer.parseInt(stField.nextToken()));
      tempField.setVarcharValue(Integer.parseInt(stField.nextToken()));
      tempField.setIsPrimaryKey(Boolean.parseBoolean(stField.nextToken()));//.booleanValue());
      tempField.setDisallowNull(Boolean.parseBoolean(stField.nextToken()));//.booleanValue());
      if (stField.hasMoreTokens()) { //Default Value may not be defined
        tempField.setDefaultValue(stField.nextToken());
      }
      alFields.add(tempField);
    }
  } // parseSaveFile()

  protected void openFile(File inputFile) {
    try {
      FileReader fr = new FileReader(inputFile);
      br = new BufferedReader(fr);
      //test for what kind of file we have
      String currentLine = br.readLine().trim();
      // numLine++;
      if (currentLine.startsWith(SAVE_ID)) { //the file chosen is a Save file created by this application
        parseFile(); //parse the file
        br.close();
        this.makeArrays(); //convert ArrayList objects into arrays of the appropriate Class type
      } else { //the file chosen is something else
        JOptionPane.showMessageDialog(null, "Unrecognized file format");
      }
    } // try
    catch (FileNotFoundException fnfe) {
      logger.error("Cannot find \"%s\".", inputFile.getName());
      System.exit(0);
    } // catch FileNotFoundException
    catch (IOException ioe) {
      logger.error(ioe);
      System.exit(0);
    } // catch IOException
  } // openFile()
}
