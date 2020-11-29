package ridesharing;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
            
            pstmt = conn.prepareStatement(stmt + "driver");
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + "vehicle");
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + "passenger");
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + "request");
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + "trip");
            pstmt.execute();
            pstmt = conn.prepareStatement(stmt + "taxi_stop");
            pstmt.execute();

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
            loadTrips(path);
            loadTaxiStops(path);

            System.out.println("Data is loaded!");
        } catch(Exception e) {
            System.out.println("\nError occured when loading data: " + e);
        }
    }

    public void checkData() {
        
    }

    private void loadDrivers(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/drivers.csv"));
        String line, data[];
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO driver VALUES (?, ?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            data = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadVehicles(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/vehicles.csv"));
        String line, data[];
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicles VALUES (?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            data = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadPassengers(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/passengers.csv"));
        String line, data[];
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO vehicles VALUES (?, ?)");
        
        while((line = csv.readLine()) != null) {
            data = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadTrips(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/trips.csv"));
        String line, data[];
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO driver VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            data = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }

    private void loadTaxiStops(String path) {
        BufferedReader csv = new BufferedReader(new FileReader(path + "/taxi_stops.csv"));
        String line, data[];
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO taxi_stops VALUES (?, ?, ?)");
        
        while((line = csv.readLine()) != null) {
            data = line.split(",");
            for(int i = 1; i <= data.length; i++) {
                pstmt.setString(i, data[i-1]);
            }
            pstmt.execute();
        }
        csv.close();
    }
}

/*
BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
while ((row = csvReader.readLine()) != null) {
    String[] data = row.split(",");
    // do something with the data
}
csvReader.close();

public static void main(String[] args)   
{  
String line = "";  
String splitBy = ",";  
try   
{  
BufferedReader br = new BufferedReader(new FileReader("CSVDemo.csv"));  
while ((line = br.readLine()) != null)   //returns a Boolean value  
{  
String[] employee = line.split(splitBy);    // use comma as separator  
System.out.println("Employee [First Name=" + employee[0] + ", Last Name=" + employee[1] + ", Designation=" + employee[2] + ", Contact=" + employee[3] + ", Salary= " + employee[4] + ", City= " + employee[5] +"]");  
}  
}   
catch (IOException e)   
{  
e.printStackTrace();  
}  
*/