package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


/** Panel in the contained in the restaurantPanel.
 * This holds the scroll panes for the customers and waiters */
public class ListPanel extends JPanel implements ActionListener{
    
    public JScrollPane pane = 
	new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private Vector<JButton> list = new Vector<JButton>();
    private JButton addPersonB = new JButton("Add");
    private JButton addThree = new JButton("addThree");
    
    private RestaurantPanel restPanel;
    private String type;
    private Object person;

    /** Constructor for ListPanel.  Sets up all the gui
     * @param rp reference to the restaurant panel
     * @param type indicates if this is for customers or waiters */
    public ListPanel(RestaurantPanel rp, String type){
	restPanel = rp;
	this.type = type;

	setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
	add(new JLabel("<html><pre> <u>"+type+ "</u><br></pre></html>"));

	addPersonB.addActionListener(this);
	addThree.addActionListener(this);
	add(addPersonB);
	add(addThree);

	view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
	pane.setViewportView(view);
	add(pane);
    }

    /** Method from the ActionListener interface. 
     * Handles the event of the add button being pressed */
    public void actionPerformed(ActionEvent e){
	
	if(e.getSource() == addPersonB)
	    addPerson(JOptionPane.showInputDialog("Please enter a name:"));
	else if(e.getSource() == addThree)
	{
		addTriple(JOptionPane.showInputDialog("Please enter a name:"),JOptionPane.showInputDialog("Please enter a amount:"));//,JOptionPane.showInputDialog("Please enter a name:"),JOptionPane.showInputDialog("Please enter a name:"));
		addTriple(JOptionPane.showInputDialog("Please enter a name:"),JOptionPane.showInputDialog("Please enter a amount:"));
		addTriple(JOptionPane.showInputDialog("Please enter a name:"),JOptionPane.showInputDialog("Please enter a amount:"));
	}
	/*else if(e.getSource() == wait)
	{
		if(person instanceof CustomerAgent){
		    CustomerAgent customer = (CustomerAgent) person;
		    customer.setLeave(false);

		}
	}
	else if(e.getSource() == leave)
	{
		if(person instanceof CustomerAgent){
		    CustomerAgent customer = (CustomerAgent) person;
		    customer.setLeave(true);

		}
	}*/
	/*else if(e.getSource() == cheap)
	{
		
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
    
    public void addPerson(String name){
	if(name != null){
	    try {
		String c;
		if (type.equals("Waiters")) 
			c="w"; 
		else if(type.equals("Producers"))
			c = "s";
		else 
			c="c"; 
		int n = Integer.valueOf( name ).intValue();
		for (int i=1; i<=n; i++) createIt(c+i);
	    }
	    catch (NumberFormatException e) {
		createIt(name);
	    }
	}
    }
    
    public void addTriple(String name,String amount){
		double n2;
		n2 = Double.parseDouble(amount);
		createThree(name,n2);
	    
	}
    
    void createIt(String name){
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
	restPanel.addPerson(type, name);
	validate();
    }
    
    void createThree(String name, double n2){
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
	restPanel.addPersonThree(type, name, n2);
	validate();
    }
}
