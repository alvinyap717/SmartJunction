import java.text.SimpleDateFormat;
import java.util.Date;

import javax.realtime.RealtimeThread;

public class car extends RealtimeThread {
	int id;
	int road;
	int distance = 0;
	car activeCar;

	public car(int id, int road) {
		this.id = id;
		this.road = road;
	}

	@Override
	public void run() {
		while (true) {
			if (distance == 0) {
				System.out.println(getTime() + "   C    Car " + id + " has entered road " + road);
			} else if (distance == 3){
				if (road == 1 || road == 4 || road == 7 || road == 10) {
					if (!trafficLight.HJunction.isLocked()) {
						if (Main.roadList.get(road+3-1).activeCars.size() < 10) {
							removeActiveCar(this);
							road += 3;
							addActiveCar(this);
							System.out.println(getTime() + "   C    Car " + id + " entering road " + road);
							distance = 0;
						} else {
							System.out.println(getTime() + "   C    Road " + (road+3) + " is full. Car " + id 
									+ " is waiting in road " + road);
						}
					} else {
						System.out.println(getTime() + "   JL   Car " + id + " is waiting in road " + road);
						distance--;
					}
				} 
				else if (road == 2 || road == 5 || road == 8 || road == 11) {
					if (!trafficLight.VJunction.isLocked()) {
						if (Main.roadList.get(road).activeCars.size() < 10) {
							removeActiveCar(this);
							road += 1;
							addActiveCar(this);
							System.out.println(getTime() + "   C    Car " + id + " entering road " + road);
							distance = 0;
						} else {
							System.out.println(getTime() + "   C    Road " + (road+1) + " is full. Car " + id 
									+ " is waiting in road " + road);
						}
					} else {
						System.out.println(getTime() + "   JL   Car " + id + " is waiting in road " + road);
						distance--;
					}
				}
				else {
					removeActiveCar(this);
					System.out.println(getTime() + "   C    Car " + id + " has reach the end of road " + road 
							+ " and leave");
					deschedulePeriodic();
					break;
				}
			}
			distance++;
			waitForNextPeriod();
		}
	}

	private void removeActiveCar(car car) {
		for (road r : Main.roadList) {
			if (r.id == road) {
				r.activeCars.remove(car);
			}
		}
	}

	private void addActiveCar(car car) {
		for (road r : Main.roadList) {
			if (r.id == road) {
				r.activeCars.add(car);
			}
		}
	}

	private String getTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Date now = new Date();

		return sdfTime.format(now);
	}
}
