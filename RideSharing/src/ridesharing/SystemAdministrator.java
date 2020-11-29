package ridesharing;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SystemAdministrator {
    private Connection conn;
    private final String tables[] = {"driver", "vehicle", "passenger", "request", "trip", "taxi_stop"};

    public SystemAdministrator(Connection c) {
        conn = c;
    }

    public void createTables() {
        System.out.print("Processing...");
        
        try {
            String stmt;
            PreparedStatement pstmt;

            // driver table
            stmt = "CREATE TABLE driver(" + 
            "id integer primary key," + 
            "name varchar(30) not null," + 
            "vehicle_id varchar(6) not null," +
            "driving_years integer not null," + 
            "FOREIGN KEY(vehicle_id) REFERENCES vehicle(id))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // vehicle table
            stmt = "CREATE TABLE vehicle(" + 
            "id integer primary key," + 
            "model varchar(30) not null," + 
            "seats integer not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // passeneger table
            stmt = "CREATE TABLE passeneger(" + 
            "id integer primary key," + 
            "name varchar(30) not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // request table
            stmt = "CREATE TABLE request(" + 
            "id integer primary key," + 
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
            stmt = "CREATE TABLE trip(" + 
            "id integer primary key," + 
            "driver_id integer not null," +
            "passenger_id integer not null," +
            "start_location varchar(20) not null," +
            "destination varchar(20) not null," +
            "start_time char(19) not null," + // java.time in “YYYY-MM-DD HH:mm:ss” format
            "end_time char(19) not null," + // java.time in “YYYY-MM-DD HH:mm:ss” format
            "fee integer not null," +
            "FOREIGN KEY(driver_id) REFERENCES driver(id)," +
            "FOREIGN KEY(passenger_id) REFERENCES passenger(id)," +
            "FOREIGN KEY(start_location) REFERENCES taxi_stop(name)," +
            "FOREIGN KEY(destination) REFERENCES taxi_stop(name))";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            // taxi_stop table
            stmt = "CREATE TABLE taxi_stop(" + 
            "name varchar(20) primary key," + 
            "location_x integer not null," + 
            "location_y integer not null)";
            pstmt = conn.prepareStatement(stmt);
            pstmt.executeUpdate();

            System.out.println("Done! Tables are created!");
        } catch(Exception e) {
            System.out.println("\nError occured when creating tables: " + e);
        }
    }

    public void deleteTables() {
        System.out.print("Processing...");
        
        try {
            String stmt = "DROP TABLE IF EXISTS ";
            PreparedStatement pstmt;
            
            for(int i = 0; i < tables.length; i++) {
                pstmt = conn.prepareStatement(stmt + tables[i]);
                pstmt.execute();
            }

            System.out.println("Done! Tables are deleted!");
        } catch(Exception e) {
            System.out.println("\nError occured when deleting tables: " + e);
        }
    }

    public void loadData() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the folder path");
        
        try {
            String path = scan.nextLine();
            System.out.print("Processing...");

            loadDrivers(path);
            loadVehicles(path);
            loadPassengers(path);
            // no request is yet created
            loadTrips(path);
            loadTaxiStops(path);

            System.out.println("Data is loaded!");
        } catch(Exception e) {
            System.out.println("\nError occured when loading data: " + e);
        }
    }

    public void checkData() {
        try {
            int counts[] = new int[6];
            String stmt = "SELECT COUNT(*) FROM ";
            PreparedStatement pstmt;

            for(int i = 0; i < tables.length; i++) {
                pstmt = conn.prepareStatement(stmt + tables[i]);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                counts[i] = rs.getInt("count(*)");
            }

            System.out.println("Numbers of records in each table:");
            for(int i = 0; i < tables.length; i++) {
                System.out.println(tables[i] + ": " + counts[i]);
            }
        } catch(Exception e) {
            System.out.println("\nError occured when checking data: " + e);
        }
    }

    private void loadDrivers(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/drivers.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO driver VALUES (?, ?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadVehicles(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/vehicles.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicles VALUES (?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadPassengers(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/passengers.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicles VALUES (?, ?)");
        
        while((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadTrips(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/trips.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO driver VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            String data[] = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadTaxiStops(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/taxi_stops.csv"));
        String line;
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO taxi_stops VALUES (?, ?, ?)");
        
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