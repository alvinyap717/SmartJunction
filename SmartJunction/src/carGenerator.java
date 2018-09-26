import java.util.LinkedList;
import java.util.Random;

import javax.realtime.RealtimeThread;

public class carGenerator extends RealtimeThread {
	LinkedList<car> carList = new LinkedList<car>();
	int[] roadNum = new int[] {1, 2, 5, 8, 11};
	int id = 1; 
	int road;
	
	@Override
	public void run() {
		while (true) {
			road = roadNum[getRoad()];
			car car = new car(id, road);
			add(car);
			id++;
			for (road r : Main.roadList) {
				if (r.id == road) {
					r.car = car;
					r.activeCars.add(car);
					Main.es.submit(r);
				}
			}
			waitForNextPeriod();
		}
	}
	
	public void add(car c) {
		carList.addLast(c);
	}
	
	public car getCar() {
		return carList.removeFirst();
	}
	
	private int getRoad() {
		Random r = new Random();
		return r.nextInt((4 - 0) + 1) + 0;
	}
}
