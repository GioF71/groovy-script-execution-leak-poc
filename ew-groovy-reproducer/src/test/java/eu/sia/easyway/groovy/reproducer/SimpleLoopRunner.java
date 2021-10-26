package eu.sia.easyway.groovy.reproducer;

public class SimpleLoopRunner implements Runnable {
	
	private final Runnable runnable;
	private final long delayMillisec;
	private final int delayNanoSec;

	SimpleLoopRunner(Runnable runnable, long delayMillisec, int delayNanoSec) {
		this.runnable = runnable;
		this.delayMillisec = delayMillisec;
		this.delayNanoSec = delayNanoSec;
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
				Thread.sleep(delayMillisec, delayNanoSec);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

