package restaurant;

import agent.Agent;
import java.util.*;
import interfaces.CostcoMarket;

import restaurant.CashierAgent.BillState;

public class CostcoMarketAgent extends Agent implements CostcoMarket
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
    private double CostcoMoney = 1000.0;
    
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
    
    
    public CostcoMarketAgent(String name)
    {
    	this.name = name;
    	this.status = MarketStatus.DOING_NOTHING;
    }
    
    /*MESSSAGES*/
    //COOK SENDS ORDER TO MARKET
    public void msgPleaseDeliver(String choice,int amount)
    {
    	print("Costco received the cook's order");
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
    	print("Cashier paid.");
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
	//delivers if there is enough of a certain item in stock, else it will send a message back to the cook notifying him
	private void Delivering()
	{
		status = MarketStatus.DELIVERING;
		if(choice.equals("Steak"))
		{
			if(SteakAvailable >= amount)
			{
				print("Market initially has " + SteakAvailable + " Steak(s)");
				SteakAvailable = SteakAvailable - amount;
				print("Costco Market now has " + SteakAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgImDeliveringOrder(amount,choice);
				stateChanged();
			}
			else
			{
				print("Costco can not fulfill your order. Please choose another market.");
				this.status = MarketStatus.DOING_NOTHING;
				cook.msgSorryImOut(choice,amount);
				stateChanged();
			}
		}
		else if(choice.equals("Chicken"))
		{
			if(ChickenAvailable >= amount)
			{
				print("Market initially has " + ChickenAvailable + " Chicken(s)");
				ChickenAvailable = ChickenAvailable - amount;
				print("Costco Market now has " + ChickenAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgImDeliveringOrder(amount,choice);
				stateChanged();
			}
			else
			{
				print("Costco can not fulfill your order. Please choose another market.");
				this.status = MarketStatus.DOING_NOTHING;
				cook.msgSorryImOut(choice,amount);
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
				print("Costco Market now has " + PizzaAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgImDeliveringOrder(amount,choice);
				stateChanged();
			}
			else
			{
				print("Costco can not fulfill your order. Please choose another market.");
				this.status = MarketStatus.DOING_NOTHING;
				cook.msgSorryImOut(choice,amount);
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
				print("Costco Market now has " + SaladAvailable + " " + choice + "(s).");
				print("Delivering " + amount + " " + choice + "s");
				cook.msgImDeliveringOrder(amount,choice);
				stateChanged();
			}
			else
			{
				print("Costco can not fulfill your order. Please choose another market.");
				this.status = MarketStatus.DOING_NOTHING;
				cook.msgSorryImOut(choice,amount);
				stateChanged();
			}
		}
		
	}
	//market sends a msg to give cashier a check
	private void giveBillToCashier(Invoice i)
	{
		status = MarketStatus.GIVE_BILL;
		cashier.msgInvoiceFromCostco(i.billNumber,i.total);
		stateChanged();
	}
	//deletes invoice and updates market money
	private void removeInvoice(Invoice i)
	{
		CostcoMoney = CostcoMoney + i.total;
		print("Costco Bank now has: $" + CostcoMoney);
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
}