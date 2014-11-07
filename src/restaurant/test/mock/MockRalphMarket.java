/**
 * 
 */
package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import interfaces.RalphMarket;

/**
 * @author Edward Tam
 * 
 */
public class MockRalphMarket extends MockAgent implements RalphMarket {

	public MockRalphMarket(String name) {
		super(name);
	}

	public EventLog log = new EventLog();
	
	public String getName()
	{
		String s = "";
		return s;
	}
	
	public void msgPleaseDeliver(String choice,int amount)
	{
		
	}
	
	public void msgThankYou(String choice)
	{
		
	}
	
	public void msgPaidInvoice(int billNumber)
	{
		log.add(new LoggedEvent("Cashier paid."));
	}
	
	public boolean pickAndExecuteAnAction()
	{
		return false;
	}
	
	public void setFood(int count,String name)
	{
		
	}
	
	public void setCook(CookAgent cook)
	{
		
	}
	
	public void setCashier(CashierAgent cashier)
	{
		
	}

	/*public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice) {
		log.add(new LoggedEvent(
				"Received message msgHereIsAnOrder from waiter "
						+ waiter.toString() + " for table number " + tableNum
						+ " to cook item " + choice + "."));

	}*/

}
