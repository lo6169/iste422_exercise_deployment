import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CreateDDLMySQLTest {
  CreateDDLMySQL testObj;

  @Before
  public void setUp() throws Exception {
    testObj = new CreateDDLMySQL(new EdgeTable[0], new EdgeField[0]);
  }
  
	@Test
	public void testGetProductName(){
		assertEquals("Product name should be MySQL:", testObj.getProductName(), "MySQL");
	}
	
	@Test
	public void testGenerateDatabaseName(){
		testObj.generateDatabaseName();
		assertNotEquals("Database name should not be blank:", testObj.getDatabaseName(), "");
	}
	
	@Test
	public void testGetSQLStringEmpty(){
		assertNotEquals("SQL string for an empty database should contain at least CREATE DATABASE and USE statements:", testObj.getSQLString(), "");
	}
	
	@Test
	public void testGetSQLStringEmptyTable(){
		testObj = bootstrapTables(false);
		assertNotEquals("SQL string should not be empty:", testObj.getSQLString(), "");
	}
	
	@Test
	public void testGetSQLStringTable(){
		testObj = bootstrapTables(true);
		assertNotEquals("SQL string should not be empty:", testObj.getSQLString(), "");
	}

  @Test
  public void testColumnNamesAreOutputed() {
    testObj 
  }
	
	public CreateDDLMySQL bootstrapTables(boolean setup){
		EdgeTable table1 = new EdgeTable("1|Table1");
		EdgeField field1 = new EdgeField("1|Field1");
		if(setup){
			field1.setTableID(table1.getNumFigure());
			field1.setIsPrimaryKey(true);
			table1.addNativeField(field1.getNumFigure());
		}
		table1.makeArrays();
		
		return new CreateDDLMySQL(
			new EdgeTable[]{
				table1
			},
			new EdgeField[]{
				field1
			}
		);
	}
	
	@Test
	public void testConvertStrBooleanToIntTrue(){
		assertEquals("Should convert true to 1:", testObj.convertStrBooleanToInt("true"), 1);
	}
	
	@Test
	public void testConvertStrBooleanToIntFalse(){
		assertEquals("Should convert false to 0:", testObj.convertStrBooleanToInt("false"), 0);
	}
}
