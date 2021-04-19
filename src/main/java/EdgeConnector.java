import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeConnector {
   private final int numConnector;
   private final int endPoint1;
   private final int endPoint2;
   private final String endStyle1;
   private final String endStyle2;
   private boolean isEP1Field;
   private boolean isEP2Field;
   private boolean isEP1Table;
   private boolean isEP2Table;
   private static Logger logger = LogManager.getLogger(EdgeConnector.class.getName());
      
   public EdgeConnector(String inputString) {
      logger.debug("EdgeConnector constructor called with the input of %s", inputString);
      StringTokenizer st = new StringTokenizer(inputString, EdgeConvertFileParser.DELIM);
      numConnector = Integer.parseInt(st.nextToken());
      endPoint1 = Integer.parseInt(st.nextToken());
      endPoint2 = Integer.parseInt(st.nextToken());
      endStyle1 = st.nextToken();
      endStyle2 = st.nextToken();
      isEP1Field = false;
      isEP2Field = false;
      isEP1Table = false;
      isEP2Table = false;
   }
   
   public int getNumConnector() {
      logger.debug("getNumConnector has been called.");
      return numConnector;
   }
   
   public int getEndPoint1() {
      logger.debug("getEndPoint1 has been called.");
      return endPoint1;
   }
   
   public int getEndPoint2() {
      logger.debug("getEndPoint2 has been called.");
      return endPoint2;
   }
   
   public String getEndStyle1() {
      logger.debug("getEndStyle1 has been called.");
      return endStyle1;
   }
   
   public String getEndStyle2() {
      logger.debug("getEndStyle2 has been called.");
      return endStyle2;
   }
   public boolean getIsEP1Field() {
      logger.debug("getIsEP1Field has been called.");
      return isEP1Field;
   }
   
   public boolean getIsEP2Field() {
      logger.debug("getIsEP2Field has been called.");
      return isEP2Field;
   }

   public boolean getIsEP1Table() {
      logger.debug("getIsEP1Table has been called.");
      return isEP1Table;
   }

   public boolean getIsEP2Table() {
      logger.debug("getIsEP2Table has been called.");
      return isEP2Table;
   }

   public void setIsEP1Field(boolean value) {
      logger.debug("setIsEP1Field has been called with a value of %s", value);
      isEP1Field = value;
   }
   
   public void setIsEP2Field(boolean value) {
      logger.debug("setIsEP2Field has been called with a value of %s", value);
      isEP2Field = value;
   }

   public void setIsEP1Table(boolean value) {
      logger.debug("setIsEP1Table has been called with a value of %s", value);
      isEP1Table = value;
   }

   public void setIsEP2Table(boolean value) {
      logger.debug("setIsEP2Table has been called with a value of %s", value);
      isEP2Table = value;
   }
}
