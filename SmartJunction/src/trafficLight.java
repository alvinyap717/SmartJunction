import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.AsynchronouslyInterruptedException;
import javax.realtime.Interruptible;
import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class trafficLight extends RealtimeThread {
	static ReentrantLock HJunction;
	static ReentrantLock VJunction;
	TLModeChanger modeChange;
	TLModeSwitcher Switch;
	static AsyncEvent event;
	static String  status = "HLock";

	public trafficLight() {
		HJunction = new ReentrantLock();
		VJunction = new ReentrantLock();

		modeChange = new TLModeChanger("VLock");
		Switch = new TLModeSwitcher(modeChange);
		event = new AsyncEvent();
		event.addHandler(Switch);
	}

	@Override
	public void run() {
		HLock HLock = new HLock();
		VLock VLock = new VLock();

		while (true) {
			if (modeChange.currentMode() == "VLock") {
				modeChange.doInterruptible(VLock);
			}
			else {
				modeChange.doInterruptible(HLock);
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

class HLock implements Interruptible {

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
//		System.out.println(trafficLight.getTime() + "   TL   *****Taffic Light Changing*****");
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			if (trafficLight.status.equals("HLock")) {
				trafficLight.status = "VLock";
				try {
					trafficLight.HJunction.lock();
					trafficLight.VJunction.unlock();
				} catch (Exception e) {
					// TODO: handle exception
				}
				System.out.println(trafficLight.getTime() + "   TL   *****Horizontal Road traffic light turns RED*****");
				System.out.println(trafficLight.getTime() + "   TL   *****Vertical Road traffic light turns GREEN*****");
				System.out.println(trafficLight.getTime() + "   TL   *****Pedestrian crossing Pedestrian cross 2*****");
			}
			try {
				Thread.sleep(1000);
			} 
			catch (Exception e) {
				// TODO: handle exception
			}
			RealtimeThread.waitForNextPeriod();
		}
	}

}

class VLock implements Interruptible {

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
//		System.out.println(trafficLight.getTime() + "   TL   *****Taffic Light Changing*****");
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			if (trafficLight.status.equals("VLock")) {
				trafficLight.status = "HLock";
				try {
					trafficLight.VJunction.lock();
					trafficLight.HJunction.unlock();
				} catch (Exception e) {
					// TODO: handle exception
				}
				System.out.println(trafficLight.getTime() + "   TL   *****Vertical Road traffic light turns RED*****");
				System.out.println(trafficLight.getTime() + "   TL   *****Horizontal Road traffic light turns GREEN*****");
				System.out.println(trafficLight.getTime() + "   TL   *****Pedestrian crossing Pedestrian cross 1*****");
			}
			try {
				Thread.sleep(1000);
			} 
			catch (Exception e) {
				// TODO: handle exception
			}
			RealtimeThread.waitForNextPeriod();
		}
	}

}

class TLModeChanger extends AsynchronouslyInterruptedException {
	String mode;

	public TLModeChanger(String initial) {
		super();
		mode = initial;
	}

	public synchronized String currentMode() {
		return mode;
	}

	public synchronized void toggleMode() {
		if (mode == "HLock") {
			mode = "VLock";
		}
		else {
			mode = "HLock";
		}
	}
}

class TLModeSwitcher extends AsyncEventHandler {
	TLModeChanger aie;

	public TLModeSwitcher(TLModeChanger aie) {
		this.aie = aie;
	}

	public void handleAsyncEvent() {
		aie.toggleMode();
		aie.fire();
	}
}

class TFsensor extends RealtimeThread {
	AsyncEvent longMode;
	AsyncEvent normalMode;
	AsyncEvent incidentMode;
	
	public TFsensor() {
		longMode = new AsyncEvent();
		longMode.addHandler(new longMode(this));
		
		normalMode = new AsyncEvent();
		normalMode.addHandler(new normalMode(this));
		
		incidentMode = new AsyncEvent();
		incidentMode.addHandler(new incidentMode(this));
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO: handle exception
			}
			trafficLight.event.fire();
			waitForNextPeriod();
		}
	}
}

class longMode extends AsyncEventHandler {
	RealtimeThread rtt;

	public longMode(RealtimeThread rtt) {
		this.rtt = rtt;
	}

	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(12000, 0));
		rtt.setReleaseParameters(rel);
		rtt.schedulePeriodic();

		System.out.println(trafficLight.getTime() + "   TL   *****Traffic Light changes each 12 seconds*****");
	}
}

class normalMode extends AsyncEventHandler {
	RealtimeThread rtt;

	public normalMode(RealtimeThread rtt) {
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

class incidentMode extends AsyncEventHandler {
	RealtimeThread rtt;

	public incidentMode(RealtimeThread rtt) {
		this.rtt = rtt;
	}

	@Override
	public void handleAsyncEvent() {
		ReleaseParameters rel = new PeriodicParameters(new RelativeTime(15000, 0));
		rtt.setReleaseParameters(rel);
		rtt.schedulePeriodic();
		System.out.println(trafficLight.getTime() + "   TL   *****Traffic Light changes each 15 seconds*****");
	}
}
