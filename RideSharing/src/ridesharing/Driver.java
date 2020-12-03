package ridesharing;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Driver {
    private Connection conn;

    public Driver(Connection c) {
        conn = c;
    }

    public void searchRequest(int did) 
    {
        int x=0, y=0, distance=0;
        Scanner keyboard = new Scanner(System.in);
		
        try {
                System.out.println("Please enter the coordinates of your location.");
                x = keyboard.nextInt();
                y = keyboard.nextInt();
			
                do {
                        System.out.println("Please enter the maximum distance from you to the passenger.");
                        distance = keyboard.nextInt();
                        
                        if (distance <= 0)
                            System.out.println("[ERROR] Distance should be greater than 0");
                } while (distance <= 0);
        } catch (Exception e) {
            System.out.println("Error:" + e);
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
                else
                {

                    stmt = "SELECT r.id, p.name, r.passengers, r.start_location, r.destination FROM request r, driver d, vehicle v, passenger p, taxi_stop t " +
                              "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND r.start_location = t.name AND d.id = ?" +
                              "AND r.taken = 'N' AND d.driving_years >= r.driving_years AND v.model LIKE CONCAT('%', r.model, '%') " + 
                              "AND v.seats >= r.passengers AND (? >= (ABS((t.location_x-?)) + ABS((t.location_y-?)))";
                    pstmt = conn.prepareStatement(stmt);
                    pstmt.setInt(1, did);
                    pstmt.setInt(2, x);
                    pstmt.setInt(3, y);
                    pstmt.setInt(4, distance);
                    rs = pstmt.executeQuery();
                
                    if (!rs.next())
                        System.out.println("There are no requests available at this moment.");
                    else
                        System.out.println("request ID, passenger name, num of passengers, start location, destination");
                    
                    do 
                    {
                        System.out.println(rs.getInt("r.id") + ", " + rs.getString("p.name") + ", " + rs.getInt("r.passengers") + ", " + rs.getString("r.start_location") + ", " + rs.getString("r.destination"));
                    } while (rs.next());
                }
        }catch (Exception exp){
                System.out.println("Error: " + exp); 
        }
    }
    
    public void takeRequest(int did)
    {
        int rid = 0;
        Scanner keyboard = new Scanner(System.in);
        boolean user_choice_passed = true;
        String stmt;
        PreparedStatement pstmt;
        
        try{

                stmt = "SELECT * FROM trip WHERE driver_id = ? AND end_time IS NULL";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, did);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()){
                    System.out.println("[ERROR] Please finish the unfinished trip before taking a new one.");
                    return;
                }

                do{
                    System.out.println("Please enter the request ID");
                    try{
                        rid = keyboard.nextInt();
                    }catch(Exception nfe){
                        System.out.println("[ERROR] The request id should be a number.");
                        user_choice_passed = false;
                        continue;
                    }

                    stmt = "SELECT * FROM request r WHERE id = ? AND taken = 'N'";
                    pstmt = conn.prepareStatement(stmt);
                    pstmt.setInt(1, rid);
                    rs = pstmt.executeQuery();

                    if(!rs.next() || rid < 0){
                        System.out.println("[ERROR] The request id is incorrect or the request have been taken.");
                        user_choice_passed = false;
                    }

                }while(!user_choice_passed);
    
                stmt = "SELECT * FROM driver d, vehicle v, request r" +
                            "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND d.id = ? AND r.id = ?" +
                            "AND r.taken = 'N' AND v.seats >= r.passengers AND v.model LIKE '%' + r.model + '%' " + 
                            "AND d.driving_years >= r.driving_years;";

                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, did);
                pstmt.setInt(2, rid);
                rs = pstmt.executeQuery();
                
                
                if(!rs.next()){
                    System.out.println("You do not satisfy all the criteria of the request");
                    return;
                }else{
                    stmt = "SELECT p.id, p.name, r.start_location, r.destination FROM request r, passenger p " +
                                "WHERE r.id = ? AND p.id = r.passenger_id AND taken = 'N'";
                    pstmt = conn.prepareStatement(stmt);
                    pstmt.setInt(1, rid);
                    rs = pstmt.executeQuery();

                    rs.next();
                    int pid = rs.getInt("p.id");
                    String name = rs.getString("p.name");
                    String start = rs.getString("r.start_location");
                    String dest = rs.getString("r.destination");
            
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date now = new java.util.Date();
                    stmt = "INSERT INTO trip (driver_id, passenger_id, start_location, destination, start_time, fee) " +
                            "VALUES (?, ?, ?, ?, ?, 0)";
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
                    
                    stmt = "SELECT t.id, p.name, t.start_time FROM trip t, passenger p" +
                            "WHERE t.passenger_id = p.id AND t.start_time = ?";
                    pstmt = conn.prepareStatement(stmt);
                    pstmt.setString(1, sdf.format(now));
                    rs = pstmt.executeQuery();
                    rs.next();
                    
                    System.out.println("Trip ID, Passenger name, Start");
                    System.out.println(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getString(3));
                }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void finishTrip(int did)
    {
        String choice = "";
        Scanner keyboard = new Scanner(System.in);
        
        try {
        String stmt;
                PreparedStatement pstmt;

                stmt = "SELECT id, passenger_id, start_time FROM trip WHERE driver_id = ? AND end_time IS NULL";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, did);
                ResultSet rs = pstmt.executeQuery();
                int tid = rs.getInt(1);
                java.util.Date start= rs.getTimestamp(3);
                
                if(!rs.next())
                    System.out.println("[ERROR] You don't have unfinished trip.");
                else
                {
                    System.out.println("Trip ID, Passenger ID, Start");
                    System.out.println(rs.getInt(1) + ", " + rs.getInt(2) + ", " + start);
                    do {
                            System.out.println("Do you wish to finish the trip? [y/n]");
                            choice = keyboard.nextLine();
                    
                            if (choice.equals("y") || choice.equals("n"))
                            {
                                if(choice.equals("n"))
                                    return;
                                else
                                {
                                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                   java.util.Date now = new java.util.Date();
                                   String end_s = sdf.format(now);
                                   java.util.Date end = sdf.parse(end_s);
                            
                                   int duration = (int) Math.floor((double)(end.getTime() - start.getTime()) / 1000 / 60);
                            
                                   stmt = "UPDATE trip SET end_time = ?, fee = ? WHERE id = ?";
                                   pstmt = conn.prepareStatement(stmt);
                                   pstmt.setString(1, end_s);
                                   pstmt.setInt(2, duration);
                                   pstmt.execute();
                            
                                   stmt = "SELECT t.id, p.name, t.start_time, t.end_time, t.fee FROM trip t, passenger p" +
                                              "WHERE t.passenger_id = p.id AND t.id = ?";
                                   pstmt = conn.prepareStatement(stmt);
                                   pstmt.setInt(1, tid);
                                   rs = pstmt.executeQuery();
                                   rs.next();
                            
                                   System.out.println("Trip ID, Passenger name, Start, End, Fee");
                                   System.out.println(rs.getInt(1) + ", " + rs.getString(2) + ", " + rs.getString(sdf.format(3)) + ", " + rs.getString(sdf.format(4)) + ", " + rs.getInt(5));
                                }
                            }
                            else
                                System.out.println("[ERROR] Please enter y/n");
                    } while (choice.equals("y") || choice.equals("n"));
                }
        } catch (Exception e){
            System.out.println("Error" + e);
        }
    }
}
