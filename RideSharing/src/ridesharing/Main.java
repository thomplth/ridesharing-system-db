package ridesharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main (String args[]){

            Connection conn = null;
            int choice = 0;
            
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group40","Group40","3170group40");
                // Do something with the Connection
            } catch(ClassNotFoundException ce){
                System.out.println("Java DB Driver not found!");
                System.exit(0);
            } 
            catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }

            switch(choice){
                case 2:
                    Passenger passenger = new Passenger(conn);
                    if(passenger.getPassenger_output()){
                        // do something in case of error
                    }
                    break;
                case 4:
                    Manager manager = new Manager(conn);
                    if(manager.get_managerOutput()){
                        // do something in case of error
                    }
                    break;
                default:
                    
            }
           
    }
    
}
