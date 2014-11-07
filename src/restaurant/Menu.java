package restaurant;


public class Menu {

	private double price; //price of food
	
	//list of food available
    public String choices[] = new String[]
	{ 
    	"Steak","Chicken","Salad","Pizza" 
	};
    
    //function returns the price of the choice the customer ordered. returns price of food
    public double getPrices(String choice)
    { 
    	if(choice == "Steak")
    		price = 15.99;
    	else if(choice == "Chicken")
    		price = 10.99;
    	else if(choice == "Salad")
    		price = 5.99;
    	else
    		price = 8.99;
    	
    	return price;
    }

}
    
