import java.sql.*;

import javax.sql.*;

public class DBConnect {

	public static void main(String[] args) {
		Connection dbConn = connect();
		dropTables(dbConn);
		createTables(dbConn);
		//Insert synthetic data
		try {
			dbConn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Database connection failed to close.");
		}
		System.out.println("Database connection closed.");
	}
	
	public static Connection connect() {
		
		System.setProperty("jdbc.drivers", "org.postgresql.Driver");
		
		try {
			Class.forName(System.getProperty("jdbc.drivers"));
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
		}
		
		System.out.println("PostgreSQL driver registered.");
		
		String dbName = "jdbc:postgresql://dbteach2.cs.bham.ac.uk/cxm373";
		
		Connection dbConn = null;
		
		try {
			 dbConn = DriverManager.getConnection(dbName, "cxm373", "computer2014");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (dbConn != null) {
			System.out.println("Connected to database successfully.");
		} else {
			System.out.println("Database connection failed.");
		}
		
		return dbConn;

	}
	
	public static void dropTables(Connection dbConn) {
		
		PreparedStatement drop;
		try {
			drop = dbConn.prepareStatement("DROP TABLE IF EXISTS practice_table CASCADE");
			drop.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Existing databases deleted.");
		
		
	}
	
	public static void createTables(Connection dbConn) {
		
		PreparedStatement create;
		try {
			create = dbConn.prepareStatement("CREATE TABLE practice_table( id serial, name varchar(10), PRIMARY KEY(id) );");
			create.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("New table added.");
		
	}
}

