import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EdgeFieldTest
{
  EdgeField testObj;

  @Before
  public void setUp() throws Exception
  {
    testObj = new EdgeField("String");
    System.out.println("Testing EdgeField...");

    System.out.println("Test 1: Ensure getters are working...");
    testGetTableID();
    testGetTableBound();

    System.out.println("Test 2: Ensure setters are working...");


    System.out.println("Test 3: Ensure errors are thrown when needed...");
  }

  // Test some of the getters

  /*
   The tableID should automatically be set to 0
   Check if it is. If it isn't, throw an error.
   */
  public boolean testGetTableID()
  {
    if (testObj.getTableID() == 0)
    {
      System.out.println("Test 1.1 passed - getTableID.");
      return true;
    }
    else
    {
      System.out.println("Test 1.1 failed - getTableID.");
      return false;
    }
  }

  /*
   The tableBound should automatically be set to 0
   Check if it is. If it isn't, throw an error.
   */
  public boolean testGetTableBound()
  {
    if (testObj.getTableBound() == 0)
    {
      System.out.println("Test 1.2 passed - getTableBound.");
      return true;
    }
    else
    {
      System.out.println("Test 1.2 failed - getTableBound.");
      return false;
    }
  }

  // Test some of the setters

  /*
  If the getters passed, then we need to check
  to ensure that the setters work.
  Set the table ID to a random integer, and
  check to see that it returns the same one.
   */
  public boolean testSetTableID()
  {
    testObj.setTableID(27);
    if (testObj.getTableID() == 27)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  // Test the toString function
  public boolean testToString()
  {
    return false;
  }


}
