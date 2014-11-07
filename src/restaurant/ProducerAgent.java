package restaurant;
import java.awt.Color;

import restaurant.CookAgent.Status;
import restaurant.CustomerAgent.AgentState;
import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import astar.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.Vector;
//import restaurant.CookAgent.Order;
//import Item;

public class ProducerAgent extends WaiterAgent
{	
	public ProducerConsumerMonitor monitor;
	public ProducerAgent(String name, AStarTraversal aStar,
			Restaurant restaurant, Table[] tables, ProducerConsumerMonitor monitor) {
		super(name, aStar, restaurant, tables);
		// TODO Auto-generated constructor stub
		//this.name = name;
		this.monitor = monitor;
	}
	
	//@Override
    /** Gives any pending orders to the cook 
     * @param customer customer that needs food cooked */
   protected void giveOrderToCook(MyCustomer customer) {
    	
    //ProducerConsumerMonitor p = new ProducerConsumerMonitor();
	//In our animation the waiter does not move to the cook in
	//order to give him an order. We assume some sort of electronic
	//method implemented as our message to the cook. So there is no
	//animation analog, and hence no DoXXX routine is needed.
	print("Putting " + customer.cmr + "'s choice of " + customer.choice + " to revolving stand");


	customer.state = CustomerState.NO_ACTION;
	//orders.add(new Order(this,customer.tableNum,customer.choice));
	Order o = new Order(this,customer.tableNum,customer.choice);
	monitor.insert(o);
	cook.msgWakeUp();
	print("did wakeup");
	stateChanged();
	
	//Here's a little animation hack. We put the first two
	//character of the food name affixed with a ? on the table.
	//Simply let's us see what was ordered.
	tables[customer.tableNum].takeOrder(customer.choice.substring(0,2)+"?");
	restaurant.placeFood(tables[customer.tableNum].foodX(),
			     tables[customer.tableNum].foodY(),
			     new Color(255, 255, 255), customer.choice.substring(0,2)+"?");
    }
    
	//@Override
    /** @return name of waiter */
   public String getName(){
        return name;
    }

    //@Override
    /** @return string representation of waiter */
  /*  public String toString(){
	return "producer " + getName();
    }*/
	
}