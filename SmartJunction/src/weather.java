import java.util.Random;

import javax.realtime.AsyncEvent;
import javax.realtime.AsyncEventHandler;
import javax.realtime.AsynchronouslyInterruptedException;
import javax.realtime.Interruptible;
import javax.realtime.PeriodicParameters;
import javax.realtime.RealtimeThread;
import javax.realtime.RelativeTime;
import javax.realtime.ReleaseParameters;

public class weather extends RealtimeThread{
	modeChanger modeChange;
	modeSwitcher Switch;
	static AsyncEvent event;
	public weather() {
		modeChange = new modeChanger(Mode.RAINNY);
		Switch = new modeSwitcher(modeChange);
		event = new AsyncEvent();
		event.addHandler(Switch);
	}
	
	public void run() {
		Rainny rainny = new Rainny();
		Sunny sunny = new Sunny();
		
		boolean ok=true;
		while (ok) {
			if (modeChange.currentMode() == Mode.RAINNY) {
				modeChange.doInterruptible(rainny);
			}
			else {
				modeChange.doInterruptible(sunny);
			}
			waitForNextPeriod();
		}				
	}
	public void checkWeather() {

		weather ex = new weather();
		Random r = new Random();
		RelativeTime release = new RelativeTime(1000,0);
		ReleaseParameters rel = new PeriodicParameters(release);
		ex.setReleaseParameters(rel);
		ex.start();
		while (true) {
			int weathernum = r.nextInt((10 - 0) + 1) + 0;
			RelativeTime slow = new RelativeTime(1000, 0);
			try {
				RealtimeThread.sleep(slow);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(weathernum == 3) {
				System.out.println(weathernum);
				event.fire();
			}
		}
	}
	
	public static void main(String[] args) {
		weather ex = new weather();
		ex.checkWeather();
	}
}

class Rainny implements Interruptible{

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println("****Now is Rainny Day******");
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		int x=1;
		System.out.println("Rainny Day");
		while (true) {
			for (int i=1;i<10;i++) {
				System.out.println("operating in Rainny Day: " + i);
				//x++;	
				try {
					Thread.sleep(90);
				}
				catch (Exception e) {}
				
			}
			
			RealtimeThread.waitForNextPeriod();
		}
	}
}
class Sunny implements Interruptible{

	@Override
	public void interruptAction(AsynchronouslyInterruptedException exception) {
		System.out.println("****Now is Sunny Day******");
	}

	@Override
	public void run(AsynchronouslyInterruptedException exception) throws AsynchronouslyInterruptedException {
		int x=1;
		System.out.println("Sunny Day");
		while (true) {
			for (int i=1;i<10;i++) {
				System.out.println("operating in Sunny Day: " + i);
				//x++;	
				try {
					Thread.sleep(90);
				}
				catch (Exception e) {}
				
			}
			RealtimeThread.waitForNextPeriod();
		}
	}
}

enum Mode {RAINNY, SUNNY}

class modeChanger extends AsynchronouslyInterruptedException{
	Mode current;
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
		if (current == Mode.RAINNY) {
			current = Mode.SUNNY;
		}
		else {
			current = Mode.RAINNY;
		}
		System.out.println("mode changed");
	}
}

class modeSwitcher extends AsyncEventHandler {
	modeChanger aie;
	public modeSwitcher(modeChanger aie) {
		this.aie=aie;
	}
	public void handleAsyncEvent() {
		System.out.println("fired");
		aie.toggleMode();
		aie.fire();
	}
}



