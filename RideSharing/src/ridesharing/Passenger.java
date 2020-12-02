package ridesharing;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Passenger {
    boolean passenger_output = false;

    public void requestRide(Scanner sc, Connection conn, int user_id){
        int passenger_num = 0;
        String start_location = "";
        String end_location = "";
        String car_model = "_";
        int driver_years = 0;

        try{          
            System.out.println("Please enter the number of passengers.");
            passenger_num = sc.nextInt();
            sc.nextLine();
            if(passenger_num < 1 || passenger_num > 8){
                throw new Exception("Wrong number of passengers!");
            }

            System.out.println("Please enter the start location.");
            start_location = sc.nextLine();
            if(start_location == "" || start_location == null){
                throw new Exception("Wrong start location!");
            }

            System.out.println("Please enter the end location.");
            end_location = sc.nextLine();
            if(end_location == "" || end_location == null){
                throw new Exception("Wrong end location!");
            }

            System.out.println("Please enter the model. (Press enter to skip)");
            car_model = sc.nextLine().toLowerCase();

            System.out.println("Please enter the minimum driving years of the driver. (Press enter to skip)");
            String pre_driver_years = sc.nextLine();
            if(pre_driver_years != null && pre_driver_years != " " && !pre_driver_years.isEmpty()){
                driver_years = Integer.parseInt(pre_driver_years);
                if(driver_years < 0){
                    throw new Exception("Wrong driver years!");
                }
            }
            System.out.println("All input received. Querying Database.");

        }catch(NumberFormatException nfe){
            System.out.println("Please enter a number instead of a string.");
            return;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return;
        }
        ArrayList<String> error_message = new ArrayList<String>();

        // Testing user id

        String psql = "SELECT * FROM passenger p WHERE p.id = ?;";
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try{
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, user_id);
            rs = pstmt.executeQuery();

            if(!rs.next()){
                error_message.add("This user does not exist");
            }
        
        // Testing locations

            psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
            pstmt = conn.prepareStatement(psql);
            pstmt.setString(1, start_location);
            rs = pstmt.executeQuery();

            if(!rs.next()){
                error_message.add("This start destination does not exist");
            }

            pstmt.setString(1, end_location);
            rs = pstmt.executeQuery();

            if(!rs.next()){
                error_message.add("This end destination does not exist");
            }

        // Checking car models

            psql = "SELECT * FROM vehicle WHERE seats >= ? AND UPPER(model) LIKE UPPER(?);";
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, passenger_num);
            pstmt.setString(2, "%" + car_model + "%");
            rs = pstmt.executeQuery();

            if(!rs.next()){
                error_message.add("Cannot find a matching car model.");
            }

            psql = "SELECT * FROM driver WHERE driving_years >= ?;";
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, driver_years);
            rs = pstmt.executeQuery();

        // Checking driver criteria

            if(!rs.next()){
                error_message.add("Cannot find a driver with sufficient driving years.");
            }

            if(error_message.size()>0){
                for(String e: error_message){
                    System.out.println(e);
                }
                return;
            }

        // At last, create the request if no errors exist

            psql = "SELECT COUNT(*) AS d_count FROM driver d, vehicle v WHERE v.seats >= ? AND driving_years >= ? AND UPPER(model) LIKE UPPER(?) AND d.vehicle_id = v.id;";
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, passenger_num);
            pstmt.setInt(2, driver_years);
            pstmt.setString(3, "%" + car_model + "%");
            rs = pstmt.executeQuery();

            if(!rs.next()){
                System.out.println("There are no drivers that can take the request.");
                return;
            }else{
                psql = "INSERT INTO request (passenger_id, start_location,destination,model,passengers,taken,driving_years) VALUES (?,?,?,?,?,0,?);";
                pstmt = conn.prepareStatement(psql);
                pstmt.setInt(1, user_id);
                pstmt.setString(2, start_location);
                pstmt.setString(3, end_location);
                pstmt.setString(4, car_model);
                pstmt.setInt(5, passenger_num);
                pstmt.setInt(6, driver_years);
                if(pstmt.executeUpdate() > 0){
                    System.out.println("Your request is placed. " + rs.getInt(1) + " drivers are able to take the request.");
                }
            }
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        passenger_output = true;
        return;
    }

    public void checkTrip(Scanner sc, Connection conn, int user_id){
        String start_date = "";
        String end_date = "";
        String end_location = "";

        Pattern date = Pattern.compile("[1-2][0-9][0-9][0-9]-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");

        try{ 
            sc.nextLine();         
            System.out.println("Please enter the start date. (YYYY-MM-DD)");
            start_date = sc.nextLine();
            if(start_date == "" || start_date == null || !date.matcher(start_date).matches()){
                throw new Exception("Wrong start date!");
            }

            System.out.println("Please enter the end date. (YYYY-MM-DD)");
            end_date = sc.nextLine();
            if(end_date == "" || end_date == null || !date.matcher(start_date).matches()){
                throw new Exception("Wrong end date!");
            }

            System.out.println("Please enter the destination.");
            end_location = sc.nextLine();
            if(end_location == "" || end_location == null){
                throw new Exception("Wrong end location!");
            }

            System.out.println("All input received. Querying Database.");

        }catch(NumberFormatException nfe){
            System.out.println("Error: Please enter a number.");
            return;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return;
        }

        String psql = "SELECT t.id,d.name,v.id,v.model,t.* FROM trip t,driver d,vehicle v WHERE t.passenger_id = ? AND t.start_time >= ? AND t.end_time <= ? AND t.destination = ? AND t.driver_id = d.id AND d.vehicle_id = v.id;";
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try{
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, user_id);
            pstmt.setString(2, start_date);
            pstmt.setString(3, end_date);
            pstmt.setString(4, end_location);
            rs = pstmt.executeQuery();

            if(!rs.next()){
                System.out.println("There are no trips matching your criteria");
                rs.beforeFirst();
            }else{
                System.out.println("Trip_ID, Driver Name, Vehicle ID, Vehicle Model, Start, End, Fee, Start Location, Destination");
                do{
                    System.out.println(String.format("%d, %s, %s, %s, %s, %s, %d, %s, %s", 
                                                rs.getInt("t.id"), rs.getString("d.name"), rs.getString("v.id"),rs.getString("v.model"),
                                                rs.getString("t.start_time"),rs.getString("t.end_time"),rs.getInt("t.fee"),
                                                rs.getString("t.start_location"),rs.getString("t.destination")));
                }while(rs.next());
            }

        }catch(Exception e){
            System.out.println("Something was wrong with the SQL query");
            e.printStackTrace();
        }
        passenger_output = true;
    }
    public boolean getPassenger_output() {
        return passenger_output;
    }
}
