package interfaces;

import restaurant.CashierAgent;
import restaurant.CookAgent;


public interface RalphMarket {

	/*public abstract void msgHereIsAnOrder(Waiter waiter, int tableNum,
			String choice);*/

	/** Returns the name of the Market */
	public abstract String getName();
	
	public abstract void msgPleaseDeliver(String choice,int amount);
	
	public abstract void msgThankYou(String choice);
	
	public abstract void msgPaidInvoice(int billNumber);
	
	public abstract boolean pickAndExecuteAnAction();
	
	public abstract void setFood(int count,String name);
	
	public abstract void setCook(CookAgent cook);
	
	public abstract void setCashier(CashierAgent cashier);

}