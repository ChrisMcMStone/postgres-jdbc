import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by chris on 30/10/14.
 */
public class Report {

    public static String generateReport(Connection dbConn, BufferedReader reader) throws IOException{

        StringBuilder sb = new StringBuilder();
        
        System.out.println("Enter Module ID:");
        System.out.print("ID: ");

        int mid = Interface.makeChoice(reader);

        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM Module where moduleID = " + mid);
            ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("No such module exists.\n");
            Interface.menu(reader, dbConn);
        }

        System.out.println("Enter a year");
        System.out.print("Year: ");

        int year = Interface.makeChoice(reader);

        while (year > Calendar.getInstance().get((Calendar.YEAR)) || year < 1995) {
            System.out.println("Please enter a year before next year and after 1995.");
            System.out.print("Year: ");
            year = Interface.makeChoice(reader);
        }

        sb.append("\n     **** Module report ****");

        try {

            PreparedStatement ps = dbConn.prepareStatement("SELECT mod.moduleID, mod.moduleName,tit.titleString, lec.foreName, lec.FamilyName "
                    + "FROM Module mod, Titles tit,Lecturer lec "
                    + "WHERE (lec.lecturerID = mod.lecturerID) AND (tit.titleID = lec.titleID) AND (mod.moduleID = ?);");

           ps.setInt(1, mid);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                sb.append("\nModule ID:               " + rs.getInt(1));
                sb.append("\nModule name:             " + rs.getString(2));
                sb.append("\nLecturer name:           " + rs.getString(3) + " " + rs.getString(4) + " " + rs.getString(5));
            }


            ps = dbConn.prepareStatement("SELECT COUNT(m.mark)"
                    + " FROM Marks m"
                    + " WHERE (m.moduleID = ?) AND (m.year = ?);");

            ps.setInt(1, mid);

            ps.setInt(2, year);

            rs = ps.executeQuery();

            rs.next();
            float studentNo= rs.getInt(1);
            sb.append("\nNumber of students:      " + studentNo);


            ps = dbConn.prepareStatement("SELECT COUNT(m.mark)"
                    + " FROM Marks m"
                    + " WHERE (m.moduleID = ?) AND (m.year = ?) AND (m.mark < 40);");

            ps.setInt(1, mid);
            ps.setInt(2, year);

            rs = ps.executeQuery();

            rs.next();
            float noFailures = rs.getInt(1);
            sb.append("\nNumber of failures:      " + noFailures);

            sb.append("\nPercentage of failures:  " + ((noFailures / studentNo)*100));


            ps = dbConn.prepareStatement("SELECT AVG(m.mark)"
                    + " FROM Marks m"
                    + " WHERE (m.moduleID = ?) AND (m.year = ?);");

            ps.setInt(1, mid);
            ps.setInt(2, year);

            rs = ps.executeQuery();

            rs.next();
            sb.append("\nAverage mark:            " + rs.getFloat(1) + "\n\n");

            return sb.toString();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Interface.menu(reader, dbConn);
        }

        return "";


    }

}
