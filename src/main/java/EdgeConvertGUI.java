import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.*;

public class EdgeConvertGUI {
   
   public static final int HORIZ_SIZE = 635;
   public static final int VERT_SIZE = 400;
   public static final int HORIZ_LOC = 100;
   public static final int VERT_LOC = 100;
   public static final String DEFINE_TABLES = "Define Tables";
   public static final String DEFINE_RELATIONS = "Define Relations";
   public static final String CANCELLED = "CANCELLED";
   private static JFileChooser jfcEdge = new JFileChooser(".");
   private static JFileChooser jfcGetClass;
   private static JFileChooser jfcOutputDir = new JFileChooser("..");
   private static ExampleFileFilter effEdge = new ExampleFileFilter("edg", "Edge Diagrammer Files");
   private static ExampleFileFilter effSave = new ExampleFileFilter("sav", "Edge Convert Save Files");
   private static ExampleFileFilter effClass;
   private File parseFile;
   private File saveFile;
   private File outputFile;
   private File outputDir;
   private File outputDirOld;
   private String truncatedFilename;
   private String sqlString;
   private String databaseName;
   EdgeMenuListener menuListener;
   EdgeRadioButtonListener radioListener;
   EdgeWindowListener edgeWindowListener;
   CreateDDLButtonListener createDDLListener;
   private EdgeConvertFileParser ecfp;
   private EdgeConvertCreateDDL eccd;
   private static PrintWriter pw;
   private EdgeTable[] tables; //master copy of EdgeTable objects
   private EdgeField[] fields; //master copy of EdgeField objects
   private EdgeTable currentDTTable; //pointers to currently selected table(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeTable currentDRTable1; //pointers to currently selected table(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeTable currentDRTable2; //pointers to currently selected table(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeField currentDTField; //pointers to currently selected field(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeField currentDRField1; //pointers to currently selected field(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeField currentDRField2; //pointers to currently selected field(s) on Define Tables (DT) and Define Relations (DR) screens
   private static boolean readSuccess = true; //this tells GUI whether to populate JList components or not
   private boolean dataSaved = true;
   private ArrayList<Object> alSubclasses = new ArrayList<>();
   private ArrayList alProductNames;
   private String[] productNames;
   private Object[] objSubclasses;

   //Define Tables screen objects
   static String[] strDataType = EdgeField.getStrDataType(); //get the list of currently supported data types
   static JFrame jfDT = new JFrame(DEFINE_TABLES);
   static JPanel jpDTBottom = new JPanel(new GridLayout(1, 2));
   static JPanel jpDTCenter = new JPanel(new GridLayout(1, 3));
   static JPanel jpDTCenter1 = new JPanel(new BorderLayout());
   static JPanel jpDTCenter2 = new JPanel(new BorderLayout());
   static JPanel jpDTCenterRight = new JPanel(new GridLayout(1, 2));
   static JPanel jpDTCenterRight1 = new JPanel(new GridLayout(strDataType.length, 1));
   static JPanel jpDTCenterRight2 = new JPanel(new GridLayout(6, 1));
   static JPanel jpDTMove = new JPanel(new GridLayout(2, 1));
   static JButton jbDTCreateDDL = new JButton("Create DDL");
   static JButton jbDTDefineRelations = new JButton (DEFINE_RELATIONS);
   static JButton jbDTVarchar = new JButton("Set Varchar Length");
   static JButton jbDTDefaultValue = new JButton("Set Default Value");
   static JButton jbDTMoveUp = new JButton("^");
   static JButton jbDTMoveDown = new JButton("v");
   static ButtonGroup bgDTDataType = new ButtonGroup();
   static JRadioButton[] jrbDataType = new JRadioButton[strDataType.length]; //create array of JRadioButtons, one for each supported data type
   static JCheckBox jcheckDTDisallowNull = new JCheckBox("Disallow Null");
   static JCheckBox jcheckDTPrimaryKey = new JCheckBox("Primary Key");
   static JTextField jtfDTVarchar = new JTextField();
   static JTextField jtfDTDefaultValue = new JTextField();
   static JLabel jlabDTFields = new JLabel("Fields List", SwingConstants.CENTER);
   static JLabel jlabDTTables = new JLabel("All Tables", SwingConstants.CENTER);
   static DefaultListModel<String> dlmDTTablesAll = new DefaultListModel<>();
   static DefaultListModel<String> dlmDTFieldsTablesAll = new DefaultListModel<>();
   static JList jlDTTablesAll = new JList(dlmDTTablesAll);
   static JList<DefaultListModel<String>> jlDTFieldsTablesAll = new JList(dlmDTFieldsTablesAll);
   static JScrollPane jspDTTablesAll = new JScrollPane(jlDTTablesAll);
   static JScrollPane jspDTFieldsTablesAll = new JScrollPane(jlDTFieldsTablesAll);
   static JMenuBar jmbDTMenuBar = new JMenuBar();
   static JMenu jmDTFile = new JMenu("File");
   static JMenu jmDTOptions = new JMenu("Options");
   static JMenu jmDTHelp = new JMenu("Help");
   static JMenuItem jmiDTOpenEdge = new JMenuItem("Open Edge File");
   static JMenuItem jmiDTOpenSave = new JMenuItem("Open Save File");
   static JMenuItem jmiDTSave = new JMenuItem("Save");
   static JMenuItem jmiDTSaveAs = new JMenuItem("Save As...");
   static JMenuItem jmiDTExit = new JMenuItem("Exit");
   static JMenuItem jmiDTOptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
   static JMenuItem jmiDTOptionsShowProducts = new JMenuItem("Show Database Products Available");
   static JMenuItem jmiDTHelpAbout = new JMenuItem("About");
   
   //Define Relations screen objects
   static JFrame jfDR = new JFrame(DEFINE_RELATIONS);
   static JPanel jpDRBottom = new JPanel(new GridLayout(1, 3));
   static JPanel jpDRCenter = new JPanel(new GridLayout(2, 2));
   static JPanel jpDRCenter1 = new JPanel(new BorderLayout());
   static JPanel jpDRCenter2 = new JPanel(new BorderLayout());
   static JPanel jpDRCenter3 = new JPanel(new BorderLayout());
   static JPanel jpDRCenter4 = new JPanel(new BorderLayout());
   static JButton jbDRCreateDDL = new JButton("Create DDL");
   static JButton jbDRDefineTables = new JButton(DEFINE_TABLES);
   static JButton jbDRBindRelation = new JButton("Bind/Unbind Relation");
   static DefaultListModel dlmDRTablesRelations = new DefaultListModel();
   static DefaultListModel dlmDRTablesRelatedTo = new DefaultListModel();
   static DefaultListModel dlmDRFieldsTablesRelations = new DefaultListModel();
   static DefaultListModel dlmDRFieldsTablesRelatedTo = new DefaultListModel();
   static JList jlDRTablesRelations = new JList(dlmDRTablesRelations);
   static JList jlDRTablesRelatedTo = new JList(dlmDRTablesRelatedTo);
   static JList jlDRFieldsTablesRelations = new JList(dlmDRFieldsTablesRelations);
   static JList jlDRFieldsTablesRelatedTo = new JList(dlmDRFieldsTablesRelatedTo);
   static JLabel jlabDRTablesRelations = new JLabel("Tables With Relations", SwingConstants.CENTER);
   static JLabel jlabDRTablesRelatedTo = new JLabel("Related Tables", SwingConstants.CENTER);
   static JLabel jlabDRFieldsTablesRelations = new JLabel("Fields in Tables with Relations", SwingConstants.CENTER);
   static JLabel jlabDRFieldsTablesRelatedTo = new JLabel("Fields in Related Tables", SwingConstants.CENTER);
   static JScrollPane jspDRTablesRelations = new JScrollPane(jlDRTablesRelations);
   static JScrollPane jspDRTablesRelatedTo = new JScrollPane(jlDRTablesRelatedTo);
   static JScrollPane jspDRFieldsTablesRelations = new JScrollPane(jlDRFieldsTablesRelations);
   static JScrollPane jspDRFieldsTablesRelatedTo = new JScrollPane(jlDRFieldsTablesRelatedTo);
   static JMenuBar jmbDRMenuBar = new JMenuBar();
   static JMenu jmDRFile = new JMenu("File");
   static JMenu jmDROptions = new JMenu("Options");
   static JMenu jmDRHelp = new JMenu("Help");
   static JMenuItem jmiDROpenEdge = new JMenuItem("Open Edge File");
   static JMenuItem jmiDROpenSave = new JMenuItem("Open Save File");
   static JMenuItem jmiDRSave = new JMenuItem("Save");
   static JMenuItem jmiDRSaveAs = new JMenuItem("Save As...");
   static JMenuItem jmiDRExit = new JMenuItem("Exit");
   static JMenuItem jmiDROptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
   static JMenuItem jmiDROptionsShowProducts = new JMenuItem("Show Database Products Available");
   static JMenuItem jmiDRHelpAbout = new JMenuItem("About");
   
   public EdgeConvertGUI() {
      menuListener = new EdgeMenuListener();
      radioListener = new EdgeRadioButtonListener();
      edgeWindowListener = new EdgeWindowListener();
      createDDLListener = new CreateDDLButtonListener();
      this.showGUI();
   } // EdgeConvertGUI.EdgeConvertGUI()
   
   public void showGUI() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //use the OS native LAF, as opposed to default Java LAF
      } catch (Exception e) {
         System.out.println("Error setting native LAF: " + e);
      }
      createDTScreen();
      createDRScreen();
   } //showGUI()

   public void createDTScreen() {//create Define Tables screen
      jfDT.setLocation(HORIZ_LOC, VERT_LOC);
      Container cp = jfDT.getContentPane();
      jfDT.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDT.addWindowListener(edgeWindowListener);
      jfDT.getContentPane().setLayout(new BorderLayout());
      jfDT.setVisible(true);
      jfDT.setSize(HORIZ_SIZE + 150, VERT_SIZE);

      //setup menubars and menus
      jfDT.setJMenuBar(jmbDTMenuBar);
      jmDTFile.setMnemonic(KeyEvent.VK_F);
      jmbDTMenuBar.add(jmDTFile);
      jmiDTOpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDTOpenEdge.addActionListener(menuListener);
      jmiDTOpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDTOpenSave.addActionListener(menuListener);
      jmiDTSave.setMnemonic(KeyEvent.VK_S);
      jmiDTSave.setEnabled(false);
      jmiDTSave.addActionListener(menuListener);
      jmiDTSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDTSaveAs.setEnabled(false);
      jmiDTSaveAs.addActionListener(menuListener);
      jmiDTExit.setMnemonic(KeyEvent.VK_X);
      jmiDTExit.addActionListener(menuListener);
      jmDTFile.add(jmiDTOpenEdge);
      jmDTFile.add(jmiDTOpenSave);
      jmDTFile.add(jmiDTSave);
      jmDTFile.add(jmiDTSaveAs);
      jmDTFile.add(jmiDTExit);
      
      jmDTOptions.setMnemonic(KeyEvent.VK_O);
      jmbDTMenuBar.add(jmDTOptions);
      jmiDTOptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDTOptionsOutputLocation.addActionListener(menuListener);
      jmiDTOptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDTOptionsShowProducts.setEnabled(false);
      jmiDTOptionsShowProducts.addActionListener(menuListener);
      jmDTOptions.add(jmiDTOptionsOutputLocation);
      jmDTOptions.add(jmiDTOptionsShowProducts);
      
      jmDTHelp.setMnemonic(KeyEvent.VK_H);
      jmbDTMenuBar.add(jmDTHelp);
      jmiDTHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDTHelpAbout.addActionListener(menuListener);
      jmDTHelp.add(jmiDTHelpAbout);
      
      jfcOutputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      jbDTCreateDDL.setEnabled(false);
      jbDTCreateDDL.addActionListener(createDDLListener);

      jbDTDefineRelations.setEnabled(false);
      jbDTDefineRelations.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               jfDT.setVisible(false);
               jfDR.setVisible(true); //show the Define Relations screen
               clearDTControls();
               dlmDTFieldsTablesAll.removeAllElements();
            }
         }
      );

      jpDTBottom.add(jbDTDefineRelations);
      jpDTBottom.add(jbDTCreateDDL);
      jfDT.getContentPane().add(jpDTBottom, BorderLayout.SOUTH);
      
      jlDTTablesAll.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDTTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDTTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTTable(selText); //set pointer to the selected table
                  int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
                  jlDTFieldsTablesAll.clearSelection();
                  dlmDTFieldsTablesAll.removeAllElements();
                  jbDTMoveUp.setEnabled(false);
                  jbDTMoveDown.setEnabled(false);
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
               }
               disableControls();
            }
         }
      );
      
      jlDTFieldsTablesAll.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  if (selIndex == 0) {
                     jbDTMoveUp.setEnabled(false);
                  } else {
                     jbDTMoveUp.setEnabled(true);
                  }
                  if (selIndex == (dlmDTFieldsTablesAll.getSize() - 1)) {
                     jbDTMoveDown.setEnabled(false);
                  } else {
                     jbDTMoveDown.setEnabled(true);
                  }
                  String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTField(selText); //set pointer to the selected field
                  enableControls();
                  jrbDataType[currentDTField.getDataType()].setSelected(true); //select the appropriate radio button, based on value of dataType
                  if (jrbDataType[0].isSelected()) { //this is the Varchar radio button
                     jbDTVarchar.setEnabled(true); //enable the Varchar button
                     jtfDTVarchar.setText(Integer.toString(currentDTField.getVarcharValue())); //fill text field with varcharValue
                  } else { //some radio button other than Varchar is selected
                     jtfDTVarchar.setText(""); //clear the text field
                     jbDTVarchar.setEnabled(false); //disable the button
                  }
                  jcheckDTPrimaryKey.setSelected(currentDTField.getIsPrimaryKey()); //clear or set Primary Key checkbox
                  jcheckDTDisallowNull.setSelected(currentDTField.getDisallowNull()); //clear or set Disallow Null checkbox
                  jtfDTDefaultValue.setText(currentDTField.getDefaultValue()); //fill text field with defaultValue
               }
            }
         }
      );
      
      jbDTMoveUp.setEnabled(false);
      jbDTMoveUp.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int selection = jlDTFieldsTablesAll.getSelectedIndex();
               currentDTTable.moveFieldUp(selection);
               //repopulate Fields List
               int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
               jlDTFieldsTablesAll.clearSelection();
               dlmDTFieldsTablesAll.removeAllElements();
               for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                  dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
               }
               jlDTFieldsTablesAll.setSelectedIndex(selection - 1);
               dataSaved = false;
            }
         }
      );
      
      jbDTMoveDown.setEnabled(false);
      jbDTMoveDown.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int selection = jlDTFieldsTablesAll.getSelectedIndex(); //the original selected index
               currentDTTable.moveFieldDown(selection);
               //repopulate Fields List
               int[] currentNativeFields = currentDTTable.getNativeFieldsArray();
               jlDTFieldsTablesAll.clearSelection();
               dlmDTFieldsTablesAll.removeAllElements();
               for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                  dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
               }
               jlDTFieldsTablesAll.setSelectedIndex(selection + 1);
               dataSaved = false;
            }
         }
      );
      jpDTMove.add(jbDTMoveUp);
      jpDTMove.add(jbDTMoveDown);

      jpDTCenter1.add(jlabDTTables, BorderLayout.NORTH);
      jpDTCenter2.add(jlabDTFields, BorderLayout.NORTH);
      jpDTCenter1.add(jspDTTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jspDTFieldsTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jpDTMove, BorderLayout.EAST);
      jpDTCenter.add(jpDTCenter1);
      jpDTCenter.add(jpDTCenter2);
      jpDTCenter.add(jpDTCenterRight);

      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i] = new JRadioButton(strDataType[i]); //assign label for radio button from String array
         jrbDataType[i].setEnabled(false);
         jrbDataType[i].addActionListener(radioListener);
         bgDTDataType.add(jrbDataType[i]);
         jpDTCenterRight1.add(jrbDataType[i]);
      }
      jpDTCenterRight.add(jpDTCenterRight1);
      
      jcheckDTDisallowNull.setEnabled(false);
      jcheckDTDisallowNull.addItemListener(
         new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
               currentDTField.setDisallowNull(jcheckDTDisallowNull.isSelected());
               dataSaved = false;
            }
         }
      );
      
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTPrimaryKey.addItemListener(
         new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
               currentDTField.setIsPrimaryKey(jcheckDTPrimaryKey.isSelected());
               dataSaved = false;
            }
         }
      );
      
      jbDTDefaultValue.setEnabled(false);
      jbDTDefaultValue.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               String prev = jtfDTDefaultValue.getText();
               boolean goodData = false;
               int i = currentDTField.getDataType();
               do {
                  String result = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the default value:",
                       "Default Value",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       prev);

                  if ((result == null)) {
                     jtfDTDefaultValue.setText(prev);
                     return;
                  }
                  switch (i) {
                     case 0: //varchar
                        if (result.length() <= Integer.parseInt(jtfDTVarchar.getText())) {
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        } else {
                           JOptionPane.showMessageDialog(null, "The length of this value must be less than or equal to the Varchar length specified.");
                        }
                        break;
                     case 1: //boolean
                        String newResult = result.toLowerCase();
                        if (newResult.equals("true") || newResult.equals("false")) {
                           jtfDTDefaultValue.setText(newResult);
                           goodData = true;
                        } else {
                           JOptionPane.showMessageDialog(null, "You must input a valid boolean value (\"true\" or \"false\").");
                        }
                        break;
                     case 2: //Integer
                        try {
                           int intResult = Integer.parseInt(result);
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        } catch (NumberFormatException nfe) {
                           JOptionPane.showMessageDialog(null, "\"" + result + "\" is not an integer or is outside the bounds of valid integer values.");
                        }
                        break;
                     case 3: //Double
                        try {
                           double doubleResult = Double.parseDouble(result);
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        } catch (NumberFormatException nfe) {
                           JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a double or is outside the bounds of valid double values.");
                        }
                        break;
                     case 4: //Timestamp
                        try {
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        }
                        catch (Exception e) {
                           
                        }
                        break;
                  }
               } while (!goodData);
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTField(selText);
                  currentDTField.setDefaultValue(jtfDTDefaultValue.getText());
               }
               dataSaved = false;
            }
         }
      ); //jbDTDefaultValue.addActionListener
      jtfDTDefaultValue.setEditable(false);

      jbDTVarchar.setEnabled(false);
      jbDTVarchar.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               String prev = jtfDTVarchar.getText();
               String result = (String)JOptionPane.showInputDialog(
                    null,
                    "Enter the varchar length:",
                    "Varchar Length",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    prev);
               if ((result == null)) {
                  jtfDTVarchar.setText(prev);
                  return;
               }
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               int varchar;
               try {
                  if (result.length() > 5) {
                     JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     return;
                  }
                  varchar = Integer.parseInt(result);
                  if (varchar > 0 && varchar <= 65535) { // max length of varchar is 255 before v5.0.3
                     jtfDTVarchar.setText(Integer.toString(varchar));
                     currentDTField.setVarcharValue(varchar);
                  } else {
                     JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     return;
                  }
               } catch (NumberFormatException nfe) {
                  JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a number");
                  jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                  return;
               }
               dataSaved = false;
            }
         }
      );
      jtfDTVarchar.setEditable(false);
      
      jpDTCenterRight2.add(jbDTVarchar);
      jpDTCenterRight2.add(jtfDTVarchar);
      jpDTCenterRight2.add(jcheckDTPrimaryKey);
      jpDTCenterRight2.add(jcheckDTDisallowNull);
      jpDTCenterRight2.add(jbDTDefaultValue);
      jpDTCenterRight2.add(jtfDTDefaultValue);
      jpDTCenterRight.add(jpDTCenterRight1);
      jpDTCenterRight.add(jpDTCenterRight2);
      jpDTCenter.add(jpDTCenterRight);
      jfDT.getContentPane().add(jpDTCenter, BorderLayout.CENTER);
      jfDT.validate();
   } //createDTScreen

   public void createDRScreen() {
      //create Define Relations screen
      jfDR.setSize(HORIZ_SIZE, VERT_SIZE);
      jfDR.setLocation(HORIZ_LOC, VERT_LOC);
      jfDR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDR.addWindowListener(edgeWindowListener);
      jfDR.getContentPane().setLayout(new BorderLayout());

      //setup menubars and menus
      jfDR.setJMenuBar(jmbDRMenuBar);
      jmDRFile.setMnemonic(KeyEvent.VK_F);
      jmbDRMenuBar.add(jmDRFile);
      jmiDROpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDROpenEdge.addActionListener(menuListener);
      jmiDROpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDROpenSave.addActionListener(menuListener);
      jmiDRSave.setMnemonic(KeyEvent.VK_S);
      jmiDRSave.setEnabled(false);
      jmiDRSave.addActionListener(menuListener);
      jmiDRSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDRSaveAs.setEnabled(false);
      jmiDRSaveAs.addActionListener(menuListener);
      jmiDRExit.setMnemonic(KeyEvent.VK_X);
      jmiDRExit.addActionListener(menuListener);
      jmDRFile.add(jmiDROpenEdge);
      jmDRFile.add(jmiDROpenSave);
      jmDRFile.add(jmiDRSave);
      jmDRFile.add(jmiDRSaveAs);
      jmDRFile.add(jmiDRExit);

      jmDROptions.setMnemonic(KeyEvent.VK_O);
      jmbDRMenuBar.add(jmDROptions);
      jmiDROptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDROptionsOutputLocation.addActionListener(menuListener);
      jmiDROptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDROptionsShowProducts.setEnabled(false);
      jmiDROptionsShowProducts.addActionListener(menuListener);
      jmDROptions.add(jmiDROptionsOutputLocation);
      jmDROptions.add(jmiDROptionsShowProducts);

      jmDRHelp.setMnemonic(KeyEvent.VK_H);
      jmbDRMenuBar.add(jmDRHelp);
      jmiDRHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDRHelpAbout.addActionListener(menuListener);
      jmDRHelp.add(jmiDRHelpAbout);

      jlDRTablesRelations.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRTablesRelations.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRTablesRelations.getElementAt(selIndex).toString();
                  setCurrentDRTable1(selText);
                  int[] currentNativeFields, currentRelatedTables, currentRelatedFields;
                  currentNativeFields = currentDRTable1.getNativeFieldsArray();
                  currentRelatedTables = currentDRTable1.getRelatedTablesArray();
                  jlDRFieldsTablesRelations.clearSelection();
                  jlDRTablesRelatedTo.clearSelection();
                  jlDRFieldsTablesRelatedTo.clearSelection();
                  dlmDRFieldsTablesRelations.removeAllElements();
                  dlmDRTablesRelatedTo.removeAllElements();
                  dlmDRFieldsTablesRelatedTo.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDRFieldsTablesRelations.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
                  for (int rIndex = 0; rIndex < currentRelatedTables.length; rIndex++) {
                     dlmDRTablesRelatedTo.addElement(getTableName(currentRelatedTables[rIndex]));
                  }
               }
            }
         }
      );

      jlDRFieldsTablesRelations.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRFieldsTablesRelations.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRFieldsTablesRelations.getElementAt(selIndex).toString();
                  setCurrentDRField1(selText);
                  if (currentDRField1.getFieldBound() == 0) {
                     jlDRTablesRelatedTo.clearSelection();
                     jlDRFieldsTablesRelatedTo.clearSelection();
                     dlmDRFieldsTablesRelatedTo.removeAllElements();
                  } else {
                     jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true);
                     jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true);
                  }
               }
            }
         }
      );

      jlDRTablesRelatedTo.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRTablesRelatedTo.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRTablesRelatedTo.getElementAt(selIndex).toString();
                  setCurrentDRTable2(selText);
                  int[] currentNativeFields = currentDRTable2.getNativeFieldsArray();
                  dlmDRFieldsTablesRelatedTo.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDRFieldsTablesRelatedTo.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
               }
            }
         }
      );

      jlDRFieldsTablesRelatedTo.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRFieldsTablesRelatedTo.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRFieldsTablesRelatedTo.getElementAt(selIndex).toString();
                  setCurrentDRField2(selText);
                  jbDRBindRelation.setEnabled(true);
               } else {
                  jbDRBindRelation.setEnabled(false);
               }
            }
         }
      );

      jpDRCenter1.add(jlabDRTablesRelations, BorderLayout.NORTH);
      jpDRCenter2.add(jlabDRFieldsTablesRelations, BorderLayout.NORTH);
      jpDRCenter3.add(jlabDRTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter4.add(jlabDRFieldsTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter1.add(jspDRTablesRelations, BorderLayout.CENTER);
      jpDRCenter2.add(jspDRFieldsTablesRelations, BorderLayout.CENTER);
      jpDRCenter3.add(jspDRTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter4.add(jspDRFieldsTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter.add(jpDRCenter1);
      jpDRCenter.add(jpDRCenter2);
      jpDRCenter.add(jpDRCenter3);
      jpDRCenter.add(jpDRCenter4);
      jfDR.getContentPane().add(jpDRCenter, BorderLayout.CENTER);

      jbDRDefineTables.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               jfDT.setVisible(true); //show the Define Tables screen
               jfDR.setVisible(false);
               clearDRControls();
               depopulateLists();
               populateLists();
            }
         }
      );

      jbDRBindRelation.setEnabled(false);
      jbDRBindRelation.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int nativeIndex = jlDRFieldsTablesRelations.getSelectedIndex();
               int relatedField = currentDRField2.getNumFigure();
               if (currentDRField1.getFieldBound() == relatedField) { //the selected fields are already bound to each other
                  int answer = JOptionPane.showConfirmDialog(null, "Do you wish to unbind the relation on field " +
                                                             currentDRField1.getName() + "?",
                                                             "Are you sure?", JOptionPane.YES_NO_OPTION);
                  if (answer == JOptionPane.YES_OPTION) {
                     currentDRTable1.setRelatedField(nativeIndex, 0); //clear the related field
                     currentDRField1.setTableBound(0); //clear the bound table
                     currentDRField1.setFieldBound(0); //clear the bound field
                     jlDRFieldsTablesRelatedTo.clearSelection(); //clear the listbox selection
                  }
                  return;
               }
               if (currentDRField1.getFieldBound() != 0) { //field is already bound to a different field
                  int answer = JOptionPane.showConfirmDialog(null, "There is already a relation defined on field " +
                                                             currentDRField1.getName() + ", do you wish to overwrite it?",
                                                             "Are you sure?", JOptionPane.YES_NO_OPTION);
                  if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION) {
                     jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true); //revert selections to saved settings
                     jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true); //revert selections to saved settings
                     return;
                  }
               }
               if (currentDRField1.getDataType() != currentDRField2.getDataType()) {
                  JOptionPane.showMessageDialog(null, "The datatypes of " + currentDRTable1.getName() + "." +
                                                currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                                "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                  return;
               }
               if ((currentDRField1.getDataType() == 0) && (currentDRField2.getDataType() == 0)) {
                  if (currentDRField1.getVarcharValue() != currentDRField2.getVarcharValue()) {
                     JOptionPane.showMessageDialog(null, "The varchar lengths of " + currentDRTable1.getName() + "." +
                                                   currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                                   "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                     return;
                  }
               }
               currentDRTable1.setRelatedField(nativeIndex, relatedField);
               currentDRField1.setTableBound(currentDRTable2.getNumFigure());
               currentDRField1.setFieldBound(currentDRField2.getNumFigure());
               JOptionPane.showMessageDialog(null, "Table " + currentDRTable1.getName() + ": native field " +
                                             currentDRField1.getName() + " bound to table " + currentDRTable2.getName() +
                                             " on field " + currentDRField2.getName());
               dataSaved = false;
            }
         }
      );

      jbDRCreateDDL.setEnabled(false);
      jbDRCreateDDL.addActionListener(createDDLListener);

      jpDRBottom.add(jbDRDefineTables);
      jpDRBottom.add(jbDRBindRelation);
      jpDRBottom.add(jbDRCreateDDL);
      jfDR.getContentPane().add(jpDRBottom, BorderLayout.SOUTH);
   } //createDRScreen
   
   public static void setReadSuccess(boolean value) {
      readSuccess = value;
   }
   
   public static boolean getReadSuccess() {
      return readSuccess;
   }
   
   private void setCurrentDTTable(String selText) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            currentDTTable = tables[tIndex];
            return;
         }
      }
   }

   private void setCurrentDTField(String selText) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) && fields[fIndex].getTableID() == currentDTTable.getNumFigure()) {
            currentDTField = fields[fIndex];
            return;
         }
      }
   }

   private void setCurrentDRTable1(String selText) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            currentDRTable1 = tables[tIndex];
            return;
         }
      }
   }

   private void setCurrentDRTable2(String selText) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (selText.equals(tables[tIndex].getName())) {
            currentDRTable2 = tables[tIndex];
            return;
         }
      }
   }

   private void setCurrentDRField1(String selText) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) &&
             fields[fIndex].getTableID() == currentDRTable1.getNumFigure()) {
            currentDRField1 = fields[fIndex];
            return;
         }
      }
   }

   private void setCurrentDRField2(String selText) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (selText.equals(fields[fIndex].getName()) &&
             fields[fIndex].getTableID() == currentDRTable2.getNumFigure()) {
            currentDRField2 = fields[fIndex];
            return;
         }
      }
   }
   
   private String getTableName(int numFigure) {
      for (int tIndex = 0; tIndex < tables.length; tIndex++) {
         if (tables[tIndex].getNumFigure() == numFigure) {
            return tables[tIndex].getName();
         }
      }
      return "";
   }
   
   private String getFieldName(int numFigure) {
      for (int fIndex = 0; fIndex < fields.length; fIndex++) {
         if (fields[fIndex].getNumFigure() == numFigure) {
            return fields[fIndex].getName();
         }
      }
      return "";
   }
   
   private void enableControls() {
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(true);
      }
      jcheckDTPrimaryKey.setEnabled(true);
      jcheckDTDisallowNull.setEnabled(true);
      jbDTVarchar.setEnabled(true);
      jbDTDefaultValue.setEnabled(true);
   }
   
   private void disableControls() {
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(false);
      }
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTDisallowNull.setEnabled(false);
      jbDTDefaultValue.setEnabled(false);
      jtfDTVarchar.setText("");
      jtfDTDefaultValue.setText("");
   }
   
   private void clearDTControls() {
      jlDTTablesAll.clearSelection();
      jlDTFieldsTablesAll.clearSelection();
   }
   
   private void clearDRControls() {
      jlDRTablesRelations.clearSelection();
      jlDRTablesRelatedTo.clearSelection();
      jlDRFieldsTablesRelations.clearSelection();
      jlDRFieldsTablesRelatedTo.clearSelection();
   }
   
   private void depopulateLists() {
      dlmDTTablesAll.clear();
      dlmDTFieldsTablesAll.clear();
      dlmDRTablesRelations.clear();
      dlmDRFieldsTablesRelations.clear();
      dlmDRTablesRelatedTo.clear();
      dlmDRFieldsTablesRelatedTo.clear();
   }
   
   private void populateLists() {
      if (readSuccess) {
         jfDT.setVisible(true);
         jfDR.setVisible(false);
         disableControls();
         depopulateLists();
         for (int tIndex = 0; tIndex < tables.length; tIndex++) {
            String tempName = tables[tIndex].getName();
            dlmDTTablesAll.addElement(tempName);
            int[] relatedTables = tables[tIndex].getRelatedTablesArray();
            if (relatedTables.length > 0) {
               dlmDRTablesRelations.addElement(tempName);
            }
         }
      }
      readSuccess = true;
   }
   
   private void saveAs() {
      int returnVal;
      jfcEdge.addChoosableFileFilter(effSave);
      returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         saveFile = jfcEdge.getSelectedFile();
         if (saveFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                return;
             }
         }
         if (!saveFile.getName().endsWith("sav")) {
            String temp = saveFile.getAbsolutePath() + ".sav";
            saveFile = new File(temp);
         }
         jmiDTSave.setEnabled(true);
         truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
         jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
         jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
      } else {
         return;
      }
      writeSave();
   }
   
   private void writeSave() {
      if (saveFile != null) {
         try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(saveFile, false)));
            //write the identification line
            pw.println(EdgeConvertFileParser.SAVE_ID);
            //write the tables 
            pw.println("#Tables#");
            for (int i = 0; i < tables.length; i++) {
               pw.println(tables[i]);
            }
            //write the fields
            pw.println("#Fields#");
            for (int i = 0; i < fields.length; i++) {
               pw.println(fields[i]);
            }
            //close the file
            pw.close();
         } catch (IOException ioe) {
            System.out.println(ioe);
         }
         dataSaved = true;
      }
   }

   private void setOutputDir() {
      int returnVal;
      outputDirOld = outputDir;
      alProductNames = new ArrayList();

      returnVal = jfcOutputDir.showOpenDialog(null);
      
      if (returnVal == JFileChooser.CANCEL_OPTION) {
         return;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputDir = jfcOutputDir.getSelectedFile();
      }
      
      getOutputClasses();

      if (alProductNames.size() == 0) {
         JOptionPane.showMessageDialog(null, "The path:\n" + outputDir + "\ncontains no valid output definition files.");
         outputDir = outputDirOld;
         return;
      }
      
      if ((parseFile != null || saveFile != null) && outputDir != null) {
         jbDTCreateDDL.setEnabled(true);
         jbDRCreateDDL.setEnabled(true);
      }

      JOptionPane.showMessageDialog(null, "The available products to create DDL statements are:\n" + displayProductNames());
      jmiDTOptionsShowProducts.setEnabled(true);
      jmiDROptionsShowProducts.setEnabled(true);
   }
   
   private String displayProductNames() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < productNames.length; i++) {
         sb.append(productNames[i] + "\n");
      }
      return sb.toString();
   }
   
   private void getOutputClasses() {
      File[] resultFiles = {};
      Class resultClass = null;
      Class[] paramTypes = {EdgeTable[].class, EdgeField[].class};
      Class[] paramTypesNull = {};
      Constructor conResultClass;
      Object[] args = {tables, fields};
      Object objOutput = null;
	
      String classLocation = EdgeConvertGUI.class.getResource("EdgeConvertGUI.class").toString();
      if (classLocation.startsWith("jar:")) {
          String jarfilename = classLocation.replaceFirst("^.*:", "").replaceFirst("!.*$", "");
          System.out.println("Jarfile: " + jarfilename);
          try (JarFile jarfile = new JarFile(jarfilename)) {
              ArrayList<File> filenames = new ArrayList<>();
              for (JarEntry e : Collections.list(jarfile.entries())) {
                  filenames.add(new File(e.getName()));
              }
              resultFiles = filenames.toArray(new File[0]);
          } catch (IOException ioe) {
              throw new RuntimeException(ioe);
          }
      } 
      else {
          resultFiles = outputDir.listFiles();
      }
      alProductNames.clear();
      alSubclasses.clear();
      try {
         for (int i = 0; i < resultFiles.length; i++) {
         System.out.println(resultFiles[i].getName());
            if (!resultFiles[i].getName().endsWith(".class")) {
               continue; //ignore all files that are not .class files
            }
            resultClass = Class.forName(resultFiles[i].getName().substring(0, resultFiles[i].getName().lastIndexOf(".")));
            if (resultClass.getSuperclass().getName().equals("EdgeConvertCreateDDL")) { //only interested in classes that extend EdgeConvertCreateDDL
               if (parseFile == null && saveFile == null) {
                  conResultClass = resultClass.getConstructor(paramTypesNull);
                  objOutput = conResultClass.newInstance(null);
                  } else {
                  conResultClass = resultClass.getConstructor(paramTypes);
                  objOutput = conResultClass.newInstance(args);
               }
               alSubclasses.add(objOutput);
               Method getProductName = resultClass.getMethod("getProductName", null);
               String productName = (String)getProductName.invoke(objOutput, null);
               alProductNames.add(productName);
            }
         }
      } catch (InstantiationException ie) {
         ie.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
         cnfe.printStackTrace();
      } catch (IllegalAccessException iae) {
         iae.printStackTrace();
      } catch (NoSuchMethodException nsme) {
         nsme.printStackTrace();
      } catch (InvocationTargetException ite) {
         ite.printStackTrace();
      }
      if (alProductNames.size() > 0 && alSubclasses.size() > 0) { //do not recreate productName and objSubClasses arrays if the new path is empty of valid files
         productNames = (String[])alProductNames.toArray(new String[alProductNames.size()]);
         objSubclasses = (Object[])alSubclasses.toArray(new Object[alSubclasses.size()]);
      }
   }
   
   private String getSQLStatements() {
      String strSQLString = "";
      String response = (String)JOptionPane.showInputDialog(
                    null,
                    "Select a product:",
                    "Create DDL",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    productNames,
                    null);
                    
      if (response == null) {
         return EdgeConvertGUI.CANCELLED;
      }
      
      int selected;
      for (selected = 0; selected < productNames.length; selected++) {
         if (response.equals(productNames[selected])) {
            break;
         }
      }

      try {
         Class selectedSubclass = objSubclasses[selected].getClass();
         Method getSQLString = selectedSubclass.getMethod("getSQLString", null);
         Method getDatabaseName = selectedSubclass.getMethod("getDatabaseName", null);
         strSQLString = (String)getSQLString.invoke(objSubclasses[selected], null);
         databaseName = (String)getDatabaseName.invoke(objSubclasses[selected], null);
      } catch (IllegalAccessException iae) {
         iae.printStackTrace();
      } catch (NoSuchMethodException nsme) {
         nsme.printStackTrace();
      } catch (InvocationTargetException ite) {
         ite.printStackTrace();
      }

      return strSQLString;
   }

   private void writeSQL(String output) {
      jfcEdge.resetChoosableFileFilters();
      String str;
      if (parseFile != null) {
         outputFile = new File(parseFile.getAbsolutePath().substring(0, (parseFile.getAbsolutePath().lastIndexOf(File.separator) + 1)) + databaseName + ".sql");
      } else {
         outputFile = new File(saveFile.getAbsolutePath().substring(0, (saveFile.getAbsolutePath().lastIndexOf(File.separator) + 1)) + databaseName + ".sql");
      }
      if (databaseName.equals("")) {
         return;
      }
      jfcEdge.setSelectedFile(outputFile);
      int returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputFile = jfcEdge.getSelectedFile();
         if (outputFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                                                         JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                return;
             }
         }
         try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));
            //write the SQL statements
            pw.println(output);
            //close the file
            pw.close();
         } catch (IOException ioe) {
            System.out.println(ioe);
         }
      }
   }
   
   class EdgeRadioButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         for (int i = 0; i < jrbDataType.length; i++) {
            if (jrbDataType[i].isSelected()) {
               currentDTField.setDataType(i);
               break;
            }
         }
         if (jrbDataType[0].isSelected()) {
            jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
            jbDTVarchar.setEnabled(true);
         } else {
            jtfDTVarchar.setText("");
            jbDTVarchar.setEnabled(false);
         }
         jtfDTDefaultValue.setText("");
         currentDTField.setDefaultValue("");
         dataSaved = false;
      }
   }
   
   class EdgeWindowListener implements WindowListener {
      public void windowActivated(WindowEvent we) {}
      public void windowClosed(WindowEvent we) {}
      public void windowDeactivated(WindowEvent we) {}
      public void windowDeiconified(WindowEvent we) {}
      public void windowIconified(WindowEvent we) {}
      public void windowOpened(WindowEvent we) {}
      
      public void windowClosing(WindowEvent we) {
         if (!dataSaved) {
            int answer = JOptionPane.showOptionDialog(null,
                "You currently have unsaved data. Would you like to save?",
                "Are you sure?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);
            if (answer == JOptionPane.YES_OPTION) {
               if (saveFile == null) {
                  saveAs();
               }
               writeSave();
            }
            if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
               if (we.getSource() == jfDT) {
                  jfDT.setVisible(true);
               }
               if (we.getSource() == jfDR) {
                  jfDR.setVisible(true);
               }
               return;
            }
         }
         System.exit(0); //No was selected
      }
   }
   
   class CreateDDLButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         while (outputDir == null) {
            JOptionPane.showMessageDialog(null, "You have not selected a path that contains valid output definition files yet.\nPlease select a path now.");
            setOutputDir();
         }
         getOutputClasses(); //in case outputDir was set before a file was loaded and EdgeTable/EdgeField objects created
         sqlString = getSQLStatements();
         if (sqlString.equals(EdgeConvertGUI.CANCELLED)) {
            return;
         }
         writeSQL(sqlString);
      }
   }

   class EdgeMenuListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         int returnVal;
         if ((ae.getSource() == jmiDTOpenEdge) || (ae.getSource() == jmiDROpenEdge)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  return;
               }
            }
            jfcEdge.addChoosableFileFilter(effEdge);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               parseFile = jfcEdge.getSelectedFile();
               ecfp = new EdgeConvertFileParser(parseFile);
               tables = ecfp.getEdgeTables();
               for (int i = 0; i < tables.length; i++) {
                  tables[i].makeArrays();
               }
               fields = ecfp.getEdgeFields();
               ecfp = null;
               populateLists();
               saveFile = null;
               jmiDTSave.setEnabled(false);
               jmiDRSave.setEnabled(false);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);
               
               truncatedFilename = parseFile.getName().substring(parseFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTOpenSave) || (ae.getSource() == jmiDROpenSave)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  return;
               }
            }
            jfcEdge.addChoosableFileFilter(effSave);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               saveFile = jfcEdge.getSelectedFile();
               ecfp = new EdgeConvertFileParser(saveFile);
               tables = ecfp.getEdgeTables();
               fields = ecfp.getEdgeFields();
               ecfp = null;
               populateLists();
               parseFile = null;
               jmiDTSave.setEnabled(true);
               jmiDRSave.setEnabled(true);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);

               truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs) ||
             (ae.getSource() == jmiDTSave) || (ae.getSource() == jmiDRSave)) {
            if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs)) {
               saveAs();
            } else {
               writeSave();
            }
         }
         
         if ((ae.getSource() == jmiDTExit) || (ae.getSource() == jmiDRExit)) {
            if (!dataSaved) {
               int answer = JOptionPane.showOptionDialog(null,
                   "You currently have unsaved data. Would you like to save?",
                   "Are you sure?",
                   JOptionPane.YES_NO_CANCEL_OPTION,
                   JOptionPane.QUESTION_MESSAGE,
                   null, null, null);
               if (answer == JOptionPane.YES_OPTION) {
                  if (saveFile == null) {
                     saveAs();
                  }
               }
               if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
                  return;
               }
            }
            System.exit(0); //No was selected
         }
         
         if ((ae.getSource() == jmiDTOptionsOutputLocation) || (ae.getSource() == jmiDROptionsOutputLocation)) {
            setOutputDir();
         }

         if ((ae.getSource() == jmiDTOptionsShowProducts) || (ae.getSource() == jmiDROptionsShowProducts)) {
            JOptionPane.showMessageDialog(null, "The available products to create DDL statements are:\n" + displayProductNames());
         }
         
         if ((ae.getSource() == jmiDTHelpAbout) || (ae.getSource() == jmiDRHelpAbout)) {
            JOptionPane.showMessageDialog(null, "EdgeConvert ERD To DDL Conversion Tool\n" +
                                                "by Stephen A. Capperell\n" +
                                                " 2007-2008");
         }
      } // EdgeMenuListener.actionPerformed()
   } // EdgeMenuListener
} // EdgeConvertGUI
