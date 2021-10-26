package eu.sia.easyway.groovy.reproducer.stat;

import lombok.Getter;

@Getter
public class Metric {
	
	private final long nanoTime;
	private final long deltaNanosec;

	public Metric(long nanoTime, long deltaNanosec) {
		this.nanoTime = nanoTime;
		this.deltaNanosec = deltaNanosec;
	}
}
