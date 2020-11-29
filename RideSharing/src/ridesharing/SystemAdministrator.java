package ridesharing;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

            System.out.print("Done! Tables are created!");
        } catch(Exception e) {
            System.out.println("\nError occured in admin operation: " + e);
        }
    }

    public void deleteTables() {

    }

    public void loadData() {
        
    }

    public void checkData() {
        
    }
}
