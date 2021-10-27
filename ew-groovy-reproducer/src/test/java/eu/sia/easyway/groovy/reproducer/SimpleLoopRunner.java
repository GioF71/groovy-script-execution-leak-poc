package eu.sia.easyway.groovy.reproducer;

import java.util.concurrent.TimeUnit;

public class SimpleLoopRunner implements Runnable {
	
	private final Runnable runnable;
	private final TimeUnit delayTimeUnit;
	private final long delay;

	SimpleLoopRunner(Runnable runnable, TimeUnit delayTimeUnit, long delay) {
		this.runnable = runnable;
		this.delayTimeUnit = delayTimeUnit;
		this.delay = delay;
	}

	@Override
	public void run() {
		boolean failed = false;
		while (!failed) {
			try {
				runnable.run();
			} catch (Exception exc) {
				failed = true;
				exc.printStackTrace();
			}
			try {
				long delayNanoSec = delayTimeUnit.toNanos(delay);
				Thread.sleep(delayNanoSec / 1000000L, (int) (delayNanoSec % 1000000L));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

