package ridesharing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Manager {
    boolean manager_output = false;
    private Connection conn;

    public Manager(Connection c){
        conn = c;
        // Scanner sc = new Scanner(System.in);
        // int user_choice = 1;
        // System.out.println("Please enter [1-2].");

        // try{
        //     user_choice = sc.nextInt();
        //     if(user_choice != 1 && user_choice != 2){
        //         throw new Exception("[ERROR] Invalid input.");
        //     }
        // }catch(Exception e){
        //    System.out.println(e.getMessage());
        // }finally{
        //     sc.close();
        // }

        // switch(user_choice){
        //     case 1:
        //         listTrips(conn);
        //         break;
        //     case 2:
        //         break;
        //     default:
        //         return;
        // }
    }

    public void listTrips(){
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
        }

        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String psql = "IF NOT EXISTS distancetable CREATE VIEW distancetable AS SELECT ts1.name AS start, ts2.name AS end, SQRT(POWER(ts1.location_x - ts2.location_x,2)+POWER(ts1.location_y - ts2.location_y,2)) AS distance FROM taxi_stop ts1, taxi_stop ts2 WHERE ts1.name != ts2.name;";
        int create_view  = 0;
        try{
            Statement stmt = conn.createStatement();
            create_view = stmt.executeUpdate(psql);
        }catch(SQLException sqle){
            System.out.println("Unable to create view.");
        }

        try{
           

            psql = "SELECT t.id,d.name,p.name,t.start_location,t.destination,t.start_time,t.end_time FROM trip t LEFT JOIN driver d ON t.driver_id = d.id LEFT JOIN passenger p ON t.passenger_id = p.id LEFT JOIN distancetable dt ON t.start_location = dt.start AND t.destination = dt.end WHERE dt.distance >= ? AND dt.distance <= ?;";
            
            pstmt = conn.prepareStatement(psql);
            pstmt.setInt(1, min_distance);
            pstmt.setInt(2, max_distance);
            rs = pstmt.executeQuery();

            if(!rs.next()){
                System.out.println("There are no trips matching your criteria");
            }else{
                System.out.println("Trip ID, Driver Name, Passenger Name, Start Location, Destination, Duration");
                do{
                    int duration = getDuration(rs.getString("t.start_time"),rs.getString("t.end_time"));
                    System.out.println(String.format("%d, %s, %s, %s, %s, %s", 
                                                rs.getInt("t.id"), rs.getString("d.name"), rs.getString("p.name"),
                                                rs.getString("t.start_location"),rs.getString("t.destination"),
                                                Integer.toString(duration)));
                }while(rs.next());
            }
        }catch(Exception e){
            System.out.println("Something was wrong with the SQL query");
            e.printStackTrace();
        }
            if(create_view == 1){
                psql = "IF EXISTS distancetable DROP VIEW distancetable;";
                try{
                    pstmt = conn.prepareStatement(psql);
                    pstmt.executeUpdate();
                }catch(Exception e){
                    System.out.println("Cannot drop view.");
                }
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
        return (int)(diff / (60 * 1000)) % 60; 
    }
}
