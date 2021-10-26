package eu.sia.easyway.groovy.reproducer.runnable;

import java.util.List;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public interface Observer {
	void increment(long deltaNanoSec);
	List<String> get();
	List<Metric> getMetricList(double lastSec, boolean removeOlder);
}
