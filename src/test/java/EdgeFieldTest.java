import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EdgeFieldTest
{
  EdgeField testObj;

  @Before
  public void setUp() throws Exception
  {
    testObj = new EdgeField("1|2|3|testStyle1|testStyle2");
  }

  // Test the getters

  @Test
  public void testGetTableID()
  {
    assertEquals("tableID initialized to 0, testing to see if 0.",0,testObj.getTableID());
  }

  @Test
  public void testGetTableBound()
  {
    assertEquals("tableBound initialized to 0, testing to see if 0.",0,testObj.getTableBound());
  }

  @Test
  public void testGetFieldBound()
  {
    assertEquals("fieldBound initialized to 0, testing to see if 0.",0,testObj.getFieldBound());
  }

  @Test
  public void testGetDisallowNull()
  {
    assertEquals("disallowNull should be set to false, testing.",false,testObj.getDisallowNull());
  }

  @Test
  public void testGetIsPrimaryKey()
  {
    assertEquals("primaryKey should be set to false, testing.",false,testObj.getIsPrimaryKey());
  }

  @Test
  public void testGetDefaultValue()
  {
    assertEquals("default should be set to empty string, testing.","",testObj.getDefaultValue());
  }







  // Test the setters

  @Test
  public void testSetTableID()
  {
    testObj.setTableID(27);
    assertEquals("tableID set to 27, testing to see if 27.",27,testObj.getTableID());
  }

  @Test
  public void testSetTableBound()
  {
    testObj.setTableBound(15);
    assertEquals("tableBound set to 15, testing to see if 15.",15,testObj.getTableBound());
  }

  @Test
  public void testSetFieldBound()
  {
    testObj.setFieldBound(82);
    assertEquals("fieldBound set to 28, testing to see if 82.",82,testObj.getFieldBound());
  }

  @Test
  public void testSetDisallowNull()
  {
    testObj.setDisallowNull(true);
    assertEquals("disallowNull should be set to true, testing.",true,testObj.getDisallowNull());
  }

  @Test
  public void testSetIsPrimaryKey()
  {
    testObj.setIsPrimaryKey(true);
    assertEquals("primaryKey should be set to true, testing.",true,testObj.getIsPrimaryKey());
  }

  @Test
  public void testSetDefaultValue()
  {
    testObj.setDefaultValue("new default");
    assertEquals("default should be set to new default, testing.","new default",testObj.getDefaultValue());
  }

}
