import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class EdgeConvertFileParserTest {
  EdgeConvertFileParser testObj;

  @Before
  public void setUp() throws Exception {
    File constructorFile = new File("Courses.edg");
    testObj = new EdgeConvertFileParser(constructorFile);
  }
}
