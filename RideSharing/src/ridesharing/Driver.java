/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ridesharing;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Driver {
    private Connection conn;

    public Driver(Connection c) 
    {
        conn = c;
    }

    public void dmenu()
    {
        int choice=0, id=0;
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Driver, what would you like to do?");
        System.out.println("1. Search requests");
        System.out.println("2. Take a request");
        System.out.println("3. Finish a trip");
        System.out.println("4. Go back");

        try {
                do {
                        System.out.println("Please enter [1-4]");
                        choice = keyboard.nextInt();
                
                        if (choice < 1 || choice > 4)
                            System.out.println("[ERROR] Invalid input");
                } while (choice < 1 || choice > 4);

                do {
                        System.out.println("Please enter your ID.");
                        id = keyboard.nextInt();
                
                        if (id <= 0)
                            System.out.println("[ERROR] Invalid input");
                } while (id <= 0);

        } catch (Exception e) {
            System.out.println("[ERROR] Invalid input");
        }
        
        if (choice == 1)
            searchRequest(id);
        else if (choice == 2)
            takeRequest(id);
        else if (choice == 3)
            finishTrip(id);
        
        return;
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
//dun know wtf i am doing ((   
                    stmt = "SELECT r.id, p.name, r.passengers, r.start_location, r.destination FROM request r, driver d, vehicle v, passenger p, taxi_stop t " +
                              "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND r.start_location = t.name AND d.id = ?" +
                              "AND r.taken = 'N' AND d.driving_years >= r.driving_years AND v.model LIKE '%' + r.model '%' " + 
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
        int rid;
        Scanner keyboard = new Scanner(System.in);
        
        try{
                String stmt;
                PreparedStatement pstmt;

                stmt = "SELECT * FROM trip WHERE driver_id = ? AND end_time IS NULL";
                pstmt = conn.prepareStatement(stmt);
                pstmt.setInt(1, did);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next())
                    System.out.println("[ERROR] Please finish the unfinished trip before taking a new one.");
                else
                {
                    System.out.println("Please enter the request ID");
                    rid = keyboard.nextInt();
                    
                    stmt = "SELECT * FROM request r WHERE ? = id AND taken = 'N'";
                    pstmt = conn.prepareStatement(stmt);
                    pstmt.setInt(1, rid);
                    rs = pstmt.executeQuery();
                    
                    if (!rs.next())
                        System.out.println("[ERROR] The request id is incorrect or the request have been taken.");
                    else
                    {
                        stmt = "SELECT COUNT(*) AS r_count FROM  driver d, vehicle v, request r" +
                                   "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND d.id = ? AND r.id = ?" +
                                   "AND r.taken = 'N' AND v.seats >= r.passengers AND v.model LIKE '%' + r.model '%' " + 
                                   "AND d.driving_years >= r.driving_years";
                        pstmt = conn.prepareStatement(stmt);
                        pstmt.setInt(1, did);
                        pstmt.setInt(2, rid);
                        rs = pstmt.executeQuery();
                        rs.next();
                        
                        if(rs.getInt("r_count") == 0)
                            System.out.println("You do not satisfy all the criteria of the request");
                        else
                        {
                            stmt = "SELECT p.id, p.name, r.start_location, r.destination FROM request r, passenger p " +
                                      "WHERE ? = r.id AND  p.id = r.passenger_id AND taken = 'N'";
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
                        }
                    }
                }
        } catch(Exception e) {
            System.out.println("Error" + e);
        }
    }
    
    public void finishTrip(int uid)
    {
    
    }
}
