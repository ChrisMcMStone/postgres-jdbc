import java.sql.*;
import java.util.Random;

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

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
            System.exit(0);
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
		
		System.out.println("Existing data tables deleted.");
		
		
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
					"postalAddress varchar(50) NOT NULL," +
					"" + 
					"FOREIGN KEY (studentID) REFERENCES Student(studentID)" + 
					");");
			
			createNextOfKinContact = dbConn.prepareStatement(
					"Create Table NextOfKinContact (" + 
					"" + 
					"studentID serial NOT NULL," + 
					"eMailAddress varchar(254) NOT NULL," + 
					"postalAddress varchar(50) NOT NULL," +
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

        System.out.println("Title records added.");

        //Insert Students

        StringBuilder studentsSQL = new StringBuilder("INSERT INTO Student (titleID, forename, familyName, dateOfBirth) VALUES ");
        for(int i = 0; i < 13; i++) {
            for (String title : titles) {
                studentsSQL.append("((SELECT titleID FROM Titles WHERE titleString = '" +
                            title + "'), '" + RandomName.getForename() + "', '" + RandomName.getSurname() + "', " + "CURRENT_DATE - integer '" + RandomName.randomDate() + "'), ");
            }
        }
        studentsSQL.append("((SELECT titleID FROM Titles WHERE titleString = '" +
                "Prof" + "'), '" + RandomName.getForename() + "', '" + RandomName.getSurname() + "', " + "CURRENT_DATE - integer '" + RandomName.randomDate() + "') ");
        try{
            PreparedStatement addStudents = dbConn.prepareStatement(studentsSQL.toString());
            addStudents.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Student records added.");

        //Insert Lecturers

        StringBuilder lecturerSQL = new StringBuilder("INSERT INTO Lecturer (titleID, forename, familyName) VALUES ");

        String[] lecturerTitles = {"Dr", "Prof"};
        for (int i = 0; i < 10; i++) {
            for(String lecTitle : lecturerTitles) {
                lecturerSQL.append("((SELECT titleID FROM Titles WHERE titleString = '" +
                        lecTitle + "'), '" + RandomName.getForename() + "', '" + RandomName.getSurname() + "'), ");
            }
        }
        lecturerSQL.append("((SELECT titleID FROM Titles WHERE titleString = '" +
                "Prof" + "'), '" + RandomName.getForename() + "', '" + RandomName.getSurname() + "')");
        try{
            PreparedStatement addLecturers = dbConn.prepareStatement(lecturerSQL.toString());
            addLecturers.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Lecturer records added.");


        //Add modules

        StringBuilder moduleSQL = new StringBuilder("INSERT INTO Module (moduleName, moduleDescription, lecturerID) VALUES ");
        moduleSQL.append("('Functional programming', 'The study of haskell', 1), ");
        moduleSQL.append("('Intro to AI', 'The study of artificial intelligence', 2), ");
        Random rand = new Random();
        for(int i = 0; i < 97; i++) {
            moduleSQL.append("('Module " + i + "', 'The study of module " + i + "', " + (rand.nextInt(9)+1) + "), ");
        }
        moduleSQL.append("('Physics', 'The study of physics', 5)");
        try{
            PreparedStatement addModules = dbConn.prepareStatement(moduleSQL.toString());
            addModules.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Module records added.");

        //Add types

        String typeSQL = "INSERT INTO Type (typeString) VALUES ('sit'), ('resit'), ('repeat')";
        try{
            PreparedStatement addTypes = dbConn.prepareStatement(typeSQL.toString());
            addTypes.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Type records added.");

        //Add Marks

        StringBuilder marksSQL = new StringBuilder("INSERT INTO Marks (studentID, moduleID, year, typeID, mark, notes) VALUES ");
        String[] years = {"2014", "2013", "2012", "2011"};
        for(int i = 0; i < 30; i++) {
            for(String year : years) {
                marksSQL.append("(" + (rand.nextInt(104) + 1) + ", " + (rand.nextInt(100) + 1) + ", " + year + ", " + (rand.nextInt(3)+1)
                        + ", " + (rand.nextInt(100)+1) + ", 'Some generic notes on the mark...'), ");
            }
        }
        marksSQL.append("(" + (rand.nextInt(104) + 1) + ", " + (rand.nextInt(100) + 1) + ", 2013, " + (rand.nextInt(3)+1)
            + ", " + (rand.nextInt(100)+1) + ", 'Some generic notes on the mark...')");
        try{
        PreparedStatement addMarks = dbConn.prepareStatement(marksSQL.toString());
        addMarks.execute();
         } catch (SQLException e) {
        e.printStackTrace();
        }
        System.out.println("Mark records added.");

        //Add Student Contact details

        StringBuilder studentContact = new StringBuilder("INSERT INTO StudentContact (studentID, eMailAddress, postalAddress) VALUES ");
        for(int i = 1; i < 104; i++) {
            studentContact.append("(" + i +", 'student" + i + "@uni.ac.uk', '" + RandomName.getSurname() + " road, " + RandomName.randomPostCode() +  "'), ");
        }
        studentContact.append("(" + 105 +", 'student" + 105 + "@uni.ac.uk', '" + RandomName.getSurname() + " road, " + RandomName.randomPostCode() +  "')");
        try{
            PreparedStatement addStudentContactDetails = dbConn.prepareStatement(studentContact.toString());
            addStudentContactDetails.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Student contact details records added.");

        //Add Next of Kin details

        StringBuilder nextOfKin = new StringBuilder("INSERT INTO NextOfKinContact (studentID, eMailAddress, postalAddress) VALUES ");
        for(int i = 1; i < 104; i++) {
            nextOfKin.append("(" + i +", 'nextOfKin" + i + "@family.com', '" + RandomName.getSurname() + " road, " + RandomName.randomPostCode() +  "'), ");
        }
        nextOfKin.append("(" + 105 +", 'nextOfKin" + 105 + "@family.com', '" + RandomName.getSurname() + " road, " + RandomName.randomPostCode() +  "')");
        try{
            PreparedStatement addNextOfKin = dbConn.prepareStatement(nextOfKin.toString());
            addNextOfKin.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Next of kin contact details records added.");
        System.out.println("Database successfully populated.");

    }
}


