/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ridesharing;

import java.sql.*;
import java.util.*;

public class Driver {
	private Connection conn;

	public Driver(Connection c) {
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
			System.out.println("[ERROR] Invalid input")
		}
		if (choice == 1)
			searchRequest(id);
		else if (choice == 2)
			takeRequest(id);
		else if (choice == 3)
			finishTrip(id);
		else
			return;
	}

	public void searchRequest(int uid) 
	{
		int x, y, distance=0;
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

		try {
			String stmt;
			PreparedStatement pstmt;

			stmt = "SELECT * FROM driver WHERE id = ?";
			pstmt = conn.prepareStatement(stmt);
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs == null)
				System.out.println("The user does not exist");
			else
			{
//dun know wtf i am doing ((   
                stmt = "SELECT r.id, p.name, r.passengers, r.start_location, r.destination FROM request r, driver d, vehicle v, passenger p, taxi_stop t " +
                       "WHERE d.vehicle_id = v.id AND r.passenger_id = p.id AND r.start_location = t.name AND d.id = ?" +
				       "AND r.taken = 0 AND d.driving_years >= r.driving_years AND v.model LIKE '%' + r.model '%' " + 
                       "AND v.seats >= r.passengers AND (? >= (ABS((t.location_x-?)) + ABS((t.location_y-?)))";
                pstmt = conn.prepareStatement(stmt);
			    pstmt.setInt(1, id);
                pstmt.setInt(2, x);
                pstmt.setInt(3, y);
                pstmt.setInt(4, distance);
			    rs = pstmt.executeQuery();
                
                if (!rs.next())
                    System.out.println("There are no requests available at this moment.");
                else
                    System.out.println("request ID, passenger name, num of passengers, start location, destination");
                while (rs.next())
                {
                    System.out.println(rs.getInt("r.id")　+＂,　＂＋ rs.getString("p.name")　+＂,　＂＋　rs.getInt("r.passengers")　+＂,　＂＋ rs.getString("r.start_location")　+＂,　＂＋　rs.getString("r.destination"));
                }
            }
		}catch (Exception e)
            System.out.println("Error: " + e); 
    }
}
