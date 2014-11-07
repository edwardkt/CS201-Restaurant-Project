package restaurant;

import agent.Agent;
import java.util.*;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent {

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num){
		    tableNum = num;
		    occupied = false;
		}	
    }

    public enum WaiterBreak {CanNotGoOnBreak,CanGoOnBreak};
    /** Private class to hold waiter information and state */
    private class MyWaiter {
	public WaiterAgent wtr;
	public boolean working = true;
	public WaiterBreak status;

	/** Constructor for MyWaiter class
	 * @param waiter
	 */
	public MyWaiter(WaiterAgent waiter){
	    wtr = waiter;
	    this.status = WaiterBreak.CanNotGoOnBreak;
	}
    }

    //List of all the customers that need a table
    private List<CustomerAgent> waitList =
		Collections.synchronizedList(new ArrayList<CustomerAgent>());

    //List of all waiter that exist.
    private List<MyWaiter> waiters =
		Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter =0; //The next waiter that needs a customer
    
    //List of all the tables
    int nTables;
    private Table tables[];

    //Name of the host
    private String name;
    Timer timer = new Timer();

    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
	super();
	this.nTables = ntables;
	tables = new Table[nTables];

	for(int i=0; i < nTables; i++){
	    tables[i] = new Table(i);
	}
	this.name = name;
    }

    // *** MESSAGES ***

    /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public void msgIWantToEat(CustomerAgent customer){
	waitList.add(customer);
	stateChanged();
    }

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public void msgTableIsFree(int tableNum){
	tables[tableNum].occupied = false;
	stateChanged();
    }

    public void msgBreakDone(WaiterAgent w)
    {
    	for(MyWaiter waiter: waiters)
    	{
    		if(waiter.wtr.equals(w))
    			waiter.working = true;
    	}
    }
    
    public void msgCanIGoOnBreak(WaiterAgent w)
    {
    	
    	for(MyWaiter waiter: waiters)
    	{
    		if(waiter.wtr.equals(w) && (waiters.size() >= 2))
    		{
    			print("you may go on break");
    			//waiter.status = WaiterBreak.CanGoOnBreak;
    			waiter.working = false;
    			BreakWaiter(waiter.wtr);
    			stateChanged();
    		}
    		/*else
    		{
    			print("you can not go on break");
    			waiter.status = WaiterBreak.CanNotGoOnBreak;
    			//waiter.working = true;
    			stateChanged();
    		}*/
    	}
    	
    	/*print("host determining if waiter can go on break");
    	//MyWaiter waiter = new MyWaiter(w);
    		for(MyWaiter waiter: waiters)
    		{
    			if(waiter.wtr.equals(w))
    			{
    				//print("waiter found");
    				if((w.isOnBreak() == true) && (waiters.size() >= 2))
    				{
    					print("waiter can go on break");
    					waiter.working = false;
    					waiter.status = WaiterBreak.IsOnBreak;
    					stateChanged();
    				}
        			else
        			{
        				print("can't go on break");
        				waiter.status = WaiterBreak.NotOnBreak;
        				stateChanged();
        			}
    			}
    		}*/
    			
    	/*MyWaiter waiter = new MyWaiter(w);
    	if(waitList.isEmpty() && )*/
    		
    }
    
    public void msgTakeMeOffList(CustomerAgent customer)
    {
    	for(int i = 0; i < waitList.size(); i++)
    	{
    		if(waitList.get(i).equals(customer))
    		{
    			print("removed " + waitList.get(i).getName());
    			waitList.remove(i);
    		}
    	}
    }
    
    
    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
	if(!waitList.isEmpty() && !waiters.isEmpty())
	{
	    synchronized(waiters)
	    {
			//Finds the next waiter that is working
			while(!waiters.get(nextWaiter).working)
			{
			    nextWaiter = (nextWaiter+1)%waiters.size();
			}
	    }
	    print("picking waiter number:"+nextWaiter);
	    print("waitList size: " + waitList.size());
	    //Then runs through the tables and finds the first unoccupied 
	    //table and tells the waiter to sit the first customer at that table
	    for(int i=0; i < nTables; i++)
	    {
			if(!tables[i].occupied)
			{
			    synchronized(waitList)
			    {
			    	tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),
				    waitList.get(0), i);
			    }
			    return true;
			}
	    }	
	}
	if(!waitList.isEmpty())
	{
		print("THERE ARE CUSTOMERS WAITING!");
		for(int i = 0; i < waitList.size(); i++)
		{
			CustomerAgent c;
			c = waitList.get(i);
			askCustomerToWait(c);
		}
	}
	/*for(MyWaiter w: waiters)
	{
		if(w.status == WaiterBreak.CanGoOnBreak)
			BreakWaiter(w);*/
		/*else if(w.status == WaiterBreak.CanNotGoOnBreak)
			DoNotBreakWaiter(w);*/
	//}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, CustomerAgent customer, int tableNum){
	print("Telling " + waiter.wtr + " to sit " + customer +" at table "+(tableNum+1));
	waiter.wtr.msgSitCustomerAtTable(customer, tableNum);
	tables[tableNum].occupied = true;
	waitList.remove(customer);
	nextWaiter = (nextWaiter+1)%waiters.size();
	stateChanged();
    }
    
    private void BreakWaiter(WaiterAgent waiter)
    {
    	print("host says waiter can go on break");
    	waiter.msgGoOnBreak(waiter);
		/*print("Taking Break " + waiter.toString() + " (2000 milliseconds)");
		timer.schedule(new TimerTask()
		{
		    public void run()
		    {		 
		    	breakDone(waiter);
		    	//endCustomer(customer);
		    }
		}, 2000);*/
    }
    private void DoNotBreakWaiter(MyWaiter waiter)
    {
    	print("host says can not go on break");
    	waiter.wtr.msgCantGoOnBreak();
    }
    
    private void askCustomerToWait(CustomerAgent customer)
    {
    	print("host asks customer if he wants to wait");
    	customer.msgDoYouWantToWait(customer);
    }
   /* private void breakDone(MyWaiter waiter)
    {
    	print("waiter now off break");
    	boolean onBreak = waiter.wtr.isOnBreak();
    	onBreak = false;
    	waiter.wtr.setBreakStatus(onBreak);
    	//waiter.wtr.msgBreakIsDone();
    }*/

  /*  private void waiterNoBreak(MyWaiter waiter)
    {
    	print("waiter can't go on break!");
    	boolean onBreak = waiter.wtr.isOnBreak();
    	onBreak = false;
    	waiter.wtr.setBreakStatus(onBreak);
    }*/
    

    // *** EXTRA ***

    /** Returns the name of the host 
     * @return name of host */
    public String getName(){
        return name;
    }    

    /** Hack to enable the host to know of all possible waiters 
     * @param waiter new waiter to be added to list
     */
    public void setWaiter(WaiterAgent waiter){
	waiters.add(new MyWaiter(waiter));
	stateChanged();
    }
    
    //Gautam Nayak - Gui calls this when table is created in animation
    public void addTable() {
	nTables++;
	Table[] tempTables = new Table[nTables];
	for(int i=0; i < nTables - 1; i++){
	    tempTables[i] = tables[i];
	}  		  			
	tempTables[nTables - 1] = new Table(nTables - 1);
	tables = tempTables;
    }
}
