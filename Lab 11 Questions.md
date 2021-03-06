# Lab 11: More GUI programs: Support Ticket Application

This program adds a GUI and database to the command line Support Ticket application from a previous lab.

This program is a prototype to manage IT support tickets for a company. Users will call or email a helpdesk to report computer problems. The technician will enter the information into this program, which will keep a record of all current problems. 

Each ticket is assigned a priority between 1-5.   

New tickets need to store 
- the ticket priority, 
- a description of the ticket, 
- the person who reported it, 
- a ticket ID number,
- and the status of the ticket - OPEN or RESOLVED, so for a new ticket the status will be OPEN.

1 is the most urgent (e.g. all servers down); 5 is the least urgent (e.g. missing mouse mat). 

When a Ticket is resolved (fixed or becomes a non-issue), the ticket's status is set to RESOLVED, the reason for resolving the ticket is recorded, and the resolvedDate is stored. 

The resolution, and the data resolved also need to be stored. 

For this lab, you will implement a GUI and database for the program. 

### Task 1: 

For all database interactions in TicketStore, use the dbURI variable that is created and initialized for you in the constructor.

Set up your database. In the constructor of TicketStore, create a ticket table, if it does not exist. 

The table will have the name **ticket**.

It should have these columns, types and constraints, in this order:

* id, an integer number, the primary key of the table. SQLite should autogenerate the values for this column.
* description, text, Can't be null.
* priority, an integer number in the range 1-5. Can't be null. Add constraints to prohibit values outside this range.
* reporter, text, can't be null.
* dateReported, an integer number, the long time value of a Date object, representing the date the ticket was created. Can't be null.
* resolution, text. May be null.
* dateResolved, an integer number, the long time value of a Date object, representing the date the ticket was marked as resolved. May be null.  
* status, text, either "OPEN" or "RESOLVED". These are the only acceptable values.

You will use the autogenerated ID values as the ticket ID.
  
SQLite integer values can store larger numbers than Java integer values, so you will be able to store a Java long value - for example, the timestamp of a Date object - in a SQLite integer column.   


### Task 2: New Ticket class Constructor 

Review the Ticket class. It is very similar to the Ticket class in the previous version of this lab, except there's no Ticket ID counter. The database will handle creating unique IDs. 

Create a second constructor in the Ticket class. This will be used to create Ticket objects from database rows. This constructor should set all the possible fields a Ticket can have. 


### Task 3: TicketGUI, Set up priorityComboBox

In TicketGUI, configure `priorityComboBox` so it contains the integer number choices 1, 2, 3, 4, 5.


### Task 4: Configure TicketGUI ticketList JList

In TicketGUI, configure `ticketList` so it will be able to display a list of Ticket objects. Use generic types. The Ticket objects should be shown in priority order, most urgent (priority=1) first. You will create a TicketStore method that returns a List of Ticket objects in the correct order.

The selection model should be `SINGLE_SELECTION`.

`ticketList` will be able to show any list of Tickets, for example, all the open Tickets, or only Tickets that match a search.

 
### Task 5: Add a new Ticket

Finish the addTicket method in TicketStore.  This method should write a new row to the database for the new Ticket. 
 
Even though this method will only be used for adding new, open tickets, you should write this method so that it would work to add a new resolved ticket to the database.
 
The new Ticket may have data that violates the database constraints and cause a SQLException to be thrown. You should use try-with-resources to ensure database resources are closed, so your code will have try and catch blocks. If a SQLException is thrown, catch it as usual, and then throw the exception again. Code in TicketController and the tests check for SQLException. 

To throw an exception from a catch block, use the `throw` keyword, for example, 

```
try ( /* connect to database, prepare statement */ ) {
   // database code here
} catch (SQLException e) {
  System.out.println("Error adding ticket because " + e);  // optional, to help debugging
  throw e;
}
```

A new Ticket does not have a resolution, or a resolved date.  You can set a null value for a String column, but you will need to use a special method to set a long value to null. The long type is a primitive type and can't be null in Java, even though a number/integer column in SQLite may contain a null value.

```
// Example code - set the column numbers to match your database 

// this works even if the ticket's resolution is null
preparedStatement.setString(1, ticket.getResolution()); 

// this will error if ticket's resolved date is null, the call to getTime() will throw 
// a NullPointerException
preparedStatement.setLong(1, ticket.getResolvedDate().getTime());

// So you'll need to check if the resolved date is null, and if so, set a null value for that column
preparedStatement.setNull(1, Types.INTEGER);   // Use the correct column number for your DB. In SQLite, Integer and Long are the same thing. 
```


When the new ticket has been added, SQLite generates an ID number - the id column - for the new ticket. You need to update your Ticket object to store this new ID. You can use Ticket objects' `setTicketID` method to store the new ID.  

Here is some example code that fetches the primary key value generated from an SQL insert statement,

```
// create a prepared statement to insert data
// set placeholders
preparedStatement.executeUpdate();   // execute statement, database will be updated
ResultSet keys = preparedStatement.getGeneratedKeys();  // ask the database for generated primary key values
keys.next();   // move to first row of the result set
int id = keys.getInt(1);   // get value from first column 

// use the ticket object's setTicketID(id); method to set the ticket's ID value.
```

Note that several tests will fail if the Ticket object's ID is not set. 

Implement the add ticket feature in TicketGUI. 

Add a listener to `addButton` which reads data from `descriptionTextField` and `reporterTextField` and `priorityComboBox`.  Ensure that all the data needed has been entered. If so, create a new Ticket and call `controller.addTicket(newTicket)` to request that the new ticket is added to the Ticket store. 

The ticketList JList should then update to show all the open Tickets, including the new Ticket. 

If data is missing (no reporter entered, or no description entered, or no selection made from the priority JComboBox), display a message dialog to warn the user. Do not create a new Ticket. 

Use the `showMessageDialog` method in TicketGUI to show the message dialog. 
 
 
### Task 6: Get all the open tickets for ticketList JList

In TicketStore, finish the `getAllOpenTickets` method. This will return all tickets with status="OPEN", sorted in priority order.  
Tickets with priority=1 will be first, tickets with priority=5 will be last. 

In TicketGUI, use the controller object's `loadAllOpenTicketsFromStore` method to request all the current open Tickets.
Configure the `ticketList` JList to display this list of open Ticket objects when the GUI first opens.

Add a listener to `showAllTicketsButton` to show all the current open tickets in `ticketList`.  The user may click this after searching for tickets. 


### Task 7: Search by ID

Finish the getTicketById method in TicketStore. If there is a row in the database with id equal to the id given, return a new Ticket created from that row.  If there is no matching row, return null.

This method should return the matching ticket, regardless of whether the ticket is OPEN or RESOLVED. 

Implement a listener for the `searchIdButton`. When this button is clicked, read the text in `idSearchTextBox`. Verify data has been entered, and that it is a positive integer. 

If there is no data, or the data is invalid (not an integer, or a negative integer) the `ticketList` should show an empty list, and set the `ticketListStatusDescription` JLabel to the String `INVALID_TICKET_ID`

If the ID is a positive integer then use `controller` to search for the matching ticket.

If a ticket with this ID is found, display it in `ticketList` and set the `ticketListStatusDescription` to `TICKET_MATCHING_ID`

If no ticket with this ID is found, `ticketList` should not show any Tickets and `ticketListStatusDescription` should be set to `NO_TICKETS_FOUND`

Do **not** show any message dialogs. 


### Task 8: Search by Description

This feature will search for all tickets with a search term in their description. This feature will find both open and resolved tickets.  The tickets should be sorted in priority order.

Implement a listener for the `searchDescriptionButton`. When this button is clicked, read the text in `descriptionSearchTextBox`. Verify data has been entered.

If there is no search data (the text in searchDescriptionButton is empty), `ticketList` should be empty, and set the `ticketListStatusDescription` JLabel to the String `NO_TICKETS_FOUND`

If there is search data, then use `controller` to search for the matching tickets. Your program will find all open and resolved tickets. 

You will need to create a method in TicketStore that will query the database and return all matching open and resolved tickets, ordered by priority. (Remember priority=1 first, priority=5 last.)

If any tickets (open or resolved) containing the description are found (remember the search should not be case sensitive), display all the matching Tickets in `ticketList` and set `ticketListStatusDescription` to `TICKETS_MATCHING_DESCRIPTION`.  

If no tickets matching this description are found, `ticketList` should not show any Tickets and `ticketListStatusDescription` should be set to `NO_TICKETS_FOUND`

Do not show any message dialogs.
 

### Task 9: Resolving a Ticket

Finish the `updateTicket` method in TicketStore. This will find the row in the database corresponding to this ticket, and update the columns with the data stored in the Ticket object.  This method will set all the columns in the database with data from the Ticket object, except for the id field. 

To resolve Tickets and remove them from the open ticket list, the user will select one Ticket in `ticketList` and click the `resolveSelectedButton` JButton.

Add a listener to `resolveSelectedButton` that checks if a Ticket is selected. Show a message dialog with an appropriate message if no Ticket is selected in `ticketList`.

If the user selects a Ticket that is already resolved, show a message dialog with the message "This ticket is already resolved". Do not modify the ticket. Do not do any further processing. The ticketList should not change.

If an open Ticket is selected in `ticketList`, then use TicketGUI's `showInputDialog` method to show an JOptionPane input dialog. 

The user will be able to click the Cancel button, if they don't want to resolve the ticket. Note that `showInputDialog` returns null if the user clicks cancel, your code can check for this. In this case, you should not resolve the selected Ticket. The `ticketList` should not change.

Or, the user will type in a resolution String and click the OK button. Do the following tasks:

* Use the String entered to set the resolution of the ticket. 
* Set the date it was resolved to the current Date 
* Set the status to TicketStatus.RESOLVED
* Call controller's `updateTicket` method to modify this Ticket's entry in the database. 
* Display a message dialog with a confirmation message. 
* The GUI should update `ticketList` so the resolved Ticket is no longer in the list. Display all the remaining open Tickets. 
* `ticketListStatusDescription` should show the String `ALL_TICKETS`. 


### Task 10: Save and Quit 

Add a listener to `saveAndQuitButton`. This should call the `controller.quitProgram()` method to close the program.

Do not display any dialogs. 