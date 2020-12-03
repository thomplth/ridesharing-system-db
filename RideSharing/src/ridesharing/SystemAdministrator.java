package ridesharing;

import java.util.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemAdministrator {
    private Connection conn;

    public SystemAdministrator(Connection c) {
        conn = c;
    }

    public void createTables() {
        System.out.print("Processing...");
        
        try {
            String stmt;
            PreparedStatement pstmt;

            // vehicle table
            stmt = "CREATE TABLE IF NOT EXISTS vehicle(" + 
            "id char(6) primary key," + 
            "model varchar(30) not null," + 
            "seats integer not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // driver table
            stmt = "CREATE TABLE IF NOT EXISTS driver(" + 
            "id integer primary key AUTO_INCREMENT," + 
            "name varchar(30) not null," + 
            "vehicle_id char(6) not null," +
            "driving_years integer not null," + 
            "FOREIGN KEY(vehicle_id) REFERENCES vehicle(id))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // taxi_stop table
            stmt = "CREATE TABLE IF NOT EXISTS taxi_stop(" + 
            "name varchar(20) primary key," + 
            "location_x integer not null," + 
            "location_y integer not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // passenger table
            stmt = "CREATE TABLE IF NOT EXISTS passenger(" + 
            "id integer primary key AUTO_INCREMENT," + 
            "name varchar(30) not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // request table
            stmt = "CREATE TABLE IF NOT EXISTS request(" + 
            "id integer primary key AUTO_INCREMENT," + 
            "passenger_id integer not null," +
            "start_location varchar(20) not null," +
            "destination varchar(20) not null," +
            "model varchar(30)," + // optional
            "passengers integer not null," +
            "taken char(1) not null," + // Y/N statment
            "driving_years integer," + // optional
            "FOREIGN KEY(passenger_id) REFERENCES passenger(id)," +
            "FOREIGN KEY(start_location) REFERENCES taxi_stop(name)," +
            "FOREIGN KEY(destination) REFERENCES taxi_stop(name))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // trip table
            stmt = "CREATE TABLE IF NOT EXISTS trip(" + 
            "id integer primary key AUTO_INCREMENT," + 
            "driver_id integer not null," +
            "passenger_id integer not null," +
            "start_location varchar(20) not null," +
            "destination varchar(20) not null," +
            "start_time datetime not null," + // java.sql.Timestamp in “YYYY-MM-DD HH:mm:ss” format
            "end_time datetime," + // java.sql.Timestamp in “YYYY-MM-DD HH:mm:ss” format
            "fee integer not null," +
            "FOREIGN KEY(driver_id) REFERENCES driver(id)," +
            "FOREIGN KEY(passenger_id) REFERENCES passenger(id)," +
            "FOREIGN KEY(start_location) REFERENCES taxi_stop(name)," +
            "FOREIGN KEY(destination) REFERENCES taxi_stop(name))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            System.out.println("Done! Tables are created!");
        } catch(Exception e) {
            System.out.println("\n[ERROR] " + e);
        }
    }

    public void deleteTables() {
        System.out.print("Processing...");

        try {
            String stmt = "DROP TABLE IF EXISTS ";
            PreparedStatement pstmt;

            String tables[] = {"driver", "vehicle", "passenger", "request", "trip", "taxi_stop"};
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
            System.out.println("\n[ERROR] " + e);
        }
    }

    public void loadData() {
        Scanner scan = new Scanner(System.in); // do not close

        while(true) {
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
                System.out.println("\n[ERROR] Invalid folder path.");
            } catch (Exception e) {
                System.out.println("\n[ERROR] Tables does not exist or files already loaded.");
                break;
            }
        }
    }

    public void checkData() {
        try {
            int counts[] = new int[6];
            String tables[] = {"vehicle", "passenger", "driver", "trip", "request", "taxi_stop"};
            String tables_title[] = {"Vehicle", "Passenger", "Driver", "Trip", "Request", "Taxi_stop"};
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
            System.out.println("[ERROR] " + e);
        }
    }

    private void loadDrivers(String path) throws IOException, SQLException {
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

    private void loadVehicles(String path) throws SQLException, IOException {
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

    private void loadPassengers(String path) throws SQLException, IOException {
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

    private void loadTrips(String path) throws SQLException, IOException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/trips.csv"));
        String line;
        int order[] = {1, 2, 3, 6, 7, 4, 5, 8}; // special order corresponds to trips.csv
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO trip VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for(int i = 0; i < data.length; i++) {
                pstmt.setString(order[i], data[i]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadTaxiStops(String path) throws SQLException, IOException {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/taxi_stops.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO taxi_stop VALUES (?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }
}