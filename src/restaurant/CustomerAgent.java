package restaurant;

import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.awt.Color;

import javax.naming.InvalidNameException;

/** Restaurant customer agent. 
 * Comes to the restaurant when he/she becomes hungry.
 * Randomly chooses a menu item and simulates eating 
 * when the food arrives. 
 * Interacts with a waiter only */
public class CustomerAgent extends Agent {
    private String name;
    private int hungerLevel = 5;  // Determines length of meal
    private RestaurantGui gui;
    private double total;
    private int billNumber;
    private double customerMoney = 50.0;
    //public Semaphore s = new Semaphore(1);
    // ** Agent connections **
    private HostAgent host;
    private WaiterAgent waiter;
    Restaurant restaurant;
    private Menu menu;
    Timer timer = new Timer();
    GuiCustomer guiCustomer; //for gui
   // ** Agent state **
    private boolean isHungry = false; //hack for gui
    public enum AgentState
	    {DoingNothing, WaitingInRestaurant, SeatedWithMenu, WaiterCalled, WaitingForFood, Eating, PayingBill,AboutToLeave,NotWaiting};
	//{NO_ACTION,NEED_SEATED,NEED_DECIDE,NEED_ORDER,NEED_EAT,NEED_LEAVE};
    private AgentState state = AgentState.DoingNothing;//The start state
    public enum AgentEvent 
	    {gotHungry, beingSeated, decidedChoice, waiterToTakeOrder, waiterToReTakeOrder, foodDelivered, doneEating, donePaying,Leaving};
    List<AgentEvent> events = new ArrayList<AgentEvent>();
    
    /** Constructor for CustomerAgent class 
     * @param name name of the customer
     * @param gui reference to the gui so the customer can send it messages
     */
    public CustomerAgent(String name, RestaurantGui gui, Restaurant restaurant) {
	super();
	this.gui = gui;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,2), new Color(0,255,0), restaurant);
    }
    public CustomerAgent(String name, Restaurant restaurant) {
	super();
	this.gui = null;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,1), new Color(0,255,0), restaurant);
    }
    // *** MESSAGES ***
    
    public void msgDoYouWantToWait(CustomerAgent customer)
    {
    	
    }
    /** Sent from GUI to set the customer as hungry */
    public void setHungry() {
	events.add(AgentEvent.gotHungry);
	isHungry = true;
	print("I'm hungry");
	stateChanged();
    }
    /** Waiter sends this message so the customer knows to sit down 
     * @param waiter the waiter that sent the message
     * @param menu a reference to a menu */
    public void msgFollowMeToTable(WaiterAgent waiter, Menu menu) {
	this.menu = menu;
	this.waiter = waiter;
	print("Received msgFollowMeToTable from" + waiter);
	events.add(AgentEvent.beingSeated);
	stateChanged();
    }
    /** Waiter sends this message to take the customer's order */
    public void msgDecided(){
	events.add(AgentEvent.decidedChoice);
	stateChanged(); 
    }
    /** Waiter sends this message to take the customer's order */
    public void msgWhatWouldYouLike(){
	events.add(AgentEvent.waiterToTakeOrder);
	stateChanged(); 
    }

    /*waiter sends this message to tell customer to 
     * reorder since we ran out of his first choice */
    public void msgPleaseReOrder()
    {
    	state = AgentState.WaiterCalled;
    	events.add(AgentEvent.waiterToReTakeOrder);
    	stateChanged();
    }
    
    /** Waiter sends this when the food is ready 
     * @param choice the food that is done cooking for the customer to eat */
    public void msgHereIsYourFood(String choice) {
	events.add(AgentEvent.foodDelivered);
	stateChanged();
    }
    /** Timer sends this when the customer has finished eating */
    public void msgDoneEating() {
	events.add(AgentEvent.doneEating);
	stateChanged(); 
    }
    
    /* waiter sends this msg to give bill to customer */
    public void msgHereIsYourBill(double amount, int billNumber)
    {
    	print("Customer receives bill");
    	this.total = amount;
    	this.billNumber = billNumber;
    	events.add(AgentEvent.donePaying);
    	stateChanged();
    }
    
    //waiter lets customer know he/she can leave
    public void msgYouCanLeave()
    {
    	print("Customer notified he/she can leave");
    	state = AgentState.AboutToLeave;
    	events.add(AgentEvent.Leaving);
    	stateChanged();
    }

    public void setLeave(boolean leaving)
    {
    	print("leaving is " + leaving);
    	if(leaving == true)
    	{
    		LeavingNotWaiting();
    		stateChanged();
    	}
    	else if(leaving == false)
    	{
    		print("I will wait!");
    	}
    		
    }

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	if (events.isEmpty()) return false;
	AgentEvent event = events.remove(0); //pop first element
	
	//Simple finite state machine
	if (state == AgentState.DoingNothing){
	    if (event == AgentEvent.gotHungry)	{
		goingToRestaurant();
		state = AgentState.WaitingInRestaurant;
		return true;
	    }
	    // elseif (event == xxx) {}
	}
	if (state == AgentState.WaitingInRestaurant) {
	    if (event == AgentEvent.beingSeated)	{
		makeMenuChoice();
		state = AgentState.SeatedWithMenu;
		return true;
	    }
	}
	if (state == AgentState.SeatedWithMenu)
	{
	    if (event == AgentEvent.decidedChoice)
	    {
	    	//callWaiter();
	    	orderingFood(event);
	    	state = AgentState.WaiterCalled;
	    	return true;
	    }	
	}
	if (state == AgentState.WaiterCalled) 
	{
	    if (event == AgentEvent.waiterToTakeOrder)
	    {
	    	orderingFood(event);
	    	state = AgentState.WaitingForFood;
			return true;
	    }
	    else if(event == AgentEvent.waiterToReTakeOrder)
	    {
	    	orderingFood(event);
	    	state = AgentState.WaitingForFood;
			return true;
	    }
	}
	if (state == AgentState.WaitingForFood) {
	    if (event == AgentEvent.foodDelivered)	{
		eatFood();
		state = AgentState.Eating;
		return true;
	    }
	}
	if (state == AgentState.Eating) {
	    if (event == AgentEvent.doneEating)	
	    {
		    Paying();	
			state = AgentState.PayingBill;
			return true;
	    }
	}
	if(state == AgentState.PayingBill)
	{
		if(event == AgentEvent.donePaying)
		{
			BillReceived();
			state = AgentState.AboutToLeave;
			return true;
		}
	}
	if(state == AgentState.AboutToLeave)
	{
		if(event == AgentEvent.Leaving)
		{
			leaveRestaurant();
			state = AgentState.DoingNothing;
			return true;
		}
	}
	/*if(state == AgentState.NotWaiting)
	{
		if(event == AgentEvent.Leaving)
		{
			LeavingNotWaiting();
			state = AgentState.DoingNothing;
			return true;
		}
	}*/

	print("No scheduler rule fired, should not happen in FSM, event="+event+" state="+state);
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Goes to the restaurant when the customer becomes hungry */
    private void goingToRestaurant() {
	print("Going to restaurant");
	guiCustomer.appearInWaitingQueue();
	host.msgIWantToEat(this);//send him our instance, so he can respond to us
	stateChanged();
    }
    
    /** Starts a timer to simulate the customer thinking about the menu */
    private void makeMenuChoice(){
	print("Deciding menu choice...(3000 milliseconds)");
	timer.schedule(new TimerTask() {
	    public void run() {  
		msgDecided();	    
	    }},
	    3000);//how long to wait before running task
	stateChanged();
    }
    
    private void orderingFood(AgentEvent event)
    {
    	if(event == AgentEvent.decidedChoice)
    		callWaiter();
    	else if(event == AgentEvent.waiterToTakeOrder)
    		orderFood();
    	else if(event == AgentEvent.waiterToReTakeOrder)
    		orderFood();
    }
    
    private void callWaiter(){
	print("I decided!");
	waiter.msgImReadyToOrder(this);
	stateChanged();
    }
    /** Picks a random choice from the menu and sends it to the waiter */
    private void orderFood()
    {
    
		String choice;
		if(customerMoney == 5.99)  //customer with money to buy cheapest item
		{
			choice = "Salad";
			print("Ordering the " + choice);
			waiter.msgHereIsMyChoice(this, choice);
			stateChanged();
		}
		else if((customerMoney < 5.99) && (customerMoney != 1)) //customer with not enough money but decides to eat anyways
		{
			choice = "Steak";
			print("Ordering the " + choice);
			waiter.msgHereIsMyChoice(this, choice);
			stateChanged();
		}
		else if(customerMoney == 1) //customer thinks place is too expensive
		{
			print("THIS PLACE IS TOO EXPENSIVE!!! IM LEAVING!");
			print("Leaving the restaurant");
			guiCustomer.leave(); //for the animation
			waiter.msgLeaving(this);
			isHungry = false;
			stateChanged();
			gui.setCustomerEnabled(this); //Message to gui to enable hunger button
	
			//hack to keep customer getting hungry. Only for non-gui customers
			if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
		}
		else //customer has more than enough
		{
			choice = menu.choices[(int)(Math.random()*4)];
			print("Ordering the " + choice);
			//waiter.s.release();
			waiter.msgHereIsMyChoice(this, choice);
			stateChanged();
		}
		
    }

    /** Starts a timer to simulate eating */
    private void eatFood() {
	print("Eating for " + hungerLevel*1000 + " milliseconds.");
	timer.schedule(new TimerTask() {
	    public void run() {
		msgDoneEating();    
	    }},
	    getHungerLevel() * 1000);//how long to wait before running task
	stateChanged();
    }
    
    //customer sends msg to waiter to let him know hes ready to pay
    private void Paying()
    {
    	print("Letting waiter know to get bill");
    	waiter.msgReadyToPay(this);
    	stateChanged();
    }
    
    //customer receives bill and pays then lets waiter know he has paid 
    private void BillReceived()
    {
    	//print("Customer paying");
    	customerMoney = customerMoney - this.total;
    	if(customerMoney < 0)
    	{
    		print("CUSTOMER DOES NOT HAVE ENOUGH MONEY!!");
    		waiter.msgNotEnoughMoney(this.billNumber);
    		stateChanged();
    	}
    	else
    	{
    		print("Customer paying");
    		waiter.msgBillIsPaid(this.billNumber);
    		stateChanged();
    	}
    }

    /** When the customer is done eating, he leaves the restaurant */
    private void leaveRestaurant() {
	print("Leaving the restaurant");
	guiCustomer.leave(); //for the animation
	waiter.msgDoneEatingAndLeaving(this);
	isHungry = false;
	stateChanged();
	gui.setCustomerEnabled(this); //Message to gui to enable hunger button

	//hack to keep customer getting hungry. Only for non-gui customers
	if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
    }
    
    private void LeavingNotWaiting()
    {
        print("Leaving the restaurant, not waiting");
        guiCustomer.leave(); //for the animation
        host.msgTakeMeOffList(this);
        waiter.msgImNotWaiting(this);
        isHungry = false;
        stateChanged();
        gui.setCustomerEnabled(this); //Message to gui to enable hunger button

        //hack to keep customer getting hungry. Only for non-gui customers
        if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
            
    }
    
    /** This starts a timer so the customer will become hungry again.
     * This is a hack that is used when the GUI is not being used */
    private void becomeHungryInAWhile() {
	timer.schedule(new TimerTask() {
	    public void run() {  
		setHungry();		    
	    }},
	    15000);//how long to wait before running task
    }

    // *** EXTRA ***

    /** establish connection to host agent. 
     * @param host reference to the host */
    public void setHost(HostAgent host) {
		this.host = host;
    }
    
    public void setMoney(double amount)
    {
    	customerMoney = amount;
    	print("customer money is $" + customerMoney);
    }
    
    /** Returns the customer's name
     *@return name of customer */
    public String getName() {
	return name;
    }

    /** @return true if the customer is hungry, false otherwise.
     ** Customer is hungry from time he is created (or button is
     ** pushed, until he eats and leaves.*/
    public boolean isHungry() {
	return isHungry;
    }

    /** @return the hungerlevel of the customer */
    public int getHungerLevel() {
	return hungerLevel;
    }
    
    /** Sets the customer's hungerlevel to a new value
     * @param hungerLevel the new hungerlevel for the customer */
    public void setHungerLevel(int hungerLevel) {
	this.hungerLevel = hungerLevel; 
    }
    public GuiCustomer getGuiCustomer(){
	return guiCustomer;
    }
    
    /** @return the string representation of the class */
    public String toString() {
	return "customer " + getName();
    }

    
}

