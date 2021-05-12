package week_11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;


public class TicketGUI extends JFrame {
    
    // TODO complete the tasks described in Lab 11 Questions.md

    // You don't need to modify the form or the GUI design in TicketGUI.form
    
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

    // TODO initialize this DefaultListModel in the constructor
    //  Use this DefaultListModel for the JList ticketList
    protected DefaultListModel<Ticket> ticketListModel;

    // Strings for messages that will be shown in ticketListStatusDescription
    // TODO Use these instead of your own Strings. The tests expect you to use these constants.
    static final String ALL_TICKETS = "Showing all open tickets";
    static final String TICKETS_MATCHING_SEARCH_DESCRIPTION = "Open tickets matching search description";
    static final String TICKET_MATCHING_ID = "Ticket matching ID";
    static final String NO_TICKETS_FOUND = "No matching tickets";
    static final String INVALID_TICKET_ID = "Invalid ticket ID";
    
    
    // A reference to the TicketController object
    // This GUI will be able to call the methods in this class to add, search for, and update tickets.
    // See example in quitProgram method.
    private TicketController controller;
    
    
    TicketGUI(TicketController controller) {


        this.controller = controller;

        /* In your code, when you need to send
        a message to the TicketProgram controller, use this controller object. So if you need
        to add a new ticket, you'll create a new Ticket object, then ask the TicketProgram controller
        to add the new Ticket to the database with
        controller.newTicket(myNewTicket);  */

        // GUI window setup and configuration
        setTitle("Support Ticket Manager");
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(700, 600));
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        for(int x = 1 ; x <= 5; x++){
            priorityComboBox.addItem(x);
        }
        ticketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // TODO configure JComboBox
        ticketListModel = new DefaultListModel<>();
        ticketList.setModel(ticketListModel);

        // TODO Configure JList, JList model

        // TODO add action listeners for each button

        // TODO show all of the open tickets in priority order in the JList
        addlistner();

    }
    private void addlistner(){
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(descriptionTextField.getText().isEmpty()){
                    showMessageDialog("enter your description");
                }
                else if (reporterTextField.getText().isEmpty()) {
                showMessageDialog("enter your reporter name");}
                else if(priorityComboBox.getSelectedItem()== null){
                    showMessageDialog("select ticket priority");
                }
                else{
                    String description = descriptionTextField.getText();
                    String reporter = reporterTextField.getText();
                    Date date = new Date();
                    int priority = (Integer) priorityComboBox.getSelectedItem();
                    Ticket newticket = new Ticket(description,priority,reporter,date);
                    boolean ticketadded= controller.addTicket(newticket);
                    ticketListModel.addElement(newticket);
                    if (!ticketadded){
                        showMessageDialog("unable to add ticket!");
                    }
                    descriptionTextField.setText("");
                    reporterTextField.setText("");
                    priorityComboBox.setSelectedIndex(1);

                }
            }

        });
        searchIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(idSearchTextBox.getText().isEmpty()){
                    ticketListModel.clear();
                    ticketListStatusDescription.setText(INVALID_TICKET_ID);
                }
                int ticketid;
                try{
                    ticketid = Integer.parseInt(idSearchTextBox.getText());
                }
                catch (NumberFormatException ne){
                    ticketListModel.clear();
                    ticketListStatusDescription.setText(INVALID_TICKET_ID);
                    return;
                }
                if(ticketid <= 0){
                    ticketListStatusDescription.setText(INVALID_TICKET_ID);
                    ticketListModel.clear();
                }
                else{
                    Ticket matchid = controller.searchById(ticketid);
                    if(matchid == null){
                        ticketListModel.clear();
                        ticketListStatusDescription.setText(NO_TICKETS_FOUND);
                    }
                    else{
                        ticketListModel.clear();
                        ticketListModel.addElement(matchid);
                        ticketListStatusDescription.setText(TICKET_MATCHING_ID);
                    }
                }
            }
        });
        searchDescriptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchtext = descriptionSearchTextBox.getText();
                if(searchtext.isEmpty()){
                    ticketListModel.clear();
                    ticketListStatusDescription.setText(NO_TICKETS_FOUND);
                }
                else {
                    List<Ticket> foundmatches = controller.searchByDescription(searchtext);
                    if(foundmatches == null || foundmatches.isEmpty()){
                        ticketListModel.clear();
                        ticketListStatusDescription.setText(NO_TICKETS_FOUND);


                    }
                    else{
                        ticketListModel.clear();
                        for(Ticket ticket: foundmatches){
                            ticketListModel.addElement(ticket);
                        }
                        ticketListStatusDescription.setText(TICKETS_MATCHING_SEARCH_DESCRIPTION);
                    }
                }
            }
        });
    }

    // Call this method to quit the program.
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


