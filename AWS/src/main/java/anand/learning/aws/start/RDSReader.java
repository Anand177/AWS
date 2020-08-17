/**
 * 
 */
package anand.learning.aws.start;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author anand
 *
 */
public class RDSReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		publicRDSDb();

	}
	
	
	public static void publicRDSDb() {
		
		try {
			Connection conn = DriverManager.getConnection(
	                "jdbc:mysql://mydb.ci7tmv98eevs.us-east-2.rds.amazonaws.com:3306",
	                "admin", "password");
            if (conn != null)
                System.out.println("Connected to the database!");
            else
                System.err.println("Failed to make connection!");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("show databases");
            
            while(rs.next()) {
            	System.out.println(rs.getString(1));
            }
            
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        }
	}

}
