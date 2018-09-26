import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import javax.realtime.RealtimeThread;
import javax.realtime.ReleaseParameters;

public class road implements Callable<car> {
	int id;
	car car;
	ReleaseParameters rel;
	LinkedList<car> activeCars = new LinkedList<car>();
	RealtimeThread carRtt;

	public road(int id, car car, ReleaseParameters rel) {
		this.id = id;
		this.car = car;
		if (car != null) {
			activeCars.add(car);
		}
		this.rel = rel;
	}

	@Override
	public car call() throws Exception {
		System.out.println(getTime() + "   Car " + car.id + " has entered road " + id);
		carRtt = new RealtimeThread(null, rel, null, null, null, car);
		carRtt.start();
		return car;
	}
	
	private String getTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Date now = new Date();

		return sdfTime.format(now);
	}
}
