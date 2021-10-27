package eu.sia.easyway.groovy.reproducer.observation;

import java.util.Comparator;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

class MetricComparator implements Comparator<Metric> {
	
	@Override
	public int compare(Metric o1, Metric o2) {
		return Long.compare(o1.getNanoTime(), o2.getNanoTime());
	}
}
