import java.sql.*;

import javax.sql.*;

public class DBConnect {

	private static String DBNAME = "jdbc:postgresql://dbteach2.cs.bham.ac.uk/cxm373";
	private static String USERNAME = "cxm373";
	private static String PASSWORD = "computer2014";

	public static void main(String[] args) {
		Connection dbConn = connect();
		dropTables(dbConn);
		createTables(dbConn);
		//Insert synthetic data
		try {
			dbConn.close();
		} catch (SQLException e) {
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
		
		Connection dbConn = null;
		
		try {
			 dbConn = DriverManager.getConnection(DBNAME, USERNAME, PASSWORD);
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
			drop = dbConn.prepareStatement("DROP TABLE IF EXISTS Student CASCADE;" + 
					"DROP TABLE IF EXISTS Lecturer CASCADE;" + 
					"DROP TABLE IF EXISTS Module CASCADE;" + 
					"DROP TABLE IF EXISTS Marks CASCADE;" + 
					"DROP TABLE IF EXISTS StudentContact CASCADE;" + 
					"DROP TABLE IF EXISTS NextOfKinContact CASCADE;" + 
					"DROP TABLE IF EXISTS Titles CASCADE;" + 
					"DROP TABLE IF EXISTS Type CASCADE");
			drop.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Existing databases deleted.");
		
		
	}
	
	public static void createTables(Connection dbConn) {
		
		PreparedStatement createTitles;
		PreparedStatement createStudent;
		try {
			createTitles = dbConn.prepareStatement(
					"Create Table Titles (" + 
					"" + 
					"titleID serial," + 
					"titleString varchar(20) NOT NULL," + 
					"" + 
					"PRIMARY KEY (titleID)" + 
					");");
			createStudent = dbConn.prepareStatement(
					"Create Table Student (" + 
					"" + 
					"studentID serial, " + 
					"titleID serial," + 
					"forename varchar(35) NOT NULL," + 
					"familyName varchar(35) NOT NULL," + 
					"dateOfBirth date NOT NULL CHECK(dateOfBirth < current_date)," + 
					"" + 
					"PRIMARY KEY (studentID)," + 
					"FOREIGN KEY (titleID) REFERENCES Titles(titleID)" + 
					");");
			createTitles.execute();
			createStudent.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("New tables added.");
		
	}
}

