package eu.sia.easyway.groovy.reproducer;

import java.util.concurrent.TimeUnit;

public class SimpleLoopRunner implements Runnable {
	
	private final Runnable runnable;

	private final long delayMilliSec;
	private final int delayRemainingNanoSec;

	private long durationNanoSec = 10L * 1000L * 1000L * 1000L; 

	SimpleLoopRunner(
			Runnable runnable, 
			TimeUnit delayTimeUnit, 
			long delay) {
		this.runnable = runnable;
		long delayNano = delayTimeUnit.toNanos(delay);
		this.delayMilliSec = nanoToMilliSec(delayNano);
		this.delayRemainingNanoSec = additionalNanoSec(delayNano);
	}
	
	private long nanoToMilliSec(long nano) {
		return nano / 1000000L;
	}
	
	private int additionalNanoSec(long nano) {
		return (int) (nano % 1000000L);
	}

	@Override
	public void run() {
		long start = System.nanoTime();
		long limit = start + durationNanoSec;
		boolean failed = false;
		boolean finished = false;
		while (!failed && !finished) {
			long now = System.nanoTime();
			if (now > limit) {
				finished = true;
			}
			try {
				runnable.run();
			} catch (Exception exc) {
				failed = true;
				exc.printStackTrace();
			}
			try {
				Thread.sleep(delayMilliSec, delayRemainingNanoSec);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

