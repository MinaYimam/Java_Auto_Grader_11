package week_11;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static week_11.Configuration.timeout;
import static week_11.TicketUtil.sameTicket;


public class TestTicketUI {
    
    // Some example test data, created before tests run, and available to all test methods
    private Ticket test_added_first, test_added_second, test_added_third;
    private List<Ticket> testTickets = new LinkedList<>();
    
    @Before
    public void clearDatabase() {
        TicketUtil.clearStore();
    }

    
    private void insertTestTickets() throws Exception {
        test_added_first = new Ticket("Server keeps rebooting", 1, "user 1", new Date());
        test_added_second = new Ticket("Mouse stolen", 5, "user 2", new Date());
        test_added_third = new Ticket("Mouse mat stolen", 3, "user 2", new Date());

        testTickets = new LinkedList<>();

        // These tickets will be expected to be stored in the database, and returned when ordered by priority,
        // in the order test_added_first, test_added_third, test_added_second
        testTickets.add(test_added_first);
        testTickets.add(test_added_third);
        testTickets.add(test_added_second);

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        store.add(test_added_first);
        store.add(test_added_second);
        store.add(test_added_third);
    }


    /* *************** Test TicketUI Methods **********************/



    // TASK 3 Create Constructor for Ticket objects

    @Test(timeout = timeout)
    public void testTicketConstructor() throws Exception{

        Class ticketClass = Class.forName("week_11.Ticket");
        Constructor[] ticketConstructors = ticketClass.getConstructors();

        assertEquals("Create a second constructor in the Ticket class. Do not delete the existing constructor",
                2, ticketConstructors.length);

        Constructor c1 = ticketConstructors[0];
        Constructor c2 = ticketConstructors[1];

        Constructor newTicket, ticketFromDB;

        int p1 = c1.getParameterCount();

        if (p1 == 4) {
            newTicket = c1;
            ticketFromDB = c2;
        } else {
            newTicket = c2;
            ticketFromDB = c1;
        }

        assertEquals("Don't modify the existing constructor in the Ticket Class", 4, newTicket.getParameterCount());
        assertEquals("The new constructor should have 8 parameters", 8, ticketFromDB.getParameterCount());

        Class[] parameters = ticketFromDB.getParameterTypes();

        // Expected parameters, although they need not be in this order
        Class[] expectedParameters = {
                String.class,    // description
                int.class,      // priority
                String.class,   // reporter
                Date.class,   // created date
                int.class,    // id
                Date.class,   // resolution date
                String.class,   // resolution
                Ticket.TicketStatus.class // status
        };

        Object[] parameterNames = Arrays.stream(parameters).map(Class::toString).toArray();
        Object[] expectedParameterNames = Arrays.stream(expectedParameters).map(Class::toString).toArray();
        Arrays.sort(parameterNames);
        Arrays.sort(expectedParameterNames);

        assertArrayEquals(parameterNames, expectedParameterNames);
    }


    // TASK 3 Configure ComboBox

    @Test(timeout=timeout)
    public void testPriorityComboBoxConfigured() {

        TicketController controller = new TicketController(new TicketStore(Configuration.TEST_DB_URI));
        TicketGUI gui = new TicketGUI(controller);

        JComboBox priorityCombo = gui.priorityComboBox;

        //priorityComboBox should have 1-5 in

        assertEquals("The priority combo box should have 5 items in ", 5, priorityCombo.getItemCount());

        for (int x = 0 ; x < 5 ; x++) {
            try {
                Integer comboItem = (Integer) priorityCombo.getModel().getElementAt(x);
                assertEquals("Combo box should contain the integers 1-5 in that order. Found " + comboItem + " at position " + x,
                        (int) comboItem, x + 1);

            } catch (ClassCastException cce) {
                fail("Priority JComboBox should contain the integers 1-5, not Strings, or other data");
            }
        }
    }

    // TASK 4 Configure JList

    @Test(timeout=timeout)
    public void testTicketListConfigured() {

        // Check JList set to single selection
        // Model uses Ticket objects

        TicketController controller = new TicketController(new TicketStore(Configuration.TEST_DB_URI));
        TicketGUI gui = new TicketGUI(controller);

        JList ticketList = gui.ticketList;

        try {
            DefaultListModel model = (DefaultListModel) ticketList.getModel();
        } catch (ClassCastException cce) {
            fail("Create a DefaultListModel<Ticket> and use it as the ticketList data model. It should only store Ticket objects");
        }

        // Should be single selection mode
        int listSelectionMode = ticketList.getSelectionMode();

        assertEquals("ticketList should use the single selection mode, to only select one ticket at a time.",
                listSelectionMode, ListSelectionModel.SINGLE_SELECTION);

    }

    // TASK 3 Get all Data

    @Test(timeout=timeout)
    public void testTicketListGetAllData() throws Exception {

        // shows example ticket list in correct order, priority 1 first, priority 5 last

        insertTestTickets();
        
        TicketController controller = new TicketController(new TicketStore(Configuration.TEST_DB_URI));
        TicketGUI gui = new TicketGUI(controller);
        
        JList ticketList = gui.ticketList;

        // Should be three tickets in list, in the given order
        assertEquals("With a test TicketList of 3 tickets, there should be 3 tickets in your JList. Test list was " + testTickets,
                3, ticketList.getModel().getSize());

        TicketUtil.expectedListSameAsJList(testTickets, ticketList);
    }

    
    // TASK 4 Add Ticket

    @Test(timeout=timeout)
    public void testAddTicketToEmptyListValidData() {

        String msg = "Ticket added to the list should have the same data as that entered into GUI.";

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUI gui = new TicketGUI(controller);

        // Example data for ticket
        String description = "Server needs updating", reporter = "User";
        int priority = 3;

        Ticket expected1 = new Ticket(description, priority, reporter, new Date());

        // enter data
        gui.descriptionTextField.setText(description);
        gui.reporterTextField.setText(reporter);
        gui.priorityComboBox.setSelectedItem(priority);

        // click add button
        gui.addButton.doClick();

        // Now ticket should be the first and only element in JList
        assertEquals("After adding 1 ticket, there should be one ticket in the JList", 1, gui.ticketList.getModel().getSize() );

        Ticket actualTicket = gui.ticketList.getModel().getElementAt(0);

        assertTrue(msg, sameTicket(actualTicket, expected1, 1000, true));
        assertEquals("Added one ticket, should be 1 in data store", 1, store.getAllOpenTickets().size());
        
        
        // Example data for another ticket
        description = "Server on fire"; reporter = "Another User";
        priority = 1;  // very urgent

        Ticket expected2 = new Ticket(description, priority, reporter, new Date());

        // enter data
        gui.descriptionTextField.setText(description);
        gui.reporterTextField.setText(reporter);
        gui.priorityComboBox.setSelectedItem(priority);

        // click add button
        gui.addButton.doClick();

        assertEquals("After adding a ticket, then another ticket, there should be two tickets in the JList", 2, gui.ticketList.getModel().getSize() );

        Ticket actualTicket2 = gui.ticketList.getModel().getElementAt(0);   // This one is more urgent so should be at the top
        Ticket actualTicket1 = gui.ticketList.getModel().getElementAt(1);

        System.out.println("Created another ticket. Expected ticket =" + expected2);
        System.out.println("Actual ticket created by the program =" + actualTicket2);

        assertTrue(msg, sameTicket(actualTicket1, expected1, 4000, true));
        assertTrue(msg, sameTicket(actualTicket2, expected2, 4000, true));

        assertEquals("Added two tickets, should be 2 in data store", 2, store.getAllOpenTickets().size());
        
    }


    // TASK 4 Add Ticket to list with data in

    @Test(timeout=timeout)
    public void testAddTicketAlreadyPopulatedListValidData() throws Exception {

        insertTestTickets();

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUI gui = new TicketGUI(controller);
        
        int originalSize = store.getAllOpenTickets().size();

        // Example data for new ticket
        String description = "Server needs updating", reporter = "User";
        int priority = 4;

        Ticket expected = new Ticket(description, priority, reporter , new Date());

        // enter data
        gui.descriptionTextField.setText(description);
        gui.reporterTextField.setText(reporter);
        gui.priorityComboBox.setSelectedItem(priority);

        // click add button
        gui.addButton.doClick();

        // New ticket added to list at position 2
        assertEquals("After adding 1 ticket to a list with 3 tickets in, there should be 4 tickets in the JList",
                4, gui.ticketList.getModel().getSize());

        Ticket actualTicket = gui.ticketList.getModel().getElementAt(2);    // New ticket should be at position 2
        
        assertTrue("A new ticket with priority 4 to the test list should be added at position 2",
                sameTicket(actualTicket, expected, 2000, true));
        
        assertEquals("A new ticket should be added to the data store. There should be one more ticket in the database.",
                originalSize + 1, store.getAllOpenTickets().size());

    }

    // TASK 4 Add Ticket, invalid data

    @Test(timeout=timeout)
    public void testAddTicketInvalidData() {

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);

        TicketGUIMockDialog gui = new TicketGUIMockDialog(controller);  // Replace GUI with mock dialog version

        // Example combinations of invalid data for new ticket. Asserts in assertFailAddInvalidTicket
        assertFailAddInvalidTicket(gui, "", "", null);
        assertFailAddInvalidTicket(gui, "Mouse mat", "", null);
        assertFailAddInvalidTicket(gui, "", "The User", null);
        assertFailAddInvalidTicket(gui, "", "", 3);
        assertFailAddInvalidTicket(gui, "Mouse mat", "User", null);
        assertFailAddInvalidTicket(gui, "", "User", 2);
        assertFailAddInvalidTicket(gui, "Mouse", "", 3);
    }

    
    // Checks various things about not adding invalid tickets.
    private void assertFailAddInvalidTicket(TicketGUIMockDialog gui, String description, String reporter, Integer comboboxSelection) {

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        
        gui.descriptionTextField.setText(description);
        gui.reporterTextField.setText(reporter);
        gui.priorityComboBox.setSelectedItem(comboboxSelection);

        gui.addButton.doClick();

        // should be message dialog shown
        assertTrue(String.format("Show a message dialog if the new ticket data if description= '%s' reporter= '%s' priority= %s", description, reporter, comboboxSelection),
                gui.checkMessageWasCalled());

        // Nothing in JList or ticket Store
        assertEquals("After attempting to add invalid ticket, there should be no tickets in the ticketList",
                0, gui.ticketList.getModel().getSize());

        assertEquals("If invalid data is entered, no ticket should be added to the ticketStore",
                0, store.getAllOpenTickets().size());
    }


    //TASK 5 search by id

    @Test(timeout=timeout)
    public void testSearchById() throws Exception {
    
        insertTestTickets();
    
        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUI gui = new TicketGUI(controller);

        JList ticketList = gui.ticketList;

        // Search for ticket_added_second
        gui.idSearchTextBox.setText(test_added_second.getTicketID() + "");
        gui.searchIdButton.doClick();

        // List should only show one ticket, ID of ticket_added_second

        assertEquals("Checking size of ticket list. When a ticket is found with the given ID, show 1 ticket in the list: the ticket with matching ID",
                1, ticketList.getModel().getSize());

        Ticket expected = (Ticket) ticketList.getModel().getElementAt(0);

        assertTrue("After searching for ticket ID that exists, only that matching ticket should be" +
                " shown in the JList", TicketUtil.sameTicket(test_added_second, expected));

        // Update search status
        assertEquals("After ticket found by ID, update the ticketListStatusDescription JLabel. Use the TicketGUI TICKET_MATCHING_ID constant.",
                TicketGUI.TICKET_MATCHING_ID, gui.ticketListStatusDescription.getText());


        // Search for ticket 3, should now only show ticket 3 in the list

        gui.idSearchTextBox.setText("3");
        gui.searchIdButton.doClick();

        // List should now only show ticket 3

        assertEquals("When a ticket is found with the given ID, show only that ticket in ticketList", 1, ticketList.getModel().getSize());

        expected = (Ticket) ticketList.getModel().getElementAt(0);

        assertTrue("After searching for ticket ID that exists, only that matching ticket should be" +
                " shown in the JList", TicketUtil.sameTicket(test_added_third, expected));

        assertEquals("After ticket found by ID, update the ticketListStatusDescription JLabel. Use the TicketGUI TICKET_MATCHING_ID constant.",
                TicketGUI.TICKET_MATCHING_ID, gui.ticketListStatusDescription.getText());


        // Search for ticket id = 400000000, (probably) does not exist in list

        gui.idSearchTextBox.setText("400000000");
        gui.searchIdButton.doClick();

        // List should be empty
        assertEquals("After searching for ticket ID that does not exist, the JList should be empty",
                0, ticketList.getModel().getSize() );

        assertEquals("After searching for ID that doesn't exist, update the ticketListStatusDescription JLabel to " + TicketGUI.NO_TICKETS_FOUND + ". Use the TicketGUI.NO_TICKETS_FOUND String.",
                TicketGUI.NO_TICKETS_FOUND, gui.ticketListStatusDescription.getText());
        
        
        // Click show all
        gui.showAllOpenTicketsButton.doClick();

        assertEquals("Update the ticketListStatusDescription JLabel to " + TicketGUI.ALL_TICKETS + ". Use TicketGUI.ALL_TICKETS String.",
                TicketGUI.ALL_TICKETS, gui.ticketListStatusDescription.getText());

        assertEquals("When show all tickets is clicked, show all tickets in the JList", 3, ticketList.getModel().getSize());

    }
    
    @Test(timeout=timeout)
    public void testSearchByIdInvalidIds() throws Exception {
        
        insertTestTickets();
        
        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUI gui = new TicketGUI(controller);
        
        JList ticketList = gui.ticketList;
        
        // Searches for things that are not valid IDs
        invalidIDSearch(gui, "");
        invalidIDSearch(gui, "Pizza");
        invalidIDSearch(gui, "-2");    // negative integers are not valid
        
        // Click show all
        gui.showAllOpenTicketsButton.doClick();
    
        assertEquals("Update the ticketListStatusDescription JLabel to " + TicketGUI.ALL_TICKETS + ". Use TicketGUI.ALL_TICKETS String.",
                TicketGUI.ALL_TICKETS, gui.ticketListStatusDescription.getText());
    
        assertEquals("When show all tickets is clicked, show all tickets in the JList", 3, ticketList.getModel().getSize());
    
    }

    private void invalidIDSearch(TicketGUI gui, String searchText) {

        gui.idSearchTextBox.setText(searchText);
        gui.searchIdButton.doClick();
        // List should be empty, invalid message shown
        assertEquals("After searching for ticket ID \"" + searchText + "\" the JList should be empty",
                0, gui.ticketList.getModel().getSize() );

        assertEquals("After searching for invalid ID, update the ticketListStatusDescription JLabel to " + TicketGUI.INVALID_TICKET_ID + ". Use the TicketGUI.INVALID_TICKET_ID String constant.",
                TicketGUI.INVALID_TICKET_ID, gui.ticketListStatusDescription.getText());
    }


    //TASK 6 Search by description

    @Test(timeout=timeout)
    public void testSearchByDescription() throws Exception{

        insertTestTickets();

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUI gui = new TicketGUI(controller);
        
        JList ticketList = gui.ticketList;

        // Search for ticket text "mouse". Should return two tickets
        gui.descriptionSearchTextBox.setText("mouse");
        gui.searchDescriptionButton.doClick();

        // List should show two tickets, in this order. Priority 1 is first, priority 5 is last
        //        test_added_third = new Ticket("Mouse mat stolen", 3, "user 2", new Date());  // has id 3
        //        test_added_second = new Ticket("Mouse stolen", 5, "user 2", new Date());     // has id 2

        try {
            Ticket expectedId3 = (Ticket) ticketList.getModel().getElementAt(0);
            Ticket expectedId2 = (Ticket) ticketList.getModel().getElementAt(1);

            assertTrue("After searching for 'mouse', all matching tickets should be" +
                    " shown in the JList", TicketUtil.sameTicket(test_added_second, expectedId2));
            assertTrue("After searching for 'mouse', all matching tickets should be" +
                    " shown in the JList", TicketUtil.sameTicket(test_added_third, expectedId3));
        } catch(IndexOutOfBoundsException e) {
            fail("Create a data model for your JList. All matching tickets should be shown in JList after the search.");
        }
        
        // Verify search status message is shown

        assertEquals("After ticket found by ID, update the ticketListStatusDescription JLabel. Use the TicketGUI.OPEN_TICKETS_MATCHING_SEARCH_DESCRIPTION String.",
                TicketGUI.TICKETS_MATCHING_SEARCH_DESCRIPTION, gui.ticketListStatusDescription.getText());

        // Search for ticket "Server keeps rebooting", should now only show ticket with id=2 in the list

        gui.descriptionSearchTextBox.setText("Server keeps rebooting");
        gui.searchDescriptionButton.doClick();

        // List should now only show this ticket
        //   test_added_first = new Ticket("Server keeps rebooting", 1, "user 1", new Date());

        Ticket expected2 = (Ticket) ticketList.getModel().getElementAt(0);

        assertTrue("After searching for ticket 'Server keeps rebooting' that exists, only that matching ticket should be" +
                " shown in the JList", TicketUtil.sameTicket(test_added_first, expected2));

        assertEquals("After ticket found by ID, update the ticketListStatusDescription JLabel. Use the TicketGUI.OPEN_TICKETS_MATCHING_SEARCH_DESCRIPTION String.",
                TicketGUI.TICKETS_MATCHING_SEARCH_DESCRIPTION, gui.ticketListStatusDescription.getText());

        // Search for ticket text = "Powerpoint", does not exist in list

        gui.descriptionSearchTextBox.setText("Powerpoint");
        gui.searchDescriptionButton.doClick();

        // List should be empty, no ticket found message shown
        assertEquals("After searching for ticket description \"Powerpoint\" the JList should be empty",
                0, ticketList.getModel().getSize() );
        assertEquals("After searching for description that does not match any tickets, update the ticketListStatusDescription JLabel to " + TicketGUI.NO_TICKETS_FOUND,
                TicketGUI.NO_TICKETS_FOUND, gui.ticketListStatusDescription.getText());


        // Search for empty string, should return no tickets
        gui.descriptionSearchTextBox.setText("");
        gui.searchIdButton.doClick();

        // List should be empty, no ticket found message shown
        assertEquals("After searching for ticket description with an empty string, the JList should be empty",
                0, ticketList.getModel().getSize() );
        assertEquals("After searching for ticket description with an empty string, " + TicketGUI.NO_TICKETS_FOUND,
                TicketGUI.INVALID_TICKET_ID, gui.ticketListStatusDescription.getText());


        // Click show all

        gui.showAllOpenTicketsButton.doClick();

        assertEquals("Update the ticketListStatusDescription JLabel to " + TicketGUI.ALL_TICKETS,
                TicketGUI.ALL_TICKETS, gui.ticketListStatusDescription.getText());

        assertEquals("When show all tickets button is clicked, show all tickets in the JList", 3, ticketList.getModel().getSize());

    }


    // TASK 7 Resolve selected, no ticket selected

    @Test(timeout=timeout)
    public void testResolveSelectedButNoneSelected() throws Exception {

        insertTestTickets();

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);

        TicketGUIMockDialog gui = new TicketGUIMockDialog(controller);

        gui.ticketList.clearSelection();   // Unselect everything

        gui.resolveSelectedButton.doClick();

        // expect a message dialog to be shown
        assertTrue("Show a message dialog if the user selects a resolved " +
                "ticket and clicks the Resolve Selected button", gui.wasMessageCalled);

    }

    // TASK 7 Resolve selected, user cancel

    @Test(timeout=timeout)
    public void testResolveSelectedUserCancel() throws Exception {

        insertTestTickets();

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUIMockDialog gui = new TicketGUIMockDialog(controller);

        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) gui.ticketList.getModel();
        
        gui.ticketList.setSelectedIndex(1);
        
        Ticket test3_model = gui.ticketList.getModel().getElementAt(1);
        
        // Changed mind. Click cancel.

        // expect input dialog after clicking the deleteSelected button, so set it up first

        // If user clicks cancel the Input dialog returns null
        gui.mockUserInput = null;

        gui.resolveSelectedButton.doClick();
        // should have an input dialog shown and returning the value given

        // The GUI should NOT remove the ticket from the list
        assertEquals("If user cancels, there should still be three tickets in the list", 3, model.getSize());


        // A message dialog should NOT been shown
        assertFalse("Do not delete or show a delete confirmation message dialog when delete was cancelled", gui.checkMessageWasCalled());

        // On the back end, resolved tickets should NOT contain this ticket
        Ticket notResolveTicket = store.getTicketById(test3_model.getTicketID());

        // The ticket should NOT be resolved
        assertNull("If ticket delete cancelled, ticket should not be resolved, resolution string is null", notResolveTicket.getResolution());
        assertTrue("If ticket delete cancelled, ticket resolved date should be null",
                (notResolveTicket.getDateResolved() == null || notResolveTicket.getDateResolved().getTime() == 0));
        assertEquals("If ticket delete cancelled, ticked should have status OPEN", Ticket.TicketStatus.OPEN, notResolveTicket.getStatus());

        // And open tickets should still contain this ticket
        String msg = "If ticket delete is cancelled, ticket should not be deleted.";
        assertNotNull(msg, controller.searchById(test_added_first.getTicketID()));
        assertNotNull(msg, controller.searchById(test_added_second.getTicketID()));
        assertNotNull(msg, controller.searchById(test_added_third.getTicketID()));
      
        assertEquals(msg, 3, store.getAllOpenTickets().size());

    }

    // TASK 7 Resolve selected

    @Test//(timeout = timeout)
    public void testDontResolveResolvedTickets() throws Exception{

        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);

        // Add two example tickets, one open, one resolved
        store.add(new Ticket("mouse", 3, "me", new Date()));
        Ticket resolvedMouse = new Ticket("MOUSE MAT", 3, "me", new Date());
        resolvedMouse.setResolution("example");
        resolvedMouse.setDateResolved(new Date());
        resolvedMouse.setStatus(Ticket.TicketStatus.RESOLVED);
        store.add(resolvedMouse);

        TicketController controller = new TicketController(store);
        TicketGUIMockDialog gui = new TicketGUIMockDialog(controller);

        gui.ticketList.clearSelection();   // Unselect everything from list

        gui.descriptionSearchTextBox.setText("mouse");  // one resolved, one open
        gui.searchDescriptionButton.doClick();

        // select the resolved ticket
        for (int x = 0 ; x < gui.ticketListModel.size(); x++) {
            Ticket ticket = (Ticket) gui.ticketListModel.getElementAt(x);
            String resolution = ticket.getResolution();
            if (resolution != null && resolution.equals("example")) {
                gui.ticketList.setSelectedIndex(x);
            }
        }

        assertNotNull("Verify the update ticket method in TicketStore is implemented",
                gui.ticketList.getSelectedValue());

        gui.resolveSelectedButton.doClick();

        // expect a message dialog to be shown
        assertTrue("Show a message dialog if the user selects a resolved " +
                "ticket and clicks the Resolve Selected button", gui.wasMessageCalled);

    }


    @Test(timeout=timeout)
    public void testResolveSelected() throws Exception {
    
        insertTestTickets();
    
        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUIMockDialog gui = new TicketGUIMockDialog(controller);

        DefaultListModel<Ticket> model = (DefaultListModel<Ticket>) gui.ticketList.getModel();

        // delete ticket ID 3, at position 1
        gui.ticketList.setSelectedIndex(1);

        // expect input dialog after clicking the deleteSelected button, so set it up first
    
        String expectedUserInput = "Replaced mouse mat";   // This should end up as resolution field in Ticket
        gui.mockUserInput = expectedUserInput;

        Date expectedResolvedDate = new Date();
        gui.resolveSelectedButton.doClick();
        
        // The GUI should remove the ticket from the list
        assertEquals("After deleting one ticket from the list, there should be two remaining", 2, model.getSize());

        assertEquals("Only the resolved ticket should be removed ", 1, model.get(0).getTicketID());
        assertEquals("Only the resolved ticket should be removed", 2, model.get(1).getTicketID());
        
        // A message dialog should have been shown
        assertTrue("Show a confirmation message dialog when ticket has been deleted", gui.checkMessageWasCalled());
        
        // On the database side, the ticket should have been updated
        Ticket resolvedTicket = store.getTicketById(test_added_third.getTicketID());
        
        assertTrue("The deleted ticket should have the same reporter, date reported and description", TicketUtil.sameTicket(resolvedTicket, test_added_third));
        assertEquals("The deleted ticket should save the resolution entered by the user", expectedUserInput, resolvedTicket.getResolution());
        assertEquals("The deleted ticket should save the resolution date, the date/time the ticket was resolved",
                expectedResolvedDate.getTime(), resolvedTicket.getDateResolved().getTime(), 3000);
        assertEquals("The resolved ticket should have the status RESOLVED", Ticket.TicketStatus.RESOLVED, resolvedTicket.getStatus());

        
        // ****  Resolve the ticket with ID  = 1 ****

        // delete ticket ID 1, at position 0
        gui.ticketList.setSelectedIndex(0);

        // expect input dialog after clicking the deleteSelected button, so set it up first

        expectedUserInput = "Replaced mouse and glued new mouse to table.";   // This should end up as resolution field in Ticket
        gui.mockUserInput = expectedUserInput;

        gui.resolveSelectedButton.doClick();

        // should have an input dialog shown and returning the value given

        // The GUI should remove the ticket from the list
        assertEquals("After deleting another ticket from the list, there should be one remaining", 1, model.getSize());
        assertEquals("The remaining ticket should be ID 2", 2, model.get(0).getTicketID());

        // A message dialog should have been shown
        assertTrue("Show a confirmation message dialog when ticket has been deleted", gui.checkMessageWasCalled());

        assertEquals("Remove resolved ticket from the JList", 1, gui.ticketList.getModel().getSize());
        
        // **** Resolve last ticket ****

        // delete ticket ID 1, at position 0
        gui.ticketList.setSelectedIndex(0);

        // expect input dialog after clicking the deleteSelected button, so set it up first
        expectedUserInput = "Rebooted the server by accidentally tripping over the power cable and pulling it out.";   // This should end up as resolution field in Ticket
        gui.mockUserInput = expectedUserInput;

        gui.resolveSelectedButton.doClick();
        
        // The GUI should remove the ticket from the list
        assertEquals("After deleting another ticket from the list, there should be none remaining", 0, model.getSize());

        // A message dialog should have been shown
        assertTrue("Show a confirmation message dialog when ticket has been deleted", gui.checkMessageWasCalled());
    
        assertEquals("Remove resolved ticket from the JList", 0, gui.ticketList.getModel().getSize());
        
    }

    // TASK 8 Quit, tickets saved, available when program relaunches

    @Test(timeout=timeout)
    public void saveAndRestoreTickets() throws Exception {
    
        insertTestTickets();
    
        TicketStore store = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller = new TicketController(store);
        TicketGUI gui = new TicketGUI(controller);
        
        List<Ticket> originalTicketList = store.getAllOpenTickets();

        Main.ticketGUI = gui;
        gui.quitButton.doClick();   // This should invoke the save and quit method for the test Tickets.

        // Now restart program
        TicketStore store2 = new TicketStore(Configuration.TEST_DB_URI);
        TicketController controller2 = new TicketController(store2);
        
        // The tickets should have been read from a file, and be available.
        List<Ticket> ticketList_relaunch = controller.loadAllOpenTicketsFromStore();

        assertEquals("There should be the same number of open tickets after your app is restarted. " +
                        "Save all open tickets to a file when app closes, read the tickets from the file when it restarts.",
                ticketList_relaunch.size(), testTickets.size());

        // Same tickets? Should be in the same priority order

        for (int t = 0 ;  t < ticketList_relaunch.size() ; t++) {
            String msg = "Read all data from the file to create a new ticket list.  " +
                    "Wrote out \n%s\n and got \n%s\n back. Make sure all the data is the same as the original ticket.";
            Ticket expected = originalTicketList.get(t);
            Ticket actual = ticketList_relaunch.get(t);

            assertTrue(String.format(msg, expected, actual), TicketUtil.sameTicket(expected, actual));
        }
    }

    // # TASK 10 QUIT

    @Test(timeout = timeout)
    public void testSaveAndQuit() {

        TicketController mockController = mock(TicketController.class);
        when(mockController.loadAllOpenTicketsFromStore()).thenReturn(new ArrayList<>());
        TicketGUI gui = new TicketGUI(mockController);

        gui.quitButton.doClick();
        // check if the quitProgram method is called.
        verify(mockController).quitProgram();

    }


    private class TicketGUIMockDialog extends TicketGUI {
        TicketGUIMockDialog(TicketController t){ super(t);}

        private boolean wasMessageCalled = false;

        boolean checkMessageWasCalled() {
            boolean copyToReturn = wasMessageCalled;
            wasMessageCalled = false;
            return copyToReturn;
        }

        @Override
        protected void showMessageDialog(String msg){
            // do nothing so the program does not show a message dialog, but record that the method was invoked.
            wasMessageCalled = true;
        }

        // Set this to force showInputDialog to return a particular value. Do this before the inputDialog is expected to be called.
        String mockUserInput;

        @Override
        protected String showInputDialog(String msg) {
            return mockUserInput;
        }

    }






}