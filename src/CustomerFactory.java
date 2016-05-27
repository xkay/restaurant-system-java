import java.util.Vector;

public class CustomerFactory extends Thread {

	private Vector<CustomerThread> customerThreadVector = new Vector<CustomerThread>();
	private Hostess hostessThread;
	
	public CustomerFactory(Hostess hostessThread) {
		this.hostessThread = hostessThread;;
		this.start();
	}
	
	public void run() {
		try {
			int customerNumber = 0;
			while (true) {
				CustomerThread ct = new CustomerThread(customerNumber++, hostessThread);
				customerThreadVector.add(ct);
				Thread.sleep(1000 * (int)(Math.random() * 5)); // customers come in between 0 and 5 seconds apart
			}
		} catch (InterruptedException ie) {
			System.out.println("CustomerFactory.run(): InterruptedException: " + ie.getMessage());
			for (CustomerThread ct : customerThreadVector) {
				ct.interrupt();
			}
		}
	}
}

class CustomerThread extends Thread {
	private int customerNumber;
	private Hostess hostessThread;
	private Table table;
	private Order order;
	
	public CustomerThread(int customerNumber, Hostess hostessThread) {
		this.customerNumber = customerNumber;
		this.hostessThread = hostessThread;
		this.start();
	}
	
	public void setOrder(Order o){
		this.order=o;
	}
	public int getCustomerNumber() {
		return this.customerNumber;
	}
	
	public Table getTable() {
		return this.table;
	}
	
	public void run() {
		try {
			table = hostessThread.seatCustomer(this);
			Thread.sleep(1000 * (int)(Math.random() * 10)); // sleep for between 0 and 10 seconds
			if(hostessThread != null){
				Restaurant.addMessage("Customer "+customerNumber+"  has received order "+order.getOrderText() +" at table "+getTable().getTableNumber());
				hostessThread.customerLeaving(this);
			}
		} catch (InterruptedException ie) {
			System.out.println("CustomerThread.run(): InterruptedException: " + ie.getMessage());
		}
	}
}