import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EdgeTableTest {
  EdgeTable testObj;

  @Before
  public void setUp() throws Exception {
   testObj = new EdgeTable("77|TestEdgeTable");
  }

  @Test
  public void testGetNumFigure() {
    assertEquals("Figure Number initialized to 77, testing to see if 77", 77, testObj.getNumFigure());
  }

  @Test
  public void testGetName() {
    assertEquals("Name is initialized to 'TestEdgeTable', testing to see if 'TestEdgeTable'", "TestEdgeTable", testObj.getName());
  }

  @Test
  public void testGetRelatedTableArray() {
    testObj.makeArrays();
    assertEquals("A relatedTable ArrayList is initialized and empty. Testing to see if it is empty", 0, testObj.getRelatedTablesArray().length);
  }

  @Test
  public void testAddingRelatedTable() {
    testObj.addRelatedTable(121);
    testObj.makeArrays();
    assertEquals("Adding related table 121. Testing to see if 121 is a first element in related Table", 121, testObj.getRelatedTablesArray()[0]);
  }

  @Test
  public void testGetRelatedFieldsArray() {
    testObj.makeArrays();
    assertEquals("Related Fields array initialized but empty. Testing if getRelatedFieldsArray returns an empty array", 0, testObj.getRelatedTablesArray().length);
  }
  
  @Test
  public void testSetRelatedFieldWithSingleNativeField() {
    testObj.addNativeField(23);
    testObj.makeArrays();
    testObj.setRelatedField(0, 24);
    assertEquals("After calling setRelatedField(24), related field array should contain 24", 24, testObj.getRelatedFieldsArray()[0]);
  }

  @Test
  public void testGetNativeFieldArray() {
    testObj.addNativeField(47);
    testObj.makeArrays();
    assertEquals("After calling adding native field 47, getNativeFieldsArray should return a element 47", 47, testObj.getNativeFieldsArray()[0]);
  }

  @Test
  public void testMoveFieldUpFromTheMiddleForNativeField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.moveFieldUp(1);
    assertArrayEquals("After moving up the middle element, a native field array should be rearranged", new int[]{20, 10, 30}, testObj.getNativeFieldsArray());
  }

  @Test
  public void testMoveFieldUpFromTheTopForNativeField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.moveFieldUp(0);
    assertArrayEquals("After moving up the top element, a native field array should not change", new int[]{10, 20, 30}, testObj.getNativeFieldsArray());
  }

  @Test
  public void testMoveFieldUpFromTheMiddleForRelatedField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.setRelatedField(0, 11);
    testObj.setRelatedField(1, 12);
    testObj.setRelatedField(2, 13);
    testObj.moveFieldUp(1);
    assertArrayEquals("After moving up the middle element, a related field array should be rearranged", new int[]{12, 11, 13}, testObj.getRelatedFieldsArray());
  }

  @Test
  public void testMoveFieldUpFromTheTopForRelatedField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.setRelatedField(0, 11);
    testObj.setRelatedField(1, 12);
    testObj.setRelatedField(2, 13);
    testObj.moveFieldUp(0);
    assertArrayEquals("After moving up the top element, a related field array should not change", new int[]{11, 12, 13}, testObj.getRelatedFieldsArray());
  }

  @Test
  public void testMoveFieldDownFromTheMiddleForNativeField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.moveFieldDown(1);
    assertArrayEquals("After moving down the middle element, a native field array should be rearranged", new int[]{10, 30, 20}, testObj.getNativeFieldsArray());
  }

  @Test
  public void testMoveFieldDownFromTheBottomForNativeField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.moveFieldDown(2);
    assertArrayEquals("After moving down the bottom element, a native field array should not change", new int[]{10, 20, 30}, testObj.getNativeFieldsArray());
  }

  @Test
  public void testMoveFieldDownFromTheMiddleForRelatedField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.setRelatedField(0, 11);
    testObj.setRelatedField(1, 12);
    testObj.setRelatedField(2, 13);
    testObj.moveFieldDown(1);
    assertArrayEquals("After moving down the middle element, a related field array should be rearranged", new int[]{11, 13, 12}, testObj.getRelatedFieldsArray());
  }

  @Test
  public void testMoveFieldDownFromTheBottomForRelatedField() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.makeArrays();
    testObj.setRelatedField(0, 11);
    testObj.setRelatedField(1, 12);
    testObj.setRelatedField(2, 13);
    testObj.moveFieldDown(2);
    assertArrayEquals("After moving down the bottom element, a related field array should not change", new int[]{11, 12, 13}, testObj.getRelatedFieldsArray());
  }

  @Test
  public void testMakeArraysWithThreeFieldsAndThreeTablesNativeFields() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.addRelatedTable(100);
    testObj.addRelatedTable(200);
    testObj.addRelatedTable(300);
    testObj.makeArrays();
    assertArrayEquals("Given three NativeFields and three RelatedTables, NativeFields arrays should look like",
      new int[]{10, 20, 30}, testObj.getNativeFieldsArray());
  }

  @Test
  public void testMakeArraysWithThreeFieldsAndThreeTablesRelatedFields() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.addRelatedTable(100);
    testObj.addRelatedTable(200);
    testObj.addRelatedTable(300);
    testObj.makeArrays();
    assertArrayEquals("Given three NativeFields and three RelatedTables, RelatedField arrays should look like",
      new int[]{0, 0, 0}, testObj.getRelatedFieldsArray());
  }

  @Test
  public void testMakeArraysWithThreeFieldsAndThreeTablesRelatedTables() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.addRelatedTable(100);
    testObj.addRelatedTable(200);
    testObj.addRelatedTable(300);
    testObj.makeArrays();
    assertArrayEquals("Given three NativeFields and three RelatedTables, RelatedTables arrays should look like",
      new int[]{100, 200, 300}, testObj.getRelatedTablesArray());
  }

  @Test
  public void testSampleToString() {
    testObj.addNativeField(10);
    testObj.addNativeField(20);
    testObj.addNativeField(30);
    testObj.addRelatedTable(100);
    testObj.addRelatedTable(200);
    testObj.addRelatedTable(300);
    testObj.makeArrays();
    testObj.setRelatedField(0, 1000);
    testObj.setRelatedField(1, 2000);
    testObj.setRelatedField(2, 3000);
    String sample = "Table: 77\r\n{\r\nTableName: TestEdgeTable\r\nNativeFields: 10|20|30\r\nRelatedTables: 100|200|300\r\nRelatedFields: 1000|2000|3000\r\n}\r\n";
    assertEquals("Given a populated object to string should look like a sample", sample, testObj.toString());
  }
}