package eu.sia.easyway.groovy.reproducer.observation;

import java.util.List;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public interface Observer {
	void increment(long deltaNanoSec);
	List<Metric> getMetricList(double seconds, boolean removeOlder);
}
