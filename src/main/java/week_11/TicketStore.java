package week_11;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * For add, edit, search operations on the Ticket database.
 */


public class TicketStore {
    
    private String dbURI;
    
    TicketStore(String databaseURI) {
        this.dbURI = databaseURI;
        
        /* TODO create the Ticket table in the database given by databaseURI.
             It should have these columns, types and constraints, in this order:
              rowid, an autogenerating ID  (remember SQLite can create this column and autogenerate the values for you)
              description, text, Can't be null.
              priority, a number in the range 1-5. Can't be null. Add constraints to prohibit values outside this range.
              dateReported, number, the long time value of a Date object, representing the date the ticket was created. Can't ben null.
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
        
        // Catch any SQL errors and print an error message. If an SQL exception is thrown, return null.
        
        return null;   // replace with your code.
    }
    
    
    /** Add ticket to the database. */
    public void add(Ticket newTicket) throws SQLException {
        
        // TODO insert a new row in the database for this ticket.
        //  Write the data from the fields in the newTicket object.
        //  Write all of the fields a Ticket could have:
        //  description, priority, reporter, dateReported, resolution, dateResolved and status.
    
        // Let any SQLExceptions be thrown from this method.
    
    }
    
 

    public Ticket getTicketById(int id) {
       
        // TODO query the database to find the ticket with this id.
        //  if the ticket is found, then create a Ticket object and return it
        //  if the ticket is not found, return null.
        
        return null; // TODO replace with your code.
    }
    
    
    public boolean updateTicket(Ticket ticket) {
        
        // TODO Use the Ticket's ID to modify the row in the database with this ID
        //  modify row in the database to set the values contained in the Ticket object
    
        // Return true if the ticket is found
        // Return false if the ticket is not found
        
        // Catch any SQLExceptions. Print an error message and return false.
        
        return false; // TODO replace with your code.
  
    }
    
    
    public List<Ticket> searchByDescription(String description) {
        // TODO search the database for all OPEN tickets that match the description
        //  If description is null, or a blank string, or empty string, return an empty list
        //  The search should be case-insensitive.
        //  The search should return partial matches.
        //  A search for "server" should return a ticket with description "The Windows Server is down"
    
        // Catch any SQLExceptions. If any exception is thrown, print an error message and return null.
        
        return null;  // TODO replace with your code.
    }

}
