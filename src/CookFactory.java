import java.util.*;
import java.util.concurrent.locks.*;

public class CookFactory {
	private static Vector<Order> ordersToCook;

	public CookFactory(CookPanel cp, int numCooks ) {
		ordersToCook = new Vector<Order>();

		for ( int i=0; i<numCooks; i++ ) {
			CookThread c = new CookThread( i, cp, this );
			c.start();
		}

	}
	
	public void appendNewOrder(Order o){
		ordersToCook.add(o);
	}
	public static Vector<Order> returnOrdersToCook(){
		return ordersToCook;
	}

}


class CookThread extends Thread {
	private int cookNumber;
	private CookPanel cookPanel;
	private CookFactory cookFactory;
	
	private Lock cookLock = new ReentrantLock();
	private Condition orderCompleteCondition = cookLock.newCondition();
	
	
	public CookThread( int n, CookPanel cp, CookFactory cf ) {
		cookNumber = n;
		cookPanel = cp;
		cookFactory = cf;		
	}
	
	public void run() {
		while(true){
		cookPanel.addCookMessage( "Cook " + cookNumber + " is ready to cook." );
		try {
				if(!CookFactory.returnOrdersToCook().isEmpty()){
					
				
					
					this.cookLock.lock();
					
					Order o = CookFactory.returnOrdersToCook().get(0);
					cookPanel.addCookMessage("Cook "+ cookNumber + " is cooking for an order for " + o.getOrderText()+ " for table " +o.getTable().getTableNumber());
					Thread.sleep(1000 * (int)(Math.random() * 10));
					CookFactory.returnOrdersToCook().remove(0);
					
					cookPanel.addCookMessage("Cook " +cookNumber + " has completed an order for " + o.getOrderText()+ " for table " +o.getTable().getTableNumber());
//					o.getWaiter().returnOrderCompleteCondition().signal();
	
					this.cookLock.unlock();
				}
				else{
					Thread.sleep(1000 * (int)(Math.random() * 20));
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
	}
}

