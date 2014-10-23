import java.sql.*;
import javax.sql.*;

public class DBConnect {
	
	
	public static void connect() {
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
		}
		
		System.out.println("PostgreSQL driver registered");
	}
}
