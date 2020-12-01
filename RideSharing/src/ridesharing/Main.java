package ridesharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
	public static void main(String args[]) {
		Connection conn;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group40", "Group40", "3170group40");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("use group40;");
			menu(conn);
			conn.close();
		} catch (ClassNotFoundException ce) {
			System.out.println("Java DB Driver not found!");
			System.exit(0);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public static void menu(Connection conn) {
		int choice = 0;
		Scanner scan = new Scanner(System.in);

		while (choice != 5) {
			System.out.println("Welcome! Who are you?");
			System.out.println("1. An administrator");
			System.out.println("2. A passenger");
			System.out.println("3. A driver");
			System.out.println("4. A manager");
			System.out.println("5. None of the above");

			while (true) {
				try {
					System.out.println("Please enter [1-4]");
					choice = scan.nextInt();
					if (choice < 1 || choice > 5)
						throw new Exception();
					break;
				} catch (Exception e) {
					System.out.println("[ERROR] Invalid input.");
				}
			}

			switch (choice) {
				case 1:
					SystemAdministrator admin = new SystemAdministrator(conn);
					admin_menu(admin);
					break;
				case 2:
					Passenger passenger = new Passenger(conn);
					passenger_menu(passenger);
					break;
				case 3:
					Driver driver = new Driver(conn);
					driver_menu(driver);
					break;
				case 4:
					Manager manager = new Manager(conn);
					manager_menu(manager);
					break;
				default:
			}
			choice = 0;
		}
		scan.close();
	}

	public static void admin_menu(SystemAdministrator admin) {
		int choice = 0;
		Scanner scan = new Scanner(System.in);

		while (choice != 5) {
			System.out.println("Administrator, what would you like to do?");
			System.out.println("1. Create tables");
			System.out.println("2. Delete tables");
			System.out.println("3. Load data");
			System.out.println("4. Check data");
			System.out.println("5. Go back");

			while (true) {
				try {
					System.out.println("Please enter [1-5]");
					choice = scan.nextInt();
					if (choice < 1 || choice > 5)
						throw new Exception();
					break;
				} catch (Exception e) {
					System.out.println("[ERROR] Invalid input.");
				}
			}

			switch (choice) {
				case 1:
					admin.createTables();
					break;
				case 2:
					admin.deleteTables();
					break;
				case 3:
					admin.loadData();
					break;
				case 4:
					admin.checkData();
					break;
				default:
			}
		}
	}

	public static void passenger_menu(Passenger passenger) {
	}

	public static void driver_menu(Driver driver) {
		// int choice = 0;
		// Scanner scan = new Scanner(System.in);

		// while (choice != 2) {
		// 	System.out.println("Driver, what would you like to do?");
		// 	System.out.println("1. Search requests");
		// 	System.out.println("2. Take a request");
		// 	System.out.println("3. Finish a trip");
		// 	System.out.println("4. Go back");

		// 	while (true) {
		// 		try {
		// 			System.out.println("Please enter [1-4]");
		// 			choice = scan.nextInt();
		// 			if (choice < 1 || choice > 4)
		// 				throw new Exception();
		// 			break;
		// 		} catch (Exception e) {
		// 			System.out.println("[ERROR] Invalid input.");
		// 		}
		// 	}

		// 	switch (choice) {
		// 		case 1:
		// 			// search requests
		// 			break;
		// 		case 2:
		// 			// take a request
		// 			break;
		// 		case 3:
		// 			// finish trip
		// 			break;
		// 		default:
		// 	}
		// }
	}

	public static void manager_menu(Manager manager) {
		int choice = 0;
		Scanner scan = new Scanner(System.in);

		while (choice != 2) {
			System.out.println("Manager, what would you like to do?");
			System.out.println("1. Find trips");
			System.out.println("2. Go back");

			while (true) {
				try {
					System.out.println("Please enter [1-2]");
					choice = scan.nextInt();
					if (choice < 1 || choice > 2)
						throw new Exception();
					break;
				} catch (Exception e) {
					System.out.println("[ERROR] Invalid input.");
				}
			}

			switch (choice) {
				case 1:
					manager.listTrips();
					break;
				default:
			}
		}
	}
}
