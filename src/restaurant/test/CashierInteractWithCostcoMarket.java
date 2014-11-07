package restaurant.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import restaurant.CashierAgent.PayState;
import restaurant.test.mock.*;
import restaurant.*;

import org.junit.Test;

public class CashierInteractWithCostcoMarket extends TestCase
{
	
	public CashierAgent cashier;
	public void testmsgInvoiceFromCostco()
	{
		//create cashier
		cashier = new CashierAgent("Edward");
		
		//create market
		MockCostcoMarket costco = new MockCostcoMarket("Costco");
		
		//set market
		cashier.setMarket(costco);
		
		//market sends invoice to cashier
		cashier.msgInvoiceFromCostco(1, 10.0);
		
		//checks if log is empty
		assertEquals(
				"Mock Costco Market should have an empty event log before the Cashier's scheduler is called. It reads: "
						+ costco.log.toString(), 0, costco.log.size());
		
		//check if cashiers state is paying costco
		assertTrue(cashier.cashierPaying == PayState.PAYING_COSTCO);
		assertTrue(cashier.payingCheckNumber == 1);
		assertTrue(cashier.payingTotal == 10.0);
		
		//run cashier's scheduler
		cashier.pickAndExecuteAnAction();
		
		// Now, make asserts to make sure that the scheduler did what it was
		// supposed to.
		assertTrue("Mock costco market should have received payment from cashier. Event log: "
				+ costco.log.toString(), costco.log.containsString("Cashier paid."));
		
		assertEquals(
				"Only 1 message should have been sent to the customer. Event log: "
						+ costco.log.toString(), 1, costco.log.size());
		
		
	}


}
