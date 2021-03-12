import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class EdgeConvertCreateDDLTest {
  EdgeConvertCreateDDL testObj;

  @Before
  public void setUp() throws Exception {
    testObj = new EdgeConvertCreateDDL() {
      @Override
      public String getDatabaseName() { return ""; }

      @Override
      public String getProductName() { return ""; }

      @Override
      public String getSQLString() { return ""; }

      @Override
      public void createDDL() { }
    };
  }

  @Test
  public void emptyConstructor() {

    // Example of how a value can be passed into a test
//    String opt1Str = System.getProperty("optionone");
//    final long opt1;
//    if (opt1Str == null) {
//      opt1 = 1;
//    }
//    else {
//      opt1 = Long.parseLong(opt1Str);
//    }
//    assertEquals("numConnector was intialized to 1 so it should be 1",(long)opt1,testObj.getNumConnector());
  }

}
