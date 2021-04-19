import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

public class EdgeFileParser extends EdgeConvertFileParser {

  public EdgeFileParser(File constructorFile) {
    super(constructorFile);
  }
  
  protected void parseFile() throws IOException {
    String currentLine;
    while ((currentLine = br.readLine()) != null) {
      currentLine = currentLine.trim();
      if (currentLine.startsWith("Figure ") && !parseFigure(currentLine)) { break; }
      if (currentLine.startsWith("Connector ")) { //this is the start of a Connector entry
        parseConnector(currentLine);
      } // if("Connector")
    } // while()
  } // parseEdgeFile()

  private boolean parseFigure(String currentLine) throws IOException {
    numFigure = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); //get the Figure number
    br.readLine(); // this should be "{"
    currentLine = br.readLine().trim();
    if (!currentLine.startsWith("Style")) { // this is to weed out other Figures, like Labels
      return true;
    } else {
      return parseStyle(currentLine);
    }
  } // parseFigure()

  private void parseConnector(String currentLine) throws IOException {
    numConnector = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1)); //get the Connector number
    br.readLine(); // this should be "{"
    br.readLine(); // not interested in Style
    currentLine = br.readLine().trim(); // Figure1
    int endPoint1 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
    currentLine = br.readLine().trim(); // Figure2
    int endPoint2 = Integer.parseInt(currentLine.substring(currentLine.indexOf(" ") + 1));
    br.readLine(); // not interested in EndPoint1
    br.readLine(); // not interested in EndPoint2
    br.readLine(); // not interested in SuppressEnd1
    br.readLine(); // not interested in SuppressEnd2
    currentLine = br.readLine().trim(); // End1
    String endStyle1 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); //get the End1 parameter
    currentLine = br.readLine().trim(); // End2
    String endStyle2 = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); //get the End2 parameter

    do { //advance to end of record
      currentLine = br.readLine().trim();
    } while (!currentLine.equals("}")); // this is the end of a Connector entry
    
    alConnectors.add(new EdgeConnector(numConnector + DELIM + endPoint1 + DELIM + endPoint2 + DELIM + endStyle1 + DELIM + endStyle2));
  } // parseConnector()

  private boolean parseStyle(String currentLine) throws IOException {
    String style = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")); //get the Style parameter
    if (style.startsWith("Relation")) { //presence of Relations implies lack of normalization
      JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + file + "\ncontains relations.  Please resolve them and try again.");
      EdgeConvertGUI.setReadSuccess(false);
      return false;
    } 
    if (style.startsWith("Entity")) {
      isEntity = true;
    }
    if (style.startsWith("Attribute")) {
      isAttribute = true;
    }
    if (!(isEntity || isAttribute)) { //these are the only Figures we're interested in
      return true;
    }
    currentLine = br.readLine().trim(); //this should be Text
    String text = currentLine.substring(currentLine.indexOf("\"") + 1, currentLine.lastIndexOf("\"")).replace(" ", ""); //get the Text parameter
    if (text.equals("")) {
      JOptionPane.showMessageDialog(null, "There are entities or attributes with blank names in this diagram.\nPlease provide names for them and try again.");
      EdgeConvertGUI.setReadSuccess(false);
      return false;
    }
    int escape = text.indexOf("\\");
    if (escape > 0) { //Edge denotes a line break as "\line", disregard anything after a backslash
      text = text.substring(0, escape);
    }

    do { //advance to end of record, look for whether the text is underlined
      currentLine = br.readLine().trim();
      if (currentLine.startsWith("TypeUnderl")) {
        isUnderlined = true;
      }
    } while (!currentLine.equals("}")); // this is the end of a Figure entry
    
    if (isEntity) { //create a new EdgeTable object and add it to the alTables ArrayList
      if (isTableDup(text)) {
        JOptionPane.showMessageDialog(null, "There are multiple tables called " + text + " in this diagram.\nPlease rename all but one of them and try again.");
        EdgeConvertGUI.setReadSuccess(false);
        return false;
      }
      alTables.add(new EdgeTable(numFigure + DELIM + text));
    }
    if (isAttribute) { //create a new EdgeField object and add it to the alFields ArrayList
      EdgeField tempField = new EdgeField(numFigure + DELIM + text);
      tempField.setIsPrimaryKey(isUnderlined);
      alFields.add(tempField);
    }
    //reset flags
    isEntity = false;
    isAttribute = false;
    isUnderlined = false;
    return true;
  } // parseStyle()

  protected void openFile(File inputFile) {
    try {
      FileReader fr = new FileReader(inputFile);
      br = new BufferedReader(fr);
      //test for what kind of file we have
      String currentLine = br.readLine().trim();
      // numLine++;
      if (currentLine.startsWith(EDGE_ID)) { //the file chosen is an Edge Diagrammer file
        parseFile(); //parse the file
        br.close();
        makeArrays(); //convert ArrayList objects into arrays of the appropriate Class type
        resolveConnectors(); //Identify nature of Connector endpoints
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
