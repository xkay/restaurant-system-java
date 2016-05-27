import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaiterFactory {

	private Vector<WaiterThread> waiterThreadVector = new Vector<WaiterThread>();
	private int numWaiters;
	private int numTablesPerWaiter;
	private Hostess hostess;
	private Lock lock = new ReentrantLock();
	private Condition waiterAvailable = lock.newCondition();

	public WaiterFactory(Hostess hostess, int numWaiters, int numTablesPerWaiter) {
		this.hostess = hostess;
		this.numWaiters = numWaiters;
		this.numTablesPerWaiter = numTablesPerWaiter;
		for (int i=0; i < numWaiters; i++) {
			waiterThreadVector.add(new WaiterThread(hostess, i, this, numTablesPerWaiter));
		}
	}

	public void returnWaiter(WaiterThread wt) {
		lock.lock();
		wt.returnTable(wt.getTable(0));
		//waiterThreadVector.set(wt.getWaiterNumber(), new WaiterThread(wt.getHostess(), wt.getWaiterNumber(), this));
		waiterAvailable.signal();
		lock.unlock();

	}

	public WaiterThread getWaiter() {
		WaiterThread wt = null;
		try {
			lock.lock();
			while (wt == null) {
				int i;
				for (i=0; i < waiterThreadVector.size(); i++) {
					wt = waiterThreadVector.get(i);
					if (wt != null) {
						// this will only allow one table per waiter
						// use a semaphore to allow more than one table per waiter
						if (wt.getNumAvailableTables() > 0) {
//						if (wt.getTable() == null) {
							break;
						}
					}
				}
				if (i == waiterThreadVector.size()) {
					// if i get here, i haven't secured a waiter yet
					waiterAvailable.await();
				}
			}
		} catch(InterruptedException ie) {
			System.out.println("WaiterFactory.getWaiter(): IE : " + ie.getMessage());
		} finally {
			lock.unlock();
		}
		return wt;
	}

}

class WaiterThread extends Thread {
	private Hostess hostess;
//	private Table table;
	private Vector<Table> tables;
	private int waiterNumber;
	private WaiterFactory waiterFactory;
	private Semaphore numTablesSemaphore;
	private Lock waiterLock = new ReentrantLock();
	private Condition tableAssignedCondition = waiterLock.newCondition();
	private Condition orderCompleteCondition = waiterLock.newCondition();
	
	private Order order;

	public WaiterThread(Hostess hostess, int waiterNumber, WaiterFactory waiterFactory, int numTablesPerWaiter) {
		this.hostess = hostess;
		this.waiterNumber = waiterNumber;
		this.waiterFactory = waiterFactory;
		this.numTablesSemaphore = new Semaphore(numTablesPerWaiter);
		tables = new Vector<Table>(numTablesPerWaiter);
		this.start();
	}
	
	public Condition returnOrderCompleteCondition(){
		return orderCompleteCondition;
	}
	
	public void setOrder(Order o){
		this.order = o;
	}
	public Order returnOrder(){
		return order;
	}

	public int getNumAvailableTables() {
		int numPermitsAvailable = numTablesSemaphore.availablePermits();
		return numPermitsAvailable;
	}
	public Hostess getHostess() {
		return this.hostess;
	}

	public void returnTable(Table table) {
//		this.table = null;
		tables.remove(table);
		numTablesSemaphore.release();
	}

	public void setTable(Table table) {
		try {
			numTablesSemaphore.acquire();
//		 	this.table = table;
			tables.add(table);
			this.waiterLock.lock();
			this.tableAssignedCondition.signalAll();
			this.waiterLock.unlock();

		} catch (InterruptedException ie) {
			System.out.println("WaiterFactory.setTable(" + table.getTableNumber() + ") IE: " + ie.getMessage());
		}

	}

	public Table getTable(int i) {
		if (tables.size() > i) {
			return tables.elementAt(i);			
		}
		return null;
//		return this.table;
	}

	public int getWaiterNumber() {
		return this.waiterNumber;
	}

	public void run() {
		try {
			while (true) {
				// waiter can only have one table currently
				// wait until notified, meaning that a table has been seated
				// i don't think i always want to do this - what if a table has already been set?
				this.waiterLock.lock();
				this.tableAssignedCondition.await();
				this.orderCompleteCondition.await();
				this.waiterLock.unlock();
				Thread.sleep(1000 * (int)(Math.random() * 10)); // sleep for between 0 and 10 seconds
				if (getTable(0) != null) {
					getTable(0).getLock().lock();
					// signal the customer who is "eating"
					getTable(0).getReadyCondition().signal();
					Restaurant.addWaiterMessage("Waiter " + getWaiterNumber() + " has delivered order " + returnOrder().getOrderText()+" to table "+order.getTable().getTableNumber(), getWaiterNumber());
					
					getTable(0).getLock().unlock();
				}
				//waiterFactory.returnWaiter(this);
			}
		} catch (InterruptedException ie) {
			System.out.println("CustomerThread.run(): InterruptedException: " + ie.getMessage());
		}
	}
}