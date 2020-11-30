package ridesharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main (String args[]){

            Connection conn = null;
            
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group40","Group40","3170group40");
                // Do something with the Connection
                Statement dropDatabase = conn.createStatement();
                Statement createDatabase = conn.createStatement();
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("use group40;");
                menu(conn);
                conn.close();
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

        int choice = 0;
		Scanner keyboard = new Scanner(System.in);

		System.out.println("Welcome! Who are you?");
		System.out.println("1. An administrator");
		System.out.println("2. A passenger");
		System.out.println("3. A driver");
		System.out.println("4. A manager");
		System.out.println("5. None of the above");
		
		try {
			do {
				System.out.println("Please enter [1-4]");
				choice = keyboard.nextInt();

				
				if (choice < 1 || choice > 4)
					System.out.println("[ERROR] Invalid input.");
			} while (choice < 1 || choice > 4);
		} 
		catch (Exception e) {
			System.out.println("[ERROR] Invalid input.");
		}

		if (choice == 1)
			administrator(conn);
		else if (choice == 2)
			passenger(conn);
		else if (choice == 3)
			driver(conn);
		else if (choice == 4)
			manager(conn);
		else
			System.out.println("Goodbye.");
           
    }
    
}
