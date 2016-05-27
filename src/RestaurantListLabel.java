import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JLabel;


public class RestaurantListLabel extends JLabel{
	ReentrantLock lock;
	ArrayList<Integer> list;
	String title;
	public RestaurantListLabel(String title, ReentrantLock lock){
		this.title = title;
		this.setText(title);
		this.lock = lock;
		list = new ArrayList<Integer>();
	}
	public void add(int n){
		lock.lock();
		StringBuilder listToDisplay = new StringBuilder(title);
		list.add(n);
		
		for(int i=0; i< list.size();i++){
			listToDisplay.append(list.get(i)+",");
		}
		if (listToDisplay.toString().contains(",")) {
			listToDisplay.replace(listToDisplay.lastIndexOf(","), listToDisplay.lastIndexOf(",") + 1, "");
		}
		this.setText(listToDisplay.toString());
		lock.unlock();
	}
	public void remove(int n){
		lock.lock();
		StringBuilder listToDisplay = new StringBuilder(title);
		
		int indexToDelete = -1; //probably not a great idea
		for(int i=0; i< list.size();i++){
			if(list.get(i)==n)
				indexToDelete = i;
			else
				listToDisplay.append(list.get(i)+",");
		}
		if (indexToDelete != -1) {
			list.remove(indexToDelete);
		}
		
		if (listToDisplay.toString().contains(",")) {
			listToDisplay.replace(listToDisplay.lastIndexOf(","), listToDisplay.lastIndexOf(",") + 1, "");
		}
		
		this.setText(listToDisplay.toString());
		lock.unlock();
	}
	public ArrayList<Integer> getList(){
		return list;
	}

}
