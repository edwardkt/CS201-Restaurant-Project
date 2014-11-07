package restaurant;

import agent.Agent;
import java.util.*;

import restaurant.CookAgent.Status;
import restaurant.WaiterAgent.CustomerState;
import restaurant.layoutGUI.Restaurant;
import interfaces.Cashier;
import interfaces.CostcoMarket;
import interfaces.RalphMarket;
import interfaces.Waiter;
import restaurant.test.mock.*;
import restaurant.Bill;


public class CashierAgent extends Agent
{
	public String name; //name of cashier
	public double cashierMoney = 500.0; //money cashier has to start
	public enum BillState {BillNotPaid,CustomerReadyToPay,BillProcessed,BillAdded,BillPaid,CustomerLeave};
	public enum PayState {NOT_PAYING,PAYING_RALPHS,PAYING_COSTCO}
	public PayState cashierPaying;
	public BillState state;
	public int payingCheckNumber;
	public double payingTotal;
	private CostcoMarket costco;
	private RalphMarket ralphs;
	
	/*public class Bill
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
	}*/
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>()); //list of bills
	
	public CashierAgent(String name)
	{
		super();
		this.name = name;
		this.cashierPaying = PayState.NOT_PAYING;
	}
	
	
	/*MESSAGES*/
	/* message from waiter getting bill from cashier */
	/* when customer wants bill and if there exists a bill that matches up with the customer
	 * it will add bill to list*/
	
	public void msgCustomerWantsBill(Waiter waiter, String choice, int tableNum, Menu menu)
	{
		print("cashier notified customer wants bill");
		bills.add(new Bill(waiter,choice,tableNum,menu));
		/*print("bill state: " + bills.get(0).state);
		print("choice " + bills.get(0).choice);
		print("total is : " + bills.get(0).total);
		print("tableNum is: " + bills.get(0).tableNum);
		print("billNumber is: " + bills.get(0).billNumber);*/
		stateChanged();
	}
	
	/* waiter notifies cashier that customer paid the bill */
	public void msgCustomerPaid(int billNumber)
	{
		print("Cashier notified customer paid");
		synchronized(bills)
		{
			for(Bill b: bills)
			{
				print("BillState : " + b.state + " " + b.billNumber);
				//print("Billnumber from cashier " + b.billNumber);
				//print("Billnumber passed from waiter " + billNumber);
				if(b.billNumber == billNumber)
				{
					b.state = BillState.BillPaid;
					stateChanged();
				}
			}
		}
		
	}
	
	public void msgCustomerNotEnough(int billNumber)
	{
		print("Cashier notified customer doesn't have enough money");
		synchronized(bills)
		{
			for(Bill b: bills)
			{
				print("BillState : " + b.state + " " + b.billNumber);
				//print("Billnumber from cashier " + b.billNumber);
				//print("Billnumber passed from waiter " + billNumber);
				if(b.billNumber == billNumber)
				{
					b.state = BillState.CustomerLeave;
					stateChanged();
				}
			}
		}
	}
	
	public void msgInvoiceFromCostco(int billNumber,double total)
	{
		this.cashierPaying = PayState.PAYING_COSTCO;
		this.payingCheckNumber = billNumber;
		this.payingTotal = total;
		stateChanged();
	}
	
	public void msgInvoiceFromRalphs(int billNumber,double total)
	{
		this.cashierPaying = PayState.PAYING_RALPHS;
		this.payingCheckNumber = billNumber;
		this.payingTotal = total;
		stateChanged();
	}

	//SCHEDULER!!!
    /** Scheduler.  Determine what action is called for, and do it. */
    public boolean pickAndExecuteAnAction() 
    {
    	synchronized(bills)
    	{
			for(Bill b: bills)
			{ 
			    if(b.state == BillState.BillNotPaid)
			    {
			    	HereIsBill(b);
			    	return true;
			    }
				
			}
    	}
		
		/*if there exist a bill thats payed, remove bill */
    	synchronized(bills)
    	{
			for(Bill b: bills)
			{ 
			    if(b.state == BillState.BillPaid)
			    {
			    	removeBill(b);
			    	return true;
			    }
			}
    	}
    	
    	synchronized(bills)
    	{
			for(Bill b: bills)
			{ 
			    if(b.state == BillState.CustomerLeave)
			    {
			    	removeBill(b);
			    	return true;
			    }
			}
    	}
		if(cashierPaying == PayState.PAYING_COSTCO)
		{
			payingCostco();
			return true;
		}
		
		else if(cashierPaying == PayState.PAYING_RALPHS)
		{
			payingRalphs();
			return true;
		}
	/*for(Bill b:bills)
	{ 
	    if(b.state == BillState.BillAdded)
	    {
	    	HereIsBill(b);
	    	return true;
	    }
	}*/
	

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
  }

	
	// *** ACTIONS ***
	//cashier gives the waiter the bill
	private void HereIsBill(Bill b)
	{ 
		//print("Bill is handed to Waiter");
		b.billNumber++;
		//print("bill number is: " + b.billNumber);
		//print("billNumber is " + b.billNumber + " and total is: " + b.total);
		b.waiter.msgHereIsBill(b.tableNum,b.total,b.billNumber);
		b.state = BillState.CustomerReadyToPay;
		stateChanged();
	}
	
	/*removes bill from list and updates cashier's money */
	private void removeBill(Bill b)
	{
		if(b.state == BillState.CustomerLeave)
		{
			print("cashier telling waiter to tell customer to leave");
			int tableNum = b.tableNum;
			synchronized(bills)
			{
				bills.remove(b);
			}
			b.waiter.msgTellHimToLeave(tableNum);
			stateChanged();
		}
		else
		{
			//print("Updating cashier's money");
			cashierMoney = cashierMoney + b.total;
			print("Cashier now has $" + cashierMoney);
			int tableNum = b.tableNum;
			synchronized(bills)
			{
				bills.remove(b);
			}
			//print("removed bill");
			b.waiter.msgNotifyCustomerCanLeave(tableNum);
			stateChanged();
		}
	}
	
	private void payingCostco()
	{
		print("Paying Costco Market");
		cashierMoney = cashierMoney - this.payingTotal;
		cashierPaying = PayState.NOT_PAYING;
		costco.msgPaidInvoice(this.payingCheckNumber);
		stateChanged();
	}
	
	private void payingRalphs()
	{
		print("Paying Ralphs Market");
		cashierMoney = cashierMoney - this.payingTotal;
		cashierPaying = PayState.NOT_PAYING;
		ralphs.msgPaidInvoice(this.payingCheckNumber);
		stateChanged();
	}
	
	
    public void setMarket(CostcoMarket costco)
    {
    	this.costco = costco;
    }
    
    public void setMarket2(RalphMarket ralphs)
    {
    	this.ralphs = ralphs;
    }
	
	
    public String getName(){
        return name;
    }


	/*@Override
	public void msgCustomerWantsBill(Waiter waiter, String choice,
			int tableNum, Menu menu) {
		// TODO Auto-generated method stub
		
	}*/


	/*@Override
	public void setMarket(CostcoMarketAgent costco) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setMarket2(RalphsMarketAgent ralphs) {
		// TODO Auto-generated method stub
		
	} */ 
}