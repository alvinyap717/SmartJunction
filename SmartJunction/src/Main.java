import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.realtime.PeriodicParameters;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class Main {
	static LinkedList<road> roadList = new LinkedList<road>();
	static ExecutorService es = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(1000, 0));
		
		for (int i = 1; i <= 13; i++) {
			roadList.add(new road(i, null, rel));
		}
		
		carGenerator cg = new carGenerator();
		cg.setReleaseParameters(rel);
		cg.start();
	}
}
