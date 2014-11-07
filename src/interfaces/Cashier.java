package interfaces;

import restaurant.CostcoMarketAgent;
import restaurant.Menu;
import restaurant.RalphsMarketAgent;
import restaurant.WaiterAgent;


public interface Cashier {

	/*public abstract void msgHereIsAnOrder(Waiter waiter, int tableNum,
			String choice);*/
	
	public abstract void msgCustomerWantsBill(Waiter waiter, String choice, int tableNum, Menu menu);
	
	public abstract void msgCustomerPaid(int billNumber);
	
	public abstract void msgCustomerNotEnough(int billNumber);
	
	public abstract void msgInvoiceFromCostco(int billNumber,double total);
	
	public abstract void msgInvoiceFromRalphs(int billNumber,double total);
	
	public abstract boolean pickAndExecuteAnAction();
	
	public abstract void setMarket(CostcoMarketAgent costco);
	
	public abstract void setMarket2(RalphsMarketAgent ralphs);

	/** Returns the name of the cashier */
	public abstract String getName();

}