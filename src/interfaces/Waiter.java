package interfaces;

import restaurant.CookAgent;
import restaurant.CostcoMarketAgent;
import restaurant.CustomerAgent;
import restaurant.CashierAgent;
import restaurant.HostAgent;
import restaurant.WaiterAgent;
import restaurant.layoutGUI.Food;


public interface Waiter {

	/*public abstract void msgHereIsAnOrder(Waiter waiter, int tableNum,
			String choice);*/

	//Object log = null;

	/** Returns the name of the Waiter */
	public abstract String getName();
	
	public abstract void msgSitCustomerAtTable(CustomerAgent customer, int tableNum);
	
	public abstract void msgImReadyToOrder(CustomerAgent customer);
	
	public abstract void msgHereIsMyChoice(CustomerAgent customer, String choice);
	
	public abstract void msgSorryOutOfItem(int tableNum, Food f);
	
	public abstract void msgOrderIsReady(int tableNum, Food f);
	
	public abstract void msgReadyToPay(CustomerAgent customer);
	
	public abstract void msgHereIsBill(int tableNum, double total, int billNumber);
	
	public abstract void msgBillIsPaid(int billNumber);
	
	public abstract void msgNotEnoughMoney(int billNumber);
	
	public abstract void msgNotifyCustomerCanLeave(int tableNum);
	
	public abstract void msgTellHimToLeave(int tableNum);
	
	public abstract void msgImNotWaiting(CustomerAgent customer);
	
	public abstract void msgLeaving(CustomerAgent customer);
	
	public abstract void msgDoneEatingAndLeaving(CustomerAgent customer);
	
	public abstract void msgGoOnBreak(final WaiterAgent waiter);
	
	public abstract void breakDone(WaiterAgent waiter);
	
	public abstract void msgCantGoOnBreak();
	
	public abstract void setBreakStatus(boolean state);
	
	public abstract boolean pickAndExecuteAnAction();
	
	public abstract String toString();
	
	public abstract void setCook(CookAgent cook);
	
	public abstract void setCashier(CashierAgent cashier);
	
	public abstract void setHost(HostAgent host);
	
	public abstract void setMarket(CostcoMarketAgent costco);
	
	public abstract boolean isOnBreak();

}