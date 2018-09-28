import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.realtime.PeriodicParameters;
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
		ReleaseParameters trel = new PeriodicParameters(new RelativeTime(1000, 0));

		for (int i = 1; i <= 13; i++) {
			roadList.add(new road(i, null, arel));
		}

		TFsensor tfS = new TFsensor();
		tfS.setReleaseParameters(new PeriodicParameters(new RelativeTime(12000, 0)));
		
		carGenerator cg = new carGenerator(tfS);
		cg.setReleaseParameters(crel);
		cg.start();
		
		trafficLight tl = new trafficLight();
		tl.setReleaseParameters(trel);
		tl.start();
		
		weather ex = new weather(tfS);
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(1000,0));
		ex.setReleaseParameters(rel);
		ex.start();
		
		camera cr = new camera(tfS);
		ReleaseParameters crrel = new PeriodicParameters(new RelativeTime(1000,0));
		cr.setReleaseParameters(crrel);
		cr.start();
		
		ACsensor acS = new ACsensor();
		acS.setReleaseParameters(new PeriodicParameters(new RelativeTime(500, 0)));
		acS.start();
		
		tfS.start();
		
		/*pedestrianRoad pr = new pedestrianRoad();
		pr.setReleaseParameters(prel);
		pr.start();*/
	}
}

