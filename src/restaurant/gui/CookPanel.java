package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


/** Panel in the contained in the restaurantPanel.
 * This holds the scroll panes for the customers and waiters */
public class CookPanel extends JPanel implements ActionListener{
    
    public JScrollPane pane = 
	new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private Vector<JButton> list = new Vector<JButton>();
    private JButton addItem = new JButton("Add");
    //private JButton addPersonB = new JButton("AddSpecialWaiter");
    
    private RestaurantPanel restPanel;
    private String type;

    /** Constructor for ListPanel.  Sets up all the gui
     * @param rp reference to the restaurant panel
     * @param type indicates if this is for customers or waiters */
    public CookPanel(RestaurantPanel rp, String type){
	restPanel = rp;
	this.type = type;

	setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
	add(new JLabel("<html><pre> <u>"+type+ "</u><br></pre></html>"));

	addItem.addActionListener(this);
	//addPersonB.addActionListener(this);
	add(addItem);
	//add(addPersonB);

	view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
	pane.setViewportView(view);
	add(pane);
    }

    /** Method from the ActionListener interface. 
     * Handles the event of the add button being pressed */
    public void actionPerformed(ActionEvent e){
	
	if(e.getSource() == addItem)
	{
	    addPerson(JOptionPane.showInputDialog("Please enter a name:"),JOptionPane.showInputDialog("Please enter amount:"));
	}
	/*else if(e.getSource() == addPersonB)
	{
		addWaiter(JOptionPane.showInputDialog("Please enter a name:"));
	}*/
	else {

	    for(int i=0; i < list.size(); i++){
		JButton temp = list.get(i);

		if(e.getSource() == temp)
		    restPanel.showInfo(type, temp.getText());		
	    }
	}
    }

    /** If the add button is pressed, this function creates 
     * a spot for it in the scroll pane, and tells the restaurant panel 
     * to add a new person.
     * @param name name of new person */
    public void addPerson(String name,String amount)
    {
    	Integer n;
    	if(name != null)
    	{
    		n = Integer.parseInt(amount);
    		createIt(name,n);
    	}
    }
    
    void createIt(String name,int amount){
	//System.out.println("createIt name="+name+"XX"); 
	JButton button = new JButton(name);
	button.setBackground(Color.white);

	Dimension paneSize = pane.getSize();
	Dimension buttonSize = new Dimension(paneSize.width-20, 
					     (int)(paneSize.height/7));
	button.setPreferredSize(buttonSize);
	button.setMinimumSize(buttonSize);
	button.setMaximumSize(buttonSize);
	button.addActionListener(this);
	list.add(button);
	view.add(button);
	restPanel.addFood(type, name, amount);
	validate();
    }
}