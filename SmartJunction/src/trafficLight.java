import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class trafficLight extends RealtimeThread {
	static ReentrantLock HJunction;
	static ReentrantLock VJunction;
	String status;
	AsyncEvent longSchedule;
	AsyncEvent normalSchedule;
	AsyncEvent longerSchedule;

	public trafficLight() {
		HJunction = new ReentrantLock();
		VJunction = new ReentrantLock();
		status = "H_Lock";
		longSchedule = new AsyncEvent();
		longSchedule.addHandler(new longMode(this));
		normalSchedule = new AsyncEvent();
		normalSchedule.addHandler(new normalMode(this));
		longerSchedule = new AsyncEvent();
		longerSchedule.addHandler(new longerMode(this));
	}

	@Override
	public void run() {
		while (true) {
			if (status.equals("H_Lock")) {
				try {
					HJunction.lock();
					System.out.println(getTime() + "   TL   *****Vertical Road GREEN Light*****");
					System.out.println(getTime() + "   TL   *****Horizontal Road RED Light*****");
					VJunction.unlock();
				} catch (Exception e) {
					// TODO: handle exception
				}
				status = "V_Lock";
			}
			else {
				try {
				VJunction.lock();
				System.out.println(getTime() + "   TL   *****Horizontal Road GREEN Light*****");
				System.out.println(getTime() + "   TL     *****Vertical Road RED Light*****");
				HJunction.unlock();
				} catch (Exception e) {
					// TODO: handle exception
				}
				status = "H_Lock";
			}
			waitForNextPeriod();
		}
	}
	
	public static String getTime() {
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

		Date now = new Date();

		return sdfTime.format(now);
	}
}

class longMode extends AsyncEventHandler {
	RealtimeThread rtt;
	
	public longMode(RealtimeThread rtt) {
		this.rtt = rtt;
	}
	
	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(6000, 0));
		rtt.setReleaseParameters(rel);
		rtt.schedulePeriodic();
		
		System.out.println(trafficLight.getTime() + "   TL   *****Traffic Light changes each 6 seconds*****");
	}
}

class normalMode extends AsyncEventHandler {
	RealtimeThread rtt;
	
	public normalMode(RealtimeThread rtt) {
		this.rtt = rtt;
	}
	
	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(3000, 0));
		rtt.setReleaseParameters(rel);
		rtt.schedulePeriodic();
		System.out.println(trafficLight.getTime() + "   TL   *****Traffic Light changes each 3 seconds*****");
	}
}

class longerMode extends AsyncEventHandler {
	RealtimeThread rtt;
	
	public longerMode(RealtimeThread rtt) {
		this.rtt = rtt;
	}
	
	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(8000, 0));
		rtt.setReleaseParameters(rel);
		rtt.schedulePeriodic();
		
		System.out.println(trafficLight.getTime() + "   TL   *****Traffic Light changes each 8 seconds*****");
	}
}