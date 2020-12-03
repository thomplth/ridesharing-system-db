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
        boolean user_choice_passed = true;
        String psql = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try{          
            do{
				System.out.println("Please enter the number of passengers.");
                passenger_num = sc.nextInt();
                sc.nextLine();
                if(passenger_num < 1 || passenger_num > 8){
					System.out.println("[ERROR] Invalid number of passengers.");
					user_choice_passed = false;
                }
            }while(user_choice_passed);

            user_choice_passed = true;

            do{
                System.out.println("Please enter the start location.");
                start_location = sc.nextLine();

                psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
                pstmt = conn.prepareStatement(psql);
                pstmt.setString(1, start_location);
                rs = pstmt.executeQuery();

                if(!rs.next() || start_location == "" || start_location == null){
					System.out.println("[ERROR] Start location not found.");
					user_choice_passed = false;
                }
            }while(user_choice_passed);

            user_choice_passed = true;

            do{
                System.out.println("Please enter the destination.");
                end_location = sc.nextLine();

                if(end_location == start_location){
                    System.out.println("[ERROR] Start location and destionation should be different.");
					user_choice_passed = false;
                }
    
                psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
                pstmt.setString(1, end_location);
                rs = pstmt.executeQuery();

                if(!rs.next() || end_location == "" || end_location == null){
					System.out.println("[ERROR] Destination not found.");
					user_choice_passed = false;
                }
            }while(user_choice_passed);

            // Choose model
            System.out.println("Please enter the model. (Press enter to skip)");
            car_model = sc.nextLine().toLowerCase();

            /*

            psql = "SELECT * FROM vehicle WHERE seats >= ? AND UPPER(model) LIKE UPPER(?);";
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, passenger_num);
            pstmt.setString(2, "%" + car_model + "%");
            rs = pstmt.executeQuery();

            if(!rs.next()){
                System.out.println("[ERROR] Invalid model.");
            }

            */

            // Enter minimum driving year

            System.out.println("Please enter the minimum driving years of the driver. (Press enter to skip)");
            String pre_driver_years = sc.nextLine();
            if(pre_driver_years != null && pre_driver_years != " " && !pre_driver_years.isEmpty()){
                driver_years = Integer.parseInt(pre_driver_years);
                if(driver_years < 0){
                    throw new Exception("Wrong driver years!");
                }
            }
            System.out.println("All input received. Querying Database.");

        }catch(Exception e){
            e.printStackTrace();
            return;
        }

        // At last, create the request if no errors exist
        try{

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
                psql = "INSERT INTO request (passenger_id, start_location,destination,model,passengers,taken,driving_years) VALUES (?,?,?,?,?,'N',?);";
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
        boolean user_choice_passed = true;
        Pattern date = Pattern.compile("[1-2][0-9][0-9][0-9]-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");
        String psql = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try{ 
            sc.nextLine();         
            do{
                System.out.println("Please enter the start date. (YYYY-MM-DD)");
                start_date = sc.nextLine();

                if(start_date == "" || start_date == null || !date.matcher(start_date).matches()){
                    user_choice_passed = false;
                    System.out.println("[ERROR] Invalid start date.");
                }
            }while(user_choice_passed);

            user_choice_passed = true;


            do{
                System.out.println("Please enter the end date. (YYYY-MM-DD)");
                end_date = sc.nextLine();

                if(end_date == "" || end_date == null || !date.matcher(start_date).matches()){
                    user_choice_passed = false;
                    System.out.println("[ERROR] Invalid end date.");
                }
            }while(user_choice_passed);

            user_choice_passed = true;


            do{
                System.out.println("Please enter the destination.");
                end_location = sc.nextLine();
                psql = "SELECT * FROM taxi_stop ts WHERE UPPER(ts.name) = UPPER(?);";
                pstmt.setString(1, end_location);
                rs = pstmt.executeQuery();

                if(!rs.next() || end_location == "" || end_location == null){
                    user_choice_passed = false;
                    System.out.println("[ERROR] Destination not found.");
                }
            }while(user_choice_passed);

            System.out.println("All input received. Querying Database.");
        }catch(Exception e){
            e.printStackTrace();
            return;
        }

        psql = "SELECT t.id,d.name,v.id,v.model,t.* FROM trip t,driver d,vehicle v WHERE t.passenger_id = ? AND t.start_time >= ? AND t.end_time <= ? AND t.destination = ? AND t.driver_id = d.id AND d.vehicle_id = v.id;";
        rs = null;
        pstmt = null;
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
