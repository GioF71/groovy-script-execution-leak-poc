package eu.sia.easyway.groovy.reproducer;

import java.util.concurrent.TimeUnit;

public class SimpleLoopRunner implements Runnable {
	
	private final Runnable runnable;

	private final long delayMilliSec;
	private final int delayRemainingNanoSec;

	private final long durationNanoSec; 

	SimpleLoopRunner(
			Runnable runnable, 
			TimeUnit delayTimeUnit, 
			long delay,
			int durationSec) {
		this.runnable = runnable;
		long delayNano = delayTimeUnit.toNanos(delay);
		this.delayMilliSec = nanoToMilliSec(delayNano);
		this.delayRemainingNanoSec = additionalNanoSec(delayNano);
		this.durationNanoSec = durationSec == -1 ? -1 : (long) durationSec * 1000L * 1000L * 1000L;
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
		Long limit = durationNanoSec == -1 ? null : start + durationNanoSec;
		boolean failed = false;
		boolean finished = false;
		while (!failed && !finished) {
			if (limit != null) {
				long now = System.nanoTime();
				if (now > limit.longValue()) {
					finished = true;
				}
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

