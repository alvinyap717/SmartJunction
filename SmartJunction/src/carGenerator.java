import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class carGenerator extends RealtimeThread {
	static LinkedList<car> carList = new LinkedList<car>();
	int[] roadNum = new int[] {1, 2, 5, 8, 11};
	int[] roadNo = new int[] {2, 8};
	int id = 1; 
	int road;

	public carGenerator(TFsensor tfS) {
		releaseCar relC = new releaseCar();
		CGsensor cgS = new CGsensor(relC, tfS);

		cgS.setReleaseParameters(new PeriodicParameters(new RelativeTime(20000, 0)));
		cgS.start();

		relC.setReleaseParameters(new PeriodicParameters(new RelativeTime(500, 0)));
		relC.start();
	}

	@Override
	public void run() {
		while (true) {
			car car;

			if (CGsensor.x == 2 || CGsensor.x == 4) {
				road = roadNo[new Random().nextInt((1 - 0) - 0) + 0];
				car = new car(id, road);
				add(car);
				id++;
			} 
			road = 11;
			car = new car(id, road);
			add(car);
			id++;
			road = roadNum[getRoad()];
			car = new car(id, road);
			add(car);
			id++;
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

	public static String getTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Date now = new Date();

		return sdfTime.format(now);
	}
}

class releaseCar extends RealtimeThread {
	ReleaseParameters rel = new PeriodicParameters(new RelativeTime(1000, 0));

	@Override
	public void run() {
		while (true) {
			try {
				for (car c : carGenerator.carList) {
					for (road r : Main.roadList) {
						if (r.id == c.road) {
							if (r.activeCars.size() == 10) {
								System.out.println(carGenerator.getTime() + "   CG   Road " + r.id + " is full. Car " + c.id + " is waiting to enter road " + r.id);
							} else {
								carGenerator.carList.remove(c);
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
}

class CGsensor extends RealtimeThread {
	TFsensor tfS;
	RealtimeThread rtt;
	static int x = 1;

	public CGsensor(RealtimeThread rtt, TFsensor tfS) {
		this.rtt = rtt;
		this.tfS = tfS;
	}

	@Override
	public void run() {
		while (true) {
			if (x == 1) {
				rtt.setReleaseParameters(new PeriodicParameters(new RelativeTime(500, 0)));
				rtt.schedulePeriodic();
				x++;
				if (!weather.currMode.equals("FLOOD")) {
					tfS.longMode.fire();
				}
				System.out.println(carGenerator.getTime() + "   CG   ****Morning****");
			}
			else if (x == 2) {
				rtt.setReleaseParameters(new PeriodicParameters(new RelativeTime(1000, 0)));
				rtt.schedulePeriodic();
				x++;
				if (!weather.currMode.equals("RAINNY")) {
					tfS.normalMode.fire();
				}
				System.out.println(carGenerator.getTime() + "   CG   ****Afternoon****");
			}
			else if (x == 3) {
				rtt.setReleaseParameters(new PeriodicParameters(new RelativeTime(500, 0)));
				rtt.schedulePeriodic();
				x++;
				if (!weather.currMode.equals("FLOOD")) {
					tfS.longMode.fire();
				}
				System.out.println(carGenerator.getTime() + "   CG   ****Evening****");
			}
			else if (x == 4){
				rtt.setReleaseParameters(new PeriodicParameters(new RelativeTime(1000, 0)));
				rtt.schedulePeriodic();
				x = 1;
				if (!weather.currMode.equals("RAINNY")) {
					tfS.normalMode.fire();
				}
				System.out.println(carGenerator.getTime() + "   CG   ****Night****");
			}
			waitForNextPeriod();
		}
	}
}
