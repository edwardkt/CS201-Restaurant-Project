/**
 * 
 */
package restaurant.test.mock;

import restaurant.CostcoMarketAgent;
import restaurant.Menu;
import restaurant.RalphsMarketAgent;
import restaurant.WaiterAgent;
import interfaces.Cashier;
import interfaces.Waiter;

/**
 * @author Edward Tam
 * 
 */
public class MockCashier extends MockAgent implements Cashier {

	public MockCashier(String name) {
		super(name);
	}

	public EventLog log = new EventLog();
	
	public void msgCustomerWantsBill(WaiterAgent waiter, String choice, int tableNum, Menu menu)
	{
		
	}
	
	public void msgCustomerPaid(int billNumber)
	{
		
	}
	
	public void msgCustomerNotEnough(int billNumber)
	{
		
	}
	
	public void msgInvoiceFromCostco(int billNumber,double total)
	{
		
	}
	
	public void msgInvoiceFromRalphs(int billNumber,double total)
	{
		
	}
	
	public boolean pickAndExecuteAnAction()
	{
		return false;
	}
	
	public void setMarket(CostcoMarketAgent costco)
	{
		
	}
	
	public void setMarket2(RalphsMarketAgent ralphs)
	{
		
	}

	/** Returns the name of the cashier */
	public String getName()
	{
		String s = "";
		return s;
	}

	@Override
	public void msgCustomerWantsBill(Waiter waiter, String choice,
			int tableNum, Menu menu) {
		// TODO Auto-generated method stub
		
	}

	/*public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice) {
		log.add(new LoggedEvent(
				"Received message msgHereIsAnOrder from waiter "
						+ waiter.toString() + " for table number " + tableNum
						+ " to cook item " + choice + "."));

	}*/

}
