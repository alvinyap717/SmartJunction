import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.AsynchronouslyInterruptedException;
import javax.realtime.Interruptible;
import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;

public class weather extends RealtimeThread{
	static String currMode = "";
	WModeChanger modeChange;
	WModeSwitcher Switch;
	static AsyncEvent event;
	Random r = new Random();
	static int temp = 20;
	static int flood;
	static boolean status = false;
	TFsensor tfS;

	public weather(TFsensor tfS) {
		currMode = "SUNNY";
		modeChange = new WModeChanger("SUNNY");
		Switch = new WModeSwitcher(modeChange);
		event = new AsyncEvent();
		event.addHandler(Switch);
		this.tfS = tfS;
		
		sensor s = new sensor();
		s.setReleaseParameters(new PeriodicParameters(new RelativeTime(100, 0)));
		s.start();
	}

	public void run() {
		Rainny rainny = new Rainny(tfS);
		Sunny sunny = new Sunny(tfS);

		while (true) {
			if (modeChange.currentMode() == "RAINNY") {
				modeChange.doInterruptible(rainny);
			}
			else if (modeChange.currentMode() == "SUNNY"){
				modeChange.doInterruptible(sunny);
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

class Rainny implements Interruptible{
	TFsensor tfS;

	public Rainny(TFsensor tfS) {
		this.tfS = tfS;
	}

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println(weather.getTime() + "   W    ****Now is Sunny Day****");
		if (weather.status) {
			System.out.println(weather.getTime() + "   W    ****Road 7 is now clear****");
			weather.status = false;
		}
		if (CGsensor.x == 1 || CGsensor.x == 3) {
			tfS.normalMode.fire();
		} else {
			tfS.longMode.fire();
		}
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			weather.temp -= new Random().nextInt((5 - 1) + 1) + 1;
			weather.flood += new Random().nextInt((10 - 1) + 1) + 1;
			if (weather.flood > 35) {
				if (!weather.status) {
					System.out.println(weather.getTime() + "   W   ****Road 7 is flood!****");
					tfS.incidentMode.fire();
					weather.currMode = "FLOOD";
					weather.status = true;
				}
			}
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {}
			RealtimeThread.waitForNextPeriod();
		}
	}
}

class Sunny implements Interruptible{
	TFsensor tfS;

	public Sunny(TFsensor tfS) {
		this.tfS = tfS;
	}

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println(weather.getTime() + "   W    ****Now is Rainny Day****");
		tfS.longMode.fire();
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			weather.temp += new Random().nextInt((10 - 1) + 1) + 1;
			weather.flood = 0;
			
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {}
			RealtimeThread.waitForNextPeriod();
		}
	}
}

class WModeChanger extends AsynchronouslyInterruptedException {
	String mode;

	public WModeChanger(String initial) {
		super();
		mode = initial;
	}

	public synchronized String currentMode() {
		return mode;
	}

	public synchronized void toggleMode() {
		if (mode == "RAINNY") {
			mode = "SUNNY";
			weather.currMode = "SUNNY";
		}
		else {
			mode = "RAINNY";
			weather.currMode = "RAINNY";
		}
	}
}

class WModeSwitcher extends AsyncEventHandler {
	WModeChanger aie;

	public WModeSwitcher(WModeChanger aie) {
		this.aie = aie;
	}

	public void handleAsyncEvent() {
		aie.toggleMode();
		aie.fire();
	}
}

class sensor extends RealtimeThread {

	public void run() {
		while (true) {
			if (weather.temp > 80) {
				weather.temp = 80;
				weather.event.fire();
			}
			else if (weather.temp < 20) {
				weather.temp = 20;
				weather.event.fire();
			}
			
			RelativeTime slow = new RelativeTime(500, 0);
			try {
				RealtimeThread.sleep(slow);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

	

