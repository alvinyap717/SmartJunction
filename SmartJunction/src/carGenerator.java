import java.util.LinkedList;
import java.util.Random;

import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class carGenerator extends RealtimeThread {
	LinkedList<car> carList = new LinkedList<car>();
	int[] roadNum = new int[] {1, 2, 5, 8, 11};
	int id = 1; 
	int road;
	static ReleaseParameters rel = new PeriodicParameters(new RelativeTime(3000, 0));

	@Override
	public void run() {
		while (true) {
			road = roadNum[getRoad()];
			car car = new car(id, road);
			add(car);
			id++;
			for (road r : Main.roadList) {
				if (r.id == road) {
					if (r.activeCars.size() == 5) {
						System.out.println(" Road " + r.id + " is full ");
						System.out.println(" Car " + car.id + " is waiting to enter road " + r.id);
					} else {
						r.car = car;
						r.activeCars.add(car);
						car.setReleaseParameters(rel);
						car.start();
					}
				}
			}
			waitForNextPeriod();
		}
	}

	public void add(car c) {
		carList.add(c);
	}

	private int getRoad() {
		Random r = new Random();
		return r.nextInt((4 - 0) + 1) + 0;
	}
}
