# Lab 11: More GUI programs: Support Ticket Application

This program adds a GUI and database to the command line Support Ticket application from a previous lab.

This program is a prototype to manage IT support tickets for a company. Users will call or email a helpdesk to report computer problems. The technician will enter the information into this program, which will keep a record of all current problems. 

The tickets are assigned a priority between 1-5.   
Tickets need to store the priority, a description of the Task, the person who reported it, and a ticket ID number.

1 is the most urgent (e.g. all servers down); 5 is the least urgent (e.g. missing mouse mat). 

When a Ticket is resolved (fixed or become non-issue), the ticket's status is set to RESOLVED and the reason for resolving the ticket is recorded. 

Your Ticket objects should be able to store another date; `resolvedDate`, the date the ticket was closed.
And, a String that documents why the ticket was closed – the fix or the resolution for the ticket. This String should be called `resolution`

For this lab, you will implement a GUI for the program. 

### Task 1: 

Set up database. In the constructor of TicketStore, create the ticket table, if it does not exist. 
It should have these columns, types and constraints, in this order:

* description, text, Can't be null.
* priority, a number in the range 1-5. Can't be null. Add constraints to prohibit values outside this range.
* dateReported, number, the long time value of a Date object, representing the date the ticket was created. Can't ben null.
* resolution, text. May be null.
* dateResolved, number, the long time value of a Date object, representing the date the ticket was marked as resolved. May be null.
* status, text, either "OPEN" or "RESOLVED". These are the only acceptable values.

Do not set a primary key column.
SQLite will generate a rowid, the first column in the database, and it will be an autogenerating ID. 
You will use this as the ticket ID.
  

### Task 2: 

Review the Ticket class. It is very similar to the Ticket class in the previous version of this lab, except there's no Ticket ID counter. The database will handle creating unique IDs. 
You will not need to modify the Ticket class.  


### Task 3: TicketGUI, Set up priorityComboBox

In TicketGUI, configure `priorityComboBox` so it contains the choices 1, 2, 3, 4, 5.


### Task 4: Configure TicketGUI ticketList JList

In TicketGUI, configure `ticketList` so it will be able to display a list of Ticket objects. Use generic types. The Ticket objects should be shown in priority order, most urgent first. TicketStore should already be returning Ticket lists in the correct order.

The selection model should be `SINGLE_SELECTION`.

`ticketList` will be able to show any list of Tickets, for example, all the open Tickets, or only Tickets that match a search.


### Task 5: Get all the open tickets for ticketList JList

In TicketStore, finish the getAllOpenTickets method. This will return all tickets with status="OPEN", sorted in priority order.

In TicketGUI, use the controller object to request all the current open Tickets.
Configure the `ticketList` JList to display this list of open Ticket objects when the GUI first opens.

Add a listener to `showAllTicketsButton` to show all of the current open tickets in `ticketList`.  The user may click this after searching for tickets. 
 
 
### Task 6: Add Ticket

Finish the addTicket method in TicketStore.  This method should write a new row to the database for the new ticket.  The new ticket may have data that violates the database constraints and cause a SQLException to be thrown. Let this exception be thrown from the method. Code in TicketController checks for this exception. 

When the new ticket has been added, SQLite generates an ID number - the rowid column - for the new ticket. You need to update your Ticket object using it's `setTicketID` method to store the new ID.  

Implement the add ticket feature in TicketGUI. 

Add a listener to addButton which reads data from `descriptionTextField` and `reporterTextField` and `priorityComboBox`.  Ensure that all the data needed has been entered. If so, create a new Ticket and call `controller.addTicket(newTicket)` to request that the new ticket is added to the Ticket store. 

The ticketList JList should then update to show all the open Tickets, including the new Ticket. 

If data (no reporter entered, or no description entered, or no selection made from the priority JComboBox) is missing, display a message dialog to warn the user. Do not create a new Ticket. 

Use the `showMessageDialog` method in TicketGUI to show the message dialog. 
 
 
### Task 7: Search by ID

Finish the getTicketById method in TicketStore. If there is a row in the database with rowid equal to the id given, return a new Ticket created from that row.  If there is no matching row, return null.

Implement a listener for the `searchIdButton`. When this button is clicked, read the text in `idSearchTextBox`. Verify data has been entered, and that it is a positive integer. 

If there is no data or the data is invalid (not an integer, or a negative integer) the `ticketList` should show an empty list, and set the `ticketListStatusDescription` JLabel to the String `INVALID_TICKET_ID`

If the ID is a positive integer then use `controller` to search for the matching ticket.

If a ticket with this ID is found, display it in `ticketList` and set the `ticketListStatusDescription` to `TICKET_MATCHING_ID`

If a ticket with this ID is not found, `ticketList` should not show any Tickets and `ticketListStatusDescription` should be set to `NO_TICKETS_FOUND`

Do not show any message dialogs. 


### Task 8: Search by Description

Implement a listener for the `searchDescriptionButton`. When this button is clicked, read the text in `descriptionSearchTextBox`. Verify data has been entered.

If there is no search data, `ticketList` should be empty, and set the `ticketListStatusDescription` JLabel to the String `NO_TICKETS_FOUND`

If there is search data, then use `controller` to search for the matching tickets.

If a tickets containing the description are found (remember the search should not be case sensitive), display all the matching Tickets in `ticketList` and set `ticketListStatusDescription` to `TICKETS_MATCHING_DESCRIPTION`. The search method you wrote in the previous version of this lab should work for this lab too. 

If no tickets matching this description are found, `ticketList` should not show any Tickets and `ticketListStatusDescription` should be set to `NO_TICKETS_FOUND`

Do not show any message dialogs.
 

### Task 9: Resolve Ticket

Finish the updateTicket method in TicketStore. This will find the row in the database corresponding to this ticket, and update the columns to the data stored in the Ticket object. 

To resolve Tickets and remove them from the open ticket list, the user will select one Ticket in `ticketList` and click the `resolveSelectedButton` button.

Add a listener to `resolveSelectedButton` that checks if a Ticket is selected. Show a message dialog with an appropriate message if no Ticket is selected in `ticketList`.

If a Ticket is selected in `ticketList`, then use TicketGUI's `showInputDialog` method to show an JOptionPane input dialog. 

The user will be able to click the Cancel button, if they don't want to resolve the ticket. Note that `showInputDialog` returns null if the user clicks cancel, your code can check for this. In this case, you should not resolve the selected Ticket. The `ticketList` should not change.

Or, the user will type in a resolution String and click the OK button. Do the following tasks:

* Use the String entered to set the resolution of the ticket. 
* Set the date it was resolved to the current date/time 
* Set the status to TicketStatus.RESOLVED
* Call controller's `resolveTicket` method to remove this Ticket from the ticket store, and add it to the resolved tickets store. 
* Display a message dialog with a confirmation message. 
* The GUI should update `ticketList` so the resolved Ticket is no longer in the list. Display all the remaining open Tickets. 
* `ticketListStatusDescription` should show the String `ALL_TICKETS`. 


### Task 10: Save and Quit 

Add a listener to `saveAndQuitButton`. This should call the `controller.quitProgram()` to close the program.
