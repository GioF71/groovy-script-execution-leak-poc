package eu.sia.easyway.groovy.reproducer.observation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public class ObserverImpl implements Observer {

	private final List<Metric> entries = new ArrayList<>();
	
	@Override
	public void increment(long deltaNanosec) {
		Metric entry = new Metric(System.nanoTime(), deltaNanosec);
		synchronized(entries) {
			entries.add(entry);
		}
	}
	
	@Override
	public List<Metric> getMetricList(double seconds, boolean removeOlder) {
		List<Metric> list = new ArrayList<>();
		long now = System.nanoTime();
		long lowerLimit = removeOlder ? now - ((long) (seconds * (1000000000.0f))) : -1;
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

