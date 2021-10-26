package eu.sia.easyway.groovy.reproducer.runnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public class ObserverImpl implements Observer {

	private final List<Metric> entries = new ArrayList<>();
	
	private Long nanoTime = System.nanoTime();
	private Integer counter = 0;
	
	@Override // delta still unused
	public void increment(long deltaNanosec) {
		// START DEPRECATING
		synchronized(counter) {
			++counter;
		}
		// STOP DEPRECATING
		Metric entry = new Metric(System.nanoTime(), deltaNanosec);
		synchronized(entries) {
			entries.add(entry);
		}
	}
	
	private Integer doGet() {
		Integer result = null;
		synchronized(counter) {
			result = counter;
			counter = 0;
		}
		return result;
	}
	
	@Override
	public List<String> get() {
		Long current = System.nanoTime();
		Long last = null;
		synchronized(nanoTime) {
			last = nanoTime;
			nanoTime = current;
		}
		Integer value = doGet();
		double deltaSec = (double) (current - last) / (1000000000.0f);
		double speed = (double) (value.intValue()) / deltaSec;
		String result = String.format("Executions: %d [%.3f exec/sec]",
			value.intValue(),
			speed);
		return Collections.singletonList(result);
	}

	@Override
	public List<Metric> getMetricList(double lastSec, boolean removeOlder) {
		List<Metric> list = new ArrayList<>();
		long now = System.nanoTime();
		long lowerLimit = removeOlder ? now - ((long) (lastSec * (1000000000.0f))) : -1;
		synchronized(entries) {
			Iterator<Metric> it = entries.iterator();
			while (it.hasNext()) {
				Metric current = it.next();
				long currentNano = current.getNanoTime();
				// old metric?
				if (currentNano < lowerLimit) {
					// remove old metric?
					if (removeOlder) {
						it.remove();
					}
				} else {
					list.add(current);
				}
			}
		}
		return list;
	}
}

