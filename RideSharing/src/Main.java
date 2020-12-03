import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
	public static void main(String args[]) {
		Connection conn;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group40", "Group40",
					"3170group40");
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
					choice = Integer.parseInt(scan.nextLine());
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
					Passenger passenger = new Passenger();
					passenger_menu(scan, passenger, conn);
					break;
				case 3:
					Driver driver = new Driver(conn);
					driver_menu(driver, conn);
					break;
				case 4:
					Manager manager = new Manager(conn);
					manager_menu(manager);
					break;
				default:

			}
		}
		scan.close();
	}

	public static void admin_menu(SystemAdministrator admin) {
		int choice = 0;
		Scanner scan = new Scanner(System.in); // do not close

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
					choice = Integer.parseInt(scan.nextLine());
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

	public static void passenger_menu(Scanner sc, Passenger passenger, Connection conn) {
		int user_choice = 1;
		int user_id = 0;
		System.out.println("Passenger, what would you like to do?");
		System.out.println("1. Request a ride");
		System.out.println("2. Check trip records");
		System.out.println("3. Go back");
		
		boolean user_choice_passed = true;
		try {
			do{
				System.out.println("Please enter [1-3].");
				user_choice = sc.nextInt();
				if(user_choice != 1 && user_choice != 2 && user_choice != 3){
					System.out.println("[ERROR] Invalid input");
					user_choice_passed = false;
				}
			}while(!user_choice_passed);

			user_choice_passed = true;
			if (user_choice == 3) {
				return;
			}

			do{
				System.out.println("Please enter your ID.");
				user_id = sc.nextInt();
				if(user_id < 0){
					System.out.println("[ERROR] Invalid ID.");
					user_choice_passed = false;
				}

				String psql = "SELECT * FROM passenger p WHERE p.id = ?;";
				ResultSet rs = null;
				PreparedStatement pstmt = null;
				pstmt = conn.prepareStatement(psql);
				pstmt.setInt(1, user_id);
				rs = pstmt.executeQuery();
	
				if(!rs.next()){
					System.out.println("[ERROR] Invalid ID.");
					user_choice_passed = false;
				}

			}while(!user_choice_passed);

		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (user_choice) {
			case 1:
				passenger.requestRide(sc, conn, user_id);
				passenger_menu(sc, passenger, conn);
				break;
			case 2:
				passenger.checkTrip(sc, conn, user_id);
				passenger_menu(sc, passenger, conn);
				break;
			default:
				return;
		}
	}

	public static void driver_menu(Driver driver, Connection conn) {
		int choice = 0, id = 0;
		Scanner scan = new Scanner(System.in);

		while (choice != 4) {
			System.out.println("Driver, what would you like to do?");
			System.out.println("1. Search requests");
			System.out.println("2. Take a request");
			System.out.println("3. Finish a trip");
			System.out.println("4. Go back");

			while (true) {
				try {
					System.out.println("Please enter [1-4]");
					choice = Integer.parseInt(scan.nextLine());
					if (choice < 1 || choice > 4)
						throw new Exception();
					if (choice == 4)
						return;

					while (true) {
						try {
							System.out.println("Please enter your ID.");
							id = scan.nextInt();
							if (id <= 0)
								throw new Exception();

							String stmt = "SELECT * FROM driver d WHERE d.id = ?;";
							PreparedStatement pstmt = conn.prepareStatement(stmt);
							pstmt.setInt(1, id);
							ResultSet rs = pstmt.executeQuery();

							if (!rs.next())
								throw new SQLException();

							break;
						} catch (SQLException sqle) {
							System.out.println("[ERROR] Driver does not exist");
						} catch (Exception ie) {
							System.out.println("[ERROR] Invalid input");
						} 
					}
					break;

				} catch (Exception e) {
					System.out.println("[ERROR] Invalid input.");
				}
			}

			switch (choice) {
				case 1:
					driver.searchRequest(id);
					break;
				case 2:
					driver.takeRequest(id);
					break;
				case 3:
					driver.finishTrip(id);
					break;
				default:
			}
		}
	}

	public static void manager_menu(Manager manager) {
		int choice = 0;
		Scanner scan = new Scanner(System.in); // do not close

		while (choice != 2) {
			System.out.println("Manager, what would you like to do?");
			System.out.println("1. Find trips");
			System.out.println("2. Go back");

			while (true) {
				try {
					System.out.println("Please enter [1-2]");
					choice = Integer.parseInt(scan.nextLine());
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
					manager_menu(manager);
					break;
				default:
					return;
			}
		}
	}
}
