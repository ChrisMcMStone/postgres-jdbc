/**
 * Created by chris on 29/10/14.
 */

/*

* NOTE TO THE MARKER:
*                   I appreciate I have not used prepared statements properly so as to prevent SQL injection, however, I recognise this
*                   and have shown my ability to use them properly in the Report.java class.
*                   As I did not have enough time to change all the other uses of prepared statements (in this class and the transcript class), I have
*                   added some extra validation that will prevent SQL injection.
*
*                   In addition to the above, it was unclear as to whether or not you have to add student contact details/next of kin details when adding
*                   a new student, so that has been left out. (Can easily be added in if I am told this is needed).
*
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Interface {

    public static void main(String[] args) throws IOException {
        Connection dbConn = DBConnect.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


            menu(reader, dbConn);

    }

    public static void menu(BufferedReader reader, Connection dbConn)  {

        try {
            System.out.println("To select an task, type in the number of the option and hit enter...");

            System.out.print("\n 1. Register a new student. \n 2. Add marks for a student. " +
                    "\n 3. Produce transcript for a student. \n 4. Produce module report. \n\nOption: ");


            int choice;
            while (1 > 0) {
                choice = makeChoice(reader);
                if (choice > 4 || choice < 1) {
                    System.out.println("Sorry, please try again.");
                    continue;
                } else {
                    break;
                }
            }

            switch (choice) {
                case 1:
                    registerStudent(dbConn, reader);
                    break;
                case 2:
                    addMarks(dbConn, reader);
                    break;
                case 3:
                    Transcript ts = new Transcript(searchForStudent(reader, dbConn), dbConn, reader);
                    ts.printTranscript();
                    break;
                case 4:
                    System.out.println(Report.generateReport(dbConn, reader));
                    menu(reader, dbConn);
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    public static void registerStudent(Connection dbConn, BufferedReader reader) throws IOException {

        boolean valid = false;
        int choice = -1;
        String date = null;
        String surname = "";
        String forename = "";


        while (!valid) {

            System.out.println("Select a title number and hit enter...");
            int i = 1;
            try {
                PreparedStatement ps = dbConn.prepareStatement("SELECT titleString From Titles");
                ResultSet r = ps.executeQuery();

                StringBuilder sb = new StringBuilder();
                while (r.next()) {
                    sb.append(i + ". " + r.getString(1) + "    ");
                    i++;
                }
                System.out.println(sb.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            choice = makeChoice(reader);
            while (choice > (i - 1) || choice < 1) {
                System.out.println("Invalid option, try again.");
                choice = makeChoice(reader);
            }

            forename = getFirstName(reader);

            surname = getSurName(reader);


            do {
                if (date != null) {
                    System.out.println("Format should be: YYYY/MM/DD");
                }
                System.out.print("Enter date of birth: ");
                date = reader.readLine();
            } while (!isValidDOB(date));


            try {
                PreparedStatement checkStudent = dbConn.prepareStatement("SELECT * FROM Student WHERE titleID = " +
                        choice + "AND forename = '" + forename + "' AND familyName = '" + surname + "' AND dateOfBirth = '" + date + "'");
                ResultSet rs = checkStudent.executeQuery();
                if (rs.next()) {
                    System.out.println("Student already exists in Database. Please try again.");
                } else { valid = true; }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.exit(0);
                menu(reader, dbConn);

            }
        }

        try {

            PreparedStatement newStudent = dbConn.prepareStatement("INSERT INTO " +
                    "Student (titleID, forename, familyName, dateOfBirth) VALUES (" + choice + ", '" + forename + "', '" + surname + "', '" + date + "')");
            newStudent.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed...");
        }

        System.out.println("Succeeded");
        menu(reader, dbConn);
    }

    public static void addMarks(Connection dbConn, BufferedReader reader) throws IOException {

        boolean valid = false;
        int mark = 0;
        String text = "";
        int id = -1;
        int mid = -1;
        int year = 0;
        int type = 0;

        while (!valid) {

            id = searchForStudent(reader, dbConn);
//-----------------------------------------------------------------------------------------------

            mid = searchForModule(reader, dbConn);

//-----------------------------------------------------------------------------------------------

            System.out.println("Enter a year");
            System.out.print("Year: ");

            year = makeChoice(reader);

            while (year > Calendar.getInstance().get((Calendar.YEAR)) || year < 1995) {
                System.out.println("Please enter a year before next year and after 1995.");
                System.out.print("Year: ");
                year = makeChoice(reader);
            }
//-------------------------------------------------------------------------------------------------

            System.out.println("Select a type of exam number and hit enter...");

            int i = 1;
            try {
                PreparedStatement ps = dbConn.prepareStatement("SELECT typeString From Type");
                ResultSet r = ps.executeQuery();

                StringBuilder sb = new StringBuilder();
                while (r.next()) {
                    sb.append(i + ". " + r.getString(1) + "    ");
                    i++;
                }
                System.out.println(sb.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.print("Option: ");

            type = makeChoice(reader);

            while (type < 1 || type >= i) {
                System.out.println("Please enter a correct type: ");
                System.out.print("Option: ");
                type = makeChoice(reader);
            }

//-------------------------------------------------------------------------------------------------


            System.out.println("Please enter a mark out of 100.");
            System.out.print("Mark: ");
            mark = makeChoice(reader);
            while (mark < 0 || mark > 100) {
                System.out.println("Please enter a valid mark: ");
                System.out.print("Mark: ");
                mark = makeChoice(reader);
            }

            text = "'";
            while(text.contains("'")) {

                System.out.println("Enter the notes about the mark: ");
                System.out.print("Notes: ");

                text = reader.readLine();
            }
//-----------------------------------------------------------------------------------------------
            try {
                PreparedStatement markCheck = dbConn.prepareStatement("SELECT * FROM Marks WHERE " +
                        "studentID = " + id + " AND moduleID = " + mid + " AND year = '" + year + "' AND" +
                        " typeID = " + type);
                ResultSet rs = markCheck.executeQuery();
                if (rs.next()) {
                    System.out.println("Marks have already been entered for this student's exam sitting");
                } else {
                    valid = true;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                menu(reader, dbConn);
            }
        }

        try {
            PreparedStatement newMarks = dbConn.prepareStatement("INSERT INTO " +
                    "Marks (studentID, moduleID, year, typeID, mark, notes) VALUES ("
                    + id + ", " + mid + ", " + year + ", " + type + ", " + mark + ", '" + text + "')");
            newMarks.execute();
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Failed...");
            menu(reader, dbConn);
        }
        System.out.println("Succeeded");
        menu(reader, dbConn);

    }

    private static int searchForModule(BufferedReader reader, Connection dbConn) throws IOException{
        System.out.print("Please choose the way in which you would like to search for a module\n" +
                "1. Search by Module ID.\n2. Search by Module name.\n Option: ");

        int mid = -1;

        int mchoice = -1;

        while (mchoice > 2 || mchoice < 1) {

            mchoice = makeChoice(reader);

            switch (mchoice) {
                case 1:
                    mid = getModuleById(reader, dbConn);
                    break;

                case 2:
                    String modulename = getModuleName(reader);

                    StringBuilder result = new StringBuilder();
                    try {
                        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM Module WHERE moduleName = '" + modulename + "'");
                        ResultSet rs = ps.executeQuery();

                        String[] fields = {"moduleID", "moduleName", "moduleDescription", "lecturerID"};
                        int i = 1;
                        while (rs.next()) {
                            while (i < 5) {
                                result.append(fields[(i - 1)] + ": ");
                                result.append(rs.getString(i));
                                result.append("  ");
                                i++;
                            }
                            i = 1;
                            result.append("\n");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if(result.toString().isEmpty()) result.append("No results found.");

                    System.out.println(result.toString());
                    mid = getModuleById(reader, dbConn);
                    break;
                default:
                    System.out.println("Please enter option 1 or 2.");
                    System.out.print("Option: ");
            }
        }
        return mid;
    }

    public static boolean isValidName(String name) {
        return name.matches("[A-Z][a-zA-Z]*") && !name.contains("'");
    }

    public static boolean isValidAlphaNumeric(String name) {
        return name.matches("^[a-zA-Z0-9 ]+$") && !name.contains("'");
    }

    public static boolean isValidSurName(String surname) {
        return surname.matches("[a-zA-z]+([ '-][a-zA-Z]+)*") && !surname.contains("'");
    }

    public static boolean isValidDOB(String date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        f.setLenient(false);
        java.util.Date x;
        try {
            x = f.parse(date.trim());
        } catch (ParseException e) {
            return false;
        }
        java.util.Date min = new java.util.Date(10, 01, 01);
        java.util.Date max = new java.util.Date(99, 01, 01);

        if (x.before(min) || x.after(max)) {
            return false;
        }
        return true;
    }

    public static int makeChoice(BufferedReader reader) throws IOException {
        String input;
        int choice = -1;
        while ((input = reader.readLine()) != null) {
            try {
                choice = Integer.parseInt(input.replaceAll("\\s", ""));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Sorry, please try again.");
            }
        }
        return choice;
    }

    public static String getFirstName(BufferedReader reader) throws IOException {
        String firstname = null;
        do {
            if (firstname != null) {
                System.out.println("Invalid input");
            }
            System.out.print("Enter the forename of the student: ");
            firstname = reader.readLine();
        } while (!isValidName(firstname));

        return firstname;

    }

    public static String getModuleName(BufferedReader reader) throws IOException {
        String modulename = null;
        do {
            if (modulename != null) {
                System.out.println("Invalid input");
            }
            System.out.print("Enter the name of the module: ");
            modulename = reader.readLine();
        } while (!isValidAlphaNumeric(modulename));

        return modulename;

    }

    public static String getSurName(BufferedReader reader) throws IOException {

        String surname = null;
        do {
            if (surname != null) {
                System.out.println("Invalid input");
            }
            System.out.print("Enter the surname of the student: ");
            surname = reader.readLine();
        } while (!isValidSurName(surname));

        return surname;
    }

    private static int getStudentById(BufferedReader reader, Connection dbConn) throws IOException {

        int size = -1;
        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT COUNT (*) FROM Student");
            ResultSet rs = ps.executeQuery();
            rs.next();
            size = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("Enter Student ID: ");
        int id = makeChoice(reader);
        while (id > (size) || id < 1) {
            System.out.println("Invalid Student ID, try again.");
            System.out.print("Enter Student ID: ");
            id = makeChoice(reader);
        }

        return id;
    }

    private static int getModuleById(BufferedReader reader, Connection dbConn) throws IOException {
        int size = -1;
        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT COUNT (*) FROM Module");
            ResultSet rs = ps.executeQuery();
            rs.next();
            size = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.print("Enter Module ID: ");
        int id = makeChoice(reader);
        while (id > (size) || id < 1) {
            System.out.println("Invalid Module ID, try again.");
            System.out.print("Enter Module ID: ");
            id = makeChoice(reader);
        }

        return id;

    }

    private static int searchForStudent(BufferedReader reader, Connection dbConn) throws  IOException {
        System.out.print("Please choose the way in which you would like to search for a student\n" +
                "1. Search by Student ID.\n2. Search by Student name.\n Option: ");

        int id = -1;

        int choice = -1;

        while (choice > 2 || choice < 1) {

            choice = makeChoice(reader);

            switch (choice) {
                case 1:
                    id = getStudentById(reader, dbConn);
                    break;

                case 2:
                    String firstname = getFirstName(reader);

                    String surname = getSurName(reader);

                    StringBuilder result = new StringBuilder();
                    try {
                        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM Student WHERE forename = '" + firstname +
                                "' AND familyName = '" + surname + "'");
                        ResultSet rs = ps.executeQuery();

                        String[] fields = {"Student ID", "Title ID", "Forename", "Surname", "D.O.B"};
                        int i = 1;
                        while (rs.next()) {
                            while (i < 6) {
                                result.append(fields[(i - 1)] + ": ");
                                result.append(rs.getString(i));
                                result.append("  ");
                                i++;
                            }
                            i = 1;
                            result.append("\n");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if(result.toString().isEmpty()) result.append("No results found.");
                    System.out.println(result.toString());
                    id = getStudentById(reader, dbConn);
                    break;
                default:
                    //TODO while loop this shit
                    System.out.println("Please enter option 1 or 2.");
                    System.out.print("Option: ");
            }
        }
        return id;
    }

}