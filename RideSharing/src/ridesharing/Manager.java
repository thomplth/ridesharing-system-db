/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ridesharing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
/**
 *
 * @author 
 */
public class Manager {
    boolean manager_output = false;
    public Manager(Connection conn){
        Scanner sc = new Scanner(System.in);
        int user_choice = 1;
        System.out.println("Please enter [1-2].");

        try{
            user_choice = sc.nextInt();
            if(user_choice != 1 && user_choice != 2){
                throw new Exception("Wrong choice!");
            }
        }catch(Exception e){
           System.out.println(e.getMessage());
        }finally{
            sc.close();
        }

        switch(user_choice){
            case 1:
                listTrips(conn);
                break;
            case 2:
                break;
            default:
                return;
        }
    }

    public void listTrips(Connection conn){
        Scanner sc = new Scanner(System.in);
        int min_distance = 0;
        int max_distance = 0;

        try{          
            System.out.println("Please enter the minimum travelling distance.");
            min_distance = sc.nextInt();
            if(min_distance < 0){
                throw new Exception("Wrong minimum distance!");
            }

            System.out.println("Please enter the maximum travelling distance.");
            max_distance = sc.nextInt();
            if(max_distance < 0 || max_distance < min_distance){
                throw new Exception("Wrong maximum distance!");
            }

            System.out.println("All input received. Querying Database.");

        }catch(NumberFormatException nfe){
            System.out.println("Error: Please enter numbers.");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally{
            sc.close();
        }
        String psql = "CREATE VIEW AS distance_table SELECT ts1.name AS start, ts2.name AS end, SQRT(SQUARE(ts1.location_x - ts2.location_y) + SQUARE(ts1.location_y - ts2.location_y)) AS distance FROM taxi_stop AS ts1, taxi_stop as ts2 WHERE ts1.name != ts2.name;" +
                        "SELECT t.id,d.name,p.name,t.start_location,t.destination,t.start_time,t.end_time FROM trip t LEFT JOIN driver d ON t.driver_id = d.id LEFT JOIN passenger p ON t.passenger_id = p.id LEFT JOIN distance_table dt ON WHERE t.start_location = dt.start AND t.destination = dt.end AND dt.distance >= ? AND dt.distance <= ?;" +
                        "DROP VIEW distance_table;" ;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try{
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, min_distance);
            pstmt.setInt(2, max_distance);
            rs = pstmt.executeQuery();

            if(!rs.next()){
                System.out.println("There are no trips matching your criteria");
            }else{
                System.out.println("Trip ID, Driver Name, Passenger Name, Start Location, Destination, Duration");
                do{
                    System.out.println(String.format("%d, %s, %s, %s, %s, %s, %d", 
                                                rs.getInt("t.id"), rs.getString("d.name"), rs.getInt("p.name"),
                                                rs.getString("t.start_location"),rs.getString("t.destionation"),
                                                getDuration(rs.getString("t.start_time"),rs.getString("t.end_time"))));
                }while(rs.next());
            }
        }catch(Exception e){
            System.out.println("Something was wrong with the SQL query");
        }
        manager_output = true;
    }
    public int getDuration(String start, String end){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try{
            d1 = format.parse(start);
            d2 = format.parse(end);
        }catch(ParseException e){
            System.out.println("Failed to compute duration.");
        }
        long diff = d2.getTime() - d1.getTime();
        return (int)diff / (60 * 1000) % 60; 
    }
    public boolean get_managerOutput(){
        return manager_output;
    }
}
