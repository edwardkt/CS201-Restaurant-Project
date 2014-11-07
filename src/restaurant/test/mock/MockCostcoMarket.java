/**
 * 
 */
package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import interfaces.CostcoMarket;

/**
 * @author Edward Tam
 * 
 */
public class MockCostcoMarket extends MockAgent implements CostcoMarket {

	public MockCostcoMarket(String name) {
		super(name);
	}

	public EventLog log = new EventLog();
	
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
	
	public void setCashier(CashierAgent cashier)
	{
		
	}

	public void setCook(CookAgent cook)
	{
		
	}
	
	/** Returns the name of the Market */
	public String getName()
	{
		return this.name;
	}

	/*public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice) {
		log.add(new LoggedEvent(
				"Received message msgHereIsAnOrder from waiter "
						+ waiter.toString() + " for table number " + tableNum
						+ " to cook item " + choice + "."));

	}*/

}
