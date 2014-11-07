package restaurant.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import restaurant.CashierAgent.PayState;
import restaurant.test.mock.*;
import restaurant.*;

import org.junit.Test;

public class CashierInteractWithRalphMarket extends TestCase
{
	
	public CashierAgent cashier;
	public void testmsgInvoiceFromRalph()
	{
		//create cashier
		cashier = new CashierAgent("Edward");
		
		//create market
		MockRalphMarket ralphs = new MockRalphMarket("Ralphs");
		
		//set market
		cashier.setMarket2(ralphs);
		
		//market sends invoice to cashier
		cashier.msgInvoiceFromRalphs(1, 10.0);
		
		//checks if log is empty
		assertEquals(
				"Mock Ralphs Market should have an empty event log before the Cashier's scheduler is called. It reads: "
						+ ralphs.log.toString(), 0, ralphs.log.size());
		
		//check if cashiers state is paying ralphs
		assertTrue(cashier.cashierPaying == PayState.PAYING_RALPHS);
		assertTrue(cashier.payingCheckNumber == 1);
		assertTrue(cashier.payingTotal == 10.0);
		
		//run cashier's scheduler
		cashier.pickAndExecuteAnAction();
		
		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.
		assertTrue("Mock ralphs market should have received payment from cashier. Event log: "
				+ ralphs.log.toString(), ralphs.log.containsString("Cashier paid."));
		
		assertEquals(
				"Only 1 message should have been sent to the customer. Event log: "
						+ ralphs.log.toString(), 1, ralphs.log.size());
		
		
	}


}