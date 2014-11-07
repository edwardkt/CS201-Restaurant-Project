package restaurant.test;

import static org.junit.Assert.*;
import java.awt.Color;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import restaurant.CashierAgent;
import restaurant.Bill;
import restaurant.CookAgent;
import restaurant.Menu;
import restaurant.WaiterAgent;
import restaurant.CashierAgent.BillState;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;
import restaurant.test.mock.MockWaiter;
import restaurant.test.mock.MockCashier;
import interfaces.*;

public class CashierWithOneCustomerNoMarket extends TestCase
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
	
	@Test
	public void testmsgCustomerPaid()
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
		
		//waiter gives bill to cashier
		cashier.msgCustomerPaid(1);

		assertTrue(cashier.bills.size() == 1);
		
		//checks if there is one log
		assertEquals(
				"Mock Waiter should have 1 event log. The event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		//run scheduler
		cashier.pickAndExecuteAnAction();
		
		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.
		assertTrue("Mock waiter should have received message to tell customer he/she can leave. Event log: "
				+ waiter.log.toString(), waiter.log.containsString("Waiter notified that customer can leave"));
		
		assertEquals(
				"Only 2 message(s) should have been sent to the waiter. Event log: "
						+ waiter.log.toString(), 2, waiter.log.size());
	
	}
	
	
	
	

}
