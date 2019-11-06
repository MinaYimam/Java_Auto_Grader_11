package week_11;

import javax.swing.*;


public class TicketGUI extends JFrame {
    
    // TODO complete the tasks described in Lab 11 Questions.md
    
    protected JPanel mainPanel;
    
    // Components for adding tickets
    protected JPanel addTicketPanel;
    protected JTextField descriptionTextField;
    protected JTextField reporterTextField;
    protected JComboBox priorityComboBox;
    protected JButton addButton;
    
    // Components for displaying ticket list
    protected JPanel ticketListPanel;
    protected JList<Ticket> ticketList;
    protected JLabel ticketListStatusDescription;
    
    // Components for searching
    protected JPanel searchPanel;
    protected JTextField descriptionSearchTextBox;
    protected JTextField idSearchTextBox;
    protected JButton searchDescriptionButton;
    protected JButton searchIdButton;
    protected JButton showAllOpenTicketsButton;
    
    // Quit button
    protected JPanel controlsPanel;
    protected JButton quitButton;
    
    // Resolving
    protected JButton resolveSelectedButton;
    
    protected DefaultListModel<Ticket> ticketListModel;
    
    // Messages for showing in ticketListStatusDescription
    // TODO Use these instead of your own Strings. The tests expect you to use these constants
    static final String ALL_TICKETS = "Showing all open tickets";
    static final String OPEN_TICKETS_MATCHING_SEARCH_DESCRIPTION = "Open tickets matching search description";
    static final String TICKET_MATCHING_ID = "Ticket matching ID";
    static final String NO_TICKETS_FOUND = "No matching tickets";
    static final String INVALID_TICKET_ID = "Invalid ticket ID";
    
    
    // A reference to the TicketProgram object
    // The GUI will call the methods in this class to add, search for, and resolve tickets.
    // See example in quitProgram method.
    private TicketController controller;
    
    
    TicketGUI(TicketController controller) {

        /* In the rest of your code, when you need to send
        a message to the TicketProgram controller, use this controller object. So if you need
        to add a new ticket, you'll create a new Ticket object, then ask the TicketProgram controller
        to add the new Ticket to the database with
        controller.newTicket(myNewTicket);  */

        this.controller = controller;
        
        setTitle("Support Ticket Manager");
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // TODO configure JComboBox

        // TODO Configure JList, JList model
        
        // TODO show all of the open tickets in the JList
        
        // TODO add action listeners for each button
    }
    
    
    
    // Call this method to quit the program. The tests expect you to use it.
    // You do not need to modify this method.
    protected void quitProgram() {
        controller.quitProgram();    // Ask the controller to quit the program.
    }
    
    // Use this method to show message dialogs displaying the message given.
    // Otherwise tests for code that shows alert dialogs will time out and fail.
    // Don't modify this method.
    protected void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    // Use this method to show input dialogs asking the given question.
    // Otherwise tests for code that shows input dialogs will time out and fail.
    // If user presses the cancel button, this method will return null.
    // Don't modify this method.
    protected String showInputDialog(String question) {
        return JOptionPane.showInputDialog(this, question);
    }
    
}


