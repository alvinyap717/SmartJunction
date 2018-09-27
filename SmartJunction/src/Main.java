import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class Main {
	static LinkedList<road> roadList = new LinkedList<road>();
	static int p1;
	static int p2;
	static ExecutorService es = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		ReleaseParameters crel = new PeriodicParameters(new RelativeTime(1000, 0));
		ReleaseParameters arel = new PeriodicParameters(new RelativeTime(3000, 0));
		ReleaseParameters prel = new PeriodicParameters(new RelativeTime(5000, 0));
		ReleaseParameters trel = new PeriodicParameters(new RelativeTime(3000, 0));

		for (int i = 1; i <= 13; i++) {
			roadList.add(new road(i, null, arel));
		}

		carGenerator cg = new carGenerator();
		cg.setReleaseParameters(crel);
		cg.start();
		
		trafficLight tl = new trafficLight();
		tl.setReleaseParameters(trel);
		tl.start();
		
		/*pedestrianRoad pr = new pedestrianRoad();
		pr.setReleaseParameters(prel);
		pr.start();*/
		
		weather ex = new weather(tl);
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(1000,0));
		ex.setReleaseParameters(rel);
		ex.start();
	}
}

