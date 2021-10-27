package com.giof71.groovy.reproducer.observation;

import java.util.List;

import com.giof71.groovy.reproducer.stat.Metric;

public interface Observer {
	void increment(long deltaNanoSec);
	List<Metric> getMetricList(double seconds, boolean removeOlder);
}
