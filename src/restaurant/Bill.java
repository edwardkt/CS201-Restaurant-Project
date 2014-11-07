package restaurant;

import interfaces.Waiter;
import restaurant.CashierAgent.BillState;

	public class Bill
	{
		public Waiter waiter;
		public String choice;
		public int tableNum;
		public Menu menu;
		public double total;
		public BillState state;
		public int billNumber = 0;
		
		public Bill(Waiter waiter, String order, int tableNum, Menu menu)
		{
			this.waiter = waiter;
			this.tableNum = tableNum;
			this.choice = order;
			this.state = BillState.BillNotPaid;
			total = menu.getPrices(choice);
		}
		}