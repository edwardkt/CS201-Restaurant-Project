package restaurant;

import agent.Agent;
import astar.AStarTraversal;

import java.util.*;

//import restaurant.WaiterAgent.Order;
//import restaurant.WaiterAgent.Order;
import restaurant.layoutGUI.*;

import java.awt.Color;
import java.util.Vector;
import restaurant.Order;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent {

    //List of all the orders
    private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
    private Map<String,FoodData> inventory = new HashMap<String,FoodData>();
    public enum Status {pending, cooking, done}; // order status
    public enum orderStatus {notOrdered,ordered,reOrder,DELIVERED_BY_COSTCO,DELIVERED_BY_RALPHS}; //food order status
    private orderStatus orderState; //food order status
    private CostcoMarketAgent costco; //market #1
    private RalphsMarketAgent ralphs; //market #2
    private int SteakAmount;
    private int ChickenAmount;
    private int PizzaAmount;
    private int SaladAmount;
    private int amountDelivered;
    private String choiceDelivered;
    public ProducerConsumerMonitor monitor;

    //Name of the cook
    private String name;

    //Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant, ProducerConsumerMonitor monitor) {
	super();

	this.name = name;
	this.orderState = orderStatus.notOrdered;
	this.restaurant = restaurant;
	this.monitor = monitor;
	//Create the restaurant's inventory.
	inventory.put("Steak",new FoodData("Steak", 5));
	inventory.put("Chicken",new FoodData("Chicken", 4));
	inventory.put("Pizza",new FoodData("Pizza", 3));
	inventory.put("Salad",new FoodData("Salad", 2));
    }
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    private class FoodData {
	String type; //kind of food
	double cookTime;
	// other things ...
	
	public FoodData(String type, double cookTime){
	    this.type = type;
	    this.cookTime = cookTime;
	}
    }

    


    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
    public void msgHereIsAnOrder(WaiterAgent waiter, int tableNum, String choice){
    //orders = orders2;
	orders.add(new Order(waiter,tableNum,choice));
	stateChanged();
    }
    
    public void msgWakeUp()
    {
    	print("does wake up " + monitor.getSize());
    	stateChanged();
    }
    public void msgImDeliveringOrder(int amount,String choice)
    {
    	this.amountDelivered = amount;
    	this.choiceDelivered = choice;
    	print("Delivered to Cook");
    	if(choice.equals("Steak"))
    		SteakAmount += amount;
    	else if(choice.equals("Chicken"))
    		ChickenAmount += amount;
    	else if(choice.equals("Pizza"))
    		PizzaAmount += amount;
    	else if(choice.equals("Salad"))
    		SaladAmount += amount;
    	orderState = orderStatus.DELIVERED_BY_COSTCO;
    	stateChanged();
    }
    
    public void msgRalphsDelivering(int amount,String choice)
    {
    	this.amountDelivered = amount;
    	this.choiceDelivered = choice;
    	print("Delivered to Cook");
    	if(choice.equals("Steak"))
    		SteakAmount += amount;
    	else if(choice.equals("Chicken"))
    		ChickenAmount += amount;
    	else if(choice.equals("Pizza"))
    		PizzaAmount += amount;
    	else if(choice.equals("Salad"))
    		SaladAmount += amount;
    	orderState = orderStatus.DELIVERED_BY_RALPHS;
    	stateChanged();
    }
    //cook notified costco is out of items he needs, now he will try ordering from ralphs
    public void msgSorryImOut(String choice, int amount)
    {
    	orderState = orderStatus.reOrder;
    	print("Cook notified his favorite market has ran out of his order.");
    	stateChanged();
    }


    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() 
    {
    	if(monitor.getSize() > 0)
    	{
    		print("adding to list");
    		Order o = monitor.remove();
    		orders.add(o);
    		print("orders size: " + orders.size() + orders.get(0).status);
    	}
    	
   
		//If there exists an order o whose status is done, place o.
	    synchronized(orders)
	    {
			for(Order o:orders)
			{
			    if(o.status == Status.done)
			    {
			    	placeOrder(o);
			    	return true;
			    }
			}
		}
	    
	    
	//cooks order if there is an item left even if its low
	synchronized(orders)
	{
		for(int i = 0; i < orders.size(); i++)
		{
			if(orders.get(i).status == Status.pending)
			{
				if(orders.get(i).choice.equals("Steak"))
				{
					if(SteakAmount != 0)
					{
						//SteakAmount--;
						//print("You have " + SteakAmount + " Steaks left.");
						cookOrder(orders.get(i));
						return true;
					}
				}
				else if(orders.get(i).choice.equals("Chicken"))
				{
					if(ChickenAmount != 0)
					{
						//ChickenAmount--;
						//print("You have " + ChickenAmount + " Chickens left.");
						cookOrder(orders.get(i));
						return true;
					}
				}
				else if(orders.get(i).choice.equals("Pizza"))
				{
					if(PizzaAmount != 0)
					{
						//PizzaAmount--;
						//print("You have " + PizzaAmount + " Pizzas left.");
						cookOrder(orders.get(i));
						return true;
					}
				}
				else if(orders.get(i).choice.equals("Salad"))
				{
					if(SaladAmount != 0)
					{
						//SaladAmount--;
						//print("You have " + SaladAmount + " Salads left.");
						cookOrder(orders.get(i));
						return true;
					}
				}
			}
		}
	}
	synchronized(orders)
	{
		for(int i = 0; i < orders.size(); i++)
		{
			if((SteakAmount < 3) && (orderState == orderStatus.notOrdered))
			{
					orderFood("Steak");
					return true;
			}
			else if((SteakAmount == 0) && (orderState == orderStatus.ordered) && (orders.get(i).status != Status.cooking))
			{
					OutOfItem(orders.get(i));
					return true;		
				
			}
			else if((SteakAmount < 3) && (orderState == orderStatus.reOrder))
			{
				reOrderFood("Steak");
				return true;
			}
		}
	}
	synchronized(orders)
	{
		for(int i = 0; i < orders.size(); i++)
		{
			if((ChickenAmount < 3) && (orderState == orderStatus.notOrdered))
			{
					orderFood("Chicken");
					return true;
			}
			else if((ChickenAmount == 0) && (orderState == orderStatus.ordered) && (orders.get(i).status != Status.cooking))
			{
					OutOfItem(orders.get(i));
					return true;		
				
			}
			else if((ChickenAmount < 3) && (orderState == orderStatus.reOrder))
			{
				reOrderFood("Chicken");
				return true;
			}
		}
	}
	synchronized(orders)
	{
		for(int i = 0; i < orders.size(); i++)
		{
			if((PizzaAmount < 3) && (orderState == orderStatus.notOrdered))
			{
					orderFood("Pizza");
					return true;
			}
			else if((PizzaAmount == 0) && (orderState == orderStatus.ordered) && (orders.get(i).status != Status.cooking))
			{
					OutOfItem(orders.get(i));
					return true;		
				
			}
			else if((PizzaAmount < 3) && (orderState == orderStatus.reOrder))
			{
				//print("goes in reorder pizza");
				reOrderFood("Pizza");
				return true;
			}
		}
	}
	synchronized(orders)
	{
		for(int i = 0; i < orders.size(); i++)
		{
			if((SaladAmount < 3) && (orderState == orderStatus.notOrdered))
			{
					orderFood("Salad");
					return true;
			}
			else if((SaladAmount == 0) && (orderState == orderStatus.ordered) && (orders.get(i).status != Status.cooking))
			{
					OutOfItem(orders.get(i));
					return true;		
				
			}
			else if((SaladAmount < 3) && (orderState == orderStatus.reOrder))
			{
				//print("goes in reorder salad");
				reOrderFood("Salad");
				return true;
			}
		}
	}
	
	if(orderState == orderStatus.DELIVERED_BY_COSTCO)
	{
		foodDeliveredByCostco();
		return true;
	}
	else if(orderState == orderStatus.DELIVERED_BY_RALPHS)
	{
		foodDeliveredByRalphs();
		return true;
	}
    	
	
	//}
		//}
	//}
	//If there exists an order o whose status is pending, cook o.
	//If there exists an order o whose status is pending and we are out of the item, notify waiter to let customer know
	//If there exists an order and the item is low, order item from market
	/*for(int i = 0; i < orders.size(); i++)
	{
		if(orders.get(i).status == Status.pending)
		{
			if(orders.get(i).choice.equals("Steak"))
			{
				if(SteakAmount != 0)
				{
					//SteakAmount--;
					//print("You have " + SteakAmount + " Steaks left.");
					cookOrder(orders.get(i));
					return true;
				}
			}
			else if(orders.get(i).choice.equals("Chicken"))
			{
				if(ChickenAmount != 0)
				{
					//ChickenAmount--;
					//print("You have " + ChickenAmount + " Chickens left.");
					cookOrder(orders.get(i));
					return true;
				}
			}
			else if(orders.get(i).choice.equals("Pizza"))
			{
				if(PizzaAmount != 0)
				{
					//PizzaAmount--;
					//print("You have " + PizzaAmount + " Pizzas left.");
					cookOrder(orders.get(i));
					return true;
				}
			}
			else if(orders.get(i).choice.equals("Salad"))
			{
				if(SaladAmount != 0)
				{
					//SaladAmount--;
					//print("You have " + SaladAmount + " Salads left.");
					cookOrder(orders.get(i));
					return true;
				}
			}
		}
	}*/
	
	//if there exists no orders then check inventory and order food as needed
	/*if(orders.size() == 0)
	{
		print("FUCK THIS SHIT");
		if(orderState == orderStatus.notOrdered)
		{
			if(SteakAmount < 3)
			{
				orderFood("Steak");
				return true;
			}
			else if(ChickenAmount < 3)
			{
				orderFood("Chicken");
				return true;
			}
			else if(PizzaAmount < 3)
			{
				orderFood("Pizza");
				return true;
			}
			else if(SaladAmount < 3)
			{
				orderFood("Salad");
				return true;
			}
		}
	}*/

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order){
    DoCooking(order);
	order.status = Status.cooking;
    }

    private void placeOrder(Order order){
	DoPlacement(order);
	if(order.choice.equals("Steak"))
	{
		SteakAmount--;
		print("You have " + SteakAmount + " Steaks left.");
	}
	else if(order.choice.equals("Chicken"))
	{
		ChickenAmount--;
		print("You have " + ChickenAmount + " Chickens left.");
	}
	else if(order.choice.equals("Pizza"))
	{
		PizzaAmount--;
		print("You have " + PizzaAmount + " Pizza left.");
	}
	else if(order.choice.equals("Salad"))
	{
		SaladAmount--;
		print("You have " + SaladAmount + " Salads left.");
	}
	order.waiter.msgOrderIsReady(order.tableNum, order.food);
	synchronized(orders)
	{
		orders.remove(order);
	}
    }
    
    /*chef lets waiter know the item is out */
    private void OutOfItem(Order order)
    {
    	order.waiter.msgSorryOutOfItem(order.tableNum,order.food);
    	synchronized(orders)
    	{
    		orders.remove(order);
    	}
    }
    
    /*chef lets market know what item and how much to order */
    private void orderFood(String choice)
    {
    	print("Cook ordering food");
    	orderState = orderStatus.ordered;
    	if(choice.equals("Steak"))
    	{
    		costco.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    	else if(choice.equals("Chicken"))
    	{
    		costco.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    	else if(choice.equals("Pizza"))
    	{
    		costco.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    	else if(choice.equals("Salad"))
    	{
    		costco.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    }
    
    /*chef lets market know what item and how much to order */
    private void reOrderFood(String choice)
    {
    	print("Cook ordering food");
    	orderState = orderStatus.ordered;
    	if(choice.equals("Steak"))
    	{
    		ralphs.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    	else if(choice.equals("Chicken"))
    	{
    		ralphs.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    	else if(choice.equals("Pizza"))
    	{
    		ralphs.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    	else if(choice.equals("Salad"))
    	{
    		ralphs.msgPleaseDeliver(choice,5);
    		stateChanged();
    	}
    }
    
    //food delivered by Costco
    private void foodDeliveredByCostco()
    {
    	print("Cook received delivery from costco");
    	orderState = orderStatus.notOrdered;
    	costco.msgThankYou(this.choiceDelivered);
    	stateChanged();
    }
    
    //food delivered by Ralphs
    private void foodDeliveredByRalphs()
    {
    	print("Cook received delivery from ralphs");
    	orderState = orderStatus.notOrdered;
    	ralphs.msgThankYou(this.choiceDelivered);
    	stateChanged();
    }


    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName(){
        return name;
    }

    private void DoCooking(final Order order){
    	print("Cooking:" + order + " for table:" + (order.tableNum+1));
	//put it on the grill. gui stuff
	order.food = new Food(order.choice.substring(0,2),new Color(0,255,255), restaurant);
	order.food.cookFood();

	timer.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception    
		order.status = Status.done;
		stateChanged();
	    }
	}, (int)(inventory.get(order.choice).cookTime*1000));
    }
    public void DoPlacement(Order order){
    	print("Order finished: " + order + " for table:" + (order.tableNum+1));
	order.food.placeOnCounter();
    }
    
    //gui sets food for the restaurant
    public void setFood(int count,String name)
    {
    	if(name.equals("Steak"))
    		SteakAmount = count;
    	else if(name.equals("Chicken"))
    		ChickenAmount = count;
    	else if(name.equals("Pizza"))
    		PizzaAmount = count;
    	else if(name.equals("Salad"))
    		SaladAmount = count;
    	print("added " + count + " " + name + "(s) to " + this.name + "s market");
    }
    
    public void setMarket(CostcoMarketAgent costco)
    {
    	this.costco = costco;
    }
    
    public void setMarket2(RalphsMarketAgent ralphs)
    {
    	this.ralphs = ralphs;
    }
    
}


    
