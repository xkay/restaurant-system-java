
public class Order {
	private int order;
	private int customerNumber;
	private Table table;
	private WaiterThread waiter;
	
	public Order( int o, int cn, Table t, WaiterThread w ) {
		order = o;
		customerNumber = cn;
		table = t;
		waiter = w;
	}
	
	public int getOrder() {
		return order;
	}
	
	public int getCustomerNumber() {
		return customerNumber;
	}
	
	public String getOrderText() {
		if ( order == 0 ) {
			return "Steak Dinner";
		} else if ( order == 1 ) {
			return "Seafood Dinner";
		} 
		
		return "Vegetarian Dinner";
	}
	
	public WaiterThread getWaiter() {
		return waiter;
	}
	
	public Table getTable() {
		return table;
	}
}
