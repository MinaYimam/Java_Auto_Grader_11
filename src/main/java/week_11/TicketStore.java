package week_11;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;


/**
 * For add, edit, search operations on the Ticket database.
 * Use try-with-resources exception handing in all of the methods to ensure the
 * database connection is closed at the end of the method.
 */


public class TicketStore {
    
    private final String dbURI;
    
    TicketStore(String databaseURI) {
        this.dbURI = databaseURI;
        String createtable = "CREATE TABLE IF NOT EXISTS ticket " +
                "(id INTEGER PRIMARY KEY, " +
                "description TEXT NOT NULL," +
                "reporter TEXT NOT NULL," +
                "priority INTEGER NOT NULL CHECK(priority >= 1 AND priority <= 5)," +
                "dateReported INTEGER  NOT NULL," +
                "resolution TEXT," +
                "dateResolved INTEGER," +
                "status TEXT NOT NULL CHECK(status = 'OPEN' OR status = 'RESOLVED'))";
        try(Connection connection = DriverManager.getConnection(dbURI);
        Statement statement = connection.createStatement()){
            statement.execute(createtable);
        }
        catch(SQLException e){
            System.out.println("Error creating table because" + e);
        }

        /* TODO create a ticket table in the database given by databaseURI.
             It should have these columns, types and constraints, in this order:
              id, the primary key  (you will let SQLite autogenerate the values for this column for you)
              description, text, Can't be null.
              reporter, text. Can't be null.
              priority, a number in the range 1-5. Can't be null. Add constraints to prohibit values outside the range 1-5.
              dateReported, number, the long time value of a Date object, representing the date the ticket was created. Can't be null.
              resolution, text. May be null.
              dateResolved, number, the long time value of a Date object, representing the date the ticket was marked as resolved. May be null.
              status, text, either "OPEN" or "RESOLVED". These are the only acceptable values. Can't be null.
        */

    }
    
    
    public List<Ticket> getAllOpenTickets() {
        String getall = "SELECT * FROM ticket WHERE status = 'OPEN' ORDER BY priority";
        try(Connection connection = DriverManager.getConnection(dbURI);
            PreparedStatement statement = connection.prepareStatement(getall)){
            ResultSet alltickets = statement.executeQuery();
            List<Ticket> tickets = new ArrayList<>();
            while(alltickets.next()){
                int id= alltickets.getInt("id");
                String description = alltickets.getString("description");
                int priority = alltickets.getInt("priority");
                String reporter = alltickets.getString("reporter");
                long dateReportedstamp = alltickets.getLong("dateReported");
                Date daterep = new Date(dateReportedstamp);
                String resolution = alltickets.getString("resolution");
                long dateresolvedstamp = alltickets.getLong("dateResolved");
                Date dateres = new Date(dateresolvedstamp);
                String status = alltickets.getString("status");
                Ticket.TicketStatus ticketstatus = Ticket.TicketStatus.valueOf(status);
                Ticket ticket = new Ticket(id, description, priority, reporter , daterep, resolution,dateres, ticketstatus);
                tickets.add(ticket);
            }
            return tickets;
        }
        catch (SQLException e){
            System.out.println("system error getting open tickets" + e);
        }


        
        // TODO Query database, get all tickets which have the status "OPEN"
        //  order the tickets by priority - tickets with priority = 1 first, priority = 5 last
        //  Remember the database can order tickets for you
        //  Create Ticket object for each row in the ResultSet
        //  Return a List of these Ticket objects
        //  If there are no Tickets in the database, return an empty List.
        //  Catch any SQL errors and use System.err.print to print an error message.
        //  If an SQL exception is thrown, return null.

        return null;   // replace with your code.
    }

    
    
    /** Add ticket to the database. */
    public void add(Ticket newTicket) throws SQLException {
        String insertrow = "INSERT INTO ticket " +
                "(description , reporter, priority , dateReported, resolution, dateResolved , status) VALUES (?, ? ,?,?,?,?,? )";


        Connection connection = DriverManager.getConnection(dbURI);
            PreparedStatement statement = connection.prepareStatement(insertrow);{
            statement.setString(1,newTicket.getDescription());
            statement.setString(2,newTicket.getReporter());
            statement.setInt(3,newTicket.getPriority());
            Date treporteddate = newTicket.getDateReported();
            statement.setLong(4,treporteddate.getTime());
            statement.setString(5,newTicket.getResolution());
            Date tresolveddate = newTicket.getDateResolved();
            if(tresolveddate == null){
                statement.setDate(6,null);

            }
            else{statement.setLong(6,tresolveddate.getTime());
            }
            Ticket.TicketStatus newticketstatus = newTicket.getStatus();
            String status = newticketstatus.toString();
            statement.setString(7,status);
            statement.execute(insertrow);
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            int id = keys.getInt(1);
            newTicket.setTicketID(id);
            }


        
        // TODO insert a new row in the database for this ticket.
        //  Write the data from the fields in the newTicket object.
        //  Write all of the fields a Ticket could have:
        //  description, priority, reporter, dateReported, resolution, dateResolved and status.
    
        // TODO Re-throw any SQLExceptions due to constraint violations from this method. The rest of the
        //  code will interpret a SQLException as meaning that the data in the ticket
        //  was not valid, for example the priority was -10 or the status was "CAT"

        // From a catch block, use the throw keyword to throw the exception, for example,

        //        String insertSQL = ""; //  write SQL statement here
        //
        //        try (Connection conn = DriverManager.getConnection(dbURI);
        //            PreparedStatement preparedStatement = conn.prepareStatement(insertSQL)) {
        //
        //            // insert data about ticket into database
        //
        //        }
        //        catch (SQLException sqle) {
        //            // use the throw keyword to throw the exception for the caller to handle
        //            throw sqle;
        //        }


    }


    public Ticket getTicketById(int id) {
        String getticketbyid = "SELECT * FROM ticket WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(dbURI);
            PreparedStatement statement = connection.prepareStatement(getticketbyid)){
           // statement.execute();
            statement.setInt(1,id);
            ResultSet ticket = statement.executeQuery();
            if (ticket.next()){
              String description = ticket.getString("description");
              String repoerter = ticket.getString("reporter");
              int priority = ticket.getInt("priority");
              Date dateReported = new Date(ticket.getLong("dateReported"));
              String resolution = ticket.getString("resolution");
              Date dateResolved = new Date(ticket.getLong("dateResolved"));
              String stringstatus = ticket.getString("status");
              Ticket.TicketStatus status = Ticket.TicketStatus.valueOf(stringstatus);
              Ticket nticket = new Ticket(id, description, priority, repoerter, dateReported,resolution,dateResolved,status);
              return nticket;
            }
            else {
                return null;
            }
            //create a ticket ibject
            //return that ticket
        }
        catch(SQLException e){
            System.out.println("Error creating table because" + e);
        }
       
        // TODO query the database to find the ticket with this id.
        //  if the ticket is found, then create a Ticket object and return it
        //  if the ticket is not found, return null.

        // Catch any SQL errors and use System.err.print to print an error message. If an SQL exception is thrown, return null.

        return null; // TODO replace with your code.

    }
    
    
    public void updateTicket(Ticket ticket) {
        String update = "UPDATE ticket SET description = ?, reporter = ? ,priority = ?, dateReported =? , resolution =? , dateResolved = ? " +
                "WHERE id =?";
        try(Connection connection = DriverManager.getConnection(dbURI);
            PreparedStatement preparedStatement = connection.prepareStatement(update)){
            preparedStatement.setString(1,ticket.getDescription());
            preparedStatement.setString(2,ticket.getReporter());
            preparedStatement.setInt(3,ticket.getPriority());
            Date dateReported = ticket.getDateReported();
            long timestamprep = dateReported.getTime();
            preparedStatement.setLong(4, timestamprep);
            preparedStatement.setString(5, ticket.getResolution());
            Date dateresolved = ticket.getDateResolved();
            long timestampres = dateresolved.getTime();
            preparedStatement.setLong(6, timestampres);
            //Ticket.TicketStatus status = Ticket.TicketStatus.valueOf(stringstatus);
            //preparedStatement.setString(7, stringstatus);

            preparedStatement.execute();
        }
        catch (SQLException e){
            System.out.println("unable to update " + e);

        }
        
        // TODO Use the Ticket's ID to modify the row in the database with this ID
        //  modify row in the database to set the values contained in the Ticket object
        //  this method can be used to resolve a ticket.
    
        // Catch any SQLException errors. Use System.err.print to print an error message if an exception if thrown.

    }
    
    
    public List<Ticket> searchByDescription(String description) {
        if(description == null || description.isEmpty()){
            return Collections.emptyList();
        }
        String searchbydescr = "SELECT * FROM Ticket WHERE description LIKE ? ORDER BY priority";
        try(Connection connection = DriverManager.getConnection(dbURI);
        PreparedStatement preparedStatement = connection.prepareStatement(searchbydescr)){
            preparedStatement.setString(1, "%"+ description + "%");
            ResultSet tickets = preparedStatement.executeQuery();
            List<Ticket> matches = new ArrayList<>();
                    while (tickets.next()){
                        int id = tickets.getInt("id");
                        String desc = tickets.getString("description");
                        int priority = tickets.getInt("priority");
                        String reporter = tickets.getString("reporter");
                        long datereoprtedtimestamp = tickets.getLong("dateReported");
                        Date datereported = new Date(datereoprtedtimestamp);
                        String resolution = tickets.getString("resolution");
                        long dateresolvedtimestamp = tickets.getLong("dateResolved");
                        Date dateresolved = new Date(dateresolvedtimestamp);
                        String status = tickets.getString("status");
                        Ticket.TicketStatus ticketstatus = Ticket.TicketStatus.valueOf(status);
                        Ticket newticket = new Ticket(id, desc, priority,reporter,datereported,resolution,dateresolved, ticketstatus);
                        matches.add(newticket);
                    }
                    if(matches.isEmpty()){
                        return Collections.emptyList();
                    }
                    return matches;

        }
        catch (SQLException e){
            System.out.println("error searching by description " + e);

        }
        // TODO search the database for all (OPEN AND RESOLVED) tickets that match the description
        //  Order the tickets by PRIORITY.
        //  If description is null, or a blank string, or empty string, return an empty list
        //  The search should be case-insensitive.
        //  The search should return partial matches.
        //  A search for "server" should return a ticket with description "The Windows Server is down"
    
        // Catch any SQLException errors. If any exception is thrown, use System.err.print to print an error message and return null.

        return null;  // TODO replace with your code.
    }

}
