/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ridesharing;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
/**
 *
 * @author 
 */
public class Passenger {
    public void requestRide(Connection conn){
        Scanner sc = new Scanner(System.in);
        int user_choice = 1;
        int user_id = 0;
        int passenger_num = 0;
        String start_location = "";
        String end_location = "";
        String car_model = "";
        int driver_years = 0;

        System.out.println("Passenger, what would you like to do?\n1. Request a ride\n2. Check trip records\n3.Go back");
        try{
            System.out.println("Please enter [1-3].");
            user_choice = sc.nextInt();
            if(user_choice != 1 || user_choice != 2 || user_choice != 3){
                throw new Exception("Wrong choice!");
            }
            
            System.out.println("Please enter your ID.");
            user_id = sc.nextInt();
            if(user_choice < 0){
                throw new Exception("Wrong ID!");
            }

            System.out.println("Please enter the number of passengers.");
            passenger_num = sc.nextInt();
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
            driver_years = sc.nextInt();
            if(driver_years < 0){
                throw new Exception("Wrong driver years!");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        ArrayList<String> error_message = new ArrayList<String>();

        // Testing user id

        String psql = "SELECT * FROM passenger p WHERE p.id = ?;";
        PreparedStatement pstmt = conn.prepareStatement(psql);
        pstmt.setInt(1, user_id);
        ResultSet rs = pstmt.executeQuery();

        if(rs == null){
            error_message.add("This user does not exist");
            return;
        }
        
        // Testing locations

        psql = "SELECT * FROM taxi_stop ts WHERE ts.name = ?;";
        pstmt = conn.prepareStatement(psql);
        pstmt.setString(1, start_location);
        rs = pstmt.executeQuery();

        if(rs == null){
            error_message.add("This start destination does not exist");
            return;
        }

        pstmt.setString(1, end_location);
        rs = pstmt.executeQuery();
        if(rs == null){
            error_message.add("This end destination does not exist");
            return;
        }

        psql = "SELECT * FROM driver d,vehicles v,taxi_stop ts," +
                        "WHERE age = ? and gender = ? ;";
        return;
    }
}
