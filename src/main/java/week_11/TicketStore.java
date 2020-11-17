package week_11;

import java.sql.*;
import java.util.List;


/**
 * For add, edit, search operations on the Ticket database.
 * Use try-with-resources exception handing in all of the methods to ensure the
 * database connection is closed at the end of the method.
 */


public class TicketStore {
    
    private final String dbURI;
    
    TicketStore(String databaseURI) {
        this.dbURI = databaseURI;
        
        /* TODO create the Ticket table in the database given by databaseURI.
             It should have these columns, types and constraints, in this order:
              id, the primary key  (you will let SQLite autogenerate the values for this column for you)
              description, text, Can't be null.
              priority, a number in the range 1-5. Can't be null. Add constraints to prohibit values outside the range 1-5.
              dateReported, number, the long time value of a Date object, representing the date the ticket was created. Can't be null.
              resolution, text. May be null.
              dateResolved, number, the long time value of a Date object, representing the date the ticket was marked as resolved. May be null.
              status, text, either "OPEN" or "RESOLVED". These are the only acceptable values. Can't be null.
        */

    }
    
    
    public List<Ticket> getAllOpenTickets() {
        
        // TODO Query database, get all tickets which have the status "OPEN"
        //  order the tickets by priority - tickets with priority = 1 first, priority = 5 last
        //  Remember the database can order tickets for you
        //  Create Ticket object for each row in the ResultSet
        //  Return a List of these Ticket objects
        //  If there are no Tickets in the database, return an empty List.

        // Catch any SQL errors and use System.err.print to print an error message. If an SQL exception is thrown, return null.

        return null;   // replace with your code.
    }

    
    
    /** Add ticket to the database. */
    public void add(Ticket newTicket) throws SQLException {
        
        // TODO insert a new row in the database for this ticket.
        //  Write the data from the fields in the newTicket object.
        //  Write all of the fields a Ticket could have:
        //  description, priority, reporter, dateReported, resolution, dateResolved and status.
    
        // Let any SQLExceptions due to constraint violations be thrown from this method. The rest of the
        // code will interpret a SQLException as meaning that the data in the ticket
        // was not valid, for example the priority was -10 or the status was "CAT"

    }


    public Ticket getTicketById(int id) {
       
        // TODO query the database to find the ticket with this id.
        //  if the ticket is found, then create a Ticket object and return it
        //  if the ticket is not found, return null.

        // Catch any SQL errors and use System.err.print to print an error message. If an SQL exception is thrown, return null.

        return null; // TODO replace with your code.

    }
    
    
    public void updateTicket(Ticket ticket) {
        
        // TODO Use the Ticket's ID to modify the row in the database with this ID
        //  modify row in the database to set the values contained in the Ticket object
        //  this method can be used to resolve a ticket.
    
        // Catch any SQLException errors. Use System.err.print to print an error message if an exception if thrown.

    }
    
    
    public List<Ticket> searchByDescription(String description) {
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
