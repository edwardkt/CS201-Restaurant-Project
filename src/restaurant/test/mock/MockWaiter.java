/**
 * 
 */
package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.CostcoMarketAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;
import restaurant.layoutGUI.Food;
import interfaces.Waiter;
import interfaces.Cashier;

/**
 * @author Edward Tam
 * 
 */
public class MockWaiter extends MockAgent implements Waiter {

	public MockWaiter(String name) {
		super(name);
	}

	public EventLog log = new EventLog();

	
	/** Returns the name of the Waiter */
	public String getName()
	{
		return this.name;
	}
	
	public void msgSitCustomerAtTable(CustomerAgent customer, int tableNum)
	{
		
	}
	
	public void msgImReadyToOrder(CustomerAgent customer)
	{
		
	}
	
	public void msgHereIsMyChoice(CustomerAgent customer, String choice)
	{
		
	}
	
	public void msgSorryOutOfItem(int tableNum, Food f)
	{
		
	}
	
	public void msgOrderIsReady(int tableNum, Food f)
	{
		
	}
	
	public void msgReadyToPay(CustomerAgent customer)
	{
		
	}
	
	public void msgHereIsBill(int tableNum, double total, int billNumber)
	{
		log.add(new LoggedEvent("Waiter received bill from cashier"));
	}
	
	public void msgBillIsPaid(int billNumber)
	{
		
	}
	
	public void msgNotEnoughMoney(int billNumber)
	{
		
	}
	
	public void msgNotifyCustomerCanLeave(int tableNum)
	{
		log.add(new LoggedEvent("Waiter notified that customer can leave"));
	}
	
	public void msgTellHimToLeave(int tableNum)
	{
		
		log.add(new LoggedEvent("Waiter notified that customer needs to leave!"));
	}
	
	public void msgImNotWaiting(CustomerAgent customer)
	{
		
	}
	
	public void msgLeaving(CustomerAgent customer)
	{
		
	}
	
	public void msgDoneEatingAndLeaving(CustomerAgent customer)
	{
		
	}
	
	public void msgGoOnBreak(final WaiterAgent waiter)
	{
		
	}
	
	public void breakDone(WaiterAgent waiter)
	{
		
	}
	
	public void msgCantGoOnBreak()
	{
		
	}
	
	public void setBreakStatus(boolean state)
	{
		
	}
	
	public boolean pickAndExecuteAnAction()
	{
		return false;
	}
	
	public String toString()
	{
		return "waiter " + getName();
	}
	
	public void setCook(CookAgent cook)
	{
		
	}
	
	public void setCashier(CashierAgent cashier)
	{
		
	}
	
	public void setHost(HostAgent host)
	{
		
	}
	
	public void setMarket(CostcoMarketAgent costco)
	{
		
	}
	
	public boolean isOnBreak()
	{
		return false;
	}
	/*public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice) {
		log.add(new LoggedEvent(
				"Received message msgHereIsAnOrder from waiter "
						+ waiter.toString() + " for table number " + tableNum
						+ " to cook item " + choice + "."));

	}*/

}
