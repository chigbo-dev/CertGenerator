
package gencert;
/**
 *
 * @author Zacch
 */
import java.sql.*;


public final class GetConnection {
//    private static final String URL = "jdbc:mysql://178.128.168.43:3306/omneydb?autoReconnect=true&useSSL=false";
//    private static final String USERNAME = "omni";
//    private static final String PASSWORD = "tvlwuz12#"; 

    private static final String URL = "jdbc:mysql://localhost:3306/certs?autoReconnect=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Africa/Lagos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "naso";  
    
//    private static final String USERNAME = "tvlbank";
//    private static final String PASSWORD = "tvlbank";   
  
 public static Connection getSimpleConnection() {
    //See your driver documentation for the proper format of this string :
    //String DB_CONN_STRING = "jdbc:mysql://localhost:3306/tvlbank";
    //Provided by your driver documentation. In this case, a MySql driver is used 
    //String USER_NAME = "tvlbank";
    //String PASSWORD = "tvlbank";
    
    Connection result = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    }
    catch (ClassNotFoundException ex){
      log("Check classpath. Cannot load db driver: ");
    }

    try {
        result = DriverManager.getConnection(URL, USERNAME, PASSWORD);    
    }
    catch (SQLException e){
      log( "Driver loaded, but cannot connect to db:");
    }
    return result;
  }

  private static void log(Object aObject){
    System.out.println(aObject);
  }
}
