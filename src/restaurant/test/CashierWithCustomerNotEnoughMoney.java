package restaurant.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import restaurant.Bill;
import restaurant.CashierAgent;
import restaurant.CashierAgent.BillState;
import restaurant.Menu;
import restaurant.test.mock.MockWaiter;


/* Test case for when the customer does not have enough money! */

public class CashierWithCustomerNotEnoughMoney extends TestCase
{
	public CashierAgent cashier;
	
	@Test
	public void testmsgCustomerWantsBill()
	{
		//create new cashier
		cashier = new CashierAgent("Edward");
		
		//create mock waiter
		MockWaiter waiter = new MockWaiter("Waiter1");
		
		//create menu
		Menu menu = new Menu();
		
		//checks if there are no bills initially
		assertTrue(cashier.bills.size() == 0);
		
		//waiter sends cashier this message when customer is ready to pay
		cashier.msgCustomerWantsBill(waiter,"Salad",3, menu);
		
		//create bill
		Bill b = new Bill(waiter,"Salad",3,menu);
		
	
		//checks if log is empty
		assertEquals(
				"Mock Waiter should have an empty event log before the Cashier's scheduler is called. Instead, the mock customer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//add bill to list
		cashier.bills.add(b);		
		
		//run scheduler
		cashier.pickAndExecuteAnAction();
		

		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.
		assertTrue("Mock waiter should have received message for bill. Event log: "
				+ waiter.log.toString(), waiter.log.containsString("Waiter received bill from cashier"));
		
		assertEquals(
				"Only 1 message should have been sent to the customer. Event log: "
						+ waiter.log.toString(), 1, waiter.log.size());
	}

	public void testmsgCustomerNotEnough()
	{
		//create new cashier
		cashier = new CashierAgent("Edward");
		
		//create mock waiter
		MockWaiter waiter = new MockWaiter("Waiter1");
		
		//create menu
		Menu menu = new Menu();
			
		//create bill
		Bill b = new Bill(waiter,"Salad",3,menu);
		
		//checks if there are no bills initially
		assertTrue(cashier.bills.size() == 0);
	
		//checks if log is empty
		assertEquals(
				"Mock Waiter should have an empty event log before the Cashier's scheduler is called. Instead, the mock customer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//add bill to list
		cashier.bills.add(b);
		
		//check if bill has been added
		assertTrue(cashier.bills.size() == 1);
		
		//run scheduler
		cashier.pickAndExecuteAnAction();
		

		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.
		assertTrue("Mock waiter should have received message for bill. Event log: "
				+ waiter.log.toString(), waiter.log.containsString("Waiter received bill from cashier"));
		
		assertEquals(
				"Only 1 message should have been sent to the waiter. Event log: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		//checks if bill still there
		assertTrue(cashier.bills.size() == 1);
		
		//waiter sends msg customer does not have enough money
		cashier.msgCustomerNotEnough(1);
		
		//check state of bill
		assertTrue(b.state == BillState.CustomerLeave);
		
		//run scheduler
		cashier.pickAndExecuteAnAction();
		
		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.
		assertTrue("Mock waiter should have received message to kick customer out. Event log: "
				+ waiter.log.toString(), waiter.log.containsString("Waiter notified that customer needs to leave!"));
		
		assertEquals(
				"Only 2 message(s) should have been sent to the waiter. Event log: "
						+ waiter.log.toString(), 2, waiter.log.size());
		
		
	}

}
