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
	modeChanger modeChange;
	modeSwitcher Switch;
	static AsyncEvent event;
	trafficLight tf;
	Random r = new Random();
	static int temp = 20;
	static int flood;
	static boolean status = false;

	public weather(trafficLight tf) {
		modeChange = new modeChanger(Mode.SUNNY);
		Switch = new modeSwitcher(modeChange);
		event = new AsyncEvent();
		event.addHandler(Switch);
		this.tf = tf;
		sensor s = new sensor();
		s.setReleaseParameters(new PeriodicParameters(new RelativeTime(100, 0)));
		s.start();
	}

	public void run() {
		Rainny rainny = new Rainny(tf);
		Sunny sunny = new Sunny(tf);

		while (true) {
			if (modeChange.currentMode() == Mode.RAINNY) {
				modeChange.doInterruptible(rainny);
			}
			else if (modeChange.currentMode() == Mode.SUNNY){
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
	trafficLight tf;

	public Rainny(trafficLight tf) {
		this.tf = tf;
	}

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println(weather.getTime() + "   W    ****Now is Sunny Day****");
		tf.normalSchedule.fire();
		carGenerator.rel = new PeriodicParameters(new RelativeTime(3000, 0));
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			weather.temp -= new Random().nextInt((5 - 1) - 1) + 1;
			weather.flood += new Random().nextInt((10 - 1) - 1) + 1;
			if (weather.flood > 35) {
				for (road r : Main.roadList) {
					if (r.id == 1 || r.id == 4 || r.id == 7) {
						for (car c : r.activeCars) {
							c.incident.fire();
						}
					} 
					else if (r.id == 10 || r.id == 13) {
						for (car c : r.activeCars) {
							c.speedUp.fire();
						}
					}
				}
				if (!weather.status) {
					System.out.println(weather.getTime() + "   W   ****Road 7 is flood!****");
					tf.longerSchedule.fire();
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
	trafficLight tf;

	public Sunny(trafficLight tf) {
		this.tf = tf;
	}

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println(weather.getTime() + "   W    ****Now is Rainny Day****");
		tf.longSchedule.fire();
		carGenerator.rel = new PeriodicParameters(new RelativeTime(5000, 0));
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		while (true) {
			weather.temp += new Random().nextInt((5 - 1) - 1) + 1;
			weather.flood = 0;
			if (weather.status) {
				System.out.println(weather.getTime() + "   W    ****Road 7 is now clear****");
				weather.status = false;
			}
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {}
			RealtimeThread.waitForNextPeriod();
		}
	}
}

enum Mode {RAINNY, SUNNY}

class modeChanger extends AsynchronouslyInterruptedException{
	static Mode current;

	public modeChanger(Mode initial) {
		super();
		current = initial;
	}

	public synchronized Mode currentMode() {
		return current;
	}

	public synchronized void setMode(Mode nextMode) {
		current = nextMode;
	}

	public synchronized void toggleMode() {
		if (current == Mode.SUNNY) {
			current = Mode.RAINNY;
		}
		else {
			current = Mode.SUNNY;
		}
	}
}

class modeSwitcher extends AsyncEventHandler {
	modeChanger aie;

	public modeSwitcher(modeChanger aie) {
		this.aie=aie;
	}

	public void handleAsyncEvent() {
		aie.toggleMode();
		aie.fire();
	}
}

class sensor extends RealtimeThread {

	public void run() {
		while (true) {
//			System.out.println("TEMP:" + weather.temp);
//			System.out.println("FLOOD:" + weather.flood);
			if (weather.temp > 80) {
				weather.temp = 80;
				try {
					for (road r : Main.roadList) {
						for (car c : r.activeCars) {
							c.slowDown.fire();
						}
					}
				}catch (Exception e) {
					// TODO: handle exception
				}
				weather.event.fire();
			}
			else if (weather.temp < 20) {
				weather.temp = 20;
				try {
					for (road r : Main.roadList) {
						for (car c : r.activeCars) {
							c.speedUp.fire();
						}
					}
				}catch (Exception e) {
					// TODO: handle exception
				}
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

	

