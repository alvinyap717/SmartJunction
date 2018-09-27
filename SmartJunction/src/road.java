import java.util.LinkedList;

import javax.realtime.RealtimeThread;
import javax.realtime.ReleaseParameters;

public class road {
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
}
