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
		populateDataBase(dbConn);
		
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
		
		System.out.println("Tables being added...");
		
		PreparedStatement createTitles;
		PreparedStatement createStudent;
		PreparedStatement createLecturer;
		PreparedStatement createModule;
		PreparedStatement createType;
		PreparedStatement createMarks;
		PreparedStatement createStudentContact;
		PreparedStatement createNextOfKinContact;
		
		try {
			createTitles = dbConn.prepareStatement(
					"Create Table Titles (" + 
					"" + 
					"titleID serial NOT NULL," + 
					"titleString varchar(20) NOT NULL," + 
					"" + 
					"PRIMARY KEY (titleID)" + 
					");");
			
			createStudent = dbConn.prepareStatement(
					"Create Table Student (" + 
					"" + 
					"studentID serial NOT NULL, " + 
					"titleID serial NOT NULL," + 
					"forename varchar(35) NOT NULL," + 
					"familyName varchar(35) NOT NULL," + 
					"dateOfBirth date NOT NULL CHECK(dateOfBirth < current_date)," + 
					"" + 
					"PRIMARY KEY (studentID)," + 
					"FOREIGN KEY (titleID) REFERENCES Titles(titleID)" + 
					");");
			
			createLecturer = dbConn.prepareStatement(
					"Create Table Lecturer (" + 
					"" + 
					"lecturerID serial NOT NULL," + 
					"titleID serial NOT NULL," + 
					"forename varchar(35) NOT NULL," + 
					"familyName varchar(35) NOT NULL," + 
					"" + 
					"PRIMARY KEY (lecturerID)," + 
					"FOREIGN KEY (titleID) REFERENCES Titles(titleID)" + 
					");");
			
			createModule = dbConn.prepareStatement(
					"Create Table Module (" + 
					"" + 
					"moduleID serial NOT NULL," + 
					"moduleName varchar(50) NOT NULL," + 
					"moduleDescription text NOT NULL," + 
					"lecturerID serial NOT NULL," + 
					"" + 
					"PRIMARY KEY (moduleID)," + 
					"FOREIGN KEY (lecturerID) REFERENCES Lecturer(lecturerID)" + 
					");");
			
			createType = dbConn.prepareStatement(
					"Create Table Type (" + 
					"" + 
					"typeID serial NOT NULL," + 
					"typeString varchar(20) NOT NULL," + 
					"" + 
					"PRIMARY KEY (typeID)" + 
					");");
			
			createMarks = dbConn.prepareStatement(
					"Create Table Marks (" + 
					"" + 
					"studentID serial NOT NULL," + 
					"moduleID serial NOT NULL," + 
					"year smallint NOT NULL," + 
					"typeID serial NOT NULL," + 
					"mark smallint CHECK(mark <= 100)," + 
					"notes text," + 
					"" + 
					"FOREIGN KEY (studentID) REFERENCES Student(studentID)," + 
					"FOREIGN KEY (moduleID) REFERENCES Module(moduleID)," + 
					"FOREIGN KEY (typeID) REFERENCES Type(typeID)" + 
					");");
			
			createStudentContact = dbConn.prepareStatement(
					"Create Table StudentContact (" + 
					"" + 
					"studentID serial NOT NULL," + 
					"eMailAddress varchar(254) NOT NULL," + 
					"postalAddress varchar(8) NOT NULL," + 
					"" + 
					"FOREIGN KEY (studentID) REFERENCES Student(studentID)" + 
					");");
			
			createNextOfKinContact = dbConn.prepareStatement(
					"Create Table NextOfKinContact (" + 
					"" + 
					"studentID serial NOT NULL," + 
					"eMailAddress varchar(254) NOT NULL," + 
					"postalAddress varchar(8) NOT NULL," + 
					"" + 
					"FOREIGN KEY (studentID) REFERENCES Student(studentID)" + 
					");");
			
			createTitles.execute();
			createStudent.execute();
			createLecturer.execute();
			createModule.execute();
			createType.execute();
			createMarks.execute();
			createStudentContact.execute();
			createNextOfKinContact.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("New tables added.");
		
	}

	public static void populateDataBase(Connection dbConn) {
		
		//Insert Titles
		
		String[] titles = {"Mr", "Mrs", "Ms", "Miss", "Master", "Rev", "Fr", "Dr"};
		StringBuilder titleSQL = new StringBuilder("INSERT INTO Titles (titleString) VALUES ");
		for(String s : titles) {
			titleSQL.append("('" + s + "'), ");
		}
		titleSQL.append("('Prof');");
		PreparedStatement addTitles;
		try{
			addTitles = dbConn.prepareStatement(titleSQL.toString());
			addTitles.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
//		//Insert Students
//		
//		StringBuilder studentsSQL = new StringBuilder("INSERT INTO Student (titleID, forename, familyName, dateOfBirth) VALUES ");
//		
//		for(int i = 0; i < 150; i++) {
//			
//			studentsSQL.append("('" + RandomName.getForename() + "', " + RandomName.getSurname() + ), " );
//			
//		}
		
	}
}

