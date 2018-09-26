import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.realtime.RealtimeThread;

public class car extends RealtimeThread {
	int id;
	int road;
	int distance = 1;
	car activeCar;

	public car(int id, int road) {
		this.id = id;
		this.road = road;
	}

	@Override
	public void run() {
		while (true) {
			if (distance == 3) {
				activeCar = getCar();
				if (road == 1 || road == 4 || road == 7 || road == 10) {
					removeActiveCar();
					road += 3;
					distance = 1;
					addActiveCar();
					System.out.println(getTime() + "   Car " + id + " entering road " + road);
				} 
				else if (road == 2 || road == 5 || road == 8 || road == 11) {
					removeActiveCar();
					road += 1;
					distance = 1;
					addActiveCar();
					System.out.println(getTime() + "   Car " + id + " entering road " + road);
				}
				else {
					removeActiveCar();
					System.out.println(getTime() + "   Car " + id + " has reach the end of road " + road + " and leave");
					deschedulePeriodic();
				}
			}
			distance++;
			waitForNextPeriod();
		}
	}

	private void removeActiveCar() {
		for (road r : Main.roadList) {
			if (r.id == road) {
				r.activeCars.remove(activeCar);
			}
		}
	}

	private void addActiveCar() {
		for (road r : Main.roadList) {
			if (r.id == road) {
				r.activeCars.add(activeCar);
			}
		}
	}

	private car getCar() {
		car car = null;

		for (road r : Main.roadList) {
			if (r.id == road) {
				if (!r.activeCars.isEmpty()) {
					for (car c : r.activeCars) {
						if (c.id == id) {
							car = c;
						}
					}
				}
			}
		}
		return car;
	}

	private String getTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Date now = new Date();

		return sdfTime.format(now);
	}
}
