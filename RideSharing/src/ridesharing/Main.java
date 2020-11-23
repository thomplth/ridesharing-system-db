package ridesharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main (String args[]){

            Connection conn = null;
            
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn =
                DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group40?" +
                                            "user=Group40&password=3170group40");

                // Do something with the Connection
            } catch(ClassNotFoundException ce){
                System.out.println("Java DB Driver not found!");
            } 
            catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
    }
    
}
