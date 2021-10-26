package eu.sia.easyway.groovy.reproducer.runnable;

import eu.sia.easyway.groovy.reproducer.observation.Observer;

public class SimpleObserverRunnable implements Runnable {

	private final Observer observer;
	private final Runnable runnable;
	
	public SimpleObserverRunnable(Observer observer, Runnable runnable) {
		this.observer = observer;
		this.runnable = runnable;
	}
	
	@Override
	public void run() {
		long start = System.nanoTime();
		runnable.run();
		long delta = System.nanoTime() - start;
		observer.increment(delta);
	}

}
