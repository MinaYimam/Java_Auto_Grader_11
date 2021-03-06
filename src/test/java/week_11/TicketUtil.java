package week_11;

import javax.swing.*;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by clara on 11/4/19.
 */
public class TicketUtil {
    
    public static void clearStore() {
        try (Connection con = DriverManager.getConnection(Configuration.TEST_DB_URI);
             Statement statement = con.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS ticket");
        } catch (SQLException e) {
            System.out.println("Error clearing data from test database. Does the database exist?");
        }
    }
    
    
    public static void expectedListSameAsJList(List<Ticket> testTickets, JList ticketList) {
        
        for ( int t = 0 ; t < testTickets.size() ; t++) {
            
            Ticket expected = testTickets.get(t);
            try {
                Ticket actual = (Ticket) ticketList.getModel().getElementAt(t);
                assertTrue(String.format("Element %d of the GUI ticket list expected to be %s but was %s", t, expected.toString(), actual.toString()),
                        sameTicket(expected, actual, 4000, false));
                
            } catch (ClassCastException cce) {
                fail("Your JList model should contain only Ticket objects.");
            }
        }
    }
    
    
    public static boolean sameTicket(Ticket t1, Ticket t2)  {
        return sameTicket(t1, t2, 0, false);
    }
    
    public static boolean sameTicket(Ticket t1, Ticket t2, boolean ignoreId)  {
        return sameTicket(t1, t2, 0, ignoreId);
    }
    
    public static boolean sameTicket(Ticket t1, Ticket t2, long acceptableMsDifferenceInDate)  {
        return sameTicket(t1, t2, acceptableMsDifferenceInDate, false);
    }
    
    public static boolean sameTicket(Ticket t1, Ticket t2, long acceptableMsDifferenceInDate, boolean ignoreId)  {
        // If either or both tickets are null, return false (because at least one of the things is not an open ticket)
    
        if (t1 == null || t2 == null)  {
            System.out.printf("Compare tickets - At least one of tickets is null \n%s\n%s\n", t1, t2);
            return false;}
        
        if (!ignoreId) {
            if (t1.getTicketID() != t2.getTicketID()) {
                System.out.printf("Compare tickets - ids don't match \n%s\n%s\n", t1, t2);
                return false;
            }
        }
        
        if (!(t1.getDescription().equals(t2.getDescription())))  {
            System.out.printf("Compare tickets - descriptions don't match \n%s\n%s\n", t1, t2);
            return false;
        }

        if (!(t1.getReporter().equals(t2.getReporter())))  {
            System.out.printf("Compare tickets - reporters don't match \n%s\n%s\n", t1, t2);
            return false;
        }

        if (t1.getPriority() != t2.getPriority()) {
            System.out.printf("Compare tickets - priorities don't match \n%s\n%s\n", t1, t2);
            return false;
        }

        if (t1.getResolution() != null && t2.getResolution() != null && !t1.getResolution().equals(t2.getResolution())) {
            System.out.printf("Compare tickets - resolutions don't match \n%s\n%s\n", t1, t2);
            return false;
        }

        // todo one is null, other is not null
        
        long dateDiff = Math.abs(t1.getDateReported().getTime() - t2.getDateReported().getTime());
        if  (dateDiff > acceptableMsDifferenceInDate) {
            System.out.printf("Compare tickets - dates reported don't match \n%s\n%s\n", t1, t2);
            return false;
        }

        if (t1.getDateResolved() != null && t2.getDateResolved() != null) {
            dateDiff = Math.abs(t1.getDateResolved().getTime() - t2.getDateResolved().getTime());
            if  (dateDiff > acceptableMsDifferenceInDate) {
                System.out.printf("Compare tickets - dates resolved don't match \n%s\n%s\n", t1, t2);
                return false;
            }
        }
        // todo one date is null, other is not null
        
        return true;
        
    }


    public static void printAllTickets(String db) {

        try (Statement s = DriverManager.getConnection(db).createStatement()) {
            ResultSet rs = s.executeQuery("SELECT * FROM ticket");
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();

            System.out.println("Data in ticket table");
            boolean rows = false;
            while(rs.next()){
                rows = true;
                for (int x = 0 ; x < columnCount ; x++) {
                    String columnName = md.getColumnName(x+1);
                    System.out.print(columnName + " " + rs.getObject(x+1) + " -- ");
                }
                System.out.println();
            }

            if (!rows) {
                System.out.println("No rows in ticket table");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all data from DB " + e);
        }
    }
    
}
