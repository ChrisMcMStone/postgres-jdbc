import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by chris on 30/10/14.
 */
public class Transcript {

    int studentID;
    StringBuilder transcriptTxt;

    public Transcript(int StudentID, Connection dbConn, BufferedReader reader) {

        studentID = StudentID;
        transcriptTxt = new StringBuilder("\n\nStudent details\n    Student ID: " + studentID);
        generateScript(dbConn, reader);

    }

    private void generateScript(Connection dbConn, BufferedReader reader) {

        appendStudentDetails(dbConn, reader);

        transcriptTxt.append("\n\nMarks:\n    ");


        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT * From Marks Where studentID = " + studentID + " ORDER BY year ASC, moduleID ASC");
            ResultSet r = ps.executeQuery();
            if(!r.next()) {
                System.out.println("No marks for this student.");
                Interface.menu(reader, dbConn);
            }
            int year = r.getInt(3);
            transcriptTxt.append(year + ":\n");
            do {
                if(r.getInt(3) != year) {
                    year = r.getInt(3);
                    transcriptTxt.append("    " + year + ":\n");
                }
                int mid = r.getInt(2);
                transcriptTxt.append("            " + getModuleDetails(mid, dbConn, reader) + getTypeandMarks(r, dbConn, reader) + "\n");

            } while(r.next());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Interface.menu(reader, dbConn);

        }

    }

    private String getTypeandMarks(ResultSet r, Connection dbConn, BufferedReader reader) {
        String typename = "";
        int mark = -1;
        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT typeString From Type Where typeID = " + r.getInt(4));
            ResultSet r1 = ps.executeQuery();
            r1.next();
            typename = r1.getString(1);
            mark = r.getInt(5);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Interface.menu(reader, dbConn);
        }


        return "Type: " + typename + ", Mark: " + mark;

    }

    private String getModuleDetails(int mid, Connection dbConn, BufferedReader reader) {

        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT moduleName From Module Where moduleID = " + mid);
            ResultSet r = ps.executeQuery();
            r.next();
            return "Module ID: " + mid + ", Name: " + r.getString(1) + ", ";

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            Interface.menu(reader, dbConn);
        }

        return "";
    }

    private void appendStudentDetails(Connection dbConn, BufferedReader reader) {
        transcriptTxt.append("\n    Name: ");

        try {
            PreparedStatement ps = dbConn.prepareStatement("SELECT * From Student Where studentID = " + studentID);
            ResultSet r = ps.executeQuery();
            r.next();
            PreparedStatement ps2 = dbConn.prepareStatement("Select titleString From Titles Where titleID = " + r.getString(2));
            ResultSet r2 = ps2.executeQuery();
            r2.next();
            transcriptTxt.append(r2.getString(1) + " ");
            transcriptTxt.append(r.getString(3) + " ");
            transcriptTxt.append(r.getString(4) + " ");
            transcriptTxt.append("\n    D.o.B: " + r.getString(5));
        } catch (SQLException e) {
            System.out.println(e.getMessage());;
            Interface.menu(reader, dbConn);
        }
    }

    public void printTranscript() {

        System.out.print(transcriptTxt + "\n\n");

    }


}
