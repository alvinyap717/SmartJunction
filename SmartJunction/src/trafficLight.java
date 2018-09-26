import java.util.concurrent.locks.ReentrantLock;

import javax.realtime.RealtimeThread;

public class trafficLight extends RealtimeThread {
	static ReentrantLock HJunction;
	static ReentrantLock VJunction;
	String status;

	public trafficLight() {
		HJunction = new ReentrantLock();
		VJunction = new ReentrantLock();
		status = "H_Lock";
	}

	@Override
	public void run() {
		while (true) {
			if (status.equals("H_Lock")) {
				try {
					HJunction.lock();
					System.out.println("Vertical Road GREEN Light");
					VJunction.unlock();
				} catch (Exception e) {
					// TODO: handle exception
				}
				status = "V_Lock";
			}
			else {
				try {
				VJunction.lock();
				System.out.println("Horizontal Road GREEN Light");
				HJunction.unlock();
				} catch (Exception e) {
					// TODO: handle exception
				}
				status = "H_Lock";
			}
			waitForNextPeriod();
		}
	}
}