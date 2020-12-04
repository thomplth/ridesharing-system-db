import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    public static Connection conn;
    public static Scanner scan = new Scanner(System.in);

    // Main Class
    public static void main(String args[]) {
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
                    if(!scan.hasNext()){
                        return;
                    }
                    String a_choice = scan.nextLine();
                    choice = Integer.parseInt(a_choice);
                    if (choice < 1 || choice > 5){
                        throw new Exception();
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("[ERROR] Invalid input.");
                }
            }

            switch (choice) {
                case 1:
                    admin_menu();
                    break;
                case 2:
                    passenger_menu();
                    break;
                case 3:
                    driver_menu();
                    break;
                case 4:
                    manager_menu();
                    break;
                default:

            }
        }
    }

    public static void admin_menu() {
        int choice = 0;

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
                    String a_choice;
                    if(scan.hasNext()){
                        a_choice = scan.nextLine();
                    }else{
                        return;
                    }
                    choice = Integer.parseInt(a_choice);
                    if (choice < 1 || choice > 5)
                        throw new Exception();
                    break;
                } catch (Exception e) {
                    System.out.println("[ERROR] Invalid input.");
                }
            }

            switch (choice) {
                case 1:
                    createTables();
                    break;
                case 2:
                    deleteTables();
                    break;
                case 3:
                    loadData();
                    break;
                case 4:
                    checkData();
                    break;
            }
        }
    }

    public static void passenger_menu() {
        int choice = 0;
        int id = 0;

        while (choice != 3) {
            System.out.println("Passenger, what would you like to do?");
            System.out.println("1. Request a ride");
            System.out.println("2. Check trip records");
            System.out.println("3. Go back");

            while (true) {
                try {
                    System.out.println("Please enter [1-3].");
                    String a_choice;
                    if(scan.hasNext()){
                        a_choice = scan.nextLine();
                    }else{
                        return;
                    }
                    choice = Integer.parseInt(a_choice);
                    if (choice != 1 && choice != 2 && choice != 3)
                        throw new Exception();
                    break;
                } catch (Exception e) {
                    System.out.println("[ERROR] Invalid input.");
                }
            }

            if (choice == 3)
                break;

            while (true) {
                try {
                    System.out.println("Please enter your ID.");
                    id = Integer.parseInt(scan.nextLine());
                    if (id <= 0)
                        throw new Exception();

                    String stmt = "SELECT * FROM passenger p WHERE p.id = ?;";
                    PreparedStatement pstmt = conn.prepareStatement(stmt);
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();

                    if (!rs.next())
                        throw new SQLException();

                    break;
                } catch (SQLException sqle) {
                    System.out.println("[ERROR] Passenger does not exist");
                } catch (Exception ie) {
                    System.out.println("[ERROR] Invalid input");
                }
            }

            switch (choice) {
                case 1:
                    requestRide(id);
                    break;
                case 2:
                    checkTrip(id);
                    break;
            }
        }
    }

    public static void driver_menu() {
        int choice = 0, id = 0;
        if(!scan.hasNext()){
            return;
        }

        while (choice != 4) {
            System.out.println("Driver, what would you like to do?");
            System.out.println("1. Search requests");
            System.out.println("2. Take a request");
            System.out.println("3. Finish a trip");
            System.out.println("4. Go back");

            while (true) {
                try {
                    System.out.println("Please enter [1-4]");
                    String a_choice;
                    if(scan.hasNext()){
                        a_choice = scan.nextLine();
                    }else{
                        return;
                    }
                    choice = Integer.parseInt(a_choice);
                    if (choice < 1 || choice > 4)
                        throw new Exception();
                    if (choice == 4)
                        return;

                    while (true) {
                        try {
                            System.out.println("Please enter your ID.");
                            if(!scan.hasNext()){
                                return;
                            }
                            id = Integer.parseInt(scan.nextLine());
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
                    searchRequest(id);
                    break;
                case 2:
                    takeRequest(id);
                    break;
                case 3:
                    finishTrip(id);
                    break;
                default:
            }
        }
    }

    public static void manager_menu() {
        int choice = 0;

        while (choice != 2) {
            System.out.println("Manager, what would you like to do?");
            System.out.println("1. Find trips");
            System.out.println("2. Go back");

            while (true) {
                try {
                    System.out.println("Please enter [1-2]");
                    String a_choice;
                    if(scan.hasNext()){
                        a_choice = scan.nextLine();
                    }else{
                        return;
                    }
                    choice = Integer.parseInt(a_choice);
                    if (choice < 1 || choice > 2)
                        throw new Exception();
                    break;
                } catch (Exception e) {
                    System.out.println("[ERROR] Invalid input.");
                }
            }

            switch (choice) {
                case 1:
                    listTrips();
                    break;
                default:
                    return;
            }
        }
    }

    // SystemAdministrator Class

    public static void createTables() {
        System.out.print("Processing...");

        try {
            String stmt;
            PreparedStatement pstmt;

            // vehicle table
            stmt = "CREATE TABLE IF NOT EXISTS vehicle(" + "id char(6) primary key," + "model varchar(30) not null,"
                    + "seats integer not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // driver table
            stmt = "CREATE TABLE IF NOT EXISTS driver(" + "id integer primary key AUTO_INCREMENT,"
                    + "name varchar(30) not null," + "vehicle_id char(6) not null," + "driving_years integer not null,"
                    + "FOREIGN KEY(vehicle_id) REFERENCES vehicle(id))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // taxi_stop table
            stmt = "CREATE TABLE IF NOT EXISTS taxi_stop(" + "name varchar(20) primary key,"
                    + "location_x integer not null," + "location_y integer not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // passenger table
            stmt = "CREATE TABLE IF NOT EXISTS passenger(" + "id integer primary key AUTO_INCREMENT,"
                    + "name varchar(30) not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // request table
            stmt = "CREATE TABLE IF NOT EXISTS request(" + "id integer primary key AUTO_INCREMENT,"
                    + "passenger_id integer not null," + "start_location varchar(20) not null,"
                    + "destination varchar(20) not null," + "model varchar(30)," + // optional
                    "passengers integer not null," + "taken char(1) not null," + // Y/N statment
                    "driving_years integer," + // optional
                    "FOREIGN KEY(passenger_id) REFERENCES passenger(id),"
                    + "FOREIGN KEY(start_location) REFERENCES taxi_stop(name),"
                    + "FOREIGN KEY(destination) REFERENCES taxi_stop(name))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // trip table
            stmt = "CREATE TABLE IF NOT EXISTS trip(" + "id integer primary key AUTO_INCREMENT,"
                    + "driver_id integer not null," + "passenger_id integer not null,"
                    + "start_location varchar(20) not null," + "destination varchar(20) not null,"
                    + "start_time datetime not null," + // java.sql.Timestamp in YYYY-MM-DD HH:mm:ss format
                    "end_time datetime," + // java.sql.Timestamp in YYYY-MM-DD HH:mm:ss format
                    "fee integer not null," + "FOREIGN KEY(driver_id) REFERENCES driver(id),"
                    + "FOREIGN KEY(passenger_id) REFERENCES passenger(id),"
                    + "FOREIGN KEY(start_location) REFERENCES taxi_stop(name),"
                    + "FOREIGN KEY(destination) REFERENCES taxi_stop(name))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            System.out.println("Done! Tables are created!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n[ERROR] " + e);
        }
    }

    public static void deleteTables() {
        System.out.print("Processing...");

        try {
            String stmt = "DROP TABLE IF EXISTS ";
            PreparedStatement pstmt;

            String tables[] = { "driver", "vehicle", "passenger", "request", "trip", "taxi_stop" };
            pstmt = conn.prepareStatement(stmt + tables[4]);
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + tables[3]);
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + tables[2]);
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + tables[5]);
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + tables[0]);
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + tables[1]);
            pstmt.execute();

            System.out.println("Done! Tables are deleted!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n[ERROR] " + e);
        }
    }

    public static void loadData() {

        while (true) {
            try {
                System.out.println("Please enter the folder path");
                String path = scan.nextLine();
                System.out.print("Processing...");

                loadPassengers(path);
                loadTaxiStops(path);
                loadVehicles(path);
                loadDrivers(path);
                loadTrips(path);

                System.out.println("Data is loaded!");
                break;
            } catch (FileNotFoundException fe) {
                fe.printStackTrace();
                System.out.println("\n[ERROR] Invalid folder path.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("\n[ERROR] Tables does not exist or files already loaded.");
                break;
            }
        }
    }

    public static void checkData() {
        try {
            int counts[] = new int[6];
            String tables[] = { "vehicle", "passenger", "driver", "trip", "request", "taxi_stop" };
            String tables_title[] = { "Vehicle", "Passenger", "Driver", "Trip", "Request", "Taxi_stop" };
            String stmt = "SELECT COUNT(*) FROM ";
            PreparedStatement pstmt;

            for (int i = 0; i < tables.length; i++) {
                pstmt = conn.prepareStatement(stmt + tables[i]);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                counts[i] = rs.getInt("count(*)");
            }

            System.out.println("Numbers of records in each table:");
            for (int i = 0; i < tables.length; i++) {
                System.out.println(tables_title[i] + ": " + counts[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] " + e);
        }
    }

    public static void loadDrivers(String path) throws IOException, SQLException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/drivers.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO driver VALUES (?, ?, ?, ?)");

        while ((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for (int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i - 1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    public static void loadVehicles(String path) throws SQLException, IOException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/vehicles.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicle VALUES (?, ?, ?)");

        while ((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for (int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i - 1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    public static void loadPassengers(String path) throws SQLException, IOException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/passengers.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO passenger VALUES (?, ?)");

        while ((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for (int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i - 1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    public static void loadTrips(String path) throws SQLException, IOException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/trips.csv"));
        String line;
        int order[] = { 1, 2, 3, 6, 7, 4, 5, 8 }; // special order corresponds to trips.csv
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO trip VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        while ((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for (int i = 0; i < data.length; i++) {
                pstmt.setString(order[i], data[i]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    public static void loadTaxiStops(String path) throws SQLException, IOException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/taxi_stops.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO taxi_stop VALUES (?, ?, ?)");

        while ((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for (int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i - 1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    // Passenger Class

    public static void requestRide(int user_id) {
        int passenger_num = 0;
        String start_location = "";
        String end_location = "";
        String car_model = "_";
        int driver_years = 0;
        boolean user_choice_passed = true;
        String psql = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        if(!scan.hasNext()){
            return;
        }

        try {
            do {
                System.out.println("Please enter the number of passengers.");
                passenger_num = Integer.parseInt(scan.nextLine());
                if (passenger_num < 1 || passenger_num > 8) {
                    System.out.println("[ERROR] Invalid number of passengers.");
                    user_choice_passed = false;
                }
            } while (!user_choice_passed);

            user_choice_passed = true;

            do {
                System.out.println("Please enter the start location.");
                start_location = scan.nextLine();

                psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
                pstmt = conn.prepareStatement(psql);
                pstmt.setString(1, start_location);
                rs = pstmt.executeQuery();

                if (!rs.next() || start_location == "" || start_location == null) {
                    System.out.println("[ERROR] Start location not found.");
                    user_choice_passed = false;
                }
            } while (!user_choice_passed);

            user_choice_passed = true;

            do {
                System.out.println("Please enter the destination.");
                end_location = scan.nextLine();

                if (end_location == start_location) {
                    System.out.println("[ERROR] Start location and destionation should be different.");
                    user_choice_passed = false;
                }

                psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
                pstmt.setString(1, end_location);
                rs = pstmt.executeQuery();

                if (!rs.next() || end_location == "" || end_location == null) {
                    System.out.println("[ERROR] Destination not found.");
                    user_choice_passed = false;
                }
            } while (!user_choice_passed);

            // Choose model
            System.out.println("Please enter the model. (Press enter to skip)");
            car_model = scan.nextLine().toLowerCase();

            /*
             * 
             * psql =
             * "SELECT * FROM vehicle WHERE seats >= ? AND UPPER(model) LIKE UPPER(?);";
             * pstmt = conn.prepareStatement(psql); pstmt.setInt(1, passenger_num);
             * pstmt.setString(2, "%" + car_model + "%"); rs = pstmt.executeQuery();
             * 
             * if(!rs.next()){ System.out.println("[ERROR] Invalid model."); }
             * 
             */

            // Enter minimum driving year

            System.out.println("Please enter the minimum driving years of the driver. (Press enter to skip)");
            String pre_driver_years = scan.nextLine();
            if (pre_driver_years != null && pre_driver_years != " " && !pre_driver_years.isEmpty()) {
                driver_years = Integer.parseInt(pre_driver_years);
                if (driver_years < 0) {
                    throw new Exception("Wrong driver years!");
                }
            }
            System.out.println("All input received. Querying Database.");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // At last, create the request if no errors exist
        try {

            psql = "SELECT COUNT(*) AS d_count FROM driver d, vehicle v WHERE v.seats >= ? AND driving_years >= ? AND UPPER(model) LIKE UPPER(?) AND d.vehicle_id = v.id;";
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, passenger_num);
            pstmt.setInt(2, driver_years);
            pstmt.setString(3, "%" + car_model + "%");
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("There are no drivers that can take the request.");
                return;
            } else {
                psql = "INSERT INTO request (passenger_id, start_location,destination,model,passengers,taken,driving_years) VALUES (?,?,?,?,?,'N',?);";
                pstmt = conn.prepareStatement(psql);
                pstmt.setInt(1, user_id);
                pstmt.setString(2, start_location);
                pstmt.setString(3, end_location);
                pstmt.setString(4, car_model);
                pstmt.setInt(5, passenger_num);
                pstmt.setInt(6, driver_years);
                if (pstmt.executeUpdate() > 0) {
                    System.out.println(
                            "Your request is placed. " + rs.getInt(1) + " drivers are able to take the request.");
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public static void checkTrip(int user_id) {
        String start_date = "";
        String end_date = "";
        String end_location = "";
        boolean user_choice_passed = true;
        Pattern date = Pattern.compile("[1-2][0-9][0-9][0-9]-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");
        String psql = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        if(!scan.hasNext()){
            return;
        }

        try {
            
            do {
                System.out.println("Please enter the start date. (YYYY-MM-DD)");
                start_date = scan.nextLine();

                if (start_date == "" || start_date == null || !date.matcher(start_date).matches()) {
                    user_choice_passed = false;
                    System.out.println("[ERROR] Invalid start date.");
                } else break;
            } while (!user_choice_passed);

            user_choice_passed = true;

            do {
                System.out.println("Please enter the end date. (YYYY-MM-DD)");
                end_date = scan.nextLine();

                if (end_date == "" || end_date == null || !date.matcher(start_date).matches()) {
                    user_choice_passed = false;
                    System.out.println("[ERROR] Invalid end date.");
                } else break;
            } while (!user_choice_passed);

            user_choice_passed = true;

            do {
                System.out.println("Please enter the destination.");
                end_location = scan.nextLine();
                psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
                pstmt = conn.prepareStatement(psql);
                pstmt.setString(1, end_location);
                rs = pstmt.executeQuery();

                if (!rs.next() || end_location == "" || end_location == null) {
                    user_choice_passed = false;
                    System.out.println("[ERROR] Destination not found.");
                }
            } while (!user_choice_passed);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        psql = "SELECT t.id,d.name,v.id,v.model,t.* FROM trip t,driver d,vehicle v WHERE t.passenger_id = ? AND t.start_time >= ? AND t.end_time <= ? AND t.destination = ? AND t.driver_id = d.id AND d.vehicle_id = v.id;";
        rs = null;
        pstmt = null;
        try {
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, start_date);
            pstmt.setString(3, end_date);
            pstmt.setString(4, end_location);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("There are no trips matching your criteria");
                rs.beforeFirst();
            } else {
                System.out.println(
                        "Trip_ID, Driver Name, Vehicle ID, Vehicle Model, Start, End, Fee, Start Location, Destination");
                do {
                    System.out.println(String.format("%d, %s, %s, %s, %s, %s, %d, %s, %s", rs.getInt("t.id"),
                            rs.getString("d.name"), rs.getString("v.id"), rs.getString("v.model"),
                            rs.getString("t.start_time"), rs.getString("t.end_time"), rs.getInt("t.fee"),
                            rs.getString("t.start_location"), rs.getString("t.destination")));
                } while (rs.next());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something was wrong with the SQL query");
            e.printStackTrace();
        }
    }

    // Driver Class

    public static void searchRequest(int did) {
        int x = 0, y = 0, distance = 0;

        try {
            System.out.println("Please enter the coordinates of your location.");
            String[] coords = scan.nextLine().split(" ");
            x = Integer.parseInt(coords[0]);
            y = Integer.parseInt(coords[1]);

            do {
                System.out.println("Please enter the maximum distance from you to the passenger.");
                distance = Integer.parseInt(scan.nextLine());

                if (distance <= 0)
                    System.out.println("[ERROR] Distance should be greater than 0");
            } while (distance <= 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] " + e);
        }

        try {
            String stmt;
            PreparedStatement pstmt;

            stmt = "SELECT * FROM driver WHERE id = ?";
            pstmt = conn.prepareStatement(stmt);
            pstmt.setInt(1, did);
            ResultSet rs = pstmt.executeQuery();

            if (rs == null)
                System.out.println("The user does not exist");
            else {

                stmt = "SELECT r.id, p.name, r.passengers, r.start_location, r.destination FROM request r, driver d, vehicle v, passenger p, taxi_stop t "
                        + "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND r.start_location = t.name AND d.id = ? "
                        + "AND r.taken = 'N' AND d.driving_years >= r.driving_years AND LOWER(v.model) LIKE LOWER(CONCAT('%', r.model, '%')) "
                        + "AND v.seats >= r.passengers AND (? >= (SQRT(POWER(t.location_x-?,2) + POWER(t.location_y-?,2))))";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, did);
                pstmt.setInt(2, x);
                pstmt.setInt(3, y);
                pstmt.setInt(4, distance);
                rs = pstmt.executeQuery();

                if (!rs.next()){
                    System.out.println("There are no requests available at this moment.");
                }else{
                    System.out.println("request ID, passenger name, num of passengers, start location, destination");
                    do {
                        System.out.println(
                                rs.getInt("r.id") + ", " + rs.getString("p.name") + ", " + rs.getInt("r.passengers") + ", "
                                        + rs.getString("r.start_location") + ", " + rs.getString("r.destination"));
                    } while (rs.next());
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            System.out.println("Error: " + exp);
        }
    }

    public static void takeRequest(int did) {
        int rid = 0;
        boolean user_choice_passed = true;
        String stmt;
        PreparedStatement pstmt;
        if(!scan.hasNext()){
            return;
        }

        try {

            stmt = "SELECT * FROM trip WHERE driver_id = ? AND end_time IS NULL";
            pstmt = conn.prepareStatement(stmt);
            pstmt.setInt(1, did);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("[ERROR] Please finish the unfinished trip before taking a new one.");
                return;
            }

            do {
                System.out.println("Please enter the request ID");
                try {
                    rid = Integer.parseInt(scan.nextLine());
                } catch (Exception nfe) {
                    System.out.println("[ERROR] The request id should be a number.");
                    user_choice_passed = false;
                    continue;
                }

                stmt = "SELECT * FROM request r WHERE id = ? AND taken = 'N'";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, rid);
                rs = pstmt.executeQuery();

                if (!rs.next() || rid < 0) {
                    System.out.println("[ERROR] The request id is incorrect or the request have been taken.");
                    user_choice_passed = false;
                }

            } while (!user_choice_passed);

            stmt = "SELECT * FROM driver d, vehicle v, request r, passenger p "
                    + "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND d.id = ? AND r.id = ? "
                    + "AND r.taken = 'N' AND v.seats >= r.passengers AND LOWER(v.model) LIKE LOWER(CONCAT('%', r.model, '%')) "
                    + "AND d.driving_years >= r.driving_years";

            pstmt = conn.prepareStatement(stmt);
            pstmt.setInt(1, did);
            pstmt.setInt(2, rid);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("You do not satisfy all the criteria of the request");
                return;
            } else {
                stmt = "SELECT p.id, p.name, r.start_location, r.destination FROM request r, passenger p "
                        + "WHERE r.id = ? AND p.id = r.passenger_id AND taken = 'N'";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, rid);
                rs = pstmt.executeQuery();

                rs.next();
                int pid = rs.getInt("p.id");
                // String name = rs.getString("p.name");
                String start = rs.getString("r.start_location");
                String dest = rs.getString("r.destination");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                stmt = "INSERT INTO trip (driver_id, passenger_id, start_location, destination, start_time, fee) "
                        + "VALUES (?, ?, ?, ?, ?, 0)";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, did);
                pstmt.setInt(2, pid);
                pstmt.setString(3, start);
                pstmt.setString(4, dest);
                pstmt.setString(5, sdf.format(now));
                pstmt.executeUpdate();

                stmt = "UPDATE request SET taken = 'Y' WHERE id = ?";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, rid);
                pstmt.executeUpdate();

                stmt = "SELECT t.id, p.name, t.start_time FROM trip t, passenger p "
                        + "WHERE t.passenger_id = p.id AND t.start_time = ?";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setString(1, sdf.format(now));
                rs = pstmt.executeQuery();
                rs.next();

                System.out.println("Trip ID, Passenger name, Start");
                System.out.println(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void finishTrip(int did) {
        String choice = "";

        try {
            String stmt;
            PreparedStatement pstmt;

            stmt = "SELECT id, passenger_id, start_time FROM trip WHERE driver_id = ? AND end_time IS NULL";
            pstmt = conn.prepareStatement(stmt);
            pstmt.setInt(1, did);
            ResultSet rs = pstmt.executeQuery();
            int tid = rs.getInt(1);
            java.sql.Timestamp start = rs.getTimestamp(3);

            if (!rs.next())
                System.out.println("[ERROR] You don't have unfinished trip.");
            else {
                System.out.println("Trip ID, Passenger ID, Start");
                System.out.println(rs.getInt(1) + ", " + rs.getInt(2) + ", " + start);
                do {
                    System.out.println("Do you wish to finish the trip? [y/n]");
                    choice = scan.nextLine();

                    if (choice.equals("y") || choice.equals("n")) {
                        if (choice.equals("n"))
                            return;
                        else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date now = new Date();
                            java.sql.Timestamp endt = new java.sql.Timestamp(now.getTime());

                            long duration = now.getTime() - start.getTime();
                            duration = (duration / 1000) / 60;

                            stmt = "UPDATE trip SET end_time = ?, fee = ? WHERE id = ?";
                            pstmt = conn.prepareStatement(stmt);
                            pstmt.setTimestamp(1, endt);
                            pstmt.setInt(2, (int) duration);
                            pstmt.setInt(3, tid);
                            pstmt.execute();

                            stmt = "SELECT t.id, p.name, t.start_time, t.end_time, t.fee FROM trip t, passenger p "
                                    + "WHERE t.passenger_id = p.id AND t.id = ?";
                            pstmt = conn.prepareStatement(stmt);
                            pstmt.setInt(1, tid);
                            rs = pstmt.executeQuery();
                            rs.next();

                            System.out.println("Trip ID, Passenger name, Start, End, Fee");
                            System.out
                                    .println(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getString(sdf.format(3))
                                            + ", " + rs.getString(sdf.format(4)) + ", " + rs.getInt(5));
                        }
                    } else
                        System.out.println("[ERROR] Please enter y/n");
                } while (choice.equals("y") || choice.equals("n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error" + e);
        }
    }

    // Manager Class

    public static void listTrips() {
        int min_distance = 0;
        int max_distance = 0;
        boolean user_choice_passed = true;

        try {
            do {
                    System.out.println("Please enter the minimum travelling distance.");
                    if(!scan.hasNext()){
                        return;
                    }
                    min_distance = Integer.parseInt(scan.nextLine());
                    System.out.println(min_distance);

                    if (min_distance < 0) {
                        user_choice_passed = false;
                        System.out.println("[ERROR] Invalid minimum distance.");
                    }
            } while (!user_choice_passed);

            user_choice_passed = true;

            do {
                    System.out.println("Please enter the maximum travelling distance.");
                    if(!scan.hasNext()){
                        return;
                    }
                    max_distance = Integer.parseInt(scan.nextLine());

                    if (max_distance < 0 || max_distance <= min_distance) {
                        user_choice_passed = false;
                        System.out.println("[ERROR] Invalid maximum distance.");
                    }
            } while (!user_choice_passed);

            System.out.println("All input received. Querying Database.");

        } catch (Exception e) {
            return;
        }

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String psql = "IF NOT EXISTS distancetable CREATE VIEW distancetable AS SELECT ts1.name AS start, ts2.name AS end, SQRT(POWER(ts1.location_x - ts2.location_x,2)+POWER(ts1.location_y - ts2.location_y,2)) AS distance FROM taxi_stop ts1, taxi_stop ts2 WHERE ts1.name != ts2.name;";
        int create_view = 0;
        try {
            Statement stmt = conn.createStatement();
            create_view = stmt.executeUpdate(psql);
        } catch (SQLException sqle) {
            System.out.println("Unable to create view.");
        }

        try {
            psql = "SELECT t.id,d.name,p.name,t.start_location,t.destination,t.start_time,t.end_time FROM trip t LEFT JOIN driver d ON t.driver_id = d.id LEFT JOIN passenger p ON t.passenger_id = p.id LEFT JOIN distancetable dt ON t.start_location = dt.start AND t.destination = dt.end WHERE dt.distance >= ? AND dt.distance <= ?;";

            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, min_distance);
            pstmt.setInt(2, max_distance);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("There are no trips matching your criteria");
            } else {
                System.out.println("Trip ID, Driver Name, Passenger Name, Start Location, Destination, Duration");
                do {
                    int duration = getDuration(rs.getString("t.start_time"), rs.getString("t.end_time"));
                    System.out.println(String.format("%d, %s, %s, %s, %s, %s", rs.getInt("t.id"),
                            rs.getString("d.name"), rs.getString("p.name"), rs.getString("t.start_location"),
                            rs.getString("t.destination"), Integer.toString(duration)));
                } while (rs.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something was wrong with the SQL query");
        }
        if (create_view == 1) {
            psql = "IF EXISTS distancetable DROP VIEW distancetable;";
            try {
                pstmt = conn.prepareStatement(psql);
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Cannot drop view.");
            }
        }
    }

    public static int getDuration(String start, String end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(start); // not sure if the casting work
            d2 = format.parse(end);
        } catch (ParseException e) {
            System.out.println("Failed to compute duration.");
        }
        long diff = d2.getTime() - d1.getTime();
        return (int) (diff / (60 * 1000)) % 60;
    }
}
