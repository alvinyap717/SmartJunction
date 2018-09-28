import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.AsynchronouslyInterruptedException;
import javax.realtime.Interruptible;
import javax.realtime.RealtimeThread;

public class camera extends RealtimeThread{
	ACModeChanger modeChange;
	ACModeSwitcher Switch;
	static AsyncEvent event;
	static boolean accident = false;
	TFsensor tfS;

	public camera(TFsensor tfS) {
		modeChange = new ACModeChanger("Normal");
		Switch = new ACModeSwitcher(modeChange);
		event = new AsyncEvent();
		event.addHandler(Switch);
		this.tfS = tfS;
	}

	@Override
	public void run() {
		Normal normal = new Normal(tfS);
		Accident accident = new Accident(tfS);

		while (true) {
			if (modeChange.currentMode() == "Normal") {
				modeChange.doInterruptible(normal);
			}
			else {
				modeChange.doInterruptible(accident);
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

class Normal implements Interruptible {
	TFsensor tfS;

	public Normal(TFsensor tfS) {
		this.tfS = tfS;
	}
	
	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println(camera.getTime() + "   AC   *****Accident occur*****");
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			int x = new Random().nextInt((2 - 1) + 1) + 1;
			if (x == 1) {
				tfS.incidentMode.fire();
				camera.accident = true;
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

class Accident implements Interruptible {
	int x = 0;
	TFsensor tfS;
	
	public Accident(TFsensor tfS) {
		this.tfS = tfS;
	}
	
	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
//		System.out.println(camera.getTime() + "   AC   *****Accident is clear!*****");
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			if (x == 30) {
				if (weather.currMode == "RAINNY") {
					tfS.longMode.fire();
				}
				else if (weather.currMode == "SUNNY") {
					if (CGsensor.x == 2 || CGsensor.x== 4) {
						tfS.longMode.fire();
					} else {
						tfS.normalMode.fire();
					}
				}
				else if (weather.currMode == "FLOOD") {
					tfS.incidentMode.fire();
				}
				System.out.println(camera.getTime() + "   AC   *****Accident is clear!*****");
				x = 0;
			}
			x++;
			
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

class ACModeChanger extends AsynchronouslyInterruptedException {
	String mode;

	public ACModeChanger(String initial) {
		super();
		mode = initial;
	}

	public synchronized String currentMode() {
		return mode;
	}

	public synchronized void toggleMode() {
		if (mode == "Normal") {
			mode = "Accident";
		}
		else {
			mode = "Normal";
		}
	}
}

class ACModeSwitcher extends AsyncEventHandler {
	ACModeChanger aie;

	public ACModeSwitcher(ACModeChanger aie) {
		this.aie = aie;
	}

	public void handleAsyncEvent() {
		aie.toggleMode();
		aie.fire();
	}
}

class ACsensor extends RealtimeThread {
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
			} 
			catch (Exception e) {
				// TODO: handle exception
			}
			if (camera.accident) {
				camera.event.fire();
				camera.accident = false;
			}
			waitForNextPeriod();
		}
	}
}



