package week_11;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static week_11.TestConfig.timeout;

/**
 * Created by clara on 11/12/19.
 */
public class TestTicket {
    
    @Test(timeout=timeout)
    public void testTicketToString() {
        Date repDate = new Date(150000000);
        Date fixDate = new Date(160000000);
        Ticket t = new Ticket("toString is broken", 3, "hello kitty", repDate);
        t.setResolution("Replace + with comma");
        t.setDateResolved(fixDate);
        t.setStatus(Ticket.TicketStatus.RESOLVED);
        
        String string = t.toString();
        
        assertTrue("toString should contain description", string.contains("toString is broken"));
        assertTrue("toString should contain priority", string.contains("3"));
        assertTrue("toString should contain reporter", string.contains("hello kitty"));
        assertTrue("toString should contain reported date", string.contains(repDate.toString()) );
        assertTrue("toString should contain resolution", string.contains("Replace + with comma"));
        assertTrue("toString should contain fix date", string.contains(fixDate.toString()) );
        assertTrue("toString should contain status", string.contains("RESOLVED"));
    }
    
}
