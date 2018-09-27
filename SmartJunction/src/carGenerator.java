import java.text.SimpleDateFormat;
import java.util.Date;
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
			try {
				for (car c : carList) {
					for (road r : Main.roadList) {
						if (r.id == c.road) {
							if (r.activeCars.size() == 5) {
								System.out.println(getTime() + "   CG   Road " + r.id + " is full. Car " + c.id + " is waiting to enter road " + r.id);
							} else {
								carList.remove(c);
								r.car = c;
								r.activeCars.add(c);
								c.setReleaseParameters(rel);
								c.start();
								break;
							}
						}
					}
				}
			} 
			catch (Exception e) {
				// TODO: handle exception
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
	
	private String getTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Date now = new Date();

		return sdfTime.format(now);
	}
}
