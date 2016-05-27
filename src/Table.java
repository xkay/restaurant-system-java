import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Table {
	
	private int tableNumber;

	private CustomerThread ct;
	private WaiterThread wt;
	private Lock lock = new ReentrantLock();
	private Condition readyCondition = lock.newCondition();
//	private BusboyThread bbt;
	
	public Table(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public int getTableNumber() {
		return this.tableNumber;
	}

	public CustomerThread getCustomer() {
		return ct;
	}
	
	public WaiterThread getWaiterThread() {
		return wt;
	}
	
	public Lock getLock() {
		return lock;
	}
	
	public Condition getReadyCondition() {
		return readyCondition;
	}

	public void seatTable(CustomerThread ct, WaiterThread wt) { //, BusboyThread bbt) {
		this.ct = ct;
		this.wt = wt;
//		this.bbt = bbt;
	}


}
