/**
 * Created by chris on 29/10/14.
 */

import javax.xml.transform.Result;
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

        System.out.println("To select an task, type in the number of the option and hit enter...");

        System.out.print("\n 1. Register a new student. \n 2. Add marks for a student. " +
                "\n 3. Produce transcript for a student. \n 4. Produce module report. \n\nOption: ");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        switch (makeChoice(reader)) {
            case 1:
                registerStudent(dbConn, reader);
                break;
            case 2:
                addMarks(dbConn, reader);
                break;
            case 3:
                transcript(dbConn);
                break;
            case 4:
                moduleReport(dbConn);
                break;
            default:
                System.out.println("Not a valid option.");
                break;
        }
    }

    public static void registerStudent(Connection dbConn, BufferedReader reader) throws IOException {

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

        int choice = makeChoice(reader);
        while (choice > (i - 1) || choice < 1) {
            System.out.println("Invalid option, try again.");
            choice = makeChoice(reader);
        }

//        if(choice < 0) {
//            return;
//        }

        String forename = getFirstName(reader);

        String surname = getSurName(reader);

        String date = null;
        do {
            if (date != null) {
                System.out.println("Format should be: YYYY/MM/DD");
            }
            System.out.print("Enter date of birth: ");
            date = reader.readLine();
        } while (!isValidDate(date));

        try {
            PreparedStatement newStudent = dbConn.prepareStatement("INSERT INTO " +
                    "Student (titleID, forename, familyName, dateOfBirth) VALUES (" + choice + ", '" + forename + "', '" + surname + "', '" + date + "')");
            newStudent.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed...");
        }

        System.out.println("Succeeded");
    }

    public static void addMarks(Connection dbConn, BufferedReader reader) throws IOException {

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
                    System.out.println(result.toString());
                    id = getStudentById(reader, dbConn);
                    break;
                default:
                    //TODO while loop this shit
                    System.out.println("Please enter option 1 or 2.");
                    System.out.print("Option: ");
            }
        }

//-----------------------------------------------------------------------------------------------
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
                    System.out.println(result.toString());
                    mid = getModuleById(reader, dbConn);
                    break;
                default:
                    System.out.println("Please enter option 1 or 2.");
                    System.out.print("Option: ");
            }
        }

//-----------------------------------------------------------------------------------------------

        System.out.println("Enter a year");
        System.out.print("Year: ");
        int year = 0;
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
        int type = 0;
        type = makeChoice(reader);

        while (type < 1 || type > i) {
            System.out.println("Please enter a correct type: ");
            System.out.print("Option: ");
            type = makeChoice(reader);
        }

//-------------------------------------------------------------------------------------------------

        int mark = 0;
        System.out.println("Please enter a mark out of 100.");
        System.out.print("Mark: ");
        mark = makeChoice(reader);
        while (mark < 0 || mark > 100) {
            System.out.println("Please enter a valid mark: ");
            System.out.print("Mark: ");
            mark = makeChoice(reader);
        }

        String text = "";
        System.out.println("Enter the notes about the mark: ");
        System.out.print("Notes: ");
        text = reader.readLine();
//-----------------------------------------------------------------------------------------------
        try {
            PreparedStatement newMarks = dbConn.prepareStatement("INSERT INTO " +
                    "Marks (studentID, moduleID, year, typeID, mark, notes) VALUES ("
                    + id + ", " + mid + ", " + year + ", " + type + ", " + mark + ", '" + text + "')");
            newMarks.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed...");
            System.exit(0);
        }

        System.out.println("Succeeded");


    }
    public static void transcript(Connection dbConn) {

        //TODO Add functionality for choosing student by name


    }

    public static void moduleReport(Connection dbConn) {
        System.out.println("Produce a report for a module.");
    }

    public static boolean isValidName(String name) {
        return name.matches("[A-Z][a-zA-Z]*");
    }

    public static boolean isValidAlphaNumeric(String name) {
        return name.matches("^[a-zA-Z0-9 ]+$");
    }

    public static boolean isValidSurName(String surname) {
        return surname.matches("[a-zA-z]+([ '-][a-zA-Z]+)*");
    }

    public static boolean isValidFullName(String name) {
        return name.matches("/^[a-z ,.'-]+$/i");
    }

    public static boolean isValidDate(String date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
        f.setLenient(false);
        java.util.Date x;
        try {
            x = f.parse(date.trim());
        } catch (ParseException e) {
            return false;
        }
        if (x.before(new java.util.Date(1910, 01, 01)) || x.after(new java.util.Date(1997, 01, 01))) {
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

}