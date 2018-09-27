import java.text.SimpleDateFormat;
import java.util.Date;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class car extends RealtimeThread {
	int id;
	int road;
	int distance = 0;
	car activeCar;
	AsyncEvent slowDown;
	AsyncEvent speedUp;
	AsyncEvent incident;

	public car(int id, int road) {
		this.id = id;
		this.road = road;
		slowDown = new AsyncEvent();
		slowDown.addHandler(new slowDown(this));
		speedUp = new AsyncEvent();
		speedUp.addHandler(new speedUp(this));
		incident = new AsyncEvent();
		incident.addHandler(new incident(this));
	}

	@Override
	public void run() {
		while (true) {
			if (distance == 0) {
				System.out.println(getTime() + "   Car " + id + " has entered road " + road);
				distance++;
			} else {
				if (road == 1 || road == 4 || road == 7 || road == 10) {
//					if (road == 4 && pedestrianRoad.P2.isLocked()) {
//						System.out.println(getTime() + "      Car " + id + " is waiting in front of Pedestrian Cross 2 in road " + road);
//					} else {
						if (!trafficLight.HJunction.isLocked()) {
							removeActiveCar(this);
							road += 3;
							addActiveCar(this);
							System.out.println(getTime() + "      Car " + id + " entering road " + road);
						} else {
							System.out.println(getTime() + "      Car " + id + " is waiting in road " + road);
						}
//					}
				} 
				else if (road == 2 || road == 5 || road == 8 || road == 11) {
//					if (road == 2 && pedestrianRoad.P1.isLocked()) {
//						System.out.println(getTime() + "      Car " + id + " is waiting in front of Pedestrian Cross 1 in road " + road);
//					} else {
						if (!trafficLight.VJunction.isLocked()) {
							removeActiveCar(this);
							road += 1;
							addActiveCar(this);
							System.out.println(getTime() + "      Car " + id + " entering road " + road);
						} else {
							System.out.println(getTime() + "      Car " + id + " is waiting in road " + road);
						}
//					}
				}
				else {
					removeActiveCar(this);
					System.out.println(getTime() + "   Car " + id + " has reach the end of road " + road + " and leave");
					deschedulePeriodic();
					break;
				}
			}
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

class slowDown extends AsyncEventHandler {
	car car;

	public slowDown(car car) {
		this.car = car;
	}

	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(5000, 0));
		car.setReleaseParameters(rel);
		car.schedulePeriodic();
	}
}

class speedUp extends AsyncEventHandler {
	car car;

	public speedUp(car car) {
		this.car = car;
	}

	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(3000, 0));
		car.setReleaseParameters(rel);
		car.schedulePeriodic();
	}
}

class incident extends AsyncEventHandler {
	car car;

	public incident(car car) {
		this.car = car;
	}

	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(8000, 0));
		car.setReleaseParameters(rel);
		car.schedulePeriodic();
	}
}
