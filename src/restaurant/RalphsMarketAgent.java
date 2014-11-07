package restaurant;

import agent.Agent;
import java.util.*;

import interfaces.Cashier;
import interfaces.RalphMarket;
import restaurant.CashierAgent.BillState;

public class RalphsMarketAgent extends Agent implements RalphMarket
{
    private int SteakAvailable;
    private int ChickenAvailable;
    private int PizzaAvailable;
    private int SaladAvailable;
    public enum MarketStatus {DOING_NOTHING,ORDER_RECEIVED,DELIVERING,GIVE_BILL}; //checks what the market is doing
    public enum InvoiceStatus{InvoiceNotPaid,InvoicePaid};
    private InvoiceStatus bill;
    private MarketStatus status;
    private String name; //name of market
    private CashierAgent cashier;
    private CookAgent cook;
    private String choice;  //needed item
    private int amount;  //amount of item needed
    private double total;
    private double RalphsMoney = 2000.0;
    
	private class Invoice
	{
		//public WaiterAgent waiter;
		public String choice;
		//public int tableNum;
		//public Menu menu;
		public double total;
		public int billNumber = 0;
		public InvoiceStatus bill;
		
		public Invoice(String order, double total)
		{
			this.choice = order;
			this.total = total;
			this.bill = InvoiceStatus.InvoiceNotPaid;
		}
	}
    
    private List<Invoice> invoices = Collections.synchronizedList(new ArrayList<Invoice>());
    
    
    public RalphsMarketAgent(String name)
    {
    	this.name = name;
    	this.status = MarketStatus.DOING_NOTHING;
    }
    
    /*MESSSAGES*/
    //COOK SENDS ORDER TO MARKET
    public void msgPleaseDeliver(String choice,int amount)
    {
    	print("Ralphs received the cook's order");
    	this.choice = choice;
    	this.amount = amount;
    	status = MarketStatus.ORDER_RECEIVED;
    	stateChanged();
    	
    }
    
    public void msgThankYou(String choice)
    {
    	this.choice = choice;
    	if(choice.equals("Steak"))
    		total = 30.0;
    	else if(choice.equals("Chicken"))
    		total = 20.0;
    	else if(choice.equals("Pizza"))
    		total = 15.0;
    	else if(choice.equals("Salad"))
    		total = 10.0;
    		
    	invoices.add(new Invoice(choice,total));
    	//status = MarketStatus.PREPARE_RECEIPT;
    	stateChanged();
    }
    
    public void msgPaidInvoice(int billNumber)
    {
    	synchronized(invoices)
    	{
	    	for(Invoice i: invoices)
	    	{
	    		if(i.billNumber == billNumber)
	    		{
	    			i.bill = InvoiceStatus.InvoicePaid;
	    			stateChanged();
	    		}
	    	}
    	}
    }
    
    /*SCHEDULER!!*/
	public boolean pickAndExecuteAnAction()
	{
		if(status == MarketStatus.ORDER_RECEIVED)
		{
			Delivering();
			return true;
		}
		synchronized(invoices)
		{
			for(Invoice i: invoices)
			{
				if(i.bill == InvoiceStatus.InvoiceNotPaid)
				{
					giveBillToCashier(i);
					return true;
				}
			}
		}
		synchronized(invoices)
		{
			for(Invoice i: invoices)
			{
				if(i.bill == InvoiceStatus.InvoicePaid)
				{
					removeInvoice(i);
					return true;
				}
			}
		}
		return false;
	}
	
	/*ACTIONS*/
	
	private void Delivering()
	{
		status = MarketStatus.DELIVERING;
		if(choice.equals("Steak"))
		{
			if(SteakAvailable >= amount)
			{
				print("Market initially has " + SteakAvailable + " Steak(s)");
				//print("Cook wants " + amount + " " + choice);
				SteakAvailable = SteakAvailable - amount;
				print("Ralphs Market now has " + SteakAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgRalphsDelivering(amount,choice);
				stateChanged();
				
				
			}
		}
		else if(choice.equals("Chicken"))
		{
			if(ChickenAvailable >= amount)
			{
				print("Market initially has " + ChickenAvailable + " CHicken(s)");
				//print("Cook wants " + amount + " " + choice);
				ChickenAvailable = ChickenAvailable - amount;
				print(" Market now has " + ChickenAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgRalphsDelivering(amount,choice);
				stateChanged();
			}
		}
		else if(choice.equals("Pizza"))
		{
			if(PizzaAvailable >= amount)
			{
				print("Market initially has " + PizzaAvailable + " Pizza(s)");
				//print("Cook wants " + amount + " " + choice);
				PizzaAvailable = PizzaAvailable - amount;
				print("Ralphs Market now has " + PizzaAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgRalphsDelivering(amount,choice);
				stateChanged();
			}
		}
		else if(choice.equals("Salad"))
		{
			if(SaladAvailable >= amount)
			{
				print("Market initially has " + SaladAvailable + " Salad(s)");
				//print("Cook wants " + amount + " " + choice);
				SaladAvailable = SaladAvailable - amount;
				print("Ralphs Market now has " + SaladAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgRalphsDelivering(amount,choice);
				stateChanged();
			}
		}
		
	}
	
	private void giveBillToCashier(Invoice i)
	{
		status = MarketStatus.GIVE_BILL;
		cashier.msgInvoiceFromRalphs(i.billNumber,i.total);
		stateChanged();
	}
	
	private void removeInvoice(Invoice i)
	{
		RalphsMoney = RalphsMoney + i.total;
		print("Cashier paid. Ralphs Bank now has: $" + RalphsMoney);
		synchronized(invoices)
		{
			invoices.remove(i);
		}
		this.status = MarketStatus.DOING_NOTHING;
		stateChanged();
	}
	
	
	//gui sets the stocks of each item 
    public void setFood(int count,String name)
    {
    	if(name.equals("Steak"))
    		SteakAvailable = count;
    	else if(name.equals("Chicken"))
    		ChickenAvailable = count;
    	else if(name.equals("Pizza"))
    		PizzaAvailable = count;
    	else if(name.equals("Salad"))
    		SaladAvailable = count;
    	
        print("added " + count + " " + name + "(s) to " + this.name + "s market");

    	
    	
    		
    }
    public String getName(){
        return name;
    } 
    
    /** Hack to set the cook for the market */
    public void setCook(CookAgent cook){
	this.cook = cook;
    }
    
    /** Hack to set the cashier for the market */
    public void setCashier(CashierAgent cashier){
    	this.cashier = cashier;
        }

	/*@Override
	public void setCashier(CashierAgent cashier) {
		// TODO Auto-generated method stub
		
	}*/
}