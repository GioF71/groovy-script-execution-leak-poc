package eu.sia.easyway.groovy.reproducer.observation;

import java.util.Comparator;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

class ReverseMetricComparator implements Comparator<Metric> {
	
	private final Comparator<Metric> cmp = new MetricComparator();
	
	@Override
	public int compare(Metric o1, Metric o2) {
		return cmp.compare(o2, o1);
	}
}
